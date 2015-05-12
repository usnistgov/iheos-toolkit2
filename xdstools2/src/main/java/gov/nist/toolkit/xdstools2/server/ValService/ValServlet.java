package gov.nist.toolkit.xdstools2.server.ValService;

import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simulators.support.ValidateMessageService;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.MessageValidatorDisplay;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.HtmlValFormatter;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ValServlet extends HttpServlet {
	static Logger logger = Logger.getLogger(ValServlet.class);

	private static final long serialVersionUID = 1L;

	ServletConfig config;
	File warHome;
	Session session;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;
		
		warHome = new File(config.getServletContext().getRealPath("/"));
		session = new Session(warHome, SiteServiceManager.getSiteServiceManager());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String warHome = getServletContext().getRealPath("/");
//		System.out.print("warHome[ValServlet]: " + warHome + "\n");
//		if (dbPath == null) {
//			tk = new ToolkitServiceImpl();
//			tk.setWarHome(warHome);
//			// force use of THIS reference to the servlet context
//			// since gwt-servlet may not be loaded yet
//			tk.propertyServiceManager.loadPropertyManager();
//			dbPath = tk.propertyServiceManager.getMessageDatabaseFile().toString();
//		}
		String uri  = request.getRequestURI().toLowerCase();
		logger.debug("uri is " + uri);
		ServletContext servletContext = config.getServletContext(); 
		
		String[] uriParts = uri.split("\\/");
		
		if (uriParts.length == 0) {
			invalidURI(response);
			return;
		}
		
		String verb = uriParts[0];
		if (verb.equals("xdr")) {
			if (uriParts.length != 4) {
				invalidURI(response);
				return;
			}
			try {
				doXDR(request, response, uriParts);
			} catch (XdsException e) {
				throw new ServletException(e);
			}
		} else if (verb.equals("xdm")) {
			doXDM(request, response, uriParts);
		} else {
			response.setStatus(400);
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println("Invalid verb");
			out.close();
		}
		
	}

	private void invalidURI(HttpServletResponse response) throws IOException {
		response.setStatus(400);
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("Invalid URI");
		out.close();
	}
	
	void doXDR(HttpServletRequest request, HttpServletResponse response, String[] uriParts) throws ServletException, IOException, XdsException {

		String msg = uriParts[1];
		String http = uriParts[2];
		String soap = uriParts[3];
		
		boolean isRequest = msg.equals("req");
		boolean hasHttp = http.equals("withhttp");
		boolean hasSoap = soap.equals("withsoap");
		
		ValidationContext vc = new ValidationContext();
		vc.hasHttp = hasHttp;
		vc.hasSoap = hasSoap;
		vc.isAsync = false;
		vc.isRequest = isRequest;
		vc.isXDR = true;
		
		byte[] msgBytes = Io.getBytesFromInputStream(request.getInputStream());
		
		runValidationService(response, vc, msgBytes);
		
	}

	void runValidationService(HttpServletResponse response,
			ValidationContext vc, byte[] msgBytes)
			throws IOException, XdsException {
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		ValidateMessageService vms = new ValidateMessageService(session, null);
		
		MessageValidationResults mvr = vms.runValidation(vc, null, msgBytes, null, gerb);
		
		HtmlValFormatter hvf = new HtmlValFormatter();
		
		MessageValidatorDisplay mvd = new MessageValidatorDisplay(hvf);
		mvd.displayResults(mvr);
		
		PrintWriter out = response.getWriter();

		out.print("<html><head><title>Validation Results</title></head><body><h1>Validation Results</h1>");
		
		out.print(hvf.toHtmlTemplate(mvr));
		
		out.print("</body></html>");
	}
	
	void doXDM(HttpServletRequest request, HttpServletResponse response, String[] uriParts) throws ServletException, IOException {
		ValidationContext vc = new ValidationContext();
		vc.isXDM = true;
		
		byte[] msgBytes = Io.getBytesFromInputStream(request.getInputStream());
		
		try {
			runValidationService(response, vc, msgBytes);
		} catch (XdsException e) {
			throw new ServletException(e);
		}
		
	}
	
	
}
