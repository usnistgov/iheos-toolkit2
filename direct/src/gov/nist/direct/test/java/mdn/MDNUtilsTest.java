package gov.nist.direct.test.java.mdn;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import gov.nist.direct.mdn.MDNStandard;
import gov.nist.direct.mdn.MDNUtils;
import gov.nist.direct.utils.Utils;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.management.Notification;

import org.junit.Test;

public class MDNUtilsTest {

	
	   @Test 
    public void testParseFieldsFromMimeMessage() throws Exception
    {
		String mdnMessagePath = "direct/src/gov/nist/direct/test/resources/mdnMessages/RIexamples/MDNMessage.txt";

            MimeMessage msg = Utils.getMimeMessage(mdnMessagePath);
            
            InternetHeaders headers = MDNUtils.getNotificationFieldsAsHeaders(msg);
           String[] dispo = headers.getHeader(MDNStandard.Headers.Disposition);
           
            assertNotNull(dispo);
            assertEquals("automatic-action/MDN-sent-automatically;processed", headers.getHeader(MDNStandard.Headers.Disposition, ","));
            
            assertNotNull(headers.getHeader(MDNStandard.Headers.ReportingAgent));
            assertEquals("starugh-stateline.com;NHIN Direct Security Agent", headers.getHeader(MDNStandard.Headers.ReportingAgent, ","));
            
            assertNotNull(headers.getHeader(MDNStandard.Headers.FinalRecipient));
            assertEquals("externUser1@starugh-stateline.com", headers.getHeader(MDNStandard.Headers.FinalRecipient, ","));  
            
            assertNotNull(headers.getHeader(MDNStandard.Headers.OriginalMessageID));
            assertEquals("<9501051053.aa04167@IETF.CNR I.Reston.VA.US>", headers.getHeader(MDNStandard.Headers.OriginalMessageID, ","));                    
    }
	   
	   
//    @Test
//    public void testParseFieldsFromMimeMessage_NonMDNMessage_AssertException() throws Exception
//    {
//		String incorrectMdnMessagePath = "";
//
//    	MimeMessage msg = Utils.getMimeMessage(incorrectMdnMessagePath);
//                        
//            boolean exceptionOccured = false;
//            try
//            {
//                    Notification.getNotificationFieldsAsHeaders(msg);
//            }
//            catch (IllegalArgumentException e)
//            {
//                    exceptionOccured = true;
//            }
//            
//            assertTrue(exceptionOccured);
//            
//                            
//    }
//	
    
    
    
}
