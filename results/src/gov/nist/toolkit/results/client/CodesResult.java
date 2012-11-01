package gov.nist.toolkit.results.client;



import com.google.gwt.user.client.rpc.IsSerializable;

public class CodesResult implements IsSerializable {
	public Result result;
	public CodesConfiguration codesConfiguration;
	
	public CodesResult() {} // For GWT
}
