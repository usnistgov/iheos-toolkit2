package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.fhir.OperationOutcomeGenerator
import gov.nist.toolkit.fhir.simulators.fhir.WrapResourceInHttpResponse
import gov.nist.toolkit.fhir.simulators.mhd.MetadataToDocumentReferenceTranslator
import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentResponseTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.fhir.utility.IFhirSearch
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.registrymetadata.client.DocumentEntry
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.registrymsg.registry.RegistryError
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListParser
import gov.nist.toolkit.results.MetadataToMetadataCollectionParser
import gov.nist.toolkit.soap.http.SoapFault
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.axiom.om.OMElement
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.HttpResponse
import org.apache.http.message.BasicHttpResponse
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.instance.model.api.IBaseResource

class SQResponseToFhirSearchResponseTransform implements ContentResponseTransform {
    static private final Logger logger = Logger.getLogger(SQResponseToFhirSearchResponseTransform.class);

    @Override
    HttpResponse run(SimProxyBase base, BasicHttpResponse response) {
        try {
            def fhirBases = [base.fhirSupportBase()]
//            List<String> fhirBases = ['http://example.com/fhir']  // Fhir servers to search for Patient references
            MetadataToDocumentReferenceTranslator xlat = new MetadataToDocumentReferenceTranslator(fhirBases, new Searcher())

            Bundle bundle = new Bundle()

            String content = Io.getStringFromInputStream(response.getEntity().content)
            Metadata metadata = MetadataParser.parseNonSubmission(content)
            if (metadata.isEmpty()) {
                SoapFault fault = SoapFault.parse(content)
                if (fault) {
                    OperationOutcome oo = OperationOutcomeGenerator.translate(fault)
                    return WrapResourceInHttpResponse.wrap(base, oo, HttpStatus.SC_INTERNAL_SERVER_ERROR)
                }
                OMElement responseEle = Util.parse_xml(content)
                List<OMElement> rel = XmlUtil.decendentsWithLocalName(responseEle, 'RegistryErrorList')
                if (rel && rel.size() > 0) {
                    List<RegistryError> re = new RegistryErrorListParser(rel[0]).getRegistryErrorList()
                    OperationOutcome oo = OperationOutcomeGenerator.translate(re)
                    return WrapResourceInHttpResponse.wrap(base, oo, HttpStatus.SC_INTERNAL_SERVER_ERROR)
                }
            }
            MetadataToMetadataCollectionParser mmparser = new MetadataToMetadataCollectionParser(metadata, 'Label')
            MetadataCollection col = mmparser.get()
            col.docEntries.each { DocumentEntry de ->
                DocumentReference dr = xlat.run(de)
                // TODO = need fullUrl for Bundle
                Bundle.BundleEntryComponent comp = new Bundle.BundleEntryComponent()
                comp.fullUrl = 'http://example.com/fhir'
                comp.resource = dr
                bundle.addEntry(comp)
            }
            return WrapResourceInHttpResponse.wrap(base, bundle, HttpStatus.SC_OK)

        } catch (Throwable e) {
            OperationOutcome oo = new OperationOutcome()
            OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
            com.setSeverity(OperationOutcome.IssueSeverity.FATAL)
            com.setCode(OperationOutcome.IssueType.EXCEPTION)
            com.setDiagnostics(ExceptionUtil.exception_details(e))
            oo.addIssue(com)
            return WrapResourceInHttpResponse.wrap(base, oo, HttpStatus.SC_OK)
        }
    }



    @Override
    HttpResponse run(SimProxyBase base, HttpResponse response) {
        throw new SimProxyTransformException('run(SimProxyBase base, HttpResponse response) not implemented.')
    }

    class Searcher implements IFhirSearch {

        @Override
        Map<URI, IBaseResource> search(String base, String resourceType, List params) {
            def map = new FhirClient().search(base, resourceType, params)
//            IBaseResource thePatient = ToolkitFhirContext.get().newXmlParser().parseResource(patient)
//            def map = [:]
//            map[uri] = theResource
            map
        }
    }

    def patient = '''
<Patient xmlns="http://hl7.org/fhir">
  <id value="example"/> 
  <text> 
    <status value="generated"/> 
    <div xmlns="http://www.w3.org/1999/xhtml">
      <table> 
        <tbody> 
          <tr> 
            <td> Name</td> 
            <td> Peter James 
              <b> Chalmers</b>  (&quot;Jim&quot;)
            </td> 
          </tr> 
          <tr> 
            <td> Address</td> 
            <td> 534 Erewhon, Pleasantville, Vic, 3999</td> 
          </tr> 
          <tr> 
            <td> Contacts</td> 
            <td> Home: unknown. Work: (03) 5555 6473</td> 
          </tr> 
          <tr> 
            <td> Id</td> 
            <td> MRN: 12345 (Acme Healthcare)</td> 
          </tr> 
        </tbody> 
      </table> 
    </div> 
  </text> 
  <!--    MRN assigned by ACME healthcare on 6-May 2001    -->
  <identifier> 
    <use value="usual"/> 
    <type> 
      <coding> 
        <system value="http://hl7.org/fhir/v2/0203"/> 
        <code value="MR"/> 
      </coding> 
    </type> 
    <system value="urn:oid:1.2.343"/> 
    <value value="123"/> 
    <period> 
      <start value="2001-05-06"/> 
    </period> 
    <assigner> 
      <display value="Acme Healthcare"/> 
    </assigner> 
  </identifier> 
  <active value="true"/> 
  <!--    Peter James Chalmers, but called "Jim"    -->
  <name> 
    <use value="official"/> 
    <family value="Chalmers"/> 
    <given value="Peter"/> 
    <given value="James"/> 
  </name> 
  <name> 
    <use value="usual"/> 
    <given value="Jim"/> 
  </name> 
  <name> 
    <!--   Maiden names apply for anyone whose name changes as a result of marriage - irrespective
     of gender   -->
    <use value="maiden"/> 
    <family value="Windsor"/> 
    <given value="Peter"/> 
    <given value="James"/> 
    <period> 
      <end value="2002"/> 
    </period> 
  </name> 
  <!--    use FHIR code system for male / female    -->
  <gender value="male"/> 
  <birthDate value="1974-12-25">
    <extension url="http://hl7.org/fhir/StructureDefinition/patient-birthTime">
      <valueDateTime value="1974-12-25T14:35:45-05:00"/> 
    </extension> 
  </birthDate> 
  <deceasedBoolean value="false"/> 
</Patient> '''

}
