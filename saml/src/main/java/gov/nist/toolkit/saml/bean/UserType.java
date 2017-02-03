package gov.nist.toolkit.saml.bean;

public class UserType {

    protected PersonNameType personName;
    protected String userName;
    protected HomeCommunityType org;
    protected CeType roleCoded;

    /**
     * Gets the value of the personName property.
     * 
     * @return
     *     possible model is
     *     {@link PersonNameType }
     *     
     */
    public PersonNameType getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     * 
     * @param value
     *     allowed model is
     *     {@link PersonNameType }
     *     
     */
    public void setPersonName(PersonNameType value) {
        this.personName = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible model is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed model is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the org property.
     * 
     * @return
     *     possible model is
     *     {@link HomeCommunityType }
     *     
     */
    public HomeCommunityType getOrg() {
        return org;
    }

    /**
     * Sets the value of the org property.
     * 
     * @param value
     *     allowed model is
     *     {@link HomeCommunityType }
     *     
     */
    public void setOrg(HomeCommunityType value) {
        this.org = value;
    }

    /**
     * Gets the value of the roleCoded property.
     * 
     * @return
     *     possible model is
     *     {@link CeType }
     *     
     */
    public CeType getRoleCoded() {
        return roleCoded;
    }

    /**
     * Sets the value of the roleCoded property.
     * 
     * @param value
     *     allowed model is
     *     {@link CeType }
     *     
     */
    public void setRoleCoded(CeType value) {
        this.roleCoded = value;
    }

}
