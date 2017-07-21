package gov.nist.toolkit.saml.bean;

public class SamlAuthnStatementType {

    protected String authInstant;
    protected String sessionIndex;
    protected String authContextClassRef;
    protected String subjectLocalityAddress;
    protected String subjectLocalityDNSName;

    /**
     * Gets the value of the authInstant property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getAuthInstant() {
        return authInstant;
    }

    /**
     * Sets the value of the authInstant property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setAuthInstant(String value) {
        this.authInstant = value;
    }

    /**
     * Gets the value of the sessionIndex property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getSessionIndex() {
        return sessionIndex;
    }

    /**
     * Sets the value of the sessionIndex property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setSessionIndex(String value) {
        this.sessionIndex = value;
    }

    /**
     * Gets the value of the authContextClassRef property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getAuthContextClassRef() {
        return authContextClassRef;
    }

    /**
     * Sets the value of the authContextClassRef property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setAuthContextClassRef(String value) {
        this.authContextClassRef = value;
    }

    /**
     * Gets the value of the subjectLocalityAddress property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getSubjectLocalityAddress() {
        return subjectLocalityAddress;
    }

    /**
     * Sets the value of the subjectLocalityAddress property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setSubjectLocalityAddress(String value) {
        this.subjectLocalityAddress = value;
    }

    /**
     * Gets the value of the subjectLocalityDNSName property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getSubjectLocalityDNSName() {
        return subjectLocalityDNSName;
    }

    /**
     * Sets the value of the subjectLocalityDNSName property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setSubjectLocalityDNSName(String value) {
        this.subjectLocalityDNSName = value;
    }

}
