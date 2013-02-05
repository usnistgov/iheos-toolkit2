package gov.nist.direct.logging.impl;

import java.io.File;

import gov.nist.direct.logging.Logger;


// 1) Logging direct messages and mdns (un-encrypted of course)
public class DirectMessageLogger implements Logger {

	@Override
	public boolean log(Object o, File f) {
		// f.printToFile(MimeMessage msg, String outputFile);
		
		return false;
	}

	
	
	
}
