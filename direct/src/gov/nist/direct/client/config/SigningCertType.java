package gov.nist.direct.client.config;


public enum SigningCertType {
	GOOD_CERT ("Sign content with good certificate", "signing_cert", ".p12"),
	INVALID_CERT ("Sign with invalid certificate (attributes specified by Direct not present)", "inv_signing_cert", ".p12"),
	EXPIRED_CERT ("Sign with expired certificate", "exp_signing_cert", ".p12"),
	CERT_FROM_DIFFERENT_TRUST_ANCHOR ("Sign with a valid cert not aligned with current trust anchor", "diff_trust_anchor", ".p12");
	
	String description;
	String subdir;   // sub-directory holding this signing cert type
	String suffix;
	
	SigningCertType() {}
	
	SigningCertType(String description, String subdir, String suffix) {
		this.description = description;
		this.subdir = subdir;
		this.suffix = suffix;
	}
	
	public String getDescription() { return description; }
	public String getSubdir() { return subdir; }
	public String getSuffix() { return suffix; }
	
}
