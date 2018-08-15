package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.testengine.engine.AbstractValidater;
import gov.nist.toolkit.testengine.engine.TestConfig;
import gov.nist.toolkit.testengine.engine.ToolkitEnvironment;
import gov.nist.toolkit.testengine.engine.SimReference;
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
	// this is processed by MhdClientTransaction for pluginType FHIR (and Xds)
	public static class Validations {

		TestKit.PluginType pluginType;

		public static class ValidaterInstance {
			public String validaterName;
			public AbstractValidater validater;
			public Map<String, String> parameters;

			ValidaterInstance(String name, AbstractValidater validater, Map<String, String> parameters) {
				this.validaterName = name;
				this.validater = validater;
				this.parameters = parameters;
			}
		}

		public List<ValidaterInstance> validaters = new ArrayList<>();

		public TestKit.PluginType getPluginType() {
			return pluginType;
		}
	}

	public Validations validations = new Validations();

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

	private ToolkitEnvironment toolkitEnvironment;

	public Assertion(ToolkitEnvironment toolkitEnvironment, OMElement asser, TestConfig testConfig, String date) {
		this.toolkitEnvironment = toolkitEnvironment;
		assertElement = asser;
		id = asser.getAttributeValue(new QName("id"));
		process = asser.getAttributeValue(new QName("process"));
		xpath = asser.getText();
		if (xpath != null) {
			xpath = xpath
					.replaceAll("\\$DATE\\$", date)
					.replaceAll("SITE", testConfig.siteXPath);
		}
		OMElement validationsEle = asser.getFirstChildWithName(new QName("Validations"));
		if (validationsEle != null) {
			String type = validationsEle.getAttributeValue(new QName("type"));
			TestKit.PluginType pluginType = TestKit.PluginType.get(type);
			if (pluginType == null)
				throw new ToolkitRuntimeException("Parsing TestPlan Assertion - do not understand validation type " + type);
			validations.pluginType = pluginType;

			for (Iterator it = validationsEle.getChildElements(); it.hasNext() ; ) {
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

				validations.validaters.add(new Validations.ValidaterInstance(validaterClassName, validaterInst, params));
			}
		}
	}

	public SimReference getSimReference(OMElement simTransactionElement) {
		if (simTransactionElement == null)
			throw new ToolkitRuntimeException(this.toString() + " has no SimReference element");
		String id = simTransactionElement.getAttributeValue(new QName("id"));
		String trans = simTransactionElement.getAttributeValue(new QName("transaction"));
		TransactionType tType = TransactionType.find(trans);
		if (tType == null) throw new ToolkitRuntimeException(this.toString() + " invalid transaction");
		SimId simId = SimDb.getFullSimId(new SimId(toolkitEnvironment.getTestSession(), id));
		return new SimReference(simId, tType);
	}


	public boolean hasValidations() {
		return !validations.validaters.isEmpty();
	}

	public List<Validations.ValidaterInstance> getAllValidaters() { return validations.validaters; }

	@Override
	public String toString() {
		String prs = "";
		if (StringUtils.isNotBlank(process)) prs = " process=" + process;
		return "[Assertion: id=" + id + prs + " xpath=" + xpath + "]";
	}

}
