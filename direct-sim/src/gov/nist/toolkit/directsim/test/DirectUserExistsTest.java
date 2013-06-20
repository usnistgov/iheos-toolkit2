package gov.nist.toolkit.directsim.test;

import gov.nist.toolkit.directsim.DirectUserManager;
import gov.nist.toolkit.directsim.DoComms;
import gov.nist.toolkit.installation.Installation;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class DirectUserExistsTest extends TestCase {
	File externalCache = new File("/Users/bmajur/Documents/Servers/th/e_c");
	DoComms dc = new DoComms(externalCache);
	
	@Override
	protected void setUp() {
		Installation.installation().externalCache(externalCache);
		System.out.println("setUp");
	}

	@Test
	public void test() {
		String directFrom = "bill@direct.microphr.com";
		DirectUserManager dum = new DirectUserManager();
		if (!dum.directUserExists(directFrom)) {
			fail();
		}

	}
}
