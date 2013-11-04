package gov.nist.direct.mdn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDNUtils {
	
	final static String atom = "[0-9,a-z,A-Z]*";
	final static String text = "[0-9,a-z,A-Z,_,.,\\-]*";
	final static String textWithSpace = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
	final static String whitespace = "\\s";
	final static String actionMode = "(manual-action|automatic-action)";
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
		Pattern pattern = Pattern.compile(textWithSpace, Pattern.CASE_INSENSITIVE);
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

}
