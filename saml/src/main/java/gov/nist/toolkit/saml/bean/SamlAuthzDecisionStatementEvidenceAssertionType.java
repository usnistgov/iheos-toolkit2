package gov.nist.toolkit.saml.bean;

public class SamlAuthzDecisionStatementEvidenceAssertionType {

    protected String id;
    protected String issueInstant;
    protected String version;
    protected String issuer;
    protected String issuerFormat;
    protected SamlAuthzDecisionStatementEvidenceConditionsType conditions;
    protected String accessConsentPolicy;
    protected String instanceAccessConsentPolicy;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the issueInstant property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getIssueInstant() {
        return issueInstant;
    }

    /**
     * Sets the value of the issueInstant property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setIssueInstant(String value) {
        this.issueInstant = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the issuer property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the value of the issuer property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setIssuer(String value) {
        this.issuer = value;
    }

    /**
     * Gets the value of the issuerFormat property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getIssuerFormat() {
        return issuerFormat;
    }

    /**
     * Sets the value of the issuerFormat property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setIssuerFormat(String value) {
        this.issuerFormat = value;
    }

    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible model is
     *     {@link SamlAuthzDecisionStatementEvidenceConditionsType }
     *     
     */
    public SamlAuthzDecisionStatementEvidenceConditionsType getConditions() {
        return conditions;
    }

    /**
     * Sets the value of the conditions property.
     * 
     * @param value
     *     allowed model is
     *     {@link SamlAuthzDecisionStatementEvidenceConditionsType }
     *     
     */
    public void setConditions(SamlAuthzDecisionStatementEvidenceConditionsType value) {
        this.conditions = value;
    }

    /**
     * Gets the value of the accessConsentPolicy property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getAccessConsentPolicy() {
        return accessConsentPolicy;
    }

    /**
     * Sets the value of the accessConsentPolicy property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setAccessConsentPolicy(String value) {
        this.accessConsentPolicy = value;
    }

    /**
     * Gets the value of the instanceAccessConsentPolicy property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getInstanceAccessConsentPolicy() {
        return instanceAccessConsentPolicy;
    }

    /**
     * Sets the value of the instanceAccessConsentPolicy property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setInstanceAccessConsentPolicy(String value) {
        this.instanceAccessConsentPolicy = value;
    }

}
