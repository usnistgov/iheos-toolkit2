package gov.nist.toolkit.server.server.gazelle.actorConfig;

public class OidEntryFactory implements IEntryFactory {

	public OidEntry mkEntry(String line) {
		return new OidEntry(line);
	}
	
}
