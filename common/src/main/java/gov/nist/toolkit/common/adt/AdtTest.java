package gov.nist.toolkit.common.adt;

import java.io.IOException;

public class AdtTest {

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Usage: adttest <sample message> <server> <port>");
			System.exit(-1);
		}
		
		String messageFile = args[0];
		String server = args[1];
		String portStr = args[2];
		int port = 8087;
		
		try {
			port = Integer.parseInt(portStr);
		} catch (Exception e) {
			System.err.println("Port: " + portStr + " must be an integer");
			System.exit(-1);	
		}
		
		String pid = "123^^^&1.2.3&ISO";
		
		new AdtSender(messageFile, server, port).send(pid);
	}

}
