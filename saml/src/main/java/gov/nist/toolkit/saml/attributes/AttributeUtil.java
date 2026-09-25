package gov.nist.toolkit.saml.attributes;
import gov.nist.toolkit.saml.bean.SamlUtil;
import gov.nist.toolkit.saml.util.SamlConstants;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.shared.xml.XMLConstants;

public class AttributeUtil extends SamlConstants {
	public static final String VERSION = "$Id: AttributeUtil.java 2950 2008-05-28 08:22:34Z jre $";

	private static final Logger logger = LoggerFactory.getLogger(AttributeUtil.class);

	private static final String ATTRIBUTE_VALUE_LOCAL_PART = "AttributeValue";

	private static final String SAML_ASSERTION_NAMESPACE = "urn:oasis:names:tc:SAML:2.0:assertion";

	// ThreadLocal ensures each thread gets its own DocumentBuilder instance,
	// avoiding the thread-safety issues of sharing a single DocumentBuilderFactory.
	// This is important in an IHE XDS/XUA context where multiple threads may
	// process concurrent SAML assertions simultaneously.
	//
	// Security features configured here prevent XXE (XML External Entity) attacks,
	// which are a critical vulnerability when parsing externally-supplied XML
	// in a healthcare document exchange environment.
	private static final ThreadLocal<DocumentBuilder> THREAD_LOCAL_BUILDER =
			ThreadLocal.withInitial(() -> {
				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
					dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
					dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
					dbFactory.setExpandEntityReferences(false);
					dbFactory.setNamespaceAware(true);
					return dbFactory.newDocumentBuilder();
				} catch (Exception e) {
					throw new IllegalStateException("Failed to create secure XML parser", e);
				}
			});

	/** Default attributes for AttributeValue */
	public static final QName XSI_TYPE_ATTRIBUTE_NAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "xsi");

	public static final String XS_STRING = XMLConstants.XSD_PREFIX + ":" + XSString.TYPE_LOCAL_NAME;

	/** QName for the attribute resource */
	public static Attribute createAttribute(String name, String friendlyName,String nameFormat) {
		Attribute attribute = new AttributeBuilder().buildObject();
		attribute.setName(name);
		attribute.setFriendlyName(friendlyName);
		attribute.setNameFormat(nameFormat);
		return attribute;
	}

	private static XSAny createAttributeValue() {
		XSAnyBuilder builder = new XSAnyBuilder();
		XSAny ep = builder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion",
										"AttributeValue",
										"saml");
		return ep;
	}

	public static XSAny createAttributeValue(String value, String type) {
		XSAny ep = createAttributeValue();
		ep.setTextContent(String.valueOf(value));
		ep.getUnknownAttributes().put(XSI_TYPE_ATTRIBUTE_NAME, type);
		
		return ep;
	}

	public static XSAny createAttributeValue(String value) {
		return createAttributeValue(value, XS_STRING);
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * surname
	 * 
	 * @param value
	 *            The surname
	 * @return The attribute
	 */
	public static Attribute createSurname(String value) {
		Attribute attribute = createAttribute(ATTRIBUTE_SURNAME_NAME,
				ATTRIBUTE_SURNAME_FRIENDLY_NAME, URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * commonName
	 * 
	 * @param value
	 *            The commonName
	 * @return The attribute
	 */
	public static Attribute createCommonName(String value) {
		Attribute attribute = createAttribute(ATTRIBUTE_COMMON_NAME_NAME,
				ATTRIBUTE_COMMON_NAME_FRIENDLY_NAME, URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given uid
	 * (userId)
	 * 
	 * @param value
	 *            The uid
	 * @return The attribute
	 */
	public static Attribute createUid(String value) {
		Attribute attribute = createAttribute(ATTRIBUTE_UID_NAME,
				ATTRIBUTE_UID_FRIENDLY_NAME, URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * mailAddress
	 * 
	 * @param value
	 *            The mail address
	 * @return The attribute
	 */
	public static Attribute createMail(String value) {
		Attribute attribute = createAttribute(ATTRIBUTE_MAIL_NAME,
				ATTRIBUTE_MAIL_FRIENDLY_NAME, URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * cvrNumber
	 * 
	 * @param value
	 *            The cvrNumber
	 * @return The attribute
	 */
	public static Attribute createCVRNumberIdentifier(String value) {
		Attribute attribute = createAttribute(
				ATTRIBUTE_CVR_NUMBER_IDENTIFIER_NAME,
				ATTRIBUTE_CVR_NUMBER_IDENTIFIER_FRIENDLY_NAME,
				URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * serialNumber
	 * 
	 * @param value
	 *            The serialNumber of the certificate
	 * @return The attribute
	 */
	public static Attribute createSerialNumber(String value) {
		Attribute attribute = createAttribute(ATTRIBUTE_SERIAL_NUMBER_NAME,
				ATTRIBUTE_SERIAL_NUMBER_FRIENDLY_NAME,
				URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * pidNumberIdentifier
	 * 
	 * @param value
	 *            The pidNumberIdentier of the certificate
	 * @return The attribute
	 */
	public static Attribute createPidNumberIdentifier(String value) {
		Attribute attribute = createAttribute(
				ATTRIBUTE_PID_NUMBER_IDENTIFIER_NAME,
				ATTRIBUTE_PID_NUMBER_IDENTIFIER_FRIENDLY_NAME,
				URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * ridNumberIdentifier
	 * 
	 * @param value
	 *            The RidNumberIdentifier of the certificate
	 * @return The attribute
	 */
	public static Attribute createRidNumberIdentifier(String value) {
		Attribute attribute = createAttribute(
				ATTRIBUTE_RID_NUMBER_IDENTIFIER_NAME,
				ATTRIBUTE_RID_NUMBER_IDENTIFIER_FRIENDLY_NAME,
				URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * userCertificate
	 * 
	 * @param value
	 *            The user certificate
	 * @return The attribute
	 */
	public static Attribute createUserCertificate(String value) {
		Attribute attribute = createAttribute(ATTRIBUTE_USER_CERTIFICATE_NAME,
				ATTRIBUTE_USER_CERTIFICATE_FRIENDLY_NAME,
				URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(createAttributeValue(value));
		}
		return attribute;
	}

	/**
	 * Create a SAML20 attribute containing one attribute value with a given
	 * assuranceLevel
	 * 
	 * @param value
	 *            The assuranceLevel
	 * @return The attribute
	 */
	public static Attribute createAssuranceLevel(Integer value) {
		Attribute attribute = createAttribute(ATTRIBUTE_ASSURANCE_LEVEL_NAME,
				ATTRIBUTE_ASSURANCE_LEVEL_FRIENDLY_NAME,
				URI_ATTRIBUTE_NAME_FORMAT);
		if (value != null) {
			attribute.getAttributeValues().add(
					createAttributeValue(String.valueOf(value)));
		}
		return attribute;
	}

	/**
	 * Extract the value of the first attributeValue within an SAML20 attribute
	 * 
	 * @param attribute
	 *            The attribute
	 * @return The text value of the attributeValue
	 * @throws Exception 
	 */

	public static String extractAttributeValueValue(Attribute attribute) throws Exception {
		List<XMLObject> attributeValues = attribute.getAttributeValues();
		for (XMLObject attributeValue : attributeValues) {
			if (attributeValue instanceof XSString) {
				XSString str = (XSString) attributeValue;
				if (isValidAttributeValue(str.getElementQName())) {
					return str.getValue();
				}
			} else if (attributeValue instanceof XSAny) {
				XSAny ep = (XSAny) attributeValue;
				if (isValidAttributeValue(ep.getElementQName())) {
					if (!ep.getUnknownXMLObjects().isEmpty()) {
						StringBuilder res = new StringBuilder();
						for (XMLObject obj : ep.getUnknownXMLObjects()) {
							res.append(SerializeSupport.nodeToString(SamlUtil.marshallObject(obj)));
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
		List<String> values = new ArrayList<>();
		List<XMLObject> attributeValues = attribute.getAttributeValues();

		for (XMLObject attributeValue : attributeValues) {
			if (attributeValue instanceof XSString) {
				XSString str = (XSString) attributeValue;
				if (isValidAttributeValue(str.getElementQName())) {
					values.add(str.getValue());
				}
			} else if (attributeValue instanceof XSAny) {
				XSAny ep = (XSAny) attributeValue;
				if (isValidAttributeValue(ep.getElementQName())) {
					if (!ep.getUnknownXMLObjects().isEmpty()) {
						StringBuilder res = new StringBuilder();
						for (XMLObject obj : ep.getUnknownXMLObjects()) {
							res.append(SerializeSupport.nodeToString(SamlUtil.marshallObject(obj)));
						}
						values.add(res.toString());
					} else {
						values.add(ep.getTextContent());
					}
				}
			}
		}
		return values;
	}

	private static boolean isValidAttributeValue(QName qName) {
		return ATTRIBUTE_VALUE_LOCAL_PART.equals(qName.getLocalPart())
				&& SAML_ASSERTION_NAMESPACE.equals(qName.getNamespaceURI());
	}



	public static Map<String, String> getCodeAttributes(String xmlstring) {
		Map<String, String> codeAttributes = new HashMap<>();
		try {
			byte[] bytes = xmlstring.getBytes(StandardCharsets.UTF_8);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

			// Reuse the thread-local DocumentBuilder rather than creating a new
			// DocumentBuilderFactory on every call. builder.reset() clears any
			// state left over from the previous parse on this thread.
			DocumentBuilder builder = THREAD_LOCAL_BUILDER.get();
			builder.reset(); // clear state from any previous parse
			Document document = builder.parse(bais);

			NodeList nl = document.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				NamedNodeMap nnm = node.getAttributes();
				if (nnm != null) {
					for (int j = 0; j < nnm.getLength(); j++) {
						Node n = nnm.item(j);
						codeAttributes.put(n.getNodeName(), n.getNodeValue());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Failed to parse XML string for code attributes", e);
		}
		return codeAttributes;
	}
}

