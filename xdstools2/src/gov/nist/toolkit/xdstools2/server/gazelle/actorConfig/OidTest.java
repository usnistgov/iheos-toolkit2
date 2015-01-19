package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class OidTest {
	File actorsDir = new File("/Users/bmajur/tmp/NA2015/actors/test");

	@Test
	public void getOidTest() throws IOException {
		OidConfigs oConfigs = new OidConfigs();
		new CSVParser(new File(actorsDir + File.separator + "listOID.csv"), new OidEntryFactory()).parse(oConfigs);

		String repUid = oConfigs.getRepUid("EHR_ECW_1");
		assertFalse(repUid.equals(""));
	}
}
