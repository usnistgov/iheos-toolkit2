package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public class BasicContext  {
	BasicContext parent_context;
	HashMap atts;
	OmLogger testLog = new TestLogFactory().getLogger();
	
	public BasicContext(BasicContext parent_context) {
		this.parent_context = parent_context;
		atts = null;
	}
	
	public void set(String attname, Object attvalue) {
		if (atts == null) atts = new HashMap();
		atts.put(attname, attvalue);
	}
	
	public Object getObj(String attname) {
		if (atts == null) atts = new HashMap();
		Object value = atts.get(attname);
		if (value != null)
			return value;
		if (parent_context != null)
			return parent_context.getObj(attname);
		return null;
	}
	
	public String get(String attname) {
		return (String) getObj(attname);
	}
	
	public PlanContext getPlan() {
		if (this instanceof PlanContext) 
			return (PlanContext) this;
		if (parent_context != null)
			return parent_context.getPlan();
		return null;
	}
	
	public String getRecursive(String attname) {
		String value = get(attname);
		//System.out.println("getRecursive (" + attname + ") : " + getClass().getName() + ": " + value);
		if (value != null) return value;
		if (parent() != null) {
			return parent().getRecursive(attname);
		}
		return null;
	}
	
	public void dumpContext() {
		System.out.println(getClass().getName());
		if (atts == null)
			return;
		Set keys = atts.keySet();
		for (Iterator it=keys.iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			Object value = atts.get(key);
			System.out.println("    " + key + " : " + value.toString() );
		}
	}
	
	public void dumpContextRecursive() {
		dumpContext();
		if (parent() != null) 
			parent().dumpContextRecursive();
	}
	
	public void dumpContextIntoOutput(OMElement output) {
		OMElement context = MetadataSupport.om_factory.createOMElement(new QName("Context"));
		context.addAttribute("class", getClass().getName(), null);
		output.addChild(context);
		if (atts != null) {
			Set keys = atts.keySet();
			for (Iterator it=keys.iterator(); it.hasNext(); ) {
				String key = (String) it.next();
				Object value = atts.get(key);
				OMElement att_ele = MetadataSupport.om_factory.createOMElement(new QName(key));
				att_ele.addChild(MetadataSupport.om_factory.createOMText(value.toString()));
				context.addChild(att_ele);
			}
		}
		if (parent() != null) {
			parent().dumpContextIntoOutput(context);
		}
	}
	
	public BasicContext parent() { return parent_context; }


	private void fatal_error(String msg) throws XdsInternalException {
		dumpContextRecursive();
		throw new XdsInternalException(msg);
	}

	public void metadata_validation_error(String msg) throws MetadataValidationException {
		throw new MetadataValidationException(msg, null);
	}

	String error(String msg) {
		String out = "Error: stepId=" + get("step_id") + " : " + msg;
		System.out.println(out);
		return out;
	}

	void error(OMElement test_step_output, String msg) throws XdsInternalException {
		testLog.add_name_value(test_step_output, "Error", msg + " (stepId=" + get("step_id") + ")");
		error(msg);
		throw new XdsInternalException("Error " + msg);
	}

	void fault(OMElement test_step_output, String code, String msg) throws XdsInternalException {
		testLog.add_name_value(test_step_output, "SOAPFault", code + ": " + msg);
		testLog.add_name_value(test_step_output, "Error ", code + ": " + msg);
		error(msg);
		throw new XdsInternalException(msg);
	}

	OMElement build_results_document() {
		OMElement result = MetadataSupport.om_factory.createOMElement("TestResults", null);
		return result;
	}


	
}
