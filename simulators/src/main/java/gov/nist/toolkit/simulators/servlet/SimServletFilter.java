package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.support.Callback;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

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
        SimulatorConfig config = (SimulatorConfig) request.getAttribute("SimulatorConfig");
        if (config == null) {
            logger.error("SimServletFilter - request.getAttribute(\"SimulatorConfig\") failed");
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

        SimulatorConfigElement callbackElement = config.get(SimulatorConfig.REST_CALLBACK_URI);
        if (callbackElement != null) {
            String callbackURI = callbackElement.asString();
            if (callbackURI != null && !callbackURI.equals("")) {
                new Callback().callback(db, config, callbackURI);
            }
        }

    }

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
