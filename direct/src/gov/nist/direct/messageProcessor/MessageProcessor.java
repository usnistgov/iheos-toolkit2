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
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.direct.messageProcessor;

import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.messageProcessor.mdn.mdnImpl.MDNMessageProcessor;
import gov.nist.direct.messageProcessor.utils.MessageDispatchUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;

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
	public void processMessage(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password, ValidationContext vc) {

		// Parse the message
		MimeMessage mm = MimeMessageParser.parseMessage(er, inputDirectMessage);
		
		MessageDispatchUtils messageUtils = new MessageDispatchUtils(er, inputDirectMessage, _directCertificate, _password);
		
		
		// determine message type
		// ------ MDN -------
		try {
			// Check if encrypted message
			if(!MessageDispatchUtils.isEncrypted(er, mm)) {
				er.error("No DTS", "Message File", "The file is not an encrypted message", "Must be encrypted", "-");
				logger.info("This is not an encrypted message");
			}
			
			// ------ MDN -------
			if (messageUtils.isMDN()){
				messageType = mdnMessageType;

				// Display Message type
				logger.info("The file was recognized as an MDN message.");
				er.detail("The file was recognized as an MDN message.");

				// Process message
				MDNMessageProcessor mdnProc = new MDNMessageProcessor();
				mdnProc.processMDNMessage(er, inputDirectMessage, _directCertificate, _password, vc);
				logger.info("MDN message was processed.");
			}


		// ------ DIRECT -------
			else if (messageUtils.isDIRECT() && MessageDispatchUtils.isEncrypted(er, mm)){
			 messageType = directMessageType;
			 
			 // Display Message type
			 er.detail("The file was recognized as a DIRECT message.");
			 logger.info("The file was recognized as a DIRECT message.");
			 
			 // Process message
			 DirectMimeMessageProcessor directProc = new DirectMimeMessageProcessor();
			 directProc.processAndValidateDirectMessage(er, inputDirectMessage, _directCertificate, _password, vc);
			 logger.info("Direct message was processed.");
			}
			
		
		
		// ----- Unknown type  -----
			else if(!MessageDispatchUtils.isEncrypted(er, mm) && !messageUtils.isDIRECT()) {
				er.error("No DTS", "Message File", "The file is neither a DIRECT message nor an MDN.", "", "-");
				logger.info("The file is neither a DIRECT message nor an MDN.");
			}
			
			else if(messageUtils.isEncrypted() && !messageUtils.isSigned()) {
				er.error("No DTS", "Message File", "The message is not signed.", "", "-");
				logger.info("The message is not signed.");
			}
			
			else {
				er.error("No DTS", "Message File", "Error parsing the file.", "", "-");
				logger.info("Error parsing the file.");
			}
			
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		
	
		
	}
	
	

	
} // end class
