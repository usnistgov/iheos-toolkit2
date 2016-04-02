package gov.nist.toolkit.saml.util;

import javax.security.auth.callback.CallbackHandler;

import org.opensaml.common.SAMLVersion;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SAMLParms {
    private String issuer;
    private SAMLVersion samlVersion;
    private CallbackHandler samlCallbackHandler;

    /**
     * Method getIssuer returns the issuer of this SAMLParms model.
     *
     * @return the issuer (type String) of this SAMLParms model.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Method setIssuer sets the issuer of this SAMLParms model.
     *
     * @param issuer the issuer of this SAMLParms model.
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    /**
     * Get the SAML Version of the SAML Assertion to generate
     * @return the SAML Version of the SAML Assertion to generate
     */
    public SAMLVersion getSAMLVersion() {
        return samlVersion;
    }
    
    /**
     * Set the SAML Version of the SAML Assertion to generate
     * @param samlVersion the SAML Version of the SAML Assertion to generate
     */
    public void setSAMLVersion(SAMLVersion samlVersion) {
        this.samlVersion = samlVersion;
    }
    
    /**
     * Get the CallbackHandler instance used to populate the SAML Assertion content
     * @return the CallbackHandler instance used to populate the SAML Assertion content
     */
    public CallbackHandler getCallbackHandler() {
        return samlCallbackHandler;
    }
    
    /**
     * Set the CallbackHandler instance used to populate the SAML Assertion content
     * @param samlCallbackHandler the CallbackHandler instance used to populate the 
     *        SAML Assertion content
     */
    public void setCallbackHandler(CallbackHandler samlCallbackHandler) {
        this.samlCallbackHandler = samlCallbackHandler;
    }
    
    
    }
