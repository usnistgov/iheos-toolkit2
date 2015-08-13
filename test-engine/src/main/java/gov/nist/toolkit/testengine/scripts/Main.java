package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.testengine.engine.TransactionSettings;
import gov.nist.toolkit.testengine.engine.Xdstest2;
import gov.nist.toolkit.utilities.io.LinesOfFile;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main implements SecurityParams {
	static Xdstest2 engine;
	File config = null;
	File logDir = null;
	String testName = null;
	File testDir = null;
	File testKit = null;
	static File toolkit = null;

	static public void main(String[] arguments) {
		List<String> args = new ArrayList<String>();
		for (int i=0; i<arguments.length; i++)
			args.add(arguments[i]);

		Main me = new Main();
		me.parseParameters(args);

		try {
			engine = new Xdstest2(toolkit, me);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		engine.setLogdirLocation(me.logDir);

		me.run();
	}

	void run() {
		parameterCheck();
		TransactionSettings ts = new TransactionSettings();
		try {
			engine.addTest(testName);
			engine.run(null, null, true, ts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean parameterCheck() {
		boolean ok = true;
//		if (config == null) 
//			fatal("-config parameter missing");
		if (testName == null)
			ok = ok & err("-t parameter missing");
		return ok;
	}

	boolean parseParameters(List<String> args) {
		boolean ok = true;
		while (args.size() >= 2) {
			String name = args.get(0);
			String value = args.get(1);
			args.remove(0);
			args.remove(0);

			if (!name.startsWith("-")) {
				usage("Do not understand flag " + name);
			}

			if (name.equals("-config")) {
				config = new File(value);
				if (!config.exists() || !config.isDirectory()) {
					usage("Error: -config - must reference existing directory.");
				}
				logDir = new File(config + File.separator + "logs");
				logDir.mkdir();
				testKit = new File(config + File.separator + "testkitIn");
			}
			else if (name.equals("-t")) {
				testName = value;
				testDir = testDir();
				if (testDir == null || !testExists())
					ok = false;
			}
		}
		return ok & parameterCheck();
	}

	void usage() {
		System.out.println("Usage: ttk [parameters]\n" +
				"Where the legal parameters are:\n" 
				+ "\t-tk <testkitIn directory>\n"
				+ "\t-log <log directory>\n"
				+ "\t-t <test name>"
				);
		System.exit(-1);
	}

	boolean testExists() {
		if (!testDir.exists())
			return err("Test directory " + testDir + " does not exist");
		if (!new File(testDir + File.separator + "readme.txt").exists())
			return err("Test directory " + testDir + " exists but is not a test directory - no readme.txt file");
		return true;
	}

	void fatal(String msg) {
		System.out.println("Error: " + msg);
		System.exit(-1);
	}

	boolean err(String msg) {
		System.out.println("Error: " + msg);
		return false;
	}

	void usage(String msg) {
		System.out.println("Error: " + msg);
		usage();
	}

	File testDir() {
		try {
			LinesOfFile lof = new LinesOfFile(new File(config + File.separator + "areas"));
			while(lof.hasNext()) {
				String area = lof.next().trim();
				File d = new File(testKit + File.separator + area + File.separator + testName);
				if (d.exists())
					return d;
			}
		} catch (IOException e) {

		}
		return null;
	}

	@Override
	public File getCodesFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getKeystore() throws EnvironmentNotSelectedException {
		return new File(config + File.separator + "keystore");
	}

	@Override
	public String getKeystorePassword() throws IOException,
	EnvironmentNotSelectedException {
		return "changeit";
	}

	@Override
	public File getKeystoreDir() throws EnvironmentNotSelectedException {
		return new File(config + File.separator );
	}
}
