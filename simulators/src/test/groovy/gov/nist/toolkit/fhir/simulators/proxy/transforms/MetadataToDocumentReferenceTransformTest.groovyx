package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.simulators.mhd.MetadataToDocumentReferenceTranslator
import gov.nist.toolkit.fhir.server.utility.IFhirSearch
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.registrymetadata.client.DocumentEntry
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.results.MetadataToMetadataCollectionParser
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.instance.model.api.IBaseResource
import spock.lang.Specification

class MetadataToDocumentReferenceTransformTest extends Specification {

    def 'test'() {
        given:
        Metadata metadata = MetadataParser.parseNonSubmission(eo1)
        MetadataToMetadataCollectionParser mmparser = new MetadataToMetadataCollectionParser(metadata, 'Label')
        MetadataCollection col = mmparser.get()
        DocumentEntry de = col.docEntries[0]

        when:
        MetadataToDocumentReferenceTranslator xfrm = new MetadataToDocumentReferenceTranslator(null, ['fhirbase'], new Searcher())
        DocumentReference dr = xfrm.run(de)

        then:
        dr  // did not terminate with exception
    }

    class Searcher implements IFhirSearch {

        @Override
        Map<String, IBaseResource> search(String base, String resourceType, List params) {
            IBaseResource thePatient = ToolkitFhirContext.get().newXmlParser().parseResource(patient)
            return ['http://example.com/fhir/Patient/1':thePatient]
        }
    }

    def eo1 = '''
<rim:LeafRegistryObjectList xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">
   <rim:ExtrinsicObject id="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
         objectType="urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1"
         mimeType="text/xml" lid="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
         status="urn:oasis:names:tc:ebxml-regrep:StatusType:Approved">
      <rim:Slot name="creationTime">
         <rim:ValueList>
            <rim:Value>20061224</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="repositoryUniqueId">
         <rim:ValueList>
            <rim:Value>1.19.6.24.109.42.1</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="size">
         <rim:ValueList>
            <rim:Value>36</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="URI">
         <rim:ValueList>
            <rim:Value>http://129.6.24.109:9080/XdsDocs/testdata/my_document.txt</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="hash">
         <rim:ValueList>
            <rim:Value>e543712c0e10501972de13a5bfcbe826c49feb75</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="languageCode">
         <rim:ValueList>
            <rim:Value>en-us</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="serviceStartTime">
         <rim:ValueList>
            <rim:Value>200612230800</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="serviceStopTime">
         <rim:ValueList>
            <rim:Value>200612230900</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="sourcePatientId">
         <rim:ValueList>
            <rim:Value>89765a87b^^^&amp;3.4.5&amp;ISO</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Slot name="sourcePatientInfo">
         <rim:ValueList>
            <rim:Value>PID-3|pid1^^^&amp;1.2.3&amp;ISO</rim:Value>
            <rim:Value>PID-5|Doe^John^^^</rim:Value>
            <rim:Value>PID-7|19560527</rim:Value>
            <rim:Value>PID-8|M</rim:Value>
            <rim:Value>PID-11|100 Main St^^Metropolis^Il^44130^USA</rim:Value>
         </rim:ValueList>
      </rim:Slot>
      <rim:Name>
         <rim:LocalizedString value="DocA"/>
      </rim:Name>
      <rim:Description/>
      <rim:VersionInfo versionName="1"/>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="" classificationScheme="urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:56a1d2ee-56f4-42ef-be58-8ebc8a8eb19c">
         <rim:Slot name="authorPerson">
            <rim:ValueList>
               <rim:Value>^Smitty^Gerald^^^</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Slot name="authorInstitution">
            <rim:ValueList>
               <rim:Value>Cleveland Clinic</rim:Value>
               <rim:Value>Parma Community</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Slot name="authorRole">
            <rim:ValueList>
               <rim:Value>Attending</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Slot name="authorSpecialty">
            <rim:ValueList>
               <rim:Value>Orthopedic</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="" classificationScheme="urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:14530707-8806-4f01-9eda-995893cca60d">
         <rim:Slot name="authorPerson">
            <rim:ValueList>
               <rim:Value>^Dopplemeyer^Sherry^^^</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Slot name="authorInstitution">
            <rim:ValueList>
               <rim:Value>Cleveland Clinic</rim:Value>
               <rim:Value>Berea Community</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Slot name="authorRole">
            <rim:ValueList>
               <rim:Value>Primary Surgon</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Slot name="authorSpecialty">
            <rim:ValueList>
               <rim:Value>Orthopedic</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="DEMO-Consult" classificationScheme="urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:86e95188-4005-4ef7-ad8e-aeafcf226f5a">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>1.3.6.1.4.1.21367.100.1</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="DEMO-Consult"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="N" classificationScheme="urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:ff3be38f-0d01-4a9e-8307-f39bb62dd9a0">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>2.16.840.1.113883.5.25</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="Normal"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="urn:ihe:rad:TEXT" classificationScheme="urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:b171e253-c4c9-4694-b8ff-e61b51da93c8">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>1.3.6.1.4.1.19376.1.2.3</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="urn:ihe:rad:TEXT"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="Outpatient" classificationScheme="urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:21489eae-7036-47b0-8db4-a90e620bcc37">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>Connect-a-thon healthcareFacilityTypeCodes 2</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="Outpatient"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="Dialysis" classificationScheme="urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:b5b8c8b6-038d-45dc-ab4a-a8b6f2a02542">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>Connect-a-thon practiceSettingCodes</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="Dialysis"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="T-D4909" classificationScheme="urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:3bdb44b9-5045-41ad-aa4c-c6e1c738e03c">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>SNM3</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="Kidney"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="T-62002" classificationScheme="urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:d675fad4-d8bf-451f-869b-a81a6d05fdca">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>SNM3</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="Liver"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:Classification classifiedObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1"
            nodeRepresentation="XTHM-WD TYPECODE" classificationScheme="urn:uuid:f0306f51-975f-434e-a61c-c59651d33983"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
            id="urn:uuid:991c31fe-536c-46d1-a0f7-887be68f93e6">
         <rim:Slot name="codingScheme">
            <rim:ValueList>
               <rim:Value>1.3.6.1.4.1.21367.100.1</rim:Value>
            </rim:ValueList>
         </rim:Slot>
         <rim:Name>
            <rim:LocalizedString value="XTHM-WD TYPECODE"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:Classification>
      <rim:ExternalIdentifier value="123^^^&amp;1.2.343&amp;ISO"
            identificationScheme="urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier"
            id="urn:uuid:673c8623-0b38-4a1c-81ac-80e51b99622b"
            registryObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1">
         <rim:Name>
            <rim:LocalizedString value="XDSDocumentEntry.patientId"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:ExternalIdentifier>
      <rim:ExternalIdentifier value="1.42.20150812190443.2"
            identificationScheme="urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"
            objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier"
            id="urn:uuid:0bec4701-3168-4b89-8d43-833af8fef5d5"
            registryObject="urn:uuid:0ebbc63a-1d55-40ef-8d2d-86e54b0c5eb1">
         <rim:Name>
            <rim:LocalizedString value="XDSDocumentEntry.uniqueId"/>
         </rim:Name>
         <rim:VersionInfo versionName="-1"/>
      </rim:ExternalIdentifier>
   </rim:ExtrinsicObject>
</rim:LeafRegistryObjectList>'''

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
