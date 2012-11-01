package gov.nist.toolkit.directlistener;


import gov.nist.toolkit.directsim.DirectUserManager;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParser;
import gov.nist.toolkit.utilities.io.Io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Title:        Sample Server
 * Description:  This utility will accept input from a socket, posting back to the socket before closing the link.
 * It is intended as a template for coders to base servers on. Please report bugs to brad at kieser.net
 * Copyright:    Copyright (c) 2002
 * Company:      Kieser.net
 * @author B. Kieser
 * @version 1.0
 */


/*
 * OBSOLETE - REAL CODE MOVED TO GOV.NIST.TOOLKIT.DIRECTSIM.LISTENER.JAVA
 * 
 * THIS SHOULD BE DELETED.
 */



public class DirectListener {

	static int port=4444, maxConnections=0;
	static File SIMDB;

	// Listen for incoming connections and handle them
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: DirectListener <port> <SimDb>");
			System.exit(-1);
		}
		SIMDB = new File(args[1]);
		if (!SIMDB.exists()) { 
			System.out.println("SimDb (" + SIMDB + ") does not exist");
			System.exit(-1);
		}
		if (!SIMDB.isDirectory()) { 
			System.out.println("SimDb not a directory");
			System.exit(-1);
		}
		if (!SIMDB.canWrite()) { 
			System.out.println("SimDb not writeable");
			System.exit(-1);
		}

		port = Integer.parseInt(args[0]);
		System.out.println("Port is " + port);

		int i = 0;

		try{
			ServerSocket listener = new ServerSocket(port);
			Socket server;

			while((i++ < maxConnections) || (maxConnections == 0)){
				doComms connection;

				server = listener.accept();
				doComms conn_c= new doComms(server, SIMDB);
				Thread t = new Thread(conn_c);
				t.start();
			}
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}



}

class doComms implements Runnable {
	Socket server;
	String line,input;
	File queue;
	File toolkitExternalCache;

	doComms(Socket server, File queue) {
		this.server = server;
		this.queue = queue;
	}

	public void run () {

		input="";

		File outfile = null;
		try {
			// Get input from the client
			DataInputStream in = new DataInputStream (server.getInputStream());
			outfile = new File(queue + File.separator + new Hl7Date().now());
			System.out.println("\n\noutfile is " + outfile.toString() + "\n\n");
			FileOutputStream fos = new FileOutputStream(outfile);
			byte[] by = new byte[256];
			int count;

			while ( (count=in.read(by)) > 0) 
				fos.write(by, 0, count);

			server.close();

		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
			System.exit(-1);
		}
		
		String fromAddr = null;
		try {
			byte[] msg = Io.bytesFromFile(outfile);
			HttpParser hpar = new HttpParser(msg);
			String from = hpar.getHeaderValue("From");
			from = rawFromHeader(from);
			if (from.equals(""))
				throw new Exception("Cannot extract From address");
			fromAddr = from;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpHeaderParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Now, is this Direct address registered?
		if (new DirectUserManager().directUserExists(fromAddr)) {
			
		} else {
			System.out.println("Direct user: " + fromAddr + " does not exist - discarding input");
		}
		
	}
	
	String rawFromHeader(String from) {
		if (from.indexOf('<') == -1)
			return from;
		int start = from.indexOf('<');
		int end = from.indexOf('>');
		if (end > 0 && end < from.length())
			return from.substring(start, end);
		return ""; // don't understand
	}
}

