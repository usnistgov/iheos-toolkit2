package gov.nist.toolkit.directsim;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import org.apache.log4j.Logger;

/**
 * Title:        Sample Server
 * Description:  This utility will accept input from a socket, posting back to the socket before closing the link.
 * It is intended as a template for coders to base servers on. Please report bugs to brad at kieser.net
 * Copyright:    Copyright (c) 2002
 * Company:      Kieser.net
 * @author B. Kieser
 * @version 1.0
 */

public class Listener {

	private static int port=4444, maxConnections=0;
	static String externalCache;
	static String pathToPrivateKey;
	static String codesXmlUrl;

	static Logger logger = Logger.getLogger(Listener.class);

	static {
		MessageValidatorFactory fact = new MessageValidatorFactory();
	}


	// Listen for incoming connections and handle them
	public static void main(String[] args) {

		if (args.length != 4) {
			System.out.println("Usage: Listener <port> <toolkit-external-cache> <keystore holding private key> <codes.xml file>");
			System.exit(-1);
		}
		try {
			port = Integer.parseInt(args[0]);
			externalCache = args[1];
			pathToPrivateKey = args[2];
			codesXmlUrl = args[3];
		} catch (Exception e) {
			System.out.println("Usage: Listener <port> <toolkit-external-cache> <keystore holding private key> <codes.xml file>: " + e.getMessage());
			System.exit(-1);
		}

		System.setProperty("XDSCodesFile", codesXmlUrl);

		Installation.installation().externalCache(new File(externalCache));

		int i=0;

		try{
			ServerSocket listener = new ServerSocket(port);
			Socket server;

			while((i++ < maxConnections) || (maxConnections == 0)){
				server = listener.accept();
				DoComms conn_c= new DoComms(server, new File(externalCache), pathToPrivateKey);
				Thread t = new Thread(conn_c);
				t.start();
			}
		} 
		catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
		catch (Exception ioe) {
			System.out.println("Exception on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}

}
