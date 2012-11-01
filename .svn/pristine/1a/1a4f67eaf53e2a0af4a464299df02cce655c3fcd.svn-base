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
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */


package gov.nist.direct.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.ParseException;

public class MessageDump {

	public static void dumpPart(Part p, boolean showStructure, boolean saveAttachments) throws Exception {
		if (p instanceof Message)
			dumpEnvelope((Message)p, showStructure);
		/** Dump input stream ..
	    InputStream is = p.getInputStream();
	    // If "is" is not already buffered, wrap a BufferedInputStream
	    // around it.
	    if (!(is instanceof BufferedInputStream))
	        is = new BufferedInputStream(is);
	    int c;
	    while ((c = is.read()) != -1)
	        System.out.write(c);
		 **/
		String ct = p.getContentType();
		pr("---------------------------", showStructure);
		try {
			pr("CONTENT-TYPE: " + (new ContentType(ct)).toString(), showStructure);
		} catch (ParseException pex) {
			pr("BAD CONTENT-TYPE: " + ct, showStructure);
		}
		String filename = p.getFileName();
		if (filename != null)
			pr("FILENAME: " + filename, showStructure);
		/*
		 * Using isMimeType to determine the content type avoids
		 * fetching the actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			pr("This is plain text", showStructure);
			pr("---------------------------", showStructure);
			if (!showStructure && !saveAttachments)
				System.out.println((String)p.getContent());
		} else if (p.isMimeType("message/rfc822")) {
			pr("This is a Nested Message", showStructure);
			pr("---------------------------", showStructure);
			level++;
			dumpPart((Part)p.getContent(), showStructure, saveAttachments);
			level--;
		} else if (p.isMimeType("multipart/*")) {
			pr("This is a Multipart", showStructure);

			pr("---------------------------", showStructure);
			Multipart mp = (Multipart)p.getContent();
			level++;
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				dumpPart(mp.getBodyPart(i), showStructure, saveAttachments);
			level--;
		} else {
			if (!showStructure && !saveAttachments) {
				/*
				 * If we actually want to see the data, and it's not a
				 * MIME type we know, fetch it and check its Java type.
				 */
				Object o = p.getContent();
				if (o instanceof String) {
					pr("This is a string", showStructure);
					pr("---------------------------", showStructure);
					System.out.println((String)o);
				} else if (o instanceof InputStream) {
					pr("This is just an input stream", showStructure);
					pr("---------------------------", showStructure);
					InputStream is = (InputStream)o;
					int c;
					while ((c = is.read()) != -1)
						System.out.write(c);
				} else {
					pr("This is an unknown type", showStructure);
					pr("---------------------------", showStructure);
					pr(o.toString(), showStructure);
				}
			} else {
				// just a separator
				pr("---------------------------", showStructure);
			}
		}
		/*
		 * If we're saving attachments, write out anything that
		 * looks like an attachment into an appropriately named
		 * file.  Don't overwrite existing files to prevent
		 * mistakes.
		 */
		if (saveAttachments && level != 0 && !p.isMimeType("multipart/*")){
			int attnum = 1;
			String disp = p.getDisposition();
			// many mailers don't include a Content-Disposition
			if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
				if (filename == null)
					filename = "Attachment" + attnum++;
				pr("Saving attachment to file " + filename, showStructure);
				try {
					File f = new File(filename);
					if (f.exists())
						// XXX - could try a series of names
						throw new IOException("file exists");
					((MimeBodyPart)p).saveFile(f);
				} catch (IOException ex) {
					pr("Failed to save attachment: " + ex, showStructure);
				}
				pr("---------------------------", showStructure);
			}
		}
	}

	public static void dumpEnvelope(Message m, boolean showStructure) throws Exception {
		pr("This is the message envelope", showStructure);
		pr("---------------------------", showStructure);
		Address[] a;
		// FROM
		if ((a = m.getFrom()) != null) {
			for (int j = 0; j < a.length; j++)
				pr("FROM: " + a[j].toString(), showStructure);
		}
		// TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++) {
				pr("TO: " + a[j].toString(), showStructure);
				InternetAddress ia = (InternetAddress)a[j];
				if (ia.isGroup()) {
					InternetAddress[] aa = ia.getGroup(false);
					for (int k = 0; k < aa.length; k++)
						pr("  GROUP: " + aa[k].toString(), showStructure);
				} 
			}
		}
		// SUBJECT
		pr("SUBJECT: " + m.getSubject(), showStructure);
		// DATE
		Date d = m.getSentDate();
		pr("SendDate: " +
				(d != null ? d.toString() : "UNKNOWN"), showStructure);
		// FLAGS
		Flags flags = m.getFlags();
		StringBuffer sb = new StringBuffer();
		Flags.Flag[] sf = flags.getSystemFlags(); // get the system flags
		boolean first = true;
		for (int i = 0; i < sf.length; i++) {
			String s;
			Flags.Flag f = sf[i];
			if (f == Flags.Flag.ANSWERED)
				s = "\\Answered";
			else if (f == Flags.Flag.DELETED)
				s = "\\Deleted";
			else if (f == Flags.Flag.DRAFT)
				s = "\\Draft";
			else if (f == Flags.Flag.FLAGGED)
				s = "\\Flagged";
			else if (f == Flags.Flag.RECENT)
				s = "\\Recent";
			else if (f == Flags.Flag.SEEN)
				s = "\\Seen";
			else
				continue;
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append(s);
		}
		// skip it
		String[] uf = flags.getUserFlags(); // get the user flag strings
		for (int i = 0; i < uf.length; i++) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append(uf[i]);
		}
		pr("FLAGS: " + sb.toString(), showStructure);
		// X-MAILER
		String[] hdrs = m.getHeader("X-Mailer");
		if (hdrs != null)
			pr("X-Mailer: " + hdrs[0], showStructure);
		else
			pr("X-Mailer NOT available", showStructure);
	}
	
	static String indentStr = "                                          ";
	static int level = 0;
	
	/**
	 * Print a, possibly indented, string.
	 */
	public static void pr(String s, boolean showStructure) {
		if (showStructure)
			System.out.print(indentStr.substring(0, level * 2));
		System.out.println(s);
	}
	
}
