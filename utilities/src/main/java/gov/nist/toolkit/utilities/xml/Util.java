package gov.nist.toolkit.utilities.xml;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.XPathEvaluator;

public class Util {


	public static OMElement parse_xml(Object o) throws FactoryConfigurationError, XdsInternalException {
		if (o instanceof String) 
			return parse_xml((String) o);
		if (o instanceof InputStream) 
			return parse_xml((InputStream) o);
		if (o instanceof File) 
			return parse_xml((File) o);
		throw new XdsInternalException("Util.parse_xml(): do not understand input format " + o.getClass().getName());
	}

	public static OMElement parse_xml(InputStream is) throws FactoryConfigurationError, XdsInternalException {

		//		create the parser
		XMLStreamReader parser=null;

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(is);
		} catch (XMLStreamException e) {
			throw new XdsInternalException("Could not create XMLStreamReader from InputStream");
		} 

		//		create the builder
		StAXOMBuilder builder = new StAXOMBuilder(parser);

		//		get the root element (in this case the envelope)
		OMElement documentElement =  builder.getDocumentElement();	
		if (documentElement == null)
			throw new XdsInternalException("No document element");
		return documentElement;
	}
	
	public static void write_xml(File outfile, OMElement ele) throws IOException {
		Io.stringToFile(outfile, new OMFormatter(ele).toString());
	}

	public static OMElement parse_xml(File infile) throws FactoryConfigurationError, XdsInternalException {

		//		create the parser
		XMLStreamReader parser=null;

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(infile));
		} catch (XMLStreamException e) {
			throw new XdsInternalException("Could not create XMLStreamReader from " + infile.getName());
		} catch (FileNotFoundException e) {
			throw new XdsInternalException("Could not find input file " + infile.getAbsolutePath());
		}

		//		create the builder
		StAXOMBuilder builder = new StAXOMBuilder(parser);

		//		get the root element (in this case the envelope)
		OMElement documentElement =  builder.getDocumentElement();	
		if (documentElement == null)
			throw new XdsInternalException("No document element");
		return documentElement;
	}

	public static OMElement parse_xml(String input) throws FactoryConfigurationError, XdsInternalException {

		//		create the parser
		XMLStreamReader parser=null;

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(input.getBytes()));
		} catch (Exception e) {
			throw new XdsInternalException("Could not create XMLStreamReader from string: " + input.substring(0, 100) + "...");
		} 

		//		create the builder
		StAXOMBuilder builder = null;
		try {
			builder = new StAXOMBuilder(parser);
		} catch (Exception e) {
			throw new XdsInternalException("Util.parse_xml(): Could not create StAXOMBuilder from parser");
		} 

		OMElement documentElement = null; 
		try {
			//			get the root element (in this case the envelope)
			documentElement =  builder.getDocumentElement();	
			if (documentElement == null)
				throw new XdsInternalException("No document element");
		} catch (Exception e) {
			throw new XdsInternalException("Could not create XMLStreamReader: " +  e.getMessage() + "  (in Util.parse_xml()) from string: " + 
//					input.substring(0, (input.length() < 100) ? input.length() : 100) +
					input.replaceAll("<", "&lt;") 
//			"..."
					);
		}
		return documentElement;
	}

	static boolean isWhite(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

//	public static OMElement deep_copy(OMElement in) throws XdsInternalException {
//		if (in == null)
//			return null;
//
//		in.build();
//		
//		// Remove processing instructions from front
//		StringBuffer buf;
//		try {
//			buf = new StringBuffer(in.toStringWithConsume());
//		} catch (XMLStreamException e) {
//			throw new XdsInternalException(e.getMessage(),e);
//		}
//
//		//removeProcessingInstructions(buf);
//
//		return parse_xml(buf.toString());
//	}
	
	public static OMElement deep_copy(OMElement in) throws XdsInternalException {
		String str = new OMFormatter(in).toString();
//		String str = in.toString();
		return parse_xml(str);
	}

	private static void removeProcessingInstructions(StringBuffer buf) {
		boolean running = true;
		while(running) {
			while (isWhite(buf.charAt(0)))
				buf.deleteCharAt(0);

			running = false;
			if (buf.length() > 2 && buf.charAt(0) == '<' && buf.charAt(1) == '?') {
				int end = buf.indexOf("?>");
				buf.delete(0, end+1);
				running = true;
			}
		}
	}
	
	static void test1() throws XdsInternalException, FactoryConfigurationError {
		String x = "<foo/>";
		OMElement x_ele = Util.parse_xml(x);
		OMElement y_ele = Util.deep_copy(x_ele);
		if (!y_ele.getLocalName().equals("foo"))
			System.out.println("test1 fails, name is " + y_ele.getLocalName());
		OMElement z_ele = Util.parse_xml("<z/>");
		z_ele.addChild(y_ele);
		System.out.println("test1: " + z_ele.toString());
	}
	
	static void test2() throws XdsInternalException, FactoryConfigurationError {
		String x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <foo/>";
		OMElement x_ele = Util.parse_xml(x);
		OMElement y_ele = Util.deep_copy(x_ele);
		if (!y_ele.getLocalName().equals("foo"))
			System.out.println("test2 fails, name is " + y_ele.getLocalName());
		OMElement z_ele = Util.parse_xml("<z/>");
		z_ele.addChild(y_ele);
		System.out.println("test2: " + z_ele.toString());
	}
	
	public static void main(String[] args) {
		try {
		test1();
		test2();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
		}
	}

	public static String getAttributeValue(OMElement doc, String xpath) throws Exception {
		XPathEvaluator eval = new XPathEvaluator();
		List<OMNode> node_list = eval.evaluateXpath(xpath, doc, null);

		for (Iterator<?> it=node_list.iterator(); it.hasNext(); ) {
			OMAttribute attr = (OMAttribute) it.next();
			return attr.getAttributeValue();
		}
		throw new Exception("Path " + xpath + " not found");
	}

	public static List<String> getAttributeValues(OMElement doc, String xpath) throws Exception {
		List<String> values = new ArrayList<String>();
		XPathEvaluator eval = new XPathEvaluator();
		List<OMNode> node_list = eval.evaluateXpath(xpath, doc, null);

		for (Iterator<?> it=node_list.iterator(); it.hasNext(); ) {
			OMAttribute attr = (OMAttribute) it.next();
			values.add(attr.getAttributeValue());
		}

		if (values.size() == 0)
			throw new Exception("No Values");

		return values;
	}

	public static List<OMElement> getElements(OMElement doc, String xpath) throws Exception {
		XPathEvaluator eval = new XPathEvaluator();
		List<OMNode> node_list = eval.evaluateXpath(xpath, doc, null);
		List<OMElement> ele_list = new ArrayList<OMElement>();

		for (Iterator<?> it=node_list.iterator(); it.hasNext(); ) {
			OMNode node = (OMNode) it.next();
			if (! (node instanceof OMElement))
				continue;
			OMElement ele = (OMElement) node;
			ele_list.add(ele);
		}
		return ele_list;

	}

	public static String getElementValue(OMElement doc, String xpath) throws Exception {
		XPathEvaluator eval = new XPathEvaluator();
		List<OMNode> node_list = eval.evaluateXpath(xpath, doc, null);

		for (Iterator<?> it=node_list.iterator(); it.hasNext(); ) {
			OMNode node = (OMNode) it.next();
			if (! (node instanceof OMElement))
				continue;
			OMElement ele = (OMElement) node;
			return ele.getText();
		}
		throw new Exception("Path " + xpath + " not found");
	}

	public static OMElement mkElement(String name, String text, OMElement parent) {
		OMElement ele = OMAbstractFactory.getOMFactory().createOMElement(name, null);
		if (text != null)
			ele.setText(text);
		if (parent != null)
			parent.addChild(ele);
		return ele;
	}

	public static OMElement xmlizeHashMap(HashMap<String, ?> map) {
		OMElement map_ele = OMAbstractFactory.getOMFactory().createOMElement("Map", null);

		for (Iterator it=map.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			Object value = map.get(key);
			OMElement item = mkElement("Item", null, map_ele);
			mkElement("Key", key, item);
			mkElement("Value", value.toString(), item);
		}

		return map_ele;
	}


}
