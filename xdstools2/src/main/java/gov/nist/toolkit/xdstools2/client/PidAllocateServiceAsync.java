package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PidAllocateServiceAsync {
	void getAssigningAuthorities(AsyncCallback<List<String>> callback);
	void getNewPatientId(String assigningAuthority, AsyncCallback<String> callback);

}