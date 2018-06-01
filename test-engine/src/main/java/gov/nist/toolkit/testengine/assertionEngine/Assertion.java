package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.testengine.engine.AbstractValidater;
import gov.nist.toolkit.testengine.engine.TestConfig;
import gov.nist.toolkit.testengine.engine.ToolkitEnvironment;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * struct to hold parsed {@code <Assert>} element
 */
public class Assertion {

	// This is used to manage assertion plugins
	static class Validation {

		static class ValidaterInstance {
			String validaterName;
			AbstractValidater validater;
			Map<String, String> parameters;

			ValidaterInstance(String name, AbstractValidater validater, Map<String, String> parameters) {
				this.validaterName = name;
				this.validater = validater;
				this.parameters = parameters;
			}
		}

		TestKit.PluginType pluginType;
		List<ValidaterInstance> validaters = new ArrayList<>();
	}

	List<Validation> validations = new ArrayList<>();

	/**
	 * {@code <Assert>} element from testplan.xml
	 */
	public OMElement assertElement;
	/**
	 * unique (inside {@code <Assertions>} element) id.
	 */
	public String id;
	/**
	 * process if present, indicates non-xpath evaluation process name
	 */
	public String process;
	/**
	 * String assertion xpath expression
	 */
	public String xpath;


	Assertion(ToolkitEnvironment toolkitEnvironment, OMElement asser, TestConfig testConfig, String date) {
		assertElement = asser;
		id = asser.getAttributeValue(new QName("id"));
		process = asser.getAttributeValue(new QName("process"));
		xpath = asser.getText();
		if (xpath != null) {
			xpath = xpath
					.replaceAll("\\$DATE\\$", date)
					.replaceAll("SITE", testConfig.siteXPath);
		}
		OMElement validations = asser.getFirstChildWithName(new QName("Validations"));
		if (validations != null) {
			String type = validations.getAttributeValue(new QName("type"));
			TestKit.PluginType pluginType = TestKit.PluginType.get(type);
			if (pluginType == null)
				throw new ToolkitRuntimeException("Parsing TestPlan Assertion - do not understand validation type " + type);

			for (Iterator it = validations.getChildElements(); it.hasNext() ; ) {
				Object o = it.next();
				if (!(o instanceof OMElement)) continue;
				OMElement valEle = (OMElement) o;

				String validaterClassName = valEle.getLocalName();

				AssertionContext.Context context = AssertionContext.get(pluginType, toolkitEnvironment.getEnvironment(), toolkitEnvironment.getTestSession());

				Map<String, String> params = new HashMap<>();
				for (Iterator ita = valEle.getAllAttributes(); ita.hasNext() ; ) {
					Object oa = ita.next();
					if (!(oa instanceof OMAttribute)) continue;
					OMAttribute att = (OMAttribute) oa;
					String aname = att.getLocalName();
					String avalue = att.getAttributeValue();
					if (aname != null && !aname.equals(""))
						params.put(aname, avalue);
				}

				AbstractValidater validaterInst = context.getValidater(validaterClassName, params);

				Validation validation = new Validation();
				validation.pluginType = pluginType;
				validation.validaters.add(new Validation.ValidaterInstance(validaterClassName, validaterInst, params));
				this.validations.add(validation);
			}
		}
	}

	@Override
	public String toString() {
		String prs = "";
		if (StringUtils.isNotBlank(process)) prs = " process=" + process;
		return "[Assertion: id=" + id + prs + " xpath=" + xpath + "]";
	}

}
