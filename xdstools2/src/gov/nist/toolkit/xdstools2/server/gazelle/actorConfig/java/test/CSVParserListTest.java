package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.CSVEntry;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.CSVParser;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.ConfigToXml;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.GazelleConfigs;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.GazelleEntryFactory;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.OidConfigs;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.OidEntry;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.OidEntryFactory;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CSVParserListTest {
	static String EC = "/Users/bill/tmp/euro2013";
	static String oidSummary = "listOfOIDsForSession.csv";
	static String all = "WebServiceConfiguration.csv";
	static String sep = File.separator;

	@Test
	public void actorsDirExistsTest() {
		assertTrue("Actors dir " + getActorsDir() + " does not exist", new File(getActorsDir()).exists()) ;
	}
	
	@Test
	public void oidSummaryExistsTest() {
		assertTrue("Actors dir " + getActorsDir() + sep + oidSummary + " does not exist", new File(getActorsDir() + sep + oidSummary).exists()) ;
	}
	
	@Test
	public void oidSummaryParseTest() {
		OidConfigs oconfigs = new OidConfigs();
		try {
			new CSVParser(new File(getActorsDir() + sep + oidSummary), oconfigs, new OidEntryFactory()).run();
		} catch (IOException e) {
			fail(ExceptionUtil.exception_details(e));
		}
	}

	@Test
	public void oidSummaryParse2Test() {
		String input = ",,,\n855,GATEWAY_AEGIS_DIL_0 (1.0),repositoryUniqueID OID,1.3.6.1.4.1.21367.2011.2.3.105";
		OidConfigs oconfigs = new OidConfigs();
		CSVParser cp = new CSVParser(input, oconfigs, new OidEntryFactory());
		cp.run();
		assertEquals(1, cp.size());
		OidEntry ce = (OidEntry) cp.get(0);
		assertEquals("GATEWAY_AEGIS_DIL_0", ce.getSystem());
	}

	@Test
	public void actorConfigParseTest() {
		GazelleConfigs gconfigs = new GazelleConfigs();
		try {
			new CSVParser(new File(getActorsDir() + sep + all), gconfigs , new GazelleEntryFactory()).run();
		} catch (IOException e) {
			fail(ExceptionUtil.exception_details(e));
		}
	}
	
	@Test
	public void saveTest() {
		OidConfigs oconfigs = new OidConfigs();
		try {
			new CSVParser(new File(getActorsDir() + sep + oidSummary), oconfigs, new OidEntryFactory()).run();
		} catch (IOException e) {
			fail(ExceptionUtil.exception_details(e));
		}
		
		oconfigs.printAll();

		GazelleConfigs gconfigs = new GazelleConfigs();
		try {
			new CSVParser(new File(getActorsDir() + sep + all), gconfigs , new GazelleEntryFactory()).run();
		} catch (IOException e) {
			fail(ExceptionUtil.exception_details(e));
		}

		try {
			new ConfigToXml(gconfigs, oconfigs, new File(getActorsDir())).run();
		} catch (IOException e) {
			fail(ExceptionUtil.exception_details(e));
		}

	}
	
	@Test
	public void para1Test() {
		String x = "abc def -  jjj";
		assertEquals(x, new CSVEntry("").rmParenthetical(x));
	}
	
	@Test
	public void para2Test() {
		String x = "abc def -  jjj";
		assertEquals(x, new CSVEntry("").rmParenthetical(x + "(1.2.3)"));
	}
	
	String getActorsDir() {
		return EC + sep + "actors";
	}

}
