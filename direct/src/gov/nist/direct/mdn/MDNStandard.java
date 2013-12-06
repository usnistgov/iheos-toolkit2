
/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/
package gov.nist.direct.mdn;



import gov.nist.direct.mime.MailStandard;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;

/**
 * Provides constants and utility functions for working with MDN
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class MDNStandard extends MailStandard 
{
	/**
	 * MIME types for MDN 
     * @author Greg Meyer
     * @author Umesh Madan
	 *
	 */
	public static class MediaType extends MailStandard.MediaType {
		/**
		 * Base MIME type for an MDN
		 */
	    public static final String ReportMessage = "multipart/report";
	
	    /**
	     * MIME type with qualifier for a disposition report.
	     */
	    public static final String  DispositionReport = ReportMessage + "; report-type=disposition-notification";
	
	    /**
	     * MIME type for the disposition notification body part of the multipart/report report
	     */
	    public static final String  DispositionNotification = "message/disposition-notification";
	}
	
	/**
	 * Standard header names for MDN headers
     * @author Greg Meyer
     * @author Umesh Madan
	 *
	 */
    public static class Headers extends MailStandard.Headers
    {

    	/**
    	 * Disposition header field name.
    	 * <p>
    	 * RFC 3798, Disposition field, 3.2.6
    	 */
    	public static final String Disposition = "Disposition";

    	/**
    	 * Disposition-Notification-To header name
    	 * <p>
    	 * RFC 3798, The Disposition-Notification-To Header, 2.1
    	 */
    	public static final String DispositionNotificationTo = "Disposition-Notification-To";

    	/**
    	 * Disposition-Notification-Options header name
    	 * <p>
    	 * RFC 3798, The Disposition-Notification-Options Header, 2.2
    	 */
    	public static final String DispositionNotificationOptions = "Disposition-Notification-Options";

    	/**
    	 * Reporting-UA field name (value is the Health Internet Addresa and software that triggered notification) 
    	 * <p>
    	 * RFC 3798, The Reporting-UA field, 3.2.1
    	 */
    	public static final String ReportingAgent = "Reporting-UA";

    	/**
    	 * MDN-Gateway field name (for SMTP to non-SMTP gateways -- e.g., XDD to SMTP)
    	 * <p>
    	 * RFC 3798, The MDN-Gateway field, 3.2.2
    	 */
    	public static final String Gateway = "MDN-Gateway";

    	/**
    	 * Original-Message-ID field name (value is message for which notification is being sent)
    	 * <p>
    	 * RFC 3798, Original-Message-ID field, 3.2.5
    	 */
    	public static final String OriginalMessageID = "Original-Message-ID";

    	/**
    	 * Final recipient field name.
    	 */
    	public static final String FinalRecipient = "Final-Recipient";    	
    	
    	/**
    	 * Failure field name, value is original failure text (e.g., exception)
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	
public static final String OriginalRecipient = "Original-Recipient";    	
    	
    	/**
    	 * Failure field name, value is original failure text (e.g., exception)
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	
    	
    	public static final String Failure = "Failure";

    	/**
    	 * Error field name, value is original error text (e.g., HL7 error report)
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	public static final String Error = "Error";

    	/**
    	 * Warning field name, value is original warning text
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	public static final String Warning = "Warning";    	
    }
        
    static final String Action_Manual = "manual-action";
    static final String Action_Automatic = "automatic-action";
    static final String Send_Manual = "MDN-sent-manually";
    static final String Send_Automatic = "MDN-sent-automatically";
    static final String Disposition_Displayed = "displayed";
    static final String Disposition_Processed = "processed";
    static final String Disposition_Deleted = "deleted";
    static final String Modifier_Error = "error";    
    
    static final String  ReportType = "report-type";
    static final String  ReportTypeValueNotification = "disposition-notification";  
    
       
}
