package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testkitutilities.TestEnvConfig;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import gov.nist.toolkit.xdsexception.XdsParameterException;

import java.io.IOException;
import java.util.List;

import org.junit.Test;


/*
 * Test agenda
 * -h done
 * -t done
 * -tc done
 * -err
 * -ls done
 * -lsc done
 * -s --site  done
 * -P --prepare
 * -S --secure
 * -V --version
 * -T --testkit  done
 * -L --logDir done
 * -run  done
 * -v
 * 
 * 
 * 
 * 
 */

public class XdsTestTest extends TestEnvConfig {

	@Test
	public void noOptions() {
		XdsTest xt = new XdsTest(true);
		
		try {
			xt.run(getConfigOptions());
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}
 
	@Test
	public void siteSpec() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-s");
		args.add("pub");

		try {
			xt.run(args);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert xt.site.getSiteName().equals("pub");
	}

	@Test
	public void siteSpec2() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("--site");
		args.add("pub");

		try {
			xt.run(args);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert xt.site.getSiteName().equals("pub");
	}

	@Test
	public void testSelectSingleSubTest() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-t");
		args.add("11710");

		try {
			xt.run(args);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert xt.testSpecs.size() == 1;
		TestLogDetails ts = xt.testSpecs.get(0);
		ts.getTestInstance().equals("11710");
		try {
			assert ts.getReadmeFirstLine().startsWith("Register IP address against Public Registry");
		} catch (IOException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}


	@Test
	public void testSelectSingleSub() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-t");
		args.add("11733");

		try {
			xt.run(args);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert xt.testSpecs.size() == 1;
		try {
			assert xt.testSpecs.get(0).getTestPlans().size() == 2;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void testSelectSingleSubWithSection() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-t");
		args.add("11733");
		args.add("eval");

		try {
			xt.run(args);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert xt.testSpecs.size() == 1;
		try {
			assert xt.testSpecs.get(0).getTestPlans().size() == 1;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void emptyToptions() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-t");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			return;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	@Test
	public void testTandTCoptions() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-t");
		args.add("11733");
		args.add("-tc");
		args.add("sql");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		
		assert xt.testSpecs.size() == 3;
	}

	@Test
	public void collectionWithSectionSpec() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-tc");
		args.add("section_test");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		
		assert xt.testSpecs.size() == 1;
	}

	@Test
	public void testTCmissingvalue() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-tc");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			return;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	@Test
	public void testCollection() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("-tc");
		args.add("sql");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		
		assert xt.testSpecs.size() == 2;
		try {
			assert xt.testSpecs.get(0).getTestPlans().size() == 1;
			assert xt.testSpecs.get(1).getTestPlans().size() == 1;
		} catch (Exception e1) {
			assert false;
		}
		
		try {
			xt.testSpecs.get(0).validateTestPlans();
			xt.testSpecs.get(1).validateTestPlans();
		} catch (XdsInternalException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}

	@Test
	public void badTestkitLocation() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("--testkit");
		args.add("/Users/bill/tmp");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			assert e.getMessage().indexOf("is not really the testkit") != -1;
			return;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	@Test
	public void forceTestkitLocation() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("--testkit");
		args.add("/Users/bill/dev/testkit");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}

	@Test
	public void badLogDirLocation() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptions();
		args.add("--logdir");
		args.add("/Users/bill/xxxtmp");

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			assert e.getMessage().indexOf("does not exist or is not a directory") != -1;
			return;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	@Test
	public void noLogDir() {
		XdsTest xt = new XdsTest(true);
		List<String> args = getConfigOptionsNoLogDir();

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}



}
