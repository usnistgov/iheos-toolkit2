package gov.nist.toolkit.wsseToolkit.browsing;

import gov.nist.toolkit.wsseToolkit.util.NamespaceContextMap;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;


/**
 * This static factory provides an implementation of a
 * javax.xml.namespace.NamespaceContext.
 * 
 * When browsing XML (by evaluating XPath expressions or using the DOM API), it
 * is important to keep document-agnostic definition of namespaces.
 * 
 * Each namespace declared in a Nwhin document should be declared in the
 * NamespaceContext that this factory produces. They can be resolved later by
 * using their uniform prefix declaration.
 * 
 * @author gerardin
 * 
 */

public class NwhinNamespaceContextFactory {

	public static NamespaceContext newInstance() {
		return new NamespaceContextMap("xsi", "http://www.w3.org/2001/XMLSchema", "S",
				"http://www.w3.org/2003/05/soap-envelope", "wsse",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse11",
				"http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd", "wsu",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "ds",
				"http://www.w3.org/2000/09/xmldsig#", "saml", "urn:oasis:names:tc:SAML:1.0:assertion", "saml2",
				"urn:oasis:names:tc:SAML:2.0:assertion", "exc14n", "http://www.w3.org/2001/10/xml-exc-c14n#", "xenc",
				"http://www.w3.org/2001/04/xmlenc#", "hl7", "urn:hl7-org:v3");
	}

	
	//TODO remove
	public static NamespaceContext newInstanceForTestOnly() {
		return new NamespaceContext() {

			@Override
			public Iterator getPrefixes(String namespaceURI) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getNamespaceURI(String prefix) {
				if (prefix.equals("wsse")) {
					return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
				} else if (prefix.equals("saml2")) {
					return "urn:oasis:names:tc:SAML:2.0:assertion";
				}

				return null;
			}
		};
	}

}
