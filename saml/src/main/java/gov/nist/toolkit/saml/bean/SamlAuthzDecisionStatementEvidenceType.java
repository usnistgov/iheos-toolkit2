package gov.nist.toolkit.saml.bean;

public class SamlAuthzDecisionStatementEvidenceType {

    protected SamlAuthzDecisionStatementEvidenceAssertionType assertion;

    /**
     * Gets the value of the assertion property.
     * 
     * @return
     *     possible model is
     *     {@link SamlAuthzDecisionStatementEvidenceAssertionType }
     *     
     */
    public SamlAuthzDecisionStatementEvidenceAssertionType getAssertion() {
        return assertion;
    }

    /**
     * Sets the value of the assertion property.
     * 
     * @param value
     *     allowed model is
     *     {@link SamlAuthzDecisionStatementEvidenceAssertionType }
     *     
     */
    public void setAssertion(SamlAuthzDecisionStatementEvidenceAssertionType value) {
        this.assertion = value;
    }

}
