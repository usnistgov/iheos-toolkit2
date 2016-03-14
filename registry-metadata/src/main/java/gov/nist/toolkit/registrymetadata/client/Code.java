package gov.nist.toolkit.registrymetadata.client;

public class Code {
	public String code;
	public String display;
	public String system;
	
	public Code(String codeDef) throws Exception {
		String[] parts = codeDef.split("\\^");
		if (parts.length != 3) 
			throw new Exception("Code " + codeDef + " is not a valid code format");
		code = parts[0];
		display = parts[1];
		system = parts[2];
	}
	
	public String getNoDisplay() {
		return code + "^^" + system;
	}
	
	public String get() {
		return code + "^" + display + "^" + system;
	}
}
