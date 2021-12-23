package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.QName;

public class OmLogger implements ILogger {
	static Logger logger = Logger.getLogger(OmLogger.class.getName());

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_simple_element(org.apache.axiom.om.OMElement, java.lang.String)
	 */
	@Override
	public OMElement add_simple_element(OMElement parent, String name) {
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		parent.addChild(ele);
		return ele;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_simple_element_with_id(org.apache.axiom.om.OMElement, java.lang.String, java.lang.String)
	 */
	@Override
	public OMElement add_simple_element_with_id(OMElement parent, String name,
			String id) {
		OMElement ele = add_simple_element(parent, name);
		ele.addAttribute("id", id, null);
		return ele;
	}
	
	public OMElement add_simple_element(OMElement parent, QName qName) {
	   return MetadataSupport.om_factory.createOMElement(qName, parent);
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_simple_element_with_id(org.apache.axiom.om.OMElement, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, java.lang.String, java.util.ArrayList)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, java.lang.String, java.util.Map)
	 */
	@Override
	public void add_name_value(OMElement parent, String name, Map<String, String> data) {
		OMElement elel = MetadataSupport.om_factory.createOMElement(name, null);
		parent.addChild(elel);
		elel.setText(encodeLT(data.toString()));
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, java.lang.String, java.lang.String)
	 */
	@Override
	public OMElement add_name_value(OMElement parent, String name, String value) {
		name = name.replaceAll(":", " ");
//		System.out.println(name + ": " + value);
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(encodeLT(value)));
		parent.addChild(ele);
		return ele;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value_with_id(org.apache.axiom.om.OMElement, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public OMElement add_name_value_with_id(OMElement parent, String name, String id, String value) {
		if (name == null) name = "";
		if (id == null) id = "";
		if (value == null) value = "";
		name = name.replaceAll(":", " ");
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.addAttribute("id", id, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(encodeLT(value)));
		parent.addChild(ele);
		return ele;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, java.lang.String, org.apache.axiom.om.OMElement)
	 */
	@Override
	public OMElement add_name_value(OMElement parent, String name, OMElement value) {
		OMNode val = value;
		name = name.replaceAll(":", "");
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		if (val == null)
			val = MetadataSupport.om_factory.createOMElement("None", null);
		else {
			try {
//				if (name.equals("InputMetadata")) {
//					System.out.println("InputMetadata:\n" + new OMFormatter(value).toString());
//				}
				val = Util.deep_copy(value);
			} catch (Exception e) {
				// added to understand Undeclared namespace prefix "wsu" (for attribute "Id") issue
				Util.mkElement("Exception", value.toString(), ele);
				return ele;
			}
		}
		try {
			ele.addChild(val);
		}
		catch (OMException e) {
			// updated to understand Undeclared namespace prefix "wsu" (for attribute "Id") issue
			Util.mkElement("Exception", "Exception writing log content\n" + OMFormatter.encodeAmp(ExceptionUtil.exception_details(e))
					+ "\n" + value.toString(), ele);
		}
		parent.addChild(ele);
		return ele;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, java.lang.String, org.apache.axiom.om.OMElement, org.apache.axiom.om.OMElement)
	 */
	@Override
	public OMElement add_name_value(OMElement parent, String name, OMElement value1, OMElement value2) {
		name = name.replaceAll(":", " ");
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

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, java.lang.String, org.apache.axiom.om.OMElement, org.apache.axiom.om.OMElement, org.apache.axiom.om.OMElement)
	 */
	@Override
	public OMElement add_name_value(OMElement parent, String name, OMElement value1, OMElement value2, OMElement value3) {
		name = name.replaceAll(":", " ");
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

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#create_name_value(java.lang.String, org.apache.axiom.om.OMElement)
	 */
	@Override
	public OMElement create_name_value(String name, OMElement value) {
		name = name.replaceAll(":", " ");
		OMNode val = value;
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		if (val == null)
			val = MetadataSupport.om_factory.createOMText("null");
		ele.addChild(val);
		return ele;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#create_name_value(java.lang.String, java.lang.String)
	 */
	@Override
	public OMElement create_name_value(String name, String value) {
		name = name.replaceAll(":", " ");
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.addChild(MetadataSupport.om_factory.createOMText(value));
		return ele;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.ILogger#add_name_value(org.apache.axiom.om.OMElement, org.apache.axiom.om.OMElement)
	 */
	@Override
	public OMElement add_name_value(OMElement parent, OMElement element) {
		parent.addChild(element);
		return element;
	}

}
