package gov.nist.toolkit.directsim;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.directsim.client.DirectRegistrationData;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.services.SendDirect;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DirectServiceManager  extends CommonServiceManager {
	Session session = null;
	static Logger logger = Logger.getLogger(DirectServiceManager.class);
		
	public DirectServiceManager(Session session) {
		this.session = session;
	}

	public DirectServiceManager() {
	}

	public void directRegistration(Session session, DirectRegistrationData direct) throws Exception {
		logger.debug(session.id() + ": " + 
				"Direct Registration of: " + direct.directAddr);
		
		try {
			DirectRegistrationManager dum = new DirectRegistrationManager(Installation.installation().externalCache());
			dum.save(DirectRegistration.toServer(direct));
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			throw new Exception("Save of Direct User Registration failed: " + e.getMessage());
		}
	}
	
	public List<Result> directSend(Map<String, String> parms) throws Exception {
		String messageName = parms.get("$ccda_attachment_file$") + ".";  // . makes sure we are only searching for extension
		File toolkitxFile = session.getToolkitFile();
		String user = session.getMesaSessionName();
		
		if (user == null || user.equals("")) {
			// without a defined user, the logs cannot be updated 
			throw new Exception("User (TestSession) must be selected to allow logging");
		}

		File dMsgDir = new File(toolkitxFile + File.separator + "testkit" + File.separator + 
				"direct-messages");
		FilenameFilter filter = new MessageFilter(messageName);
		String[] ls = dMsgDir.list(filter);
		if (ls == null || ls.length != 1) {
			return buildResultList("Message name " + messageName + " not found");
		}
		String messagePath = dMsgDir + File.separator + ls[0];
		parms.put("$messagePath$", messagePath);
		
		return new SendDirect(session, parms, session.getlastUpload(), session.getlastUpload2(), session.getPassword1(), session.getPassword2()).run();
	}
	
	class MessageFilter implements FilenameFilter {
		String messageName;
		MessageFilter(String messageName) {
			this.messageName = messageName;
		}
	    public boolean accept(File dir, String name) {
	        return (name.startsWith(messageName));
	    }
	}
	
	public String toolkitPubCert() {
		return "NIST Cert";
	}
	
	public Map<String, String> validationEndpoints() {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("val1", "val1@certbody.com");
		map.put("val2", "val2@certbody.com");
		map.put("val3", "val3@certbody.com");
		return map;
	}
	
}
