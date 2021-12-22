package gov.nist.toolkit.fhir.simulators.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.util.logging.Logger;

public class SimServletResponseWrapper extends HttpServletResponseWrapper {
	HttpServletResponse response;
	Map<String, String> headers = new HashMap<String, String>();
	String contentType = null;
	static Logger logger = Logger.getLogger(SimServletResponseWrapper.class.getName());

	
	public SimServletResponseWrapper(HttpServletResponse response) {
		super(response);
		this.response = response;
	}
	
	public void setHeader(String name, String value) {
		logger.finest(name + " => " + value);
		headers.put(name, value);
		response.setHeader(name, value);
	}
	
	public void addHeader(String name, String value) {
		logger.finest(name + " => " + value);
		headers.put(name, value);
		response.addHeader(name, value);
	}

	public void setContentType(String type) {
		logger.info("ContentType => " + type);
		response.setContentType(type);
		contentType = type;
	}

}
