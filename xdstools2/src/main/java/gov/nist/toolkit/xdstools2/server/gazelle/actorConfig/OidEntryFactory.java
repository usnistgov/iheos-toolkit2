package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

public class OidEntryFactory implements IEntryFactory {

	public OidEntry mkEntry(String line) {
		return new OidEntry(line);
	}
	
}
