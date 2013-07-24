package gov.nist.toolkit.directsim.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import gov.nist.toolkit.directsim.DoComms;
import gov.nist.toolkit.valsupport.message.HtmlValFormatter;

import java.io.File;

import org.junit.Test;

public class GetContactAddrTest {
	File externalCache = new File("/Users/bmajur/Documents/Servers/th/e_c");
	DoComms dc = new DoComms(externalCache);
	
	@Test
	public void lookupBillTest() {
		String contactAddr = dc.getContactAddr(new HtmlValFormatter(), "bill@direct.microphr.com");
		assertFalse(contactAddr == null);
		assertEquals("bmajur@gmail.com", contactAddr);
	}

	@Test
	public void lookupJulienTest() {
		String contactAddr = dc.getContactAddr(new HtmlValFormatter(), "  julien@direct.microphr.com");
		assertFalse(contactAddr == null);
		assertEquals("julien.perugini@gmail.com", contactAddr);
	}
	
	@Test
	public void lookupTest2() {
		String contactAddr = dc.getContactAddr(new HtmlValFormatter(), dc.stripBrackets("Bill <bill@direct.microphr.com>"));
		assertFalse(contactAddr == null);
	}
}
