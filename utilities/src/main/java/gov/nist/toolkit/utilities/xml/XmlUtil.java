/*
 * XmlUtil.java
 *
 * Created on November 2, 2004, 11:15 AM
 */

package gov.nist.toolkit.utilities.xml;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * static utility methods for working with XML using Axis
 */
public class XmlUtil {
	static public OMFactory om_factory = OMAbstractFactory.getOMFactory();
	static public OMNamespace xml_namespace =   om_factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");

	/**
    * Return first child element with passed local name.
    * @param ele parent element to evaluate
    * @param localName local name of child element to match
	 * @return first OMElement meeting criteria, or null if none do.
	 */
	public static OMElement firstChildWithLocalName(OMElement ele, String localName) {
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				return child;
		}
		return null;
	}
   
   /**
    * Return first child element with passed local name and attribute with 
    * passed name and value.
    * @param ele parent element to evaluate
    * @param localName local name of child element to consider
    * @param attr name of attribute to look for in child element
    * @param value value that attribute should have
    * @return first OMElement meeting criteria, or null if none do.
    */
   public static OMElement firstChildWithLocalNameAndAttribute(OMElement ele, 
      String localName, String attr, String value) {
      for(OMElement chil :  childrenWithLocalName(ele, localName)) {
         String v = getAttributeValue(chil, attr);
         if (v != null && v.equals(value)) return chil;
      }
      return null;
   }
	
	/**
	 * Return element which is first child repetitively. For example:<br/>
	 * firstChildChain(top, "Section", "Step", SubStep") would start with top,
	 * look for a first child "Section" under it, then a first child "Step" under
	 * that, and finally a first child "SubStep" under that. The "SubStep"
	 * element would be returned. If at any point the first child is not found,
	 * null is returned.
	 * @param ele parent element to start with
	 * @param localNames one or more local names of the child elements at each
	 * level.
	 * @return the last child, or null if at any point the next child could not
	 * be found.
	 */
	public static OMElement firstChildChain(OMElement ele, String... localNames) {
	   OMElement chld = ele;
	   for (String name : localNames) {
	      chld = XmlUtil.firstChildWithLocalName(chld, name);
	      if (chld == null) break;
	   }
	   return chld;
	}

	public static OMElement firstChildWithLocalNameEndingWith(OMElement ele, String localNameSuffix) {
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().endsWith(localNameSuffix))
				return child;
		}
		return null;
	}
   /**
    * Get the one and only one child element of parent with given name.
    * @param ele parent element
    * @param localName of desired child. if blank, matches any name
    * @return child element with local name, provided there is one and only one
    * such child. 
    * @throws Exception on error, or if child is not unique or doesn't exist.
    */
   public static OMElement onlyChildWithLocalName(OMElement ele, String localName) 
      throws Exception {
      List<OMElement> children = new ArrayList<>();
      for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
         OMElement child = (OMElement) it.next();
         if (StringUtils.isBlank(localName) || child.getLocalName().equals(localName)) children.add(child);
      }
      if (children.size() == 1) return children.get(0);
      StringBuilder em = new StringBuilder("error in XmlUtil#onlyChildWithLocalName: parent element ");
      em.append(ele.getLocalName()).append(" has ").append(children.size())
        .append(" children"); 
      if (StringUtils.isNotBlank(localName)) em.append(" with local name ").append(localName);
      throw new Exception(em.toString());
   }
	

	/**
	 * Returns child elements with passed tag name.
	 * @param ele parent OMElement
	 * @param localName name to match
	 * @return {@link List} or OMElements, may be empty, never null;
	 */
	public static List<OMElement> childrenWithLocalName(OMElement ele, String localName) {
		List<OMElement> al = new ArrayList<OMElement>();
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				al.add(child);
		}
		return al;
	}
	
	/**
    * Returns child elements 
    * @param ele parent OMElement
    * @return {@link List} or OMElements, may be empty, never null;
    */
   public static List<OMElement> children(OMElement ele) {
      List<OMElement> al = new ArrayList<OMElement>();
      for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
         OMElement child = (OMElement) it.next();
            al.add(child);
      }
      return al;
   }

	/**
	 * Returns a list of the local names of all child elements  of passed element
	 * @param ele parent element
	 * @return {@link List} of String names. May be empty, never null.
	 */
	public static List<String> childrenLocalNames(OMElement ele) {
		List<String> al = new ArrayList<String>();
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			al.add(child.getLocalName());
		}
		return al;
	}

	public static OMElement firstDecendentWithLocalName(OMElement ele, String localName) {
		List<OMElement> decendents = decendentsWithLocalName(ele, localName);
		if (decendents.size() == 0) return null;
		return decendents.get(0);
	}
	
	public static OMElement decendentWithLocalName(OMElement ele, String localName)
	   throws Exception {
	   List<OMElement> decendents = decendentsWithLocalName(ele, localName);
	   if (decendents.size() != 1) {
	      String em = ele.getLocalName() + " had " + decendents.size() +
	         " children with name " + localName;
	      throw new Exception(em);
	   }
	   return decendents.get(0);
	}

	public static List<OMElement> decendentsWithLocalName(OMElement ele, String localName) {
		return decendentsWithLocalName(ele, localName, -1);
	}

	public static List<OMElement> decendentsWithLocalName(OMElement ele, String localName, int depth) {
		List<OMElement> al = new ArrayList<OMElement>();
		if (ele == null || localName == null)
			return al;
		decendentsWithLocalName1(al, ele, localName, depth);
		return al;
	}

	private static void decendentsWithLocalName1(List<OMElement> decendents, OMElement ele, String localName, int depth) {
		if (depth == 0)
			return;
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				decendents.add(child);
			decendentsWithLocalName1(decendents, child, localName, depth - 1);
		}
	}

	public static List<OMElement> descendantsWithLocalNameEndsWith(OMElement ele, String localName) {
		List<OMElement> al = new ArrayList<OMElement>();
		if (ele == null || localName == null)
			return al;
		descendantsWithLocalNameEndsWith(al, ele, localName, -1);
		return al;
	}

	private static void descendantsWithLocalNameEndsWith(List<OMElement> descendants, OMElement ele, String localName, int depth) {
		if (depth == 0)
			return;
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().toLowerCase().endsWith(localName.toLowerCase()))
				descendants.add(child);
			decendentsWithLocalName1(descendants, child, localName, depth - 1);
		}
	}
	
	public static String getAttributeValue(OMElement element, String attributeName) {
	   return element.getAttributeValue(new QName(attributeName));
	}

	public static OMElement createElement(String localName, OMNamespace ns) {
		return om_factory.createOMElement(localName, ns);
	}
	
	/**
	 * Creates a new OMElement with the same local name and namespace as the 
	 * parameter element. <b>NOT</b> a clone
	 * @param element reference element for new element
	 * @return new element
	 */
	public static OMElement createElement(OMElement element) {
	   return om_factory.createOMElement(element.getLocalName(), element.getNamespace());
	}

	public static OMElement addChild(String localName, OMNamespace ns, OMElement parent) {
		return om_factory.createOMElement(localName, ns, parent);
	}
	
	/**
    * Creates a new child OMElement with the same local name and namespace as the 
    * parameter element. <b>NOT</b> a clone
	 * @param element reference element
	 * @param parent for new element
	 * @return new element
	 */
	public static OMElement addChild(OMElement element, OMElement parent) {
      return om_factory.createOMElement(element.getLocalName(), element.getNamespace(), parent);
	}


	static public String XmlWriter(Node node) {
		return XmlFormatter.format(XmlFormatter.normalize(XmlUtil.getStringFromNode(node)), false);
	}

	static public String getStringFromNode(Node node) {
		StringBuffer sb = new StringBuffer();
		if ( node == null ) {
			return null;
		}

		int type = node.getNodeType();
		switch ( type ) {
			case Node.DOCUMENT_NODE:
				sb.append("");
				sb.append(XmlUtil.getStringFromNode((((Document)node).getDocumentElement())));
				break;

			case Node.ELEMENT_NODE:
				sb.append('<');

				sb.append(node.getNodeName());
				NamedNodeMap attrs = node.getAttributes();

				for ( int i = 0; i < attrs.getLength(); i++ ) {
					sb.append(' ');
					sb.append(attrs.item(i).getNodeName());
					sb.append("=\"");

					sb.append(attrs.item(i).getNodeValue());
					sb.append('"');
				}
				sb.append('>');
				sb.append("\n"); // HACK
				NodeList children = node.getChildNodes();
				if ( children != null ) {
					int len = children.getLength();
					for ( int i = 0; i < len; i++ ) {
						sb.append(XmlUtil.getStringFromNode(children.item(i)));
					}
				}
				break;

			case Node.TEXT_NODE:
				sb.append(node.getNodeValue());
				break;

		}

		if ( type == Node.ELEMENT_NODE ) {
			sb.append("</");
			sb.append(node.getNodeName());
			sb.append(">");
			sb.append("\n"); // HACK
		}

		return sb.toString();
	}
	
	static public OMElement strToOM(String xml) throws XMLStreamException {
	   return AXIOMUtil.stringToOM(xml);
	}
	
	/**
	 * Converts xml element to string.
	 * @param element parent (document) element
	 * @return String value, or null on error.
	 */
	static public String OMToStr(OMElement element) {
	   try {
         return element.toStringWithConsume();
      } catch (XMLStreamException e) {
         return null;
      }
	} 
   
	/**
	 * Sets text for descendant Document elements to "...". Used to truncate
	 * documents in retrieve document set response for display. 
	 * @param element parent element
	 */
	public static void truncateDocuments(OMElement element) {
      for (OMElement doc : decendentsWithLocalName(element, "Document")) {
         doc.setText("...");
      }
      return;
   }

}
