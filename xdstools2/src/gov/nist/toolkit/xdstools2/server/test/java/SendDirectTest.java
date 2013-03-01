package gov.nist.toolkit.xdstools2.server.test.java;

import static org.junit.Assert.*;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.directsim.DirectServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SendDirectTest {
	static final String tk = File.separator + "Users" + File.separator + "bill" + File.separator  + "Documents" + File.separator + "sf" + File.separator  + "toolkit";

	@Test
	public void Test1() {
		String warHome = tk + File.separator + "xdstools2" + File.separator + "war";
		Session session = new Session(new File(warHome), SiteServiceManager.getSiteServiceManager());
		try {
			session.setLastUpload(
					"filename",
					Io.bytesFromFile(new File(
							tk + File.separator + "xdstools2" + File.separator + "cert" + File.separator + "hit-testing.nist.gov.p12")), // signing
							"", // password
							"filename",
							Io.bytesFromFile(new File(
									tk + File.separator + "xdstools2" + File.separator + "cert" + File.separator + "hit-testing.nist.gov.der")), //  encryption
									"" // password
					);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Map<String, String> parms = new HashMap<String, String>();
		//		parms.put("$direct_server_name$", "hit-testing.nist.gov");
		parms.put("$direct_server_name$", "localhost");
		parms.put("$direct_from_address$", "bmajur@gmail.com");
		parms.put("$direct_to_address$", "direct-clinical-summary@hit-testing.nist.gov");
		parms.put("$ccda_attachment_file$", "CCDA_CCD_Ambulatory");

		try {
			List<Result> results = new DirectServiceManager(session).directSend(parms);
			Assert.assertTrue(results.get(0).passed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
