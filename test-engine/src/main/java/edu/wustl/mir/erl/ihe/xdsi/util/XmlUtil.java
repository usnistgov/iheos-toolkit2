/*
 * Copyright (c) 2014 Washington University in St. Louis All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. The License is available at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: Initial author: Ralph Moulton / MIR WUSM IHE Development
 * Project moultonr@mir.wustl.edu
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * static XML related Utility methods. *
 */
@SuppressWarnings("restriction")
public class XmlUtil implements Serializable {
   private static final long serialVersionUID = 1L;

   /**
    * null safe check, does list contain any Elements?
    * 
    * @param elementList list to check
    * @return boolean true if list is null or empty, false otherwise.
    */
   public static boolean isEmpty(List <Element> elementList) {
      if (elementList == null) return true;
      if (elementList.isEmpty()) return true;
      return false;
   }

   /**
    * null safe check, does list not contain any Elements?
    * 
    * @param elementList list to check
    * @return boolean false if list is null or empty, true otherwise.
    */
   public static boolean isNotEmpty(List <Element> elementList) {
      return !isEmpty(elementList);
   }

   /**
    * Returns text content of passed node, but not that of descendant nodes.
    * 
    * @param node Node to get text from.
    * @return Text of this node; null if node is null or no text subnodes exist.
    */
   public static String getFirstLevelTextContent(Node node) {
      if (node == null) return null;
      NodeList list = node.getChildNodes();
      if (list == null) return null;
      boolean textFound = false;
      StringBuilder textContent = new StringBuilder();
      for (int i = 0; i < list.getLength(); ++i) {
         Node child = list.item(i);
         if (child.getNodeType() == Node.TEXT_NODE) {
            textContent.append(child.getTextContent());
            textFound = true;
         }
      }
      if (textFound) return textContent.toString();
      return null;
   }

   /**
    * Parses an xml string and returns the corresponding {@link Element}. 
    * 
    * @param xmlStr String to parse; should be valid xml.
    * @return Element represented by string
    * @throws Exception on parsing error.
    */
   public static Element strToElement(String xmlStr) throws Exception {
      InputStream sbis =
         new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(sbis);
      return doc.getDocumentElement();
   }
   
   /**
    * Converts an Element (including all descendants, to a string).
    * @param element Element to convert
    * @return String of element
    * @throws Exception on error - not likely
    */
   public static String elementToStr(Element element) throws Exception {
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      StringWriter buffer = new StringWriter();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(element),
            new StreamResult(buffer));
      return buffer.toString();
   }
   
   /**
    * Gets first level child Elements of passed node.
    * @param node Node to examine
    * @return an array of the Elements found. May be empty, but will not be null
    */
   public static Element[] getFirstLevelChildElements(Node node) {
      List <Element> found = new ArrayList <>();
      if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
         NodeList children = node.getChildNodes();
         for (int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if (child != null && child.getNodeType() == Node.ELEMENT_NODE)
               found.add((Element) child);
         }
      }
      return found.toArray(new Element[0]);
   }
   
   /**
    * Gets first level child Elements of passed node which have passed name.
    * @param node Node to examine
    * @param name String name to match
    * @return an array of the Elements found. May be empty, but will not be null
    */
   public static Element[] getFirstLevelChildElementsByName(Node node,
      String name) {
      List <Element> found = new ArrayList <>();
      if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
         NodeList children = node.getChildNodes();
         for (int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if (child != null && child.getNodeType() == Node.ELEMENT_NODE
               && child.getLocalName().equalsIgnoreCase(name))
               found.add((Element) child);
         }
      }
      return found.toArray(new Element[0]);
   }
   
   /**
    * Attempts to format an XML String for pretty printing.
    * 
    * @param msg XML String to format.
    * @return String If the attempt to format the XML message for pretty print
    * succeeds, the message in pretty print format is returned.
    * <p>If the attempt fails, a log message giving some reason as to why it failed
    * will be generated, and a copy of the original message is returned, but no
    * exception is thrown.</p>
    */
   public static String prettyPrintXML(String msg) {
      String xml;
      String n = System.getProperty("line.separator");
      try {
         // ---------------------------- make sure something is there
         String m = StringUtils.stripToEmpty(msg);
         if (m.length() == 0) throw new Exception("message empty or null");
         String hdr = StringUtils.substringBefore(m, "<?xml");
         if (hdr.length() == m.length())
            throw new Exception("no XML document in message");
         xml = StringUtils.substringAfter(m, hdr);
         // -------------------------------------- String => Document
         InputSource src = new InputSource(new StringReader(xml));
         DocumentBuilderFactory dbFactory =
            DocumentBuilderFactory.newInstance();
         dbFactory.setNamespaceAware(true);
         Document doc = dbFactory.newDocumentBuilder().parse(src);
         // ------------------------------------ Pretty print format
         OutputFormat format = new OutputFormat();
         format.setMediaType("text");
         format.setLineWidth(80);
         format.setIndenting(true);
         format.setIndent(3);
         format.setEncoding("UTF-8");
         // -------------------------------------- Document => String
         StringWriter stringOut = new StringWriter();
         XMLSerializer serial = new XMLSerializer(stringOut, format);
         serial.serialize(doc);
         return hdr + n + stringOut.toString();

      } catch (Exception e) {
         Utility.getLog().warn("prettyPrintXML error:" + e.getMessage());
         return msg;
      }
   } // EO prettyPrintXML

   /**
    * Attempts to format a SOAP message for pretty printing.
    * 
    * @param msg SOAP msg to format.
    * @return String If the attempt to format the SOAP message for pretty print
    * succeeds, the message in pretty print format is returned.
    * <p>If the attempt fails, a log message giving some reason as to why it failed
    * will be generated, and a copy of the original message is returned, but no
    * exception is thrown.</p>
    */
   public static String prettyPrintSOAP(String msg) {
      try {
         // ---------------------------- make sure something is there
         String xml = StringUtils.stripToEmpty(msg);
         if (xml.length() == 0) throw new Exception("message empty or null");
         // -------------------------------------- String => Document
         InputSource src = new InputSource(new StringReader(xml));
         DocumentBuilderFactory dbFactory =
            DocumentBuilderFactory.newInstance();
         dbFactory.setNamespaceAware(true);
         Document doc = dbFactory.newDocumentBuilder().parse(src);
         // ------------------------------------ Pretty print format
         OutputFormat format = new OutputFormat();
         format.setMediaType("text");
         format.setLineWidth(80);
         format.setIndenting(true);
         format.setIndent(3);
         format.setEncoding("UTF-8");
         format.setOmitXMLDeclaration(true);
         // -------------------------------------- Document => String
         StringWriter stringOut = new StringWriter();
         XMLSerializer serial = new XMLSerializer(stringOut, format);
         serial.serialize(doc);
         return stringOut.toString();

      } catch (Exception e) {
         Utility.getLog().warn("prettyPrintXML error:" + e.getMessage());
         return Utility.nl + msg;
      }
   } // EO prettyPrintXML
   
   public static void truncateDocuments(Element element) {
      NodeList documents = element.getElementsByTagName("*");
      if (documents == null) return;
      for (int i = 0; i < documents.getLength(); i++) {
         Node document = documents.item(i);
         if(document.getNodeType() != Node.ELEMENT_NODE) continue;
         if(document.getLocalName().equalsIgnoreCase("Document") == false) continue;
         Element doc = (Element) document;
         doc.setTextContent("...");
      }
      return;
   }


} // EO XmlUtil class
