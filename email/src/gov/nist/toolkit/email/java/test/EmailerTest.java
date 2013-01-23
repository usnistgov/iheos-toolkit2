package gov.nist.toolkit.email.java.test;

import static org.junit.Assert.fail;
import gov.nist.toolkit.email.Emailer;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.TkPropsServer;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

public class EmailerTest {
	String dir="/Users/bill/Documents/sfhg/workspace/direct_dev/email/src/gov/nist/toolkit/email/java/test/";
	TkPropsServer props;
	String to = "bmajur@gmail.com";

	@Before
	public void init() throws Exception
	{
		// Log4J junit configuration.
		BasicConfigurator.configure();
	}

	public void loadProperties() {
		try {
			props = TkLoader.LOAD(new File(dir + "gmail2.txt"));
			System.out.println(props.toString());
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void send() {
		loadProperties();
		Emailer em = new Emailer(props);
		try {
			em.sendEmail2(to, "TESTING", "This is a test");
		} catch (AddressException e) {
			e.printStackTrace();
			fail();
		} catch (MessagingException e) {
			e.printStackTrace();
			fail();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}
}
