package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.results.client.TestId;

import java.io.File;

public class Xdstest2test {
	
	static public void t1() throws Exception {
		Xdstest2 x = new Xdstest2(new File("/Users/bill/dev/xdstoolkit"), null);
		x.addTest(new TestId("11990"));
//		x.setSite("dev");
//		boolean ok = x.run();
//		System.out.println("ok is " + ok);
	}

	static public void main(String[] args) {
		try {
			t1();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
