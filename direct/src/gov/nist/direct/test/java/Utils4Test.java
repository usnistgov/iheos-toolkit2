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

package gov.nist.direct.test.java;


import gov.nist.toolkit.utilities.io.Io;

import java.io.File;

public class Utils4Test {
	
	// ------=== Collections of test data ===-------
	
	// Set #1 of Direct Message + certs + password
	public final static String DIRECT_MESSAGE_PATH_1 = "direct/src/gov/nist/direct/test/resources/directMessages/signed-encrypted-xdm.eml";
	public final static String SIGNING_CERT_PATH_1 = "direct/src/gov/nist/direct/test/resources/certificates/mhunter.p12";
	public final static String ENCRYPTION_CERT_PATH_1 = "direct/src/gov/nist/direct/test/resources/certificates/mhunter.cer";
	public final static String PASSWORD_1 = "mhunter";

	// ---------------------------------------------
	
	


	/**
	 * Parses a text file and returns the SMTP message
	 * 
	 * @param testData the text file that contains an SMTP message
	 * @return the SMTP message
	 */
	public static byte[] getMessage(String testData) {
		byte[] input = null;
		try {
			input = Io.bytesFromFile(new File(testData));
		} catch (Exception e) {
			//er.err(null, e);
			return input;
		}

		return input;

	}




	public static String getDIRECT_MESSAGE_PATH_1() {
		return DIRECT_MESSAGE_PATH_1;
	}




	public static String getSIGNING_CERT_PATH_1() {
		return SIGNING_CERT_PATH_1;
	}




	public static String getENCRYPTION_CERT_PATH_1() {
		return ENCRYPTION_CERT_PATH_1;
	}




	public static String getPASSWORD_1() {
		return PASSWORD_1;
	}

	

	
}
