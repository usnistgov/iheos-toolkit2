package gov.nist.direct.mdn;

import gov.nist.direct.utils.ValidationUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MDNUtils {
	
	final static String atom = "[0-9a-zA-Z]*";
	final static String text = "[0-9a-zA-Z_.-]*";
	final static String whitespace = "\\s";
	final static String actionMode = "(normal-action|automatic-action)";
	final static String sendingMode = "(mdn-sent-manually|mdn-sent-automatically)";
	final static String dispositionType = "(displayed|processed|deleted)";
	final static String dispositionModifier = "(error|" + atom + ")";
	
	
	
	public static boolean validateAtomTextField(String field) {
		final String stringPattern =  atom + ";" + text;
		Pattern pattern = Pattern.compile(stringPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(field);
		if(matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean validateDisposition(String disposition) {
		String dispositionPattern = "(" + actionMode + "/" + sendingMode + ")" + ";" +"(\\s)?" + dispositionType;
		dispositionPattern += "(/" + dispositionModifier + "(,\\s" + dispositionModifier + ")*)?";
		Pattern pattern = Pattern.compile(dispositionPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(disposition);
		if(matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean validateTextField(String textField) {
		Pattern pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(textField);
		if(matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getAtom() {
		return atom;
	}
	

	/**
	 * Function taken from the Direct RI
	 * 
	 * Parses the notification part fields of a MDN MimeMessage message.  The message is expected to conform to the MDN specification
	 * as described in RFC3798.
	 * @return The notification part fields as a set of Internet headers. 
	 */		
	public static InternetHeaders getNotificationFieldsAsHeaders(MimeMessage message)
	{
		if (message == null)
			throw new IllegalArgumentException("Message can not be null");
		
		MimeMultipart mm = null;
		
		try
		{
			ByteArrayDataSource dataSource = new ByteArrayDataSource(message.getRawInputStream(), message.getContentType());
			mm = new MimeMultipart(dataSource);
		}
		catch (Exception e)
		{
			System.out.println("Converting the message to an InternetHeaders format did not succeed.");
			e.printStackTrace();
		}
		
		return getNotificationFieldsAsHeaders(mm);
	}	
	
	
	/**
	 * Function taken from the Direct RI
	 * 
	 * Parses the notification part fields of the MimeMultipart body of a MDN message.  The multipart is expected to conform to the MDN specification
	 * as described in RFC3798.
	 * @return The notification part fields as a set of Internet headers. 
	 */	
    public static InternetHeaders getNotificationFieldsAsHeaders(MimeMultipart mm)
    {
            InternetHeaders retVal = null;
            
            if (mm == null)
                    throw new IllegalArgumentException("Multipart can not be null");
            
            try
            {
                    if (mm.getCount() < 2)
                            throw new IllegalArgumentException("Multipart can not be null");
                    
                    // the second part should be the notification
                    BodyPart part = mm.getBodyPart(1);
                    try {
						part.writeTo(new FileOutputStream("MDNsecondpart.txt"));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (part != null) System.out.println("bodypart is not null");
                    System.out.println("disposition " + part.getDisposition());
                            
                    // parse fields
                    retVal = ValidationUtils.getHeadersAndContent(part) ;
                 
            }
            catch (MessagingException e)
            {
            	e.printStackTrace();
            }
            
            return retVal;
            
    }       
	

}
