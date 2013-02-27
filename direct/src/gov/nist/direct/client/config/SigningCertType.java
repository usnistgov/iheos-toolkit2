package gov.nist.direct.client.config;


public enum SigningCertType {
	GOOD_CERT ("Sign content with good certificate", "signing_cert"),
	INVALID_CERT ("Sign with invalid certificate (attributes specified by Direct not present)", "inv_signing_cert"),
	EXPIRED_CERT ("Sign with expired certificate", "exp_signing_cert"),
	CERT_FROM_DIFFERENT_TRUST_ANCHOR ("Sign with a valid cert not aligned with current trust anchor", "diff_trust_anchor");
	
	String description;
	String subdir;   // sub-directory holding this signing cert type
	
	SigningCertType() {}
	
	SigningCertType(String description, String subdir) {
		this.description = description;
		this.subdir = subdir;
	}
	
	public String getDescription() { return description; }
	public String getSubdir() { return subdir; }
	
}
