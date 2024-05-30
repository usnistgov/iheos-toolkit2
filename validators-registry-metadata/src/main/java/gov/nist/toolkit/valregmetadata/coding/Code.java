package gov.nist.toolkit.valregmetadata.coding;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Code code1 = (Code) o;
		return code.equals(code1.code) &&
				scheme.equals(code1.scheme);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, scheme);
	}

	public String toString() {
		return code + "^" + display + "^" + scheme;
	}
}
