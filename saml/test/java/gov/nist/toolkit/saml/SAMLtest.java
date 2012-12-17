package gov.nist.toolkit.saml;

import static org.junit.Assert.*;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmsg.message.SAMLMessageValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.junit.Test;

public class SAMLtest {

		@Test
		public void simpleTest() throws XdsInternalException, FactoryConfigurationError {
		//	String filename = "sovann_PDI.xml";
			String filename = "SSA_S2QDI12_RI2_Request_2010-08-26T115311770-0400.xml";
			File f = new File("saml/test/resources/"+ filename );
			OMElement xml = Util.parse_xml(f);
			
			ValidationContext vc = new ValidationContext();
			SAMLMessageValidator validator = new SAMLMessageValidator(vc, xml, null , null , null);
			ErrorRecorder err = new TextErrorRecorder();
			validator.run(err, null);
			
			System.out.println("Error Recording : ");
			System.out.println( err.toString() );
			
		}
		
		@Test
		public void ttt_request_message_Test() throws XdsInternalException, FactoryConfigurationError {
		//	String filename = "sovann_PDI.xml";
			String filename = "almost_valid_saml_headers.xml";
			File f = new File("saml/test/resources/"+ filename );
			OMElement xml = Util.parse_xml(f);
			
			ValidationContext vc = new ValidationContext();
			SAMLMessageValidator validator = new SAMLMessageValidator(vc, xml, null , null , null);
			ErrorRecorder err = new TextErrorRecorder();
			validator.run(err, null);
			
			System.out.println("Error Recording : ");
			System.out.println( err.toString() );
			
		}


}
