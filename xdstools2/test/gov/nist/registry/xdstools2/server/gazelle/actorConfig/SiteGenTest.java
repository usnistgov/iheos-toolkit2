package gov.nist.registry.xdstools2.server.gazelle.actorConfig;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.registry.toolkit.actorTransaction.shared.ATFactory;
import gov.nist.registry.toolkit.siteManagement.shared.Site;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SiteGenTest {
	String actorsDir="/Users/bill/tmp/toolkit/actors";
	String header = "\"Configuration Type\", \"Company\",  \"System\" , \"Host\" , \"Actor\" , \"is secured\", \"is approved\" , \"comment\" , \"url\", \"port\" ,\"port proxy\" ,\"port secured\" , \"assigningAuthority\", \"ws-type\"";
	
//	@Test
//	public void testRegistry() {
//		GazelleEntry gazelleEntry = new GazelleEntry("\"Webservice\",\"Ekstrem\",\"OTHER_Ekstrem\",\"ekstrem2\",\"DOC_REGISTRY\",\"true\",\"true\",\"x\",\"XdsService/XdsRegistry\",\"x\",\"29941\",\"2500\",\"x\",\"ITI-42:Register.b:r.b\"");
//		
//		OidConfigs oConfigs = new OidConfigs();
//		try {
//			new CSVParser(new File(actorsDir + File.separator + "oidSummary.csv"), oConfigs, new OidEntryFactory());
//		} catch (IOException e) {
//			fail(e.getMessage());
//		}
//
//		
//		GazelleConfigs gConfigs = new GazelleConfigs();
//		new CSVParser(header + "\n" + gazelleEntry, gConfigs, new GazelleEntryFactory());
//
//		ConfigToXml configToXml = new ConfigToXml(gConfigs, oConfigs, null);
//		try {
//			configToXml.run();
//		} catch (IOException e) {
//			fail(e.getMessage());
//		}
//
//		Site site = configToXml.lastSite;
//		
//		assertTrue(site.size() == 1);
//		
//		assertTrue("OTHER_Ekstrem".equals(site.getName()));
//		assertTrue(site.transactions.transactions.get(0).getTransactionType() == ATFactory.TransactionType.REGISTER);
//	}
	
	@Test
	public void testRepository() {
		GazelleEntry gazelleEntry = new GazelleEntry("\"Webservice\",\"Ekstrem\",\"OTHER_Ekstrem\",\"ekstrem2\",\"DOC_REPOSITORY\",\"true\",\"true\",\"x\",\"XdsService/XdsRepository\",\"x\",\"29938\",\"2501\",\"x\",\"ITI-41:Provide and Register.b:pr.b\"");
		
		OidConfigs oConfigs = new OidConfigs();
		try {
			new CSVParser(new File(actorsDir + File.separator + "oidSummary.csv"), oConfigs, new OidEntryFactory());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		
		GazelleConfigs gConfigs = new GazelleConfigs();
		new CSVParser(header + "\n" + gazelleEntry, gConfigs, new GazelleEntryFactory());

		ConfigToXml configToXml = new ConfigToXml(gConfigs, oConfigs, null);
		try {
			configToXml.run();
		} catch (IOException e) {
			fail(e.getMessage());
		}

		Site site = configToXml.lastSite;
		
		assertTrue(site.size() == 1);
		
		assertTrue("OTHER_Ekstrem".equals(site.getName()));
		assertTrue(site.transactions().transactions.get(0).getTransactionType() == ATFactory.TransactionType.PROVIDE_AND_REGISTER);
	}
	
}
