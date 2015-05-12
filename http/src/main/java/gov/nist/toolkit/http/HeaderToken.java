package gov.nist.toolkit.http;

public enum HeaderToken {
	OPEN_PAREN("("),
	CLOSE_PAREN(")"),
	LT("<"),
	GT(">"),
	EQUALS("="),
	AT("@"),
	COMMA(","),
	SEMICOLON(";"),
	COLON(":"),
	PERIOD("."),
	OPEN_BRACKET("["),
	CLOSE_BRACKET("]")
	;
	
	String value;
	
	HeaderToken(String value) { this.value = value; }
	
	static public boolean isSpecial(char c) {
		return 
				OPEN_PAREN.value.charAt(0) == c 
				|| CLOSE_PAREN.value.charAt(0) == c
				|| LT.value.charAt(0) == c
				|| GT.value.charAt(0) == c
				|| AT.value.charAt(0) == c
				|| COMMA.value.charAt(0) == c
				|| EQUALS.value.charAt(0) == c
				|| SEMICOLON.value.charAt(0) == c
				|| COLON.value.charAt(0) == c
				|| PERIOD.value.charAt(0) == c
				|| OPEN_BRACKET.value.charAt(0) == c
				|| CLOSE_BRACKET.value.charAt(0) == c;
	}
	
	static public boolean isSpecial(String s) {
		if (s.length() == 1)
			return isSpecial(s.charAt(0));
		return false;
	}
	
	public boolean equals(HeaderToken t) {
		if (t == null) return false;
		return value.equals(t.value);
	}
	
	public String toString() { return value; }
	
	static public HeaderToken toToken(String string) {
		for (HeaderToken ht : values()) {
			if (ht.value.equals(string))
				return ht;
		}
		return null;
	}
	
	static public boolean isHeaderToken(String string) { return toToken(string) != null; }
}
