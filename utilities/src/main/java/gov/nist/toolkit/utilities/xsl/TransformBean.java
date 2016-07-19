/*
 * transformBean.java
 *
 * Created on October 13, 2004, 9:43 AM
 */


package gov.nist.toolkit.utilities.xsl;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;

/**
 * JavaBean wrapper around Transform class.  Allows JavaBean access to Transform
 * functionality.
 * @author Bill Majurski
 */
public class TransformBean {

	Transform tform;

	/**
	 * Holds value of property xslt.
	 */
	private String xslt;

	/**
	 * Holds value of property input.
	 */
	private String input;

	/**
	 * Holds value of property systemId.
	 */
	private String systemId;

	/**
	 * Holds value of property xsltFile.
	 */
	private String xsltFile;

	/**
	 * Holds value of property paramName.
	 */
	private String paramName;

	/** Creates a new instance of transformBean */
	public TransformBean() {
		tform = new Transform();
	}

	/**
	 * Getter for property xslt.
	 * @return Value of property xslt.
	 */
	public String getXslt() {
		return this.xslt;
	}

	/**
	 * Setter for property xslt.
	 * @param xslt New value of property xslt.
	 * @throws java.lang.Exception Thrown if there is a problem accessing the XSLT file/location.
	 */
	public void setXslt(String xslt) throws Exception {
		this.xslt = xslt;
		tform.setTransform(xslt, systemId);
	}


	/**
	 * Setter for property input.
	 * @param input New value of property input.
	 */
	public void setInput(String input) {
		this.input = input;
		tform.setInput(input);
	}

	/**
	 * Getter for property output.
	 * @return Value of property output.
	 * @throws XdsInternalException Thrown if there is a problem accessing input or output locations.
	 */
	public String getOutput() throws XdsInternalException {
		tform.run();
		return tform.getOutputString();
	}

	/**
	 * For testing and demonstration purposes.
	 */
	public static void main(String[] argv) throws Exception {
		TransformBean tform = new TransformBean();
		tform.setSystemId("http://localhost:8084/xdsServices/xds/validation/validator.xsl");
		tform.setInput("<foo>a</foo>");
		System.out.println(tform.getOutput());
	}

	/**
	 * Getter for property systemId.
	 * @return Value of property systemId.
	 */
	public String getSystemId() {
		return this.systemId;
	}

	/**
	 * Setter for property systemId.
	 * @param systemId New value of property systemId.
	 * @throws TransformerConfigurationException Thrown if there is a problem accessing URI.
	 */
	public void setSystemId(String systemId) throws TransformerConfigurationException {
		tform.setTransform(systemId);
	}

	/**
	 * Getter for property xsltFile.
	 * @return Value of property xsltFile.
	 */
	public String getXsltFile() {
		return this.xsltFile;
	}

	/**
	 * Setter for property xsltFile.
	 * @param xsltFile New value of property xsltFile.
	 * @throws java.lang.Exception Thrown if there is a problem accessing XSLT file.
	 */
	public void setXsltFile(String xsltFile) throws Exception {
		this.xsltFile = xsltFile;
		tform.setTransform(new File(xsltFile));
	}

	public void setXsltFile(File xsltFile) throws Exception {
		this.xsltFile = xsltFile.getAbsolutePath();
		tform.setTransform(xsltFile);
	}

	/**
	 * Getter for property paramName.
	 * @return Value of property paramName.
	 */
	public String getParamName() {
		return this.paramName;
	}

	/**
	 * Setter for property paramName.
	 * @param paramName New value of property paramName.
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * Setter for property paramValue.
	 * @param paramValue New value of property paramValue.
	 */
	public void setParamValue(Object paramValue) {
		tform.setParameter(paramName, paramValue);
	}
	
	public void setParam(String paramName, Object paramValue) {
		tform.setParameter(paramName, paramValue);
	}

}
