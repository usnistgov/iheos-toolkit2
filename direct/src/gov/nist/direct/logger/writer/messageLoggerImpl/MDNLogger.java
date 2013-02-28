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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


// 2) Logging message processing status (waiting for MDN, error, etc).
// TODO this is probably a duplicate of Directmsglogger
public class MDNLogger {
	LogPathsSingleton ls;
	
	public MDNLogger(){
		ls = LogPathsSingleton.getLogStructureSingleton();
	}

		public void log(MimeMessage mdn, String transactionType, String messageType, String username, String messageId) throws FileNotFoundException, IOException {
			String mdnLogPath = ls.getEncryptedMessageLogPath(transactionType, messageType, username, messageId);
	
			try {
				mdn.writeTo(new FileOutputStream(mdnLogPath));
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		

}
