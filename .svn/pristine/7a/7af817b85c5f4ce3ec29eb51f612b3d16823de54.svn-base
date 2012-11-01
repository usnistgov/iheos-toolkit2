package gov.nist.toolkit.http.util;

import gov.nist.toolkit.http.httpclient.HttpClientInfo;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.IOException;

public class RestService {
	static String port = null;
	static String service = null;
	static String host = null;
	static String input_file_name = null;
	static String input = null;

	static void usage() {
		System.out.println("Usage:\nrest [options]\n" +
		"\t-h, --help\t\tShow this message and exit\n" +
		"\t-p, --port PORT\t\tPort\n" +
		"\t-s, --service SERVICE\tService\n" +
		"\t-h, --host HOST\t\tHost\n" +
		"\t-i, --in FILENAME\tInput file");
	}

	static public void main(String[] args) {

		if (args.length == 0) {
			usage();
			return;
		}

		for (int i=0; i<args.length; i++) {
			String arg = args[i];

			if (arg.equals("-h") || arg.equals("--help")) {
				usage();
				return;
			}
			else if (arg.equals("-p") || arg.equals("--port")) {
				i++;  arg = args[i];
				if (i >= args.length) err("No port value specified");
				port = arg;
			}
			else if (arg.equals("-s") || arg.equals("--service")) {
				i++;  arg = args[i];
				if (i >= args.length) err("No service value specified");
				service = arg;
			}
			else if (arg.equals("-h") || arg.equals("--host")) {
				i++;  arg = args[i];
				if (i >= args.length) err("No host value specified");
				host = arg;
			}
			else if (arg.equals("-i") || arg.equals("--in")) {
				i++;  arg = args[i];
				if (i >= args.length) err("No input file specified");
				input_file_name = arg;
			}
		}

		if (host == null) specify("Host");
		if (port == null) specify("Port");
		if (service == null) specify("Service");
		if (input_file_name == null) specify("Input File Name");

		try {
			input = Io.stringFromFile(new File(input_file_name));
		}
		catch (IOException e) {
			err("Cannot open file " + input_file_name);
		}
		
		try {
		run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void run() throws XdsInternalException {
		HttpClientInfo info;
		info = new HttpClientInfo();
		info.setRestHost(host);    // point to back-end registry
		info.setRestPort(Integer.parseInt(port));
		info.setRestService(service);

		HttpClientBean httpBean;
		httpBean = new HttpClientBean();
		httpBean.setHttpClientInfo(info);
		httpBean.setMetadata(input);

		String response_string = httpBean.getQueryResponse();

		System.out.println(response_string);

	}

	static void err(String msg) {
		System.out.println("Error: " + msg);
		usage();
		System.exit(-1);
	}

	static void specify(String what) {
		err(what + " must be specified");
	}





}
