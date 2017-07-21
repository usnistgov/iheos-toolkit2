package gov.nist.toolkit.saml.bean;

public class PhoneType {

    protected String areaCode;
    protected String countryCode;
    protected String extension;
    protected String localNumber;
    protected CeType phoneNumberType;

    /**
     * Gets the value of the areaCode property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Sets the value of the areaCode property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setAreaCode(String value) {
        this.areaCode = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the localNumber property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getLocalNumber() {
        return localNumber;
    }

    /**
     * Sets the value of the localNumber property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setLocalNumber(String value) {
        this.localNumber = value;
    }

    /**
     * Gets the value of the phoneNumberType property.
     * 
     * @return
     *     possible model is
     *     {@link CeType }
     *     
     */
    public CeType getPhoneNumberType() {
        return phoneNumberType;
    }

    /**
     * Sets the value of the phoneNumberType property.
     * 
     * @param value
     *     allowed model is
     *     {@link CeType }
     *     
     */
    public void setPhoneNumberType(CeType value) {
        this.phoneNumberType = value;
    }

}
