package gov.nist.direct.utils;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

public class ParseUtils {
	

	
	/**
	 * Looking for header in a Message
	 * 
	 * @param m Message
	 * @param header Targeted header
	 * @return Header value
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<String> searchHeader(Part m, String header) {
		String[] tab = {header};
		String head = "";
		ArrayList<String> res = new ArrayList<String>();
		Enumeration e = null;
		try {
			e = m.getMatchingHeaders(tab);
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		while (e.hasMoreElements()) {
			Header hed = (Header)e.nextElement();
			head = hed.getValue();
			res.add(head);
		}
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	public static String searchHeaderSimple(Part m, String header) {
		String[] tab = {header};
		String head = "";
		Enumeration e = null;
		try {
			e = m.getMatchingHeaders(tab);
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		while (e.hasMoreElements()) {
			Header hed = (Header)e.nextElement();
			head = hed.getValue();
		}
		return head;
	}
	

	@SuppressWarnings("rawtypes")
	public static boolean isHeaderPresent(Part m, String header) {
		String[] tab = {header};
		String head = "";
		Enumeration e = null;
		try {
			e = m.getAllHeaders();
		} catch (MessagingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		while (e.hasMoreElements()) {
			Header hed = (Header)e.nextElement();
			head = hed.getValue();
			System.out.println(header);
			System.out.println(head);
			if (head.contains(header)) return true;
			else return false;
		}
		return false;
	}


}
