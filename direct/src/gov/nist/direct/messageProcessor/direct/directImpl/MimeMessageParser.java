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


package gov.nist.direct.messageProcessor.direct.directImpl;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;



public class MimeMessageParser {

	public static MimeMessage parseMessage(ErrorRecorder er, byte[] message){

		//
		// Get a Session object with the default properties.
		//         
		Properties props = System.getProperties();

		Session session = Session.getDefaultInstance(props, null);

		MimeMessage msg = null;

		if (message != null){
			// Convert byte[] into InputStream and create MimeMessage
				InputStream is = new ByteArrayInputStream(message);
				try {
					msg = new MimeMessage(session, is);
				} catch (MessagingException e) {
					er.err( null, e);
					e.printStackTrace();
				}
		}
		return msg;
	}

}
