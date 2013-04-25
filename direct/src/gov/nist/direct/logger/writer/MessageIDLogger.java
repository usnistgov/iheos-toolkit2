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

package gov.nist.direct.logger.writer;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;

import java.io.IOException;

/**
 * Logs MDN message-id independently from the Direct message id that is used in the logger architecture
 * @author dazais
 *
 */
public class MessageIDLogger {
	 LogPathsSingleton ls;
	
	public MessageIDLogger(){
		 ls = LogPathsSingleton.getLogStructureSingleton();
	}

	// Logging a message status
	public void logMessageId(String messageIdToBeLogged, String transactionType, String messageType, String username, String directReferenceMessageId) throws IOException {
		String msgIdLogPath = ls.getMessageIdLogPath(transactionType, messageType, username, directReferenceMessageId);
		Utils.writeToFile(messageIdToBeLogged, msgIdLogPath);
		}
	


}
