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

package gov.nist.direct.error;

import java.io.File;
import java.util.ArrayList;

import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;



public class ErrorReader {
	static ErrorRecorder er;
	static File f;
	ArrayList<String> errorAllTextPlusIds, errorIDs, errorText;

	public ErrorReader(){
		//processErrorText();
	}
	
	    
/**
 * Splits all error messages (from text file) into two Arraylists.
 * One contains the error codes, the other the error messages.
 */
public void processErrorText(){
	errorAllTextPlusIds = Utils.readFile(f);
	String current = "";
	for (int i = 0;i<errorAllTextPlusIds.size();i++){
		current = errorAllTextPlusIds.get(i);
		String[] split = current.split("=");
		if (split.length == 2) {
			errorIDs.add(split[0]);
			errorText.add(split[1]);
		}
		else {
			//er.err(null, "The error message is incorrect, line "+ i +".");
		}
}
}

/**
 * Returns an error message based on the error code.
 * @param errorCode the error code. Error codes refer to elements in the specification
 * as listed in the file Mod_Spec_P2_Transport_and_Security_Test_Package_v1.0c.xls 
 * from ONC.
 * @return the error message
 */
public String getErrorText(String errorCode){
	for (int i = 0;i<errorIDs.size();i++){
		if (errorIDs.get(i) == errorCode){
			return errorText.get(i);
		}
	}
	return "Unlisted error.";
}
	
	

	// Getters and setters
	public ErrorRecorder getErrorRecorder() {
		return er;
	}
	
	public void setErrorRecorder(ErrorRecorder er) {
		ErrorReader.er = er;
	}
	
	public void setFile(File f) {
		ErrorReader.f = f;
	}
	
	
	
}
