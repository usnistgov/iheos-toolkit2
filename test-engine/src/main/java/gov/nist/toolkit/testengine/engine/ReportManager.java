package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {
    private final static Logger logger = Logger.getLogger(ReportManager.class);
	List<ReportDTO> reportDTOs;
	OMElement root;
	Map<String, OMElement> sections;
	TestConfig testConfig;

	public ReportManager(TestConfig config) {
		testConfig = config;
		reportDTOs = new ArrayList<ReportDTO>();
		sections = new HashMap<String, OMElement>();
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
		addReport(reportDTO);
	}

	public void setXML(OMElement xml) throws XdsInternalException {
//		String str = new OMFormatter(xml).toString();
//		String str = in.toString();
		// XPath will search entire tree, even if we give it an intermediate node
		root = Util.deep_copy(xml);
	}

	String stringAround(String s, int focus) {
		int offset=25;
		int from = focus - offset;
		int to = focus + offset;
		if (from < 0)
			from = 0;
		if (to >= s.length())
			to = s.length();
		return s.substring(from, to);
	}

	public OMElement getSection(String sectionName) throws XdsInternalException {
		AXIOMXPath xpathExpression;
		try {
			xpathExpression = new AXIOMXPath ("//*[local-name()='" + sectionName + "']");
			List<OMElement> y = (List<OMElement>) xpathExpression.selectNodes(root);
			OMElement x = (OMElement) xpathExpression.selectSingleNode(root);
			return x;
		} catch (JaxenException e) {
			throw new XdsInternalException("Error extracting section " + sectionName + " from log output", e);
		}

	}

	public void generate() throws XdsInternalException {
		for (ReportDTO reportDTO : reportDTOs) {
            logger.info("Generating Report " + reportDTO.toString());
			AXIOMXPath xpathExpression;
			try {
				if (reportDTO.getXpath() != null && !reportDTO.getXpath().equals("")) {
					OMElement section = getSection(reportDTO.getSection());
					xpathExpression = new AXIOMXPath (reportDTO.getXpath());
					String val;
					try {
						val = xpathExpression.stringValueOf(section);
					} catch (Exception e) {
                        val = String.format("Error generating report: %s", e.getMessage());
//						throw new XdsInternalException("Error evaluating XPath expression [" +
//								reportBuilder.getXpath() + "] against output of section [" + reportBuilder.getSection() + "]");
					}
					reportDTO.setValue(val);
					if (val == null || val.equals(""))
						val = "Report " + reportDTO.getName() +
								" which has XPath " + reportDTO.getXpath() +
								" evaluates to [" + val + "] when evaluated " +
								"against section " + reportDTO.getSection();
//                    throw new XdsInternalException("ReportBuilder " + reportBuilder.name +
//                            " which has XPath " + reportBuilder.getXpath() +
//                            " evaluates to [" + val + "] when evaluated " +
//                            "against section " + reportBuilder.getSection());
				}
			}
			catch (JaxenException e) {
				throw new XdsInternalException("Error evaluating Report " + reportDTO.getName(), e);
			}

		}
	}

	public void report(Map<String, String> map) {
		if (map == null) return;
		for (String name : map.keySet()) {
			String value = map.get(name);
			ReportDTO r = new ReportDTO(name, value);
			reportDTOs.add(r);
		}
	}

	public void report(Map<String, String> map, String nameSuffix) {
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

}
