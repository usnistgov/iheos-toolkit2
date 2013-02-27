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

package gov.nist.direct.logger.writer.messageLoggerImpl;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.writer.MessageLoggerInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

public class LoggerDispatch implements MessageLoggerInterface {

	@Override
	public void log(Object o, LogPathsSingleton ls, String transactionType, String messageType, String partType, String username, String messageId) {

		if(!o.equals(null)){ 

			// Logging a Direct Message
			if (o instanceof MimeMessage){
				if( partType == "DECRYPTED_MSG_PART") {

					DirectMessageLogger directLogger = new DirectMessageLogger();
					try {
						directLogger.log((MimeMessage)o, ls, transactionType, messageType, username, messageId);


					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if( partType == "ENCRYPTED_MSG") {
					EncryptedMessageLogger encryptLogger = new EncryptedMessageLogger();
					try {
						encryptLogger.log((MimeMessage)o, ls, transactionType, messageType, username, messageId);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // if



			} // instance of



			// Logging an MDN Message - this probably does not work, MimeMultipartReport is never detected as a type.
			if (o instanceof MimeMultipartReport){
				MDNLogger mdnLogger = new MDNLogger();
				try {
					mdnLogger.log((MimeMessage)o, transactionType, messageType, username, messageId);


				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} //mimemultipartReport


		} //equals null
	}




}
