/*
 * Transform.java
 *
 * Created on December 6, 2003, 6:52 PM
 */

package gov.nist.toolkit.utilities.xsl;

import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * A class for handling XSL transforms.
 * @author Bill Majurski
 */
public class Transform {
	static HashMap transforms = null;
	InputStream inputStream;
	OutputStream outputStream;
	static boolean disableCache = true;
	HashMap parameters = null;
	Transformer transformer;

	static TransformerFactory tFactory = null;

	public static String run( String systemId, String input
	) throws Exception {
		return Transform.run(systemId, input,
				null, null,
				null, null,
				null, null);
	}
	public static String run( String systemId, String input,
			String parm1Name, String parm1Value
	) throws Exception {
		return Transform.run(systemId, input,
				parm1Name, parm1Value,
				null, null,
				null, null);
	}
	public static String run( String systemId, String input,
			String parm1Name, String parm1Value,
			String parm2Name, String parm2Value
	) throws Exception {
		return Transform.run(systemId, input,
				parm1Name, parm1Value,
				parm2Name, parm2Value,
				null, null);
	}
	public static String run( String systemId, String input,
			String parm1Name, String parm1Value,
			String parm2Name, String parm2Value,
			String parm3Name, String parm3Value
	) throws Exception {
		Transform x = new Transform();
		x.setTransform(systemId);
		x.setInput(input);
		if (parm1Name != null)
			x.setParameter(parm1Name, parm1Value);
		if (parm2Name != null)
			x.setParameter(parm2Name, parm2Value);
		if (parm3Name != null)
			x.setParameter(parm3Name, parm3Value);
		x.run();
		return x.getOutputString();
	}

	/**
	 * Loads a transform file and returns that transform in a Transform object.
	 * @param f The transform file.
	 * @throws java.lang.Exception Thrown if there is a parsing error.
	 * @return A Tranform object representing the transform.
	 */
	static public Transform load(File f)
	throws Exception {
		Transform tform;
		String path="";
		if (!disableCache) {
			if (transforms == null)
				transforms = new HashMap();
			path = f.getPath();
			tform = (Transform) transforms.get(path);
			if (tform != null)
				return tform;
		}
		tform = new Transform();
		tform.setTransform(f);
		if (!disableCache)
			transforms.put(path, tform);
		return tform;
	}

	/** Creates a new instance of Transform */
	public Transform() {
		inputStream = null;
		outputStream = null;
	}

	/**
	 * Set the transform by providing a File object.
	 * @param f The File representing the transform file.
	 * @throws java.lang.Exception Thrown if there is a problem accessing the file.
	 */
	public void setTransform(File f) throws Exception {
		StreamSource streamSource;
		if (!f.exists())
			throw new Exception("File: " + f.getPath() + " does not exist");
		streamSource = new StreamSource(f);
		if (tFactory == null)
			tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(streamSource);
	}

	/**
	 * Set the transform by providing an XML String and  String representing a URI.
	 * @param s The XML String.
	 * @param systemId The URI location of a transform.
	 * @throws java.lang.Exception Thrown if there is a problem accessing the URI.
	 */
	public void setTransform(String s, String systemId) throws Exception {
		StreamSource streamSource;
		//StringBufferInputStream is = new StringBufferInputStream(s);
		StringReader sr = new StringReader(s);
		streamSource = new StreamSource(sr, systemId);
		if (tFactory == null)
			tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(streamSource);
	}

	/**
	 * Set the transform by providing a String representing a URI.
	 * @param systemId The URI location of a transform.
	 * @throws java.lang.Exception Thrown if there is a problem accessing the URI.
	 */
	public void setTransform(String systemId) throws TransformerConfigurationException {
		System.out.println("Transform:setTransform: " + systemId);
		StreamSource streamSource;
		streamSource = new StreamSource(systemId);
		if (tFactory == null)
			tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(streamSource);
	}

	/**
	 * Adds parameter name/value pairs which will be passed along to the transform
	 * handler.
	 * @param name The name of the parameter.
	 * @param value The value of the parameter.
	 */
	public void setParameter(String name, Object value) {
		if (value == null)
			value = "";
		if (parameters == null)
			parameters = new HashMap();
		parameters.put(name, value);
		System.out.println("Transform:setParameter " + name + "=>" + value);
	}

	/**
	 * Set the input as a String object.
	 * @param input The input String.
	 */
	public void setInput(String input) {
		int display = 80;
		System.out.println("Transform.setInput: " +
				input.substring(0,
						(input.length() < display) ? input.length() : display));
		inputStream = new StringBufferInputStream(input);
		outputStream = new ByteArrayOutputStream();
	}

	/**
	 * Run the transform.
	 * @throws java.lang.Exception Throws an exception if there is a problem using the input or output
	 * sources.
	 */
	public void run() throws XdsInternalException {
		transformer.clearParameters();
		if (parameters != null) {
			System.out.println("Transform.java: Has parameters");
			for (Iterator it=parameters.keySet().iterator(); it.hasNext(); ) {
				String key = (String) it.next();
				Object value = parameters.get(key);
				System.out.println("key=" + key + " value=" + (String) value);
				transformer.setParameter(key, value);
			}
			System.out.println("parameters set");
		}
		StreamSource in;
		try {
			in = new StreamSource(inputStream);
		} catch (Exception e) {
			throw new XdsInternalException("XSL Transform: error creating input stream source: " + e.getMessage());
		}
		StreamResult out;
		try {
			out = new StreamResult(outputStream);
		} catch (Exception e) {
			throw new XdsInternalException("XSL Transform: error creating ouput stream source: " + e.getMessage());
		}
		try {
			transformer.transform(in, out);
		} catch (Exception e) {
			throw new XdsInternalException("XSL Transform: error running transform: " + e.getMessage());
		}
	}

	/**
	 * Get the results of the transform.
	 * @return A String representing the output of the transform.
	 */
	public String getOutputString() {
		return outputStream.toString();
	}

}
