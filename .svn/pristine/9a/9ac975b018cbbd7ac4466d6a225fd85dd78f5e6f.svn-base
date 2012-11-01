package gov.nist.toolkit.directsim;

public class DirectEmailAddr {
	String email;
	
	public DirectEmailAddr(String email) {
		this.email = email;
	}
	
	// If you're not sure whether you've already converted the 
	// name, this should be callable repeatabily without changing
	// the answer.
	public String filename() {
		return email.replaceAll("\\.", "-").replace('@', '-');
	}
	
	public static String FILENAME(String email) {
		return new DirectEmailAddr(email).filename();
	}
	
	public String toString() {
		return email;
	}
}
