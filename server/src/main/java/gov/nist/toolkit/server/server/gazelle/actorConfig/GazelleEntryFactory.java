package gov.nist.toolkit.server.server.gazelle.actorConfig;

public class GazelleEntryFactory implements IEntryFactory {

	public GazelleEntry mkEntry(String line) {
		return new GazelleEntry(line);
	}

}
