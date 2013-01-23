package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

public class GazelleEntry extends CSVEntry {

	static final int GazelleConfigSystem = 2;
	static final int GazelleConfigHost = 3;
	static final int GazelleConfigActor = 4;
	static final int GazelleConfigIsSecure = 5;
	static final int GazelleConfigIsApproved = 6;
	static final int GazelleConfigUrl = 8;
	static final int GazelleConfigPort = 12;
	static final int GazelleConfigPortSecured = 11;
	static final int GazelleConfigDetail = 10;

	public String getSystem() { return get(GazelleConfigSystem); }
	public String getHost() { return get(GazelleConfigHost); }
	public String getActor() { return get(GazelleConfigActor); }
	public boolean getIsSecure() { return "true".equals(get(GazelleConfigIsSecure).trim()); }
	public boolean getIsApproved() { return "true".equals(get(GazelleConfigIsApproved).trim()); }
	public String getURL() { return get(GazelleConfigUrl); }
	public String getPort() { return get(GazelleConfigPort); }
	public String getPortSecured() { return get(GazelleConfigPortSecured); }
	public String getDetail() { return get(GazelleConfigDetail); }
	
	public String getTransId() {
		String detailStr = getDetail();
		String[] parts = detailStr.split(":");
		if (parts.length == 0)
			return null;
		return parts[0];
	}

	public boolean isAsync() {
		String detailStr = getDetail();
		String[] parts = detailStr.split(":");
		if (parts.length < 2)
			return false;
		return parts[1].startsWith("Async");
	}

	public GazelleEntry(String line) {
		super(line);
		
		System.out.println("System: " + getSystem());
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("[");
		buf.append(" System=").append(getSystem());
		buf.append(" Host=").append(getHost());
		buf.append(" Actor=").append(getActor());
		buf.append(" Trans=").append(getTransId());
		buf.append(" Secure=").append(getIsSecure());
		buf.append(" Approved=").append(getIsApproved());
		buf.append(" URL=").append(getURL());
		buf.append("]");
		
		return buf.toString();
	}
}
