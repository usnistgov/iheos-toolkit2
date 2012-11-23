package gov.nist.direct.mdn.test;

import static org.junit.Assert.assertTrue;
import gov.nist.direct.mdn.MDNGenerator;
import gov.nist.direct.mdn.messageDispatch.MessageDispatchUtils;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;
import org.junit.Test;

import com.google.gwt.dev.jjs.ast.JField.Disposition;

public class TestMDNGenerator {
	
	ErrorRecorder er = new TextErrorRecorder();
	
		
	@Test
	public void testMDNGenerationValidationCycle(){
		
		MimeMultipartReport mdn = null;
		try {
			mdn = MDNGenerator.create("ack", "starugh-stateline.com", "NHIN Direct Security Agent", null,
					"externUser1@starugh-stateline.com", "<9501051053.aa04167@IETF.CNR I.Reston.VA.US>", Disposition.COMPILE_TIME_CONSTANT);
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
			// Creates a new MimeMessage message using the MimeMultipartReport contents
			Properties props = System.getProperties();
		    Session session = Session.getDefaultInstance(props, null);
			MimeMessage mimeMsg = new MimeMessage(session);
			try {
				mimeMsg.setContent(mdn);
				Utils.printToFile(mimeMsg, "MimeMultipartReportfile.txt");
		
				assertTrue(MessageDispatchUtils.isMDN(mimeMsg));
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	
	
	
}