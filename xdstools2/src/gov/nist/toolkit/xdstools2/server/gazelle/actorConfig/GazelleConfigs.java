package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

public class GazelleConfigs extends CSVTable {

	public String getSystem(int entry) { return ((GazelleEntry)get(entry)).getSystem(); }
	public String getHost(int entry) { return ((GazelleEntry)get(entry)).getHost(); }
	public String getActor(int entry) { return ((GazelleEntry)get(entry)).getActor(); }
	public boolean getIsSecure(int entry) { return "true".equals(((GazelleEntry)get(entry)).getIsSecure()); }
	public boolean getIsApproved(int entry) { return "true".equals(((GazelleEntry)get(entry)).getIsApproved()); }
	public String getURL(int entry) { return ((GazelleEntry)get(entry)).getURL(); }
	public String getPort(int entry) { return ((GazelleEntry)get(entry)).getPort(); }
	public String getPortSecured(int entry) { return ((GazelleEntry)get(entry)).getPortSecured(); }
	public String getDetail(int entry) { return ((GazelleEntry)get(entry)).getDetail(); }

	public GazelleEntry get(int i) {
		return (GazelleEntry) super.get(i);
	}
}
