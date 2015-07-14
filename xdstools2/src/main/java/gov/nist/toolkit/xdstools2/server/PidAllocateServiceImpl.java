package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.valregmetadata.field.CodeValidation;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import gov.nist.toolkit.xdstools2.client.PidAllocateService;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
