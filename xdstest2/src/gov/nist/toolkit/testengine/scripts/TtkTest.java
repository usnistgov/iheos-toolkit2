package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.testengine.XdsTest;

import java.io.File;

import org.junit.Test;

public class TtkTest {
	String toolkit = "/Users/bill/v3workspace/toolkit2/xdstools2/war";
//	String toolkit = "/Users/bill/tomcat1/webapps/xdstools2";
	String[] a = {
			"-Dsite=test", 
			"-DXDSTOOLKIT=" + toolkit + "/toolkitx", 
			"-DXDSTESTLOGDIR=" + toolkit + "/toolkitx/logs", 
			"-DXDSTESTKIT=" + toolkit + "/toolkitx/testkit",
			"-tc", "PR.b", 
			"-err"};

	String[] b = {
			"-Dsite=test", 
			"-DXDSTOOLKIT=" + toolkit + "/toolkitx", 
			"-DXDSTESTLOGDIR=" + toolkit + "/toolkitx/logs", 
			"-DXDSTESTKIT=" + toolkit + "/toolkitx/testkit",
			"-t", "12002", 
			"-err"};

	@Test
	public void aTest() throws Exception {
		System.setProperty("XDSSchemaDir", "file://" + toolkit + "/toolkitx/schema");
		System.setProperty("XDSCodesFile", toolkit + "/toolkitx/codes/codes.xml");
		Installation.installation().externalCache(new File(toolkit + "/toolkitx/logs"));
		// manually copy tc files
		XdsTest.main(a);
	}
	
	/**
	 * 11966 fails - simulator does not support XDSUnknownPatientId 
	 * 12379/no_support - extra metadata is supported - error ok
	 * 11998 fails because we cannot feed it a second patient id
	 * 12002 fails because we cannot feed it a second patient id
	 */
}
