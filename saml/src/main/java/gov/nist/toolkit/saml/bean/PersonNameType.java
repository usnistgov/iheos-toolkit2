package gov.nist.toolkit.saml.bean;

public class PersonNameType {

    protected String familyName;
    protected String givenName;
    protected CeType nameType;
    protected String secondNameOrInitials;
    protected String fullName;
    protected String prefix;
    protected String suffix;

    /**
     * Gets the value of the familyName property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Sets the value of the familyName property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setFamilyName(String value) {
        this.familyName = value;
    }

    /**
     * Gets the value of the givenName property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Sets the value of the givenName property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setGivenName(String value) {
        this.givenName = value;
    }

    /**
     * Gets the value of the nameType property.
     * 
     * @return
     *     possible model is
     *     {@link CeType }
     *     
     */
    public CeType getNameType() {
        return nameType;
    }

    /**
     * Sets the value of the nameType property.
     * 
     * @param value
     *     allowed model is
     *     {@link CeType }
     *     
     */
    public void setNameType(CeType value) {
        this.nameType = value;
    }

    /**
     * Gets the value of the secondNameOrInitials property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getSecondNameOrInitials() {
        return secondNameOrInitials;
    }

    /**
     * Sets the value of the secondNameOrInitials property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setSecondNameOrInitials(String value) {
        this.secondNameOrInitials = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the prefix property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the value of the prefix property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setPrefix(String value) {
        this.prefix = value;
    }

    /**
     * Gets the value of the suffix property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets the value of the suffix property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setSuffix(String value) {
        this.suffix = value;
    }

}
