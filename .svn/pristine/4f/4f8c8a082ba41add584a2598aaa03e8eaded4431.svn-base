package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.testengine.XdsTest;
import gov.nist.toolkit.testkitutilities.TestEnvConfig;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsParameterException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class RunTest extends TestEnvConfig {

	@Test
	public void testRun() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-s", "dev", "-t", "11710", "-run", "-err"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));

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
	public void testRunDefaultSite() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-t", "11710", "-run", "-err"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));

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
	public void logInTestkit() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-t", "11710", "-run", "-err"};
		List<String> args = getConfigOptionsNoLogDir();
		args.addAll(Arrays.asList(sargs));

		File logFile = new File(TestEnvConfig.testkit + File.separator + "tests" + 
				File.separator + "11710" + File.separator + "log.xml"); 

		if (logFile.exists())
			logFile.delete();
		
		assert !logFile.exists();
		
		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		
		assert logFile.exists();
		logFile.delete();
		
	}

	
	public void ls() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-ls"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	
	public void lsc() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-lsc"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	
	public void t11728() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-t", "11728", "-err", "-run"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));

		try {
			xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert false;
	}

	@Test
	public void t11999submit() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-t", "11999", "submit", "-err", "-run", "--response"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));
		boolean status = false;

		try {
			status = xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert status;
	}

	@Test
	public void t11999eval() {
		XdsTest xt = new XdsTest(true);
		String[] sargs = {  "-t", "11999", "eval", "-err", "-run", "--response"};
		List<String> args = getConfigOptions();
		args.addAll(Arrays.asList(sargs));
		boolean status = false;

		try {
			status = xt.run(args);
		} catch (XdsParameterException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
		assert status;
	}

}
