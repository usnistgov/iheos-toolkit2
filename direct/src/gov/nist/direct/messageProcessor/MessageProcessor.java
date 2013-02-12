/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */

package gov.nist.direct.messageProcessor;

import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.messageProcessor.mdn.mdnImpl.MDNMessageProcessor;
import gov.nist.direct.messageProcessor.utils.MessageDispatchUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.text.ParseException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;



/**
 * Facade for the Message Processing component
 * @author dazais
 *
 * @param <T>
 */
public class MessageProcessor implements MessageProcessorInterface {

	private String directMessageType = "DIRECT";
	private String mdnMessageType = "MDN";
	
	private String messageType;
	static Logger logger = Logger.getLogger(DirectMimeMessageProcessor.class);
	
	
	
	/**
	 * Facade handler to process all types of messages
	 */
	public void processMessage(ErrorRecorder er, byte[] message, byte[] _directCertificate, String _password, ValidationContext vc) {

		// Parse the message
		MimeMessage mm = MimeMessageParser.parseMessage(er, message);
		
		
		// determine message type
		// ------ MDN -------
		try {
			if (MessageDispatchUtils.isMDN(er, mm)){
				 messageType = mdnMessageType;
				 
				 // Display Message type
				 er.detail("The file was recognized as an MDN message.");
				 
				 // Process message
				 MDNMessageProcessor mdnProc = new MDNMessageProcessor();
				 try {
					mdnProc.processMDNMessage(er, message, _directCertificate, _password, vc);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								 
			}
			
			
		// ------ DIRECT -------
		if (MessageDispatchUtils.isDIRECT(er, mm)){
			 messageType = directMessageType;
			 
			 // Display Message type
			 er.detail("The file was recognized as a DIRECT message.");
			}
			
			else {
				er.err("Message File", "The file is neither a DIRECT message nor an MDN.", "", "", "Message File");
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		
	
		
	}
	
	

	
} // end class
