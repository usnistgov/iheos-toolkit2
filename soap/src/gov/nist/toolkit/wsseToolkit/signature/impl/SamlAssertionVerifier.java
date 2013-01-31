package gov.nist.toolkit.wsseToolkit.signature.impl;

import gov.nist.toolkit.wsseToolkit.signature.SignatureVerifier;

import java.security.Key;


/**
 * For SAML assertion, the only verification method accepted is public-key based
 * 
 * @author gerardin
 *
 */
public class SamlAssertionVerifier extends SignatureVerifier {

	@Override
	protected Key getKey() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
