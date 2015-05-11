package gov.nist.toolkit.common.adt;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdtMessage {
	String input;
//	String[] lines;
	
	static public byte[] end = { 0x1c, 0x0d }; 
	
	static public enum ErrorCodes {  NO_ERROR, ERROR_INCORRECT_BEGINNING, ERROR_INCORRECT_ENDING, ERROR_INCORRECT_MESSAGE_TYPE, ERROR_INCORRECT_NUMBER_OF_LINES };
	
	public AdtMessage(String input) {
		this.input = input;
//		lines = input.split("\n");
//		for (int i=0; i<lines.length; i++) {
//			if (lines[i] == null)
//				continue;
//			lines[i] = trimLeft(lines[i]);
//		}
	}
	
	boolean isWhite(char c) {
		if (c == ' ')
			return true;
		if (c == '\t')
			return true;
		if (c == '\n')
			return true;
		if (c == '\r')
			return true;
		return false;
	}
	
	String trimLeft(String in) {
		while (in.length() > 2 && isWhite(in.charAt(0))) {
			in = in.substring(1);
		}
		return in;
	}
	
	public AdtRecord getAdtRecord() throws ClassNotFoundException, SQLException, AdtMessageParseException {
		AdtRecordBean bean = new AdtRecordBean();
		
		bean.setPatientId(getPatientId());
		List<Hl7Name> patientNames = new ArrayList<Hl7Name>();
		String name = getPatientName();
		String[] nameparts = name.split("\\^");
		if (nameparts.length < 2)
			throw new AdtMessageParseException("Cannot decode Patient Name");
		Hl7Name hname = new Hl7Name();
		hname.setFamilyName(nameparts[0]);
		hname.setGivenName(nameparts[1]);
		patientNames.add(hname);
//		bean.setPatientNames(patientNames);
		
		return bean.getRecord();
	}
	
	public String getPatientId() throws AdtMessageParseException {
		String line = getLine("PID");
		if (line == null)
			throw new AdtMessageParseException("No PID segment\nInput was\n" + input);
		String[] parts = line.split("\\|");
		String pid = parts[3];
		return cleanUpPid(pid);
	}
	
	public String getPatientName() throws AdtMessageParseException {
		String line = getLine("PID");
		if (line == null)
			throw new AdtMessageParseException("No PID segment\nInput was\n" + input);
		String[] parts = line.split("\\|");
		String name = parts[5];
		return name;
	}
	
	String getLine(String type) {
//		for (String line : lines)
//			if (line.startsWith(type))
//				return line;
//		return null;
		
		int start = input.indexOf(type + "|");
		if (start == -1)
			return null;
		return input.substring(start);
		
	}
	
	String cleanUpPid(String pid) {
		String[] parts = pid.split("\\^");
		if (parts.length < 4)
			return pid;
		String pid3 = parts[3];
		while (pid3.charAt(0) != '&' && pid3.length() > 2)
			pid3 = pid3.substring(1);
		parts[3] = pid3;
		return join(parts, "^");
		
	}
	
	String join(String[] parts, String sep) {
		StringBuffer buf = new StringBuffer();
		
		int i=0;
		for (String part : parts) {
			if (i > 0)
				buf.append(sep);
			buf.append(part);
			i++;
		}
		
		return buf.toString();
	}
	
	public ErrorCodes isValid() {
		return ErrorCodes.NO_ERROR;
	}
	
	public char[] getAck() {
		char[] val = new char[4];
		
		val[0] = 0x0b;
		val[1] = 0x06;
		val[2] = 0x1c;
		val[3] = 0x0d;
		
		return val;
	}
	
	public char[] getNack() {
		char[] val = new char[4];
		
		val[0] = 0x0b;
		val[1] = 0x15;
		val[2] = 0x1c;
		val[3] = 0x0d;
		
		return val;
	}
	
}
