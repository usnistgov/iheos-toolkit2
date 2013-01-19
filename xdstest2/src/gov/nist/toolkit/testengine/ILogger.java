package gov.nist.toolkit.testengine;

import java.util.ArrayList;
import java.util.Map;

import org.apache.axiom.om.OMElement;

public interface ILogger {

	public  OMElement add_simple_element(OMElement parent, String name);

	public  OMElement add_simple_element_with_id(OMElement parent,
			String name, String id);

	public  OMElement add_simple_element_with_id(OMElement parent,
			String name, String id, String value);

	public  void add_name_value(OMElement parent, String name,
			ArrayList<OMElement> data);

	public  void add_name_value(OMElement parent, String name,
			Map<String, String> data);

	public  OMElement add_name_value(OMElement parent, String name,
			String value);

	public  OMElement add_name_value_with_id(OMElement parent,
			String name, String id, String value);

	public  OMElement add_name_value(OMElement parent, String name,
			OMElement value);

	public  OMElement add_name_value(OMElement parent, String name,
			OMElement value1, OMElement value2);

	public  OMElement add_name_value(OMElement parent, String name,
			OMElement value1, OMElement value2, OMElement value3);

	public  OMElement create_name_value(String name, OMElement value);

	public  OMElement create_name_value(String name, String value);

	public  OMElement add_name_value(OMElement parent, OMElement element);

}