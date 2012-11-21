package gov.nist.toolkit.testengine;

import gov.nist.toolkit.common.adt.AdtSender;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PatientIdFeedSender {
	String host;
	int port;

	public PatientIdFeedSender(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void send(String pid) throws IOException {
		InputStream templateInputStream = getClass().getResourceAsStream("V2FeedMessageWithWrapper.txt");

		new AdtSender(templateInputStream, host, port).send(pid);

	}

	static public void main(String[] args) {
		PatientIdFeedSender sender = new PatientIdFeedSender("nistblue", 8087); 
		
		try {
//			String fileContents = Io.stringFromFile(new File("/Users/bill/Downloads/Connectathon-demographics-red.csv"));
//			String fileContents = Io.stringFromFile(new File("/Users/bill/Downloads/Connectathon-demographics-green.csv"));
			String fileContents = Io.stringFromFile(new File("/Users/bill/Downloads/Connectathon-demographics-blue.csv"));
			String[] lines = fileContents.split("\r");
			
			for (String line : lines) {
				String[] parts = line.split(",");
				System.out.println(parts[1] + "^^^" + parts[3]);
				sender.send(parts[1] + "^^^" + parts[3]);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
