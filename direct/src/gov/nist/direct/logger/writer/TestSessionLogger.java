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

import org.apache.log4j.Logger;

/**
 * Logs status of message validation to MDN and Direct log file structure
 * @author jperugini
 *
 */
public class TestSessionLogger {
	LogPathsSingleton ls;
	static Logger logger = Logger.getLogger(TestSessionLogger.class);

	public TestSessionLogger(){
		ls = LogPathsSingleton.getLogStructureSingleton();
	}

	// Logging an MDN validation status
	public void logTestSession(String testSession, String transactionType, String messageType, String username, String messageId) throws IOException {
		logger.info("Test Session Name: " + testSession);
		String testSessionLogPath = ls.getTestSessionLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(testSession, testSessionLogPath);
	}



}
