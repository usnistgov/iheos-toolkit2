package gov.nist.toolkit.testengine;


import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;

public class OmLogger {

	public OMElement add_simple_element(OMElement parent, String name) {
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		parent.addChild(ele);
		return ele;
	}

	public OMElement add_simple_element_with_id(OMElement parent, String name,
			String id) {
		OMElement ele = add_simple_element(parent, name);
		ele.addAttribute("id", id, null);
		return ele;
	}

	public OMElement add_simple_element_with_id(OMElement parent, String name,
			String id, String value) {
		if (name == null) name = "";
		if (id == null) id = "";
		if (value == null) value = "";
		OMElement ele = add_simple_element(parent, name);
		ele.addAttribute("id", id, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(encodeLT(value)));
		return ele;
	}

	public void add_name_value(OMElement parent, String name, ArrayList<OMElement> data) {
		for (OMElement ele : data) {
			OMElement elel = MetadataSupport.om_factory.createOMElement(name, null);
			try {
				elel.addChild(Util.deep_copy(ele));
			} catch (XdsInternalException e) {
				e.printStackTrace();
			}
			parent.addChild(elel);
		}
	}
	
	String encodeLT(String msg) {
		return msg;
	}

	public void add_name_value(OMElement parent, String name, Map<String, String> data) {
		OMElement elel = MetadataSupport.om_factory.createOMElement(name, null);
		parent.addChild(elel);
		elel.setText(encodeLT(data.toString()));
	}

	public OMElement add_name_value(OMElement parent, String name, String value) {
		System.out.println(name + ": " + value);
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(encodeLT(value)));
		parent.addChild(ele);
		return ele;
	}

	public OMElement add_name_value_with_id(OMElement parent, String name, String id, String value) {
		if (name == null) name = "";
		if (id == null) id = "";
		if (value == null) value = "";
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.addAttribute("id", id, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(encodeLT(value)));
		parent.addChild(ele);
		return ele;
	}

	public OMElement add_name_value(OMElement parent, String name, OMElement value) {
		OMNode val = value;
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		if (val == null)
			val = MetadataSupport.om_factory.createOMElement("None", null);
		else {
			try {
//				if (name.equals("InputMetadata")) {
//					System.out.println("InputMetadata:\n" + new OMFormatter(value).toString());
//				}
				val = Util.deep_copy(value);
			} catch (Exception e) {}
		}
		try {
			ele.addChild(val);
		}
		catch (OMException e) {
			Util.mkElement("Exception", "Exception writing log content\n" + OMFormatter.encodeAmp(ExceptionUtil.exception_details(e))
					+ "\n" + new OMFormatter(value).toString(), ele);
		}
		parent.addChild(ele);
		return ele;
	}

	public OMElement add_name_value(OMElement parent, String name, OMElement value1, OMElement value2) {
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		OMNode val1 = value1;
		if (val1 == null)
			val1 = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val1);
		OMNode val2 = value2;
		if (val2 == null)
			val2 = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val2);
		parent.addChild(ele);
		return ele;
	}

	public OMElement add_name_value(OMElement parent, String name, OMElement value1, OMElement value2, OMElement value3) {
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		OMNode val1 = value1;
		if (val1 == null)
			val1 = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val1);
		OMNode val2 = value2;
		if (val2 == null)
			val2 = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val2);
		OMNode val3 = value3;
		if (val3 == null)
			val3 = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val3);
		parent.addChild(ele);
		return ele;
	}

	public OMElement create_name_value(String name, OMElement value) {
		OMNode val = value;
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		if (val == null)
			val = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val);
		return ele;
	}

	public OMElement create_name_value(String name, String value) {
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(value));
		return ele;
	}

	public OMElement add_name_value(OMElement parent, OMElement element) {
		parent.addChild(element);
		return element;
	}

}
