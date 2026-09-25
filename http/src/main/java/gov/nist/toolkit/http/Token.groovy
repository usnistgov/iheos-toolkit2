package gov.nist.toolkit.http;

public class Token {
	HeaderToken token = null;
	String string = null;
	
	public Token(HeaderToken token) { this.token = token; }
	public Token(String string) {
		if (HeaderToken.isHeaderToken(string))
			this.token = HeaderToken.toToken(string);
		else
			this.string = string; 
	}
	public boolean isString() { return string != null; }
	public boolean isToken() { return !isString(); }
	public String getValue() { 
		if (isString())
			return string;
		return token.value;
	}
	
	public HeaderToken getToken() { return token; }
	
	public String toString() { return getValue(); }
	
	public boolean equals(Token t) { 
		if (t == null)
			return false;
		if (token == null && t.token != null) {
			return false;
		} else {
			if (token != null && !token.equals(t.token)) return false;
		}
		if (token != null && token.equals(t.token)) return true;
		if (string == null && t.string != null) {
			return false;
		} else {
			if (!string.equals(t.string)) return false;
		}
		return true;
	}
	
	public boolean equals(String s) {
		if (isToken()) return false;
		return string.equals(s);
	}
	
	public boolean equals(HeaderToken t) {
		if (isString()) return false;
		return token.equals(t);
	}
}
