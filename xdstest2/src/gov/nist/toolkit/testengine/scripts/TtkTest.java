package gov.nist.toolkit.testengine.scripts;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.testengine.XdsTest;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class TtkTest {
//	String toolkit = "/Users/bill/v3workspace/toolkit2/xdstools2/war";
	String toolkit = "/Users/bmajur/workspace/toolkit/xdstools2/war";
	String[] prb = {
			"-Dsite=pub", 
			"-DXDSTOOLKIT=" + toolkit + "/toolkitx", 
			"-DXDSTESTLOGDIR=" + toolkit + "/toolkitx/logs", 
			"-DXDSTESTKIT=" + toolkit + "/toolkitx/testkit",
			"-tc", "Pr.b", 
			"-err"};

	String[] rb = {
			"-Dsite=test", 
			"-DXDSTOOLKIT=" + toolkit + "/toolkitx", 
			"-DXDSTESTLOGDIR=" + toolkit + "/toolkitx/logs", 
			"-DXDSTESTKIT=" + toolkit + "/toolkitx/testkit",
			"-tc", "R.b", 
			"-err"};

	String[] single = {
			"-Dsite=test", 
			"-DXDSTOOLKIT=" + toolkit + "/toolkitx", 
			"-DXDSTESTLOGDIR=" + toolkit + "/toolkitx/logs", 
			"-DXDSTESTKIT=" + toolkit + "/toolkitx/testkit",
			"-t", "11998", 
			"-err"};

	@Before
	public void init() {
		System.setProperty("XDSSchemaDir", "file://" + toolkit + "/toolkitx/schema");
		System.setProperty("XDSCodesFile", toolkit + "/toolkitx/codes/codes.xml");
		Installation.installation().externalCache(new File(toolkit + "/toolkitx/logs"));
	}
	
//	@Test
	public void prbTest() throws Exception {
		// manually copy tc files
		assertTrue(XdsTest.main(prb));
	}
	
//	@Test
	public void rbTest() throws Exception {
		// manually copy tc files
		assertTrue(XdsTest.main(rb));
	}
	
	@Test
	public void singleTest() throws Exception {
		// manually copy tc files
		assertTrue(XdsTest.main(single));
	}
	
	/**
	 * R.b tests
	 * 11966 fails - simulator does not support XDSUnknownPatientId 
	 * 12379/no_support - extra metadata is supported - error ok
	 * 11998 fails because we cannot feed it a second patient id
	 * 12002 fails because we cannot feed it a second patient id
	 */
}
