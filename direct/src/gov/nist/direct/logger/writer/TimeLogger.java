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
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Logs dates to MDN and Direct log file structure.
 * @author dazais
 *
 */
public class TimeLogger {
	LogPathsSingleton ls;
	
	public TimeLogger(){
		ls = LogPathsSingleton.getLogStructureSingleton();
	}

	public void logDirectReceivedDate(Date d, String transactionType, String messageType, String username, String messageId) throws IOException {
		String dateLogPath = ls.getDateLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(formatDateForLogging(d), dateLogPath); // ask to overwrite? which failsafes?
		}
	
	public void logMDNReceivedDate(Date d, String transactionType, String messageType, String username, String messageId) throws IOException {
		String dateLogPath = ls.getMDNReceivedDateLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(formatDateForLogging(d), dateLogPath); // ask to overwrite? which failsafes?
		}

	public void logExpirationDate(Date d, String transactionType, String messageType, String username, String messageId) throws IOException {
		String expiration = ls.getDateExpirationLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(formatDateForLogging(d), expiration); // ask to overwrite? which failsafes?
		}

	
	String formatDateForLogging(Date d){
        DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
        //sdf.setLenient(true);
        //DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		return sdf.format(d);
	}
	

}
