package gov.nist.toolkit.common.adt;

import gov.nist.toolkit.utilities.io.Io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AdtSender {
	String templateFile = null;
	InputStream templateInputStream = null;
	String server;
	int port;

	public AdtSender(String templateFile, String server, int port) {
		this.templateFile = templateFile;
		this.server = server;
		this.port = port;
	}

	public AdtSender(InputStream templateInputStream, String server, int port) {
		this.templateInputStream = templateInputStream;
		this.server = server;
		this.port = port;
	}

	public void send(String pid) throws IOException {
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			echoSocket = new Socket(server, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + server);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: " + server);
			System.exit(1);
		}

		char c;

		c = 0x0b;
		out.print(c);

		
		String template;
		
		if (templateInputStream != null)
			template = Io.getStringFromInputStream(templateInputStream);
		else
			template = Io.stringFromFile(new File(templateFile));

		
		template = template.replace("$pid$", pid);


		out.print(template);

		c = 0x1c;
		out.print(c);

		c = 0x0d;
		out.print(c);
		out.flush();

		in.readLine();

		out.close();
		in.close();
		echoSocket.close();
	}

}


