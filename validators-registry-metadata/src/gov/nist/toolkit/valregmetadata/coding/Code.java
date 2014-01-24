package gov.nist.toolkit.valregmetadata.coding;

public class Code {
	private String code;
	private String scheme;
	private String display;
	
	public Code(String code, String scheme, String display) {
		this.code = code;
		this.scheme = scheme;
		this.display = display;
	}

	public String getCode() {
		return code;
	}

	public String getScheme() {
		return scheme;
	}

	public String getDisplay() {
		return display;
	}
	
	public boolean equals(Code c) {
		if (c == null) return false;
		if (!c.getCode().equals(code)) return false;
		if (!c.getScheme().equals(scheme)) return false;
		return true;
	}
	
	public String toString() {
		return code + "^" + scheme + "^" + display;
	}
}
