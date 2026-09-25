package gov.nist.toolkit.saml.bean;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.config.Configuration;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSString;
import org.w3c.dom.Element;
import gov.nist.toolkit.saml.builder.OpenSamlBootStrap;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SamlUtil {
	/**
	 * Extract the value of the first attributeValue within an SAML20 attribute
	 * 
	 * @param attribute
	 *            The attribute
	 * @return The text value of the attributeValue
	 * @throws Exception 
	 */
	public static String extractAttributeValueValue(Attribute attribute) throws Exception {
		for (int i = 0; i < attribute.getAttributeValues().size(); i++) {
			if (attribute.getAttributeValues().get(i) instanceof XSString) {
				XSString str = (XSString) attribute.getAttributeValues().get(i);
				if ("AttributeValue".equals(str.getElementQName().getLocalPart())
						&& "urn:oasis:names:tc:SAML:2.0:assertion".equals(str.getElementQName().getNamespaceURI())) {
					return str.getValue();
				}
			} else {
				XSAny ep = (XSAny) attribute.getAttributeValues().get(i);
				if ("AttributeValue".equals(ep.getElementQName().getLocalPart())
						&& "urn:oasis:names:tc:SAML:2.0:assertion".equals(ep.getElementQName().getNamespaceURI())) {
					if (ep.getUnknownXMLObjects().size() > 0) {
						StringBuilder res = new StringBuilder();
						for (XMLObject obj : ep.getUnknownXMLObjects()) {
							res.append(marshallObject(obj).getTextContent());
						}
						return res.toString();
					}
					return ep.getTextContent();
				}
			}
		}
		return null;
	}
	
	/**
	 * Extract all attribute values within an SAML20 attribute
	 * 
	 * @param attribute The attribute
	 * @return A list containing the text value of each attributeValue
	 * @throws Exception 
	 */
	public static List<String> extractAttributeValueValues(Attribute attribute) throws Exception {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < attribute.getAttributeValues().size(); i++) {
			if (attribute.getAttributeValues().get(i) instanceof XSString) {
				XSString str = (XSString) attribute.getAttributeValues().get(i);
				if ("AttributeValue".equals(str.getElementQName().getLocalPart())
						&& "urn:oasis:names:tc:SAML:2.0:assertion".equals(str.getElementQName().getNamespaceURI())) {
					values.add(str.getValue());
				}
			} else {
				XSAny ep = (XSAny) attribute.getAttributeValues().get(i);
				if ("AttributeValue".equals(ep.getElementQName().getLocalPart())
						&& "urn:oasis:names:tc:SAML:2.0:assertion".equals(ep.getElementQName().getNamespaceURI())) {
					if (ep.getUnknownXMLObjects().size() > 0) {
						StringBuilder res = new StringBuilder();
						for (XMLObject obj : ep.getUnknownXMLObjects()) {
							res.append(marshallObject(obj).getTextContent());
						}
						values.add(res.toString());
					}
					values.add(ep.getTextContent());
				}
			}
		}
		return values;
	}
	
	public static Element marshallObject(XMLObject object) throws Exception {
		if (object.getDOM() == null) {
			Marshaller m = (Marshaller) OpenSamlBootStrap.getMarshallerFactory().getMarshaller(object);
			if (m == null) {
				throw new IllegalArgumentException("No unmarshaller for " + object);
			}
			try {
				return m.marshall(object);
			} catch (MarshallingException e) {
				throw new Exception(e);
			}
		} else {
			return object.getDOM();
		}
	}
}
