package gov.nist.toolkit.osid.repository;

public class RepositoryUtils {

	public static String AS_FILENAME(String s) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(s);
		for (int i=0; i<s.length(); i++) {
			char c = buf.charAt(i);
			if (c == ' ' || c == '\n' || c == '\t') {
				c = '_';
				buf.setCharAt(i, c);
			}
		}
		
		return buf.toString();
	}
}
