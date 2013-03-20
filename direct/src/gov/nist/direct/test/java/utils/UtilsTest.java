package gov.nist.direct.test.java.utils;

import static org.junit.Assert.*;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.test.java.Utils4Test;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void stringArraytoByteArrayTest(){
		ArrayList<String> array = new ArrayList<String>();
				array.add("a");
				array.add("b");
				byte[] expectedResult = {'a','b'};
				
			byte[] result = Utils.stringArraytoByteArray(array);
			
			assertTrue(Arrays.equals(expectedResult, result));	
	}
	
	@Test
	public void getMimeMessageTest(){
		ErrorRecorder er = new TextErrorRecorderModif();
		String path = Utils4Test.getDIRECT_MESSAGE_PATH_1();
		
		byte[] inputDirectMessage = Utils.getByteFile(path);
		MimeMessage expectedResult = MimeMessageParser.parseMessage(er, inputDirectMessage);
		
		MimeMessage result = Utils.getMimeMessage(path);
		
		assertEquals(result, expectedResult);
	}
	
	
}
