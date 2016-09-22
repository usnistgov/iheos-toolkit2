/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.namespace.QName;

import org.apache.axiom.om.*;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;
import sun.misc.BASE64Decoder;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.XmlFormatter;

/**
 * A bit of a kludge, this is the XmlUtil class from the NIST tools, because we
 * have an XmlUtil class using wc3.dom.
 *
 */
@SuppressWarnings("restriction")
public class OMEUtil {
   static public OMFactory om_factory = OMAbstractFactory.getOMFactory();
   static public OMNamespace xml_namespace =   om_factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");

   public static OMElement firstChildWithLocalName(OMElement ele, String localName) {
      for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
         OMElement child = (OMElement) it.next();
         if (child.getLocalName().equals(localName))
            return child;
      }
      return null;
   }

   public static OMElement firstChildWithLocalNameEndingWith(OMElement ele, String localNameSuffix) {
      for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
         OMElement child = (OMElement) it.next();
         if (child.getLocalName().endsWith(localNameSuffix))
            return child;
      }
      return null;
   }

   public static List<OMElement> childrenWithLocalName(OMElement ele, String localName) {
      List<OMElement> al = new ArrayList<OMElement>();
      for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
         OMElement child = (OMElement) it.next();
         if (child.getLocalName().equals(localName))
            al.add(child);
      }
      return al;
   }

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
      return XmlFormatter.format(XmlFormatter.normalize(getStringFromNode(node)), false);
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
            sb.append(getStringFromNode((((Document)node).getDocumentElement())));
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
                  sb.append(getStringFromNode(children.item(i)));
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
   
   /**
    * Processes a {@code <RetrieveDocumentSetResponse>} body extracting the
    * documents for storage.
    * @param respEle Element to process. The {@code <RetrieveDocumentSetResponse>}
    * element or an ancestor.
    * @return Map of the documents contents. The key is the 
    * {@code <DocumentUniqueId>} text value, the value is the corresponding
    * document's decoded bytes. <b>Note:</b> If there is no DocumentUniqueId
    * element or it is empty, "unknown" will be used. If multiple documents with
    * the same DocumentUniqueId (including "unknown") are found, a counter will
    * be appended to the id to distinguish them.
    * @throws Exception on error, usually IO or parsing errors.
    */
   public static Map<String, byte[]> getImgDocs(Element respEle) throws Exception {
      int d = 0;
      String uid;
      Map<String, byte[]> map = new HashMap<>();
      OMElement respOME = AXIOMUtil.stringToOM(XmlUtil.elementToStr(respEle));
      for (OMElement docResp : OMEUtil.childrenWithLocalName(respOME, "DocumentResponse")) {
         OMElement docUid = OMEUtil.firstChildWithLocalName(docResp, "DocumentUniqueId");
         uid = "unknown";
         if (docUid != null) {
            String t = docUid.getText();
            if (StringUtils.isNotBlank(t)) uid = t;
         }
         OMElement doc = OMEUtil.firstChildWithLocalName(docResp, "Document");
         byte[] bytes = decode(doc);
         String key = uid;
         while (map.containsKey(key)) { key = uid + d++; }
         map.put(key, bytes);
      }
      return map;
   }
   /**
    * Decodes Document element text.
    * @param ele document element to decode
    * @return byte[] of contents
    * @throws Exception on error.
    */
   private static byte[] decode(OMElement ele) throws Exception {
      OMText binaryNode = (OMText) ele.getFirstOMChild();

      if (binaryNode.isOptimized()) {
         Object xhandler = binaryNode.getDataHandler();
         if ( !(xhandler instanceof javax.activation.DataHandler)) 
            throw new IOException("Expected instance of javax.activation.DataHandler, got instead " + xhandler.getClass().getName());
         javax.activation.DataHandler datahandler = (javax.activation.DataHandler) xhandler;
         InputStream is = null;
         try {
            is = datahandler.getInputStream();
            return Io.getBytesFromInputStream(is);
         }
         catch (IOException e) {
            throw new Exception("Error accessing XOP encoded document content from message");
         }
      } 
         String base64 = binaryNode.getText();
         BASE64Decoder d  = new BASE64Decoder();
         return d.decodeBuffer(base64);
      }
   }
