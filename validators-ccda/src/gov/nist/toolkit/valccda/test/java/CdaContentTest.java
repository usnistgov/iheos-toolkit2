package gov.nist.toolkit.valccda.test.java;

import static org.junit.Assert.fail;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder.ErrorInfo;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valccda.CcdaValidator;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;

public class CdaContentTest {

	File badCcdaFile = new File("validators-ccda/src/gov/nist/toolkit/valccda/test/resources/CCDA_CCD_Ambulatory_blank_lines.xml");

	
	@Test
	public void badCcdaTest() {
		try {
		InputStream is = Io.getInputStreamFromFile(badCcdaFile);
		String validationType = "Transitions Of Care Ambulatory Summary";
		TextErrorRecorder er = new TextErrorRecorder();
		CcdaValidator.validateCDA(
				is, 
				validationType, 
				er);
		System.out.println("Got errors?: " + er.hasErrors());
		for (ErrorInfo e : er.getErrorMsgs()) {
			if (e.isError)
				System.out.println("Err: " + e.msg);
			else
				System.out.println("Msg: " + e.msg);
		}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getClass().getName());
			System.out.println(ExceptionUtil.exception_details(e));
			fail();
		}
	}
}
