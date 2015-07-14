package gov.nist.toolkit.soap.http;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.OMFormatter;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

/**
 * Build a SOAP message.  Must be called in the order: buildSoapEnvelope, 
 * attachSoapHeader, attachSoapBody.
 * @author bill
 *
 */
public class SoapUtil {
	
	static public OMElement buildSoapEnvelope() {
		return MetadataSupport.om_factory.createOMElement(MetadataSupport.soap_env_qnamens);
	}

	static public OMElement attachSoapHeader(String to, String wsaction, String messageId, String relatesTo, OMElement soapEnvelope) {
		OMElement header = MetadataSupport.om_factory.createOMElement(MetadataSupport.soap_hdr_qnamens);
		boolean muDone = false;

		if (wsaction != null) {
			OMElement mid = MetadataSupport.om_factory.createOMElement("Action", MetadataSupport.ws_addressing_namespace);
			mid.setText(wsaction);
			header.addChild(mid);

			if (!muDone) {
				OMAttribute a = MetadataSupport.om_factory.createOMAttribute("mustUnderstand", MetadataSupport.soap_env_namespace, "1");
				mid.addAttribute(a);
				muDone = true;
			}
		}

		if (messageId != null) {
			OMElement mid = MetadataSupport.om_factory.createOMElement("MessageID", MetadataSupport.ws_addressing_namespace);
			mid.setText(messageId);
			header.addChild(mid);

			if (!muDone) {
				mid.addAttribute("mustUnderstand", "1", MetadataSupport.soap_env_namespace);
				muDone = true;
			}
		}

		if (relatesTo != null) {
			OMElement mid = MetadataSupport.om_factory.createOMElement("RelatesTo", MetadataSupport.ws_addressing_namespace);
			mid.setText(relatesTo);
			header.addChild(mid);

			if (!muDone) {
				mid.addAttribute("mustUnderstand", "1", MetadataSupport.soap_env_namespace);
				muDone = true;
			}
		}

		if (to != null) {
			OMElement mid = MetadataSupport.om_factory.createOMElement("To", MetadataSupport.ws_addressing_namespace);
			mid.setText(to);
			header.addChild(mid);

			if (!muDone) {
				mid.addAttribute("mustUnderstand", "1", MetadataSupport.soap_env_namespace);
				muDone = true;
			}
		}

		if (soapEnvelope != null) {
			soapEnvelope.addChild(header);
		}

		return header;
	}

	static public OMElement attachSoapBody(OMElement contents, OMElement soapEnvelope) {
		OMElement body = MetadataSupport.om_factory.createOMElement(MetadataSupport.soap_body_qnamens);

		if (contents != null) {
			body.addChild(contents);
		}

		if (soapEnvelope != null) {
			soapEnvelope.addChild(body);
		}

		return body;
	}

}
