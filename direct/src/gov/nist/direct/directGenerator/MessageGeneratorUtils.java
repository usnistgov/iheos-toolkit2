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

package gov.nist.direct.directGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.FileUtils;

public class MessageGeneratorUtils {

	public static MimeBodyPart addText(String textMessage) throws Exception {
		MimeBodyPart    msg1 = new MimeBodyPart();
		msg1.setText(textMessage);
		return msg1;
	}
	
	public static MimeBodyPart addAttachement(File attachmentContentFile) throws Exception {
		byte[] fileContent = FileUtils.readFileToByteArray(attachmentContentFile);
        //byte[] content = Base64.encodeBase64(fileContent);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream base64OutputStream = MimeUtility.encode(baos, "base64");
        base64OutputStream.write(fileContent);
        base64OutputStream.close();
        
        byte[] content = baos.toByteArray();
        
        InternetHeaders partHeaders = new InternetHeaders();
        if(attachmentContentFile.getName().contains(".xml")) {
        	partHeaders.addHeader("Content-Type", "text/xml; name="+attachmentContentFile.getName());
        } else if(attachmentContentFile.getName().contains(".zip")) {
        	partHeaders.addHeader("Content-Type", "application/zip; name="+attachmentContentFile.getName());
        } else {
        	partHeaders.addHeader("Content-Type", "application/octet-stream; name="+attachmentContentFile.getName());
        }
        partHeaders.addHeader("Content-Transfer-Encoding", "base64");
        partHeaders.addHeader("Content-Disposition", "attachment; filename="+attachmentContentFile.getName());

        MimeBodyPart ccda = new MimeBodyPart(partHeaders, content);
        return ccda;
	}
}
