package gov.nist.toolkit.saml.util;

import java.io.Serializable;

public class CodeBean implements Serializable 
{
	protected String code;
	protected String display;
	protected String codingScheme;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getCodingScheme() {
		return codingScheme;
	}
	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}
	
}
