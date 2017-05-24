package gov.nist.toolkit.server.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.server.client.PidAllocateService;
import gov.nist.toolkit.valregmetadata.top.CodeValidation;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PidAllocateServiceImpl extends RemoteServiceServlet implements
		PidAllocateService {
	CodeValidation cv = null;

	public PidAllocateServiceImpl()   {
	}
	
	public List<String> getAssigningAuthorities() {
		try {
			cv = new CodeValidation();
		} catch (XdsInternalException e) {
			List<String> er = new ArrayList<String>();
			er.add(e.getMessage());
			return er;
		}
		return cv.getAssigningAuthorities();
	}

	public String getNewPatientId(String assigningAuthority) throws  Exception {
		return HttpClient.GET("http://localhost:9080/xdstools/pidallocate?rest=1&aa=" + assigningAuthority);
	}
	
}
