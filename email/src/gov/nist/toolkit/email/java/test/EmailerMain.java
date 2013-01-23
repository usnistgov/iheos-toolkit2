package gov.nist.toolkit.email.java.test;

import gov.nist.toolkit.email.Emailer;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.TkPropsServer;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class EmailerMain {

	public static void main(String[] args) {
		
		if (args.length != 4) {
			usage();
			System.exit(-1)	;
		}

		String propFilePath = args[0];
		String from = args[1];
		String password = args[2];
		String to = args[3];
		
		File propFile = new File(propFilePath);
		if (!propFile.exists()) {
			System.out.println("Properties file " + propFilePath + " does not exist.");
			System.exit(-1);
		}
		
		TkPropsServer props = null;
		try {
			props = TkLoader.LOAD(propFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		props.set("from", from);
		props.set("smtp.user", from);
		props.set("smtp.password", password);

		Emailer em = new Emailer(props);
		try {
			em.sendEmail2(to, "Test Message", "This is a test");
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	static void usage() {
		System.out.println("Args are:  propertiesfile fromAddr password toAddr");
	}
}
