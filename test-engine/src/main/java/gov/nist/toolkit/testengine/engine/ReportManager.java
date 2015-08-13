package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testenginelogging.Report;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

public class ReportManager {
	List<Report> reports;
	OMElement root;
	Map<String, OMElement> sections;
	TestConfig testConfig;

	public ReportManager(TestConfig config) {
		testConfig = config;
		reports = new ArrayList<Report>();
		sections = new HashMap<String, OMElement>();
	}

	public String toString() {
		return "ReportManager: " + reports.toString();
	}

	public void addReport(Report r) {
		reports.add(r);
	}

	public void addReport(OMElement r) {
		Report report = new Report();
		report.name = r.getAttributeValue(new QName("name"));
		report.setSection(r.getAttributeValue(new QName("section")));
		report.setXpath(r.getText());
		addReport(report);
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
		for (Report report : reports) {
			AXIOMXPath xpathExpression;
			try {
				if (report.getXpath() != null && !report.getXpath().equals("")) {
					OMElement section = getSection(report.getSection());
					xpathExpression = new AXIOMXPath (report.getXpath());
					String val;
					try {
						val = xpathExpression.stringValueOf(section);
					} catch (Exception e) {
						throw new XdsInternalException("Error evaluating XPath expression [" +
								report.getXpath() + "] against output of section [" + report.getSection() + "]");
					}
					report.setValue(val);
					if (val == null || val.equals(""))
						throw new XdsInternalException("Report " + report.name +
								" which has XPath " + report.getXpath() +
								" evaluates to [" + val + "] when evaluated " +
								"against section " + report.getSection());
				}
			}
			catch (JaxenException e) {
				throw new XdsInternalException("Error evaluating Report " + report.name, e);
			}

		}
	}

	public void report(Map<String, String> map) {
		if (map == null) return;
		for (String name : map.keySet()) {
			String value = map.get(name);
			Report r = new Report(name, value);
			reports.add(r);
		}
	}

	public void report(Map<String, String> map, String nameSuffix) {
		for (String name : map.keySet()) {
			String value = map.get(name);
			Report r = new Report(name + nameSuffix, value);
			reports.add(r);
		}
	}

	public OMElement toXML() {
		OMElement top = MetadataSupport.om_factory.createOMElement("Reports", null);

		for (Report report : reports) {
			OMElement rep = MetadataSupport.om_factory.createOMElement("Report", null);

			rep.addAttribute("name", report.name, null);
			rep.setText(report.getValue());

			top.addChild(rep);
		}

		return top;
	}

}
