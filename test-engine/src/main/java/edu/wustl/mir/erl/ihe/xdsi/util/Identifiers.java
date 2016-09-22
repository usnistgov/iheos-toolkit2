package edu.wustl.mir.erl.ihe.xdsi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Identifiers {
	
	private static SimpleDateFormat hack = new SimpleDateFormat("yyyyMMddHHmmsss");
	
	public static String generateIdentifierDepartment() {
		return "DEPT" + getUIDTail();
	}
	
	public static String generateIdentifierAffinityDomain() {
		return "AD" + getUIDTail();
	}
	
	public static String generateAccessionNumber() {
		return "AC" + getUIDTail().substring(2);
	}
	
	public static String getAssigningAuthorityAffinityDomain() {
		return "&1.3.6.1.4.1.21367.2005.13.20.1000&ISO";
	}
	
	public static String getAssigningAuthorityDepartment() {
		return "&1.3.6.1.4.1.21367.1800.13.20.1000&ISO";
	}
	
	public static String getUIDBase() {
		return "1.3.6.1.4.1.21367.201599";
	}
	
	public static String generateStudyInstanceUID() {
		return getUIDBase() + ".1." + getUIDTail();
	}
	
	public static String generateSeriesInstanceUID() {
		return getUIDBase() + ".2." + getUIDTail();
	}
	
	public static String generateSOPInstanceUID() {
		return getUIDBase() + ".3." + getUIDTail();
	}
	
	public static String generateUniqueOID() {
	   return "2.25." + getUIDTail();
	}
	
	private static String lastTimeHack = "";
	private static int timeHackCounter = 0;
	private static synchronized String getUIDTail() {
		String timeHack = hack.format(new Date());
		if (lastTimeHack.equals(timeHack)) {
			timeHackCounter++;
			return timeHack + "." + timeHackCounter;
		} else {
			lastTimeHack = timeHack;
			timeHackCounter = 0;
			return timeHack;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0) {
			String id="";
			if (args[0].equals("DEPARTMENT")) {
				id = Identifiers.generateIdentifierDepartment();
			} else if (args[0].equals("AFFINITYDOMAIN")) {
				id = Identifiers.generateIdentifierAffinityDomain();
			} else if (args[0].equals("ACCESSION")) {
				id = Identifiers.generateAccessionNumber();
			} else if (args[0].equals("STUDY_UID")) {
				id = Identifiers.generateStudyInstanceUID();
			} else if (args[0].equals("ASSIGNINGAUTHORITY-AD")) {
				id = Identifiers.getAssigningAuthorityAffinityDomain();
			}
			System.out.println(id);
		}

	}

}
