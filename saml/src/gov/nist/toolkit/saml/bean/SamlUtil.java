package gov.nist.toolkit.saml.bean;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.Configuration;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

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
				if (AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME.equals(str.getElementQName().getLocalPart())
						&& SAMLConstants.SAML20_NS.equals(str.getElementQName().getNamespaceURI())) {
					return str.getValue();
				}
			} else {
				XSAny ep = (XSAny) attribute.getAttributeValues().get(i);
				if (AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME.equals(ep.getElementQName().getLocalPart())
						&& SAMLConstants.SAML20_NS.equals(ep.getElementQName().getNamespaceURI())) {
					if (ep.getUnknownXMLObjects().size() > 0) {
						StringBuilder res = new StringBuilder();
						for (XMLObject obj : ep.getUnknownXMLObjects()) {
							res.append(XMLHelper.nodeToString(marshallObject(obj)));
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
				if (AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME.equals(str.getElementQName().getLocalPart())
						&& SAMLConstants.SAML20_NS.equals(str.getElementQName().getNamespaceURI())) {
					values.add(str.getValue());
				}
			} else {
				XSAny ep = (XSAny) attribute.getAttributeValues().get(i);
				if (AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME.equals(ep.getElementQName().getLocalPart())
						&& SAMLConstants.SAML20_NS.equals(ep.getElementQName().getNamespaceURI())) {
					if (ep.getUnknownXMLObjects().size() > 0) {
						StringBuilder res = new StringBuilder();
						for (XMLObject obj : ep.getUnknownXMLObjects()) {
							res.append(XMLHelper.nodeToString(SamlUtil.marshallObject(obj)));
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
			Marshaller m = (Marshaller) Configuration.getMarshallerFactory().getMarshaller(object);
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
