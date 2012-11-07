package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.session.server.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendDirect extends CommonServiceManager {
	Session session;
	Map<String, String> params;
	byte[] signingCert;   // need password
	byte[] encryptionCert;
	String signingPassword;
	String password2;
	
	public SendDirect(Session session, Map<String, String> params, byte[] cert, byte[] cert2, String password1, String password2)  {
		this.session = session;
		this.params = params;
		this.signingCert = cert;
		this.encryptionCert = cert2;
		this.signingPassword = password1;
		this.password2 = password2;
	}

	public List<Result> run() {
		try {
//			session.transactionSettings.assignPatientId = false;
			List<String> sections = null;

			String[] areas = new String[1];
			areas[0] = "utilities";
			
			Map<String, Object> params2 = new HashMap<String, Object>();
			params2.put("signingCert", signingCert);
			params2.put("encryptionCert", encryptionCert);
			
			params.put("signingCertPassword", signingPassword);

			escapeWindowsBackslashes(params);
			
			Result r = session.xdsTestServiceManager().xdstest("DirectSendTemplate", sections, params, params2, areas, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}
	
	void escapeWindowsBackslashes(Map<String, String> parms) {
		for (String key : parms.keySet()) {
			String value = parms.get(key);
			value = value.replaceAll("\\\\", "\\\\\\\\");
			parms.put(key, value);
		}
	}
	
	static public void main(String[] args) {
		String value = "ab\\cd";
		String value2 = value.replaceAll("\\\\", "\\\\\\\\");
		System.out.println("new value is " + value2);
	}

}
