package gov.nist.toolkit.testkitutilities;

import java.util.ArrayList;
import java.util.List;

public class TestEnvConfig {
	static List<String> configOptions;
	static List<String> logDirOptions;
	static public String testkit = "/Users/bill/dev/testkit";
	
	static {
		configOptions = new ArrayList<String>();		
		configOptions.add("--testkit");
		configOptions.add(testkit);
		configOptions.add("--toolkit");
		configOptions.add("/Users/bill/exp/xdstoolkit");

		logDirOptions = new ArrayList<String>();
		logDirOptions.add("--logdir");
		logDirOptions.add("/Users/bill/tmp/xdstest");
}

	static public List<String> getConfigOptions() {
		List<String> a = new ArrayList<String>();
		a.addAll(configOptions);
		a.addAll(logDirOptions);
		return a;
	}

	static protected List<String> getConfigOptionsNoLogDir() {
		List<String> a = new ArrayList<String>();
		a.addAll(configOptions);
		return a;
	}

}
