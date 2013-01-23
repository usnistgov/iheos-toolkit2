package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

public class OidEntry  extends CSVEntry {

	static final int System = 1;
	static final int Type = 2;
	static final int Value = 3;
	
	public String getSystem() { return get(System); }
	public boolean isSourceId() { return get(Type).startsWith("sourceID"); }
	public boolean isRepositoryUniqueId() { return get(Type).startsWith("repositoryUniqueID"); }
	public boolean isHomeCommunityId() { return get(Type).startsWith("homeCommunityID"); }
	public String getValue() { return get(Value); }

	public OidEntry(String line) {
		super(line);
	}
}
