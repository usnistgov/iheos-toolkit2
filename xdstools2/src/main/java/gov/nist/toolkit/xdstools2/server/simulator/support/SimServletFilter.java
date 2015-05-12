package gov.nist.toolkit.xdstools2.server.simulator.support;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SimServletFilter implements Filter {
	static Logger logger = Logger.getLogger(SimServletFilter.class);

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		SimServletResponseWrapper wrapper = new SimServletResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request,wrapper);
		
		logger.debug("in doFilter");
		
		SimDb db = (SimDb) request.getAttribute("SimDb");
		if (db == null) {
			logger.error("SimServletFilter - request.getAttribute(\"SimDb\") failed");
			return;
		}
		
		Map<String, String> hdrs = wrapper.headers;
		String contentType = wrapper.contentType;
		
		HttpMessage hmsg = new HttpMessage();
		hmsg.setHeaderMap(hdrs);
		String messageHeader;
		try {
			messageHeader = hmsg.asMessage();
		} catch (HttpParseException e) {
			Io.stringToFile(db.getResponseHdrFile(), ExceptionUtil.exception_details(e));
			return;
		}
		
		if (contentType != null) {
			if (messageHeader == null || messageHeader.equals(""))
				messageHeader = "Content-Type: " + contentType + "\r\n\r\n";
			else 
				messageHeader = messageHeader + "Content-Type: " + contentType + "\r\n\r\n";
		}
		
		Io.stringToFile(db.getResponseHdrFile(), messageHeader);
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
