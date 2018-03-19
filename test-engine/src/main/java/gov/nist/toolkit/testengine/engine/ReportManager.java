package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {
   private final static Logger logger = Logger.getLogger(ReportManager.class);
   List <ReportDTO> reportDTOs;
   OMElement root;
   Map <String, OMElement> sections;
   TestConfig testConfig;

   public ReportManager(TestConfig config) {
      testConfig = config;
      reportDTOs = new ArrayList <ReportDTO>();
      sections = new HashMap <String, OMElement>();
   }

   public String toString() {
      return "ReportManager: " + reportDTOs.toString();
   }

   public void addReport(ReportDTO r) {
      reportDTOs.add(r);
   }

   public void addReport(OMElement r) {
      ReportDTO reportDTO = new ReportDTO();
      reportDTO.setName(r.getAttributeValue(new QName("name")));
      reportDTO.setSection(r.getAttributeValue(new QName("section")));
      reportDTO.setXpath(r.getText());
      reportDTO.setEscapedCharsInXml(r.getAttributeValue(new QName("escapedCharsInXml")));
      addReport(reportDTO);
   }

   public void setXML(OMElement xml) throws XdsInternalException {
      // String str = new OMFormatter(xml).toString();
      // String str = in.toString();
      // XPath will search entire tree, even if we give it an intermediate node
      root = Util.deep_copy(xml);
   }

   String stringAround(String s, int focus) {
      int offset = 25;
      int from = focus - offset;
      int to = focus + offset;
      if (from < 0) from = 0;
      if (to >= s.length()) to = s.length();
      return s.substring(from, to);
   }

   public OMElement getSection(String sectionName) throws XdsInternalException {
      AXIOMXPath xpathExpression;
      try {
         xpathExpression = new AXIOMXPath("//*[local-name()='" + sectionName + "']");
         List <OMElement> y = (List <OMElement>) xpathExpression.selectNodes(root);
         OMElement x = (OMElement) xpathExpression.selectSingleNode(root);
         return x;
      } catch (JaxenException e) {
         throw new XdsInternalException("Error extracting section " + sectionName + " from log output", e);
      }

   }

   /**
    * Calculates xpath based Report values
    * @throws XdsInternalException on xpath error.
    */
   public void generate() throws XdsInternalException {
      for (ReportDTO reportDTO : reportDTOs) {
         logger.info("Generating Report " + reportDTO.toString());
         
         AXIOMXPath xpathExpression;
         try {
            if (StringUtils.isBlank(reportDTO.getXpath())) continue;
            
            OMElement section = getSection(reportDTO.getSection());
            if (section == null) {
               logger.error("Section " + reportDTO.getName() + " is not defined");
               throw new XdsInternalException("Section " + reportDTO.getName() + " is not defined");
            }
//            logger.info("Got section: " + section.toString());
            
            xpathExpression = new AXIOMXPath(reportDTO.getXpath());
            
            String val;
            try {
               if ("true".equals(reportDTO.getEscapedCharsInXml())) {
                  String sectionStr = section.toString();
                  sectionStr = getDecodedStr(sectionStr);
                  section = Util.parse_xml(sectionStr);
                  List nodeList = xpathExpression.selectNodes(section);
                  if (nodeList.size() != 1) {
                     logger.info("ReportManager: section path extraction failed! ReportDTO: " + reportDTO.toString());
                  }
                  OMElement targetEle = (OMElement) nodeList.get(0);
                  val = targetEle.toString();
               } else { // The default
                  val = xpathExpression.stringValueOf(section);
               }
            } catch (Exception e) {
               val = String.format("Error generating report: %s", e.getMessage());
               logger.info(val);
            }
            reportDTO.setValue(val);
            if (StringUtils.isBlank(val)) {
               val = "Report " + reportDTO.getName() + " which has XPath " + reportDTO.getXpath() + " evaluates to ["
                  + val + "] when evaluated " + "against section " + reportDTO.getSection();
               logger.info(val);
            }
         } catch (JaxenException e) {
            throw new XdsInternalException("Error evaluating Report " + reportDTO.getName(), e);
         }

      } // EO for each ReportDTO loop
   }

   public static String getDecodedStr(String sectionStr) {
      sectionStr = sectionStr.replace("&lt;", "<").replace("&gt;", ">").replaceAll(">\\s*<", "><");
      return sectionStr;
   }

   public void report(Map <String, String> map) {
      if (map == null) return;
      for (String name : map.keySet()) {
         String value = map.get(name);
         ReportDTO r = new ReportDTO(name, value);
         reportDTOs.add(r);
      }
   }

   public void report(Map <String, String> map, String nameSuffix) {
      for (String name : map.keySet()) {
         String value = map.get(name);
         ReportDTO r = new ReportDTO(name + nameSuffix, value);
         reportDTOs.add(r);
      }
   }

   public OMElement toXML() {
      OMElement top = MetadataSupport.om_factory.createOMElement("Reports", null);

      for (ReportDTO reportDTO : reportDTOs) {
         OMElement rep = MetadataSupport.om_factory.createOMElement("Report", null);

         rep.addAttribute("name", reportDTO.getName(), null);
         rep.setText(reportDTO.getValue());

         top.addChild(rep);
      }

      return top;
   }

   public void add(String name, String value) {
      ReportDTO ur = new ReportDTO(name, value);
      reportDTOs.add(ur);
   }

   public void setRetInfo(RetrievedDocumentModel ri, int docIndex) {

          add("$repuid_doc" + Integer.toString(docIndex)  + "$", ri.getRepUid());
          add("$mimetype_doc" + Integer.toString(docIndex)  + "$", ri.getContent_type());
          add("$hash_doc" + Integer.toString(docIndex)  + "$", ri.getHash());
          add("$home_doc" + Integer.toString(docIndex)  + "$", ri.getHome());
          add("$size_doc" + Integer.toString(docIndex)  + "$", Integer.toString(ri.getSize()));
       }
}
