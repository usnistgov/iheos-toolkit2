package gov.nist.toolkit.saml.bean;

public class SamlAuthzDecisionStatementEvidenceConditionsType {

    protected String notBefore;
    protected String notOnOrAfter;

    /**
     * Gets the value of the notBefore property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getNotBefore() {
        return notBefore;
    }

    /**
     * Sets the value of the notBefore property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setNotBefore(String value) {
        this.notBefore = value;
    }

    /**
     * Gets the value of the notOnOrAfter property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getNotOnOrAfter() {
        return notOnOrAfter;
    }

    /**
     * Sets the value of the notOnOrAfter property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setNotOnOrAfter(String value) {
        this.notOnOrAfter = value;
    }

}
