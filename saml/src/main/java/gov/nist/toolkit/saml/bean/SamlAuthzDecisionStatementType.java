package gov.nist.toolkit.saml.bean;

public class SamlAuthzDecisionStatementType {

    protected String decision;
    protected String resource;
    protected String action;
    protected String actionNameSpace ;
    protected SamlAuthzDecisionStatementEvidenceType evidence;

    /**
	 * @return the actionNameSpace
	 */
	public String getActionNameSpace() {
		return actionNameSpace;
	}

	/**
	 * @param actionNameSpace the actionNameSpace to set
	 */
	public void setActionNameSpace(String actionNameSpace) {
		this.actionNameSpace = actionNameSpace;
	}

	/**
     * Gets the value of the decision property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getDecision() {
        return decision;
    }

    /**
     * Sets the value of the decision property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setDecision(String value) {
        this.decision = value;
    }

    /**
     * Gets the value of the resource property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getResource() {
        return resource;
    }

    /**
     * Sets the value of the resource property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setResource(String value) {
        this.resource = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the evidence property.
     * 
     * @return
     *     possible model is
     *     {@link SamlAuthzDecisionStatementEvidenceType }
     *     
     */
    public SamlAuthzDecisionStatementEvidenceType getEvidence() {
        return evidence;
    }

    /**
     * Sets the value of the evidence property.
     * 
     * @param value
     *     allowed model is
     *     {@link SamlAuthzDecisionStatementEvidenceType }
     *     
     */
    public void setEvidence(SamlAuthzDecisionStatementEvidenceType value) {
        this.evidence = value;
    }

}
