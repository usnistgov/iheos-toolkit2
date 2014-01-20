package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class OidTest {
	File actorsDir = new File("/Users/bmajur/tmp/NA2014/actors/test");

	@Test
	public void getOidTest() throws IOException {
		OidConfigs oConfigs = new OidConfigs();
		new CSVParser(new File(actorsDir + File.separator + "listOfOIDsForSession.csv"), new OidEntryFactory()).parse(oConfigs);

		String repUid = oConfigs.getRepUid("EHR_ECW_1");
		assertFalse(repUid.equals(""));
	}
}
