package gov.nist.toolkit.testengine.smtp;

public class SMTPAddress {
	
	public String parseEmailAddr(String header) throws Exception {
		String[] parts = header.split(":");
		String value;
		if (parts.length != 2)
			value = header;
		else
			value = parts[1];

		int i = value.indexOf('@');
		if (i == -1) {
			throw new Exception("Cannot parse email address: " + value + " - no @ character present");
		}
		while(i > 0 && value.charAt(i) != ' ' && value.charAt(i) != '<')
			i--;
		int from = i;
		if (value.charAt(from) == '<')
			from++;

		i = value.indexOf('@');
		if (i == -1) {
			throw new Exception("Cannot parse email address: " + value + " - no @ character present");
		}
		while(i < value.length() && value.charAt(i) != ' ' && value.charAt(i) != '>')
			i++;
		int to = i;
		if (to >= value.length() || value.charAt(to) == '>')
			to--;
		String xx = value.substring(from, to+1); 

		xx = xx.trim();
		if (xx.charAt(0) == '"' && xx.charAt(xx.length()-1) == '"') {
			xx = xx.substring(1, xx.length()-1);
		}
		return xx;
	}
	
	public String properEmailAddr(String header) throws Exception {
		return "<" + parseEmailAddr(header) + ">";
	}


}
