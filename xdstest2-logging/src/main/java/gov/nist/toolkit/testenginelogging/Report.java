package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Report implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -370504909542903482L;
	public String name;
	String section;
	String xpath;
	String value = "Unavailable";

	static QName name_qname = new QName("name");

	static public Report parse(OMElement rep) throws XdsInternalException {
		Report r = new Report();

		r.name = rep.getAttributeValue(name_qname);
		r.value = rep.getText();

		if (r.name == null || r.name.equals(""))
			throw new XdsInternalException("Cannot parse Report: " + rep.toString());

		return r;
	}

	public Report() {

	}

	public Report(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("Report ").append(name).append(" section=").append(section)
		.append(" value=").append(value);

		return buf.toString();
	}

	static List<Report> parseReports(OMElement ele) throws XdsInternalException {
		List<Report> reports = new ArrayList<Report>();

		for (OMElement rep : XmlUtil.childrenWithLocalName(ele, "Report")) {
			Report r = parse(rep);
			reports.add(r);
		}

		return reports;
	}

	static void setSection(List<Report> reports, String section) {
		for (Report report : reports) {
			report.section = section;
		}
	}

	public String getSection() {
        return section;
    }

	public void setSection(String section) { this.section = section; }

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
