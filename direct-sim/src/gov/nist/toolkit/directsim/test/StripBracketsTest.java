package gov.nist.toolkit.directsim.test;

import static org.junit.Assert.assertEquals;
import gov.nist.toolkit.directsim.DoComms;

import org.junit.Test;

public class StripBracketsTest {
	DoComms dc = new DoComms();
	
	@Test
	public void noWork() {
		assertEquals("julien@direct.microphr.com", dc.stripBrackets("  julien@direct.microphr.com"));
	}
	
	@Test
	public void bracketsOnly() {
		assertEquals("boo@booboo.com", dc.stripBrackets("<boo@booboo.com>"));
	}

	@Test
	public void withName() {
		assertEquals("boo@booboo.com", dc.stripBrackets("Boo Boo <boo@booboo.com>"));
	}
}
