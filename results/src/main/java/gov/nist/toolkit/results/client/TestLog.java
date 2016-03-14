package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Map;

public class TestLog implements IsSerializable {

	public String stepName;
	public String inHeader;
	public String outHeader;
	public String inputMetadata;
	public String result;
	public boolean status;
	public String endpoint;
	
	public String log;
	public String errors;
    public Map<String, String> assignedIds;
    public Map<String, String> assignedUids;
}
