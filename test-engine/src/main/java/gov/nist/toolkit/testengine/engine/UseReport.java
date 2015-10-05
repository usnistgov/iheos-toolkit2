package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.client.TestInstance;
import org.apache.axiom.om.OMElement;


public class UseReport {
	public TestInstance testInstance;
	public String section;
	public String step;
	public String reportName;
	public String useAs;
	public String value;

	public OMElement toXML() {
		OMElement ur = MetadataSupport.om_factory.createOMElement("UseReport", null);

		normalize();

		if (!testInstance.isEmpty())
			ur.addAttribute("test", testInstance.getId(), null);
		ur.addAttribute("section", section, null);
		ur.addAttribute("step", step, null);
		ur.addAttribute("reportName", reportName, null);
		ur.addAttribute("useAs", useAs, null);
		ur.addAttribute("value", value, null);

		return ur;
	}

	public void normalize() {
		if (testInstance == null) testInstance = new TestInstance();
		if (section == null) section = "";
		if (step == null) step = "";
		if (reportName == null) reportName = "";
		if (useAs == null) useAs = "";
		if (value == null) value = "";
	}

	public String toString() {

		normalize();

		return "UseReport:" +
		( (testInstance.isEmpty() ? "" : " test=" + testInstance.getId())  )  +
		" section=" + section +
		" step=" + step +
		" reportName=" + reportName +
		" useAs=" + useAs +
		" value=" + value;
	}

	public boolean isComplete() {
		normalize();
		return ! (  //section.equals("") ||
				step.equals("") ||
				reportName.equals("") ||
				useAs.equals(""));

	}

}
