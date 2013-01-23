package gov.nist.toolkit.valccda;


import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpParser;
import org.apache.log4j.Logger;

/*
 * This service is being built on top of the apache commons project.
 * Some key web pages are:
 *  
 *  * http://www.codejava.net/java-ee/servlet/code-example-file-upload-servlet-with-apache-common-file-api
 *  
 *  This example forwards the content to a JSP for processing, we will call the class CcdaValidator
 *  instead.
 */

public class CcdaValidationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(CcdaValidationServlet.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, byte[]> contentMap = null;
		String validationType = request.getParameter("CCDA_Validation_Type");
		if (!ServletFileUpload.isMultipartContent(request))
			return;
		

	}
}
