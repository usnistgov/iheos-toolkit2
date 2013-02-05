package gov.nist.toolkit.valregmetadata.object.test.direct

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.TextErrorRecorder
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.valregmetadata.object.SubmissionSet
import gov.nist.toolkit.valsupport.client.ValidationContext

import org.apache.axiom.om.OMElement
import org.junit.Test
import static org.junit.Assert.*


class SubmissionSetTest {
	def goodss = '''
   <foo>
        <RegistryPackage id="SubmissionSet01">
        	<Slot name="intendedRecipient">
        	  <ValueList>
                <Value>Some Hospital^^^^^^^^^1.2.3.4.5.6.7.8.9.1789.45|^Wel^Marcus^^^Dr^MD|^^Internet^marcus.wel@direct.example.org</Value>
			  </ValueList>
			</Slot>
            <Slot name="submissionTime">
                <ValueList>
                    <Value>20121025235050</Value>
                </ValueList>
            </Slot>
            <Classification  id="author01" classificationScheme="urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d"
                classifiedObject="SubmissionSet01" nodeRepresentation="">
				<Slot name="authorTelecommunication">
					<!-- shall be single valued -->
					<ValueList>
						<Value>^^Internet^john.doe@healthcare.example.org</Value>
					</ValueList>
				</Slot>
            </Classification>
            <ExternalIdentifier id="uid01" registryObject="SubmissionSet01" identificationScheme="urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8"
                value="1.43.456.45889.23451.1.2.234.1">
                <Name>
                    <LocalizedString value="XDSSubmissionSet.uniqueId"/>
                </Name>
            </ExternalIdentifier>
            <ExternalIdentifier id="sid01" registryObject="SubmissionSet01" identificationScheme="urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832"
                value="1.43.456.45889.23451.1.2">
                <Name>
                    <LocalizedString value="XDSSubmissionSet.sourceId"/>
                </Name>
            </ExternalIdentifier>
        </RegistryPackage>
        <Classification classifiedObject="SubmissionSet01"
                classificationNode="urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd"/>
   </foo>
'''

	@Test
	public void testValid() {
		OMElement xml = Util.parse_xml(goodss)
		Metadata m = MetadataParser.parse(xml)
		OMElement ss = m.getSubmissionSet()
		assertFalse (ss == null)
		SubmissionSet sso = new SubmissionSet(m, ss)
		ValidationContext vc = new ValidationContext()
		vc.isXDRMinimal = true
		ErrorRecorder er = new TextErrorRecorder();
		er.sectionHeading("testValid()")
		sso.validate(er, vc, new HashSet<String>());
		System.out.println(er.toString());
		assertFalse er.hasErrors()
	}

}
