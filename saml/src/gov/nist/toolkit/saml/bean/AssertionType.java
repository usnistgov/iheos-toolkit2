package gov.nist.toolkit.saml.bean;

import java.util.ArrayList;
import java.util.List;

public class AssertionType {

    protected AddressType address;
    protected String dateOfBirth;
    protected String explanationNonClaimantSignature;
    protected boolean haveSecondWitnessSignature;
    protected boolean haveSignature;
    protected boolean haveWitnessSignature;
    protected HomeCommunityType homeCommunity;
    protected PersonNameType personName;
    protected PhoneType phoneNumber;
    protected AddressType secondWitnessAddress;
    protected PersonNameType secondWitnessName;
    protected PhoneType secondWitnessPhone;
    protected String ssn;
    protected List<String> uniquePatientId;
    protected AddressType witnessAddress;
    protected PersonNameType witnessName;
    protected PhoneType witnessPhone;
    protected UserType userInfo;
    protected boolean authorized;
    protected CeType purposeOfDisclosureCoded;
    protected SamlAuthnStatementType samlAuthnStatement;
    protected SamlAuthzDecisionStatementType samlAuthzDecisionStatement;
    protected SamlSignatureType samlSignature;
    protected String messageId;
    protected List<String> relatesToList;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setAddress(AddressType value) {
        this.address = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfBirth(String value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the explanationNonClaimantSignature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExplanationNonClaimantSignature() {
        return explanationNonClaimantSignature;
    }

    /**
     * Sets the value of the explanationNonClaimantSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExplanationNonClaimantSignature(String value) {
        this.explanationNonClaimantSignature = value;
    }

    /**
     * Gets the value of the haveSecondWitnessSignature property.
     * 
     */
    public boolean isHaveSecondWitnessSignature() {
        return haveSecondWitnessSignature;
    }

    /**
     * Sets the value of the haveSecondWitnessSignature property.
     * 
     */
    public void setHaveSecondWitnessSignature(boolean value) {
        this.haveSecondWitnessSignature = value;
    }

    /**
     * Gets the value of the haveSignature property.
     * 
     */
    public boolean isHaveSignature() {
        return haveSignature;
    }

    /**
     * Sets the value of the haveSignature property.
     * 
     */
    public void setHaveSignature(boolean value) {
        this.haveSignature = value;
    }

    /**
     * Gets the value of the haveWitnessSignature property.
     * 
     */
    public boolean isHaveWitnessSignature() {
        return haveWitnessSignature;
    }

    /**
     * Sets the value of the haveWitnessSignature property.
     * 
     */
    public void setHaveWitnessSignature(boolean value) {
        this.haveWitnessSignature = value;
    }

    /**
     * Gets the value of the homeCommunity property.
     * 
     * @return
     *     possible object is
     *     {@link HomeCommunityType }
     *     
     */
    public HomeCommunityType getHomeCommunity() {
        return homeCommunity;
    }

    /**
     * Sets the value of the homeCommunity property.
     * 
     * @param value
     *     allowed object is
     *     {@link HomeCommunityType }
     *     
     */
    public void setHomeCommunity(HomeCommunityType value) {
        this.homeCommunity = value;
    }

    /**
     * Gets the value of the personName property.
     * 
     * @return
     *     possible object is
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
     *     allowed object is
     *     {@link PersonNameType }
     *     
     */
    public void setPersonName(PersonNameType value) {
        this.personName = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneType }
     *     
     */
    public PhoneType getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneType }
     *     
     */
    public void setPhoneNumber(PhoneType value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the secondWitnessAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getSecondWitnessAddress() {
        return secondWitnessAddress;
    }

    /**
     * Sets the value of the secondWitnessAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setSecondWitnessAddress(AddressType value) {
        this.secondWitnessAddress = value;
    }

    /**
     * Gets the value of the secondWitnessName property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameType }
     *     
     */
    public PersonNameType getSecondWitnessName() {
        return secondWitnessName;
    }

    /**
     * Sets the value of the secondWitnessName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameType }
     *     
     */
    public void setSecondWitnessName(PersonNameType value) {
        this.secondWitnessName = value;
    }

    /**
     * Gets the value of the secondWitnessPhone property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneType }
     *     
     */
    public PhoneType getSecondWitnessPhone() {
        return secondWitnessPhone;
    }

    /**
     * Sets the value of the secondWitnessPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneType }
     *     
     */
    public void setSecondWitnessPhone(PhoneType value) {
        this.secondWitnessPhone = value;
    }

    /**
     * Gets the value of the ssn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSSN() {
        return ssn;
    }

    /**
     * Sets the value of the ssn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSSN(String value) {
        this.ssn = value;
    }

    /**
     * Gets the value of the uniquePatientId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uniquePatientId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUniquePatientId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUniquePatientId() {
        if (uniquePatientId == null) {
            uniquePatientId = new ArrayList<String>();
        }
        return this.uniquePatientId;
    }

    /**
     * Gets the value of the witnessAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getWitnessAddress() {
        return witnessAddress;
    }

    /**
     * Sets the value of the witnessAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setWitnessAddress(AddressType value) {
        this.witnessAddress = value;
    }

    /**
     * Gets the value of the witnessName property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameType }
     *     
     */
    public PersonNameType getWitnessName() {
        return witnessName;
    }

    /**
     * Sets the value of the witnessName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameType }
     *     
     */
    public void setWitnessName(PersonNameType value) {
        this.witnessName = value;
    }

    /**
     * Gets the value of the witnessPhone property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneType }
     *     
     */
    public PhoneType getWitnessPhone() {
        return witnessPhone;
    }

    /**
     * Sets the value of the witnessPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneType }
     *     
     */
    public void setWitnessPhone(PhoneType value) {
        this.witnessPhone = value;
    }

    /**
     * Gets the value of the userInfo property.
     * 
     * @return
     *     possible object is
     *     {@link UserType }
     *     
     */
    public UserType getUserInfo() {
        return userInfo;
    }

    /**
     * Sets the value of the userInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserType }
     *     
     */
    public void setUserInfo(UserType value) {
        this.userInfo = value;
    }

    /**
     * Gets the value of the authorized property.
     * 
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Sets the value of the authorized property.
     * 
     */
    public void setAuthorized(boolean value) {
        this.authorized = value;
    }

    /**
     * Gets the value of the purposeOfDisclosureCoded property.
     * 
     * @return
     *     possible object is
     *     {@link CeType }
     *     
     */
    public CeType getPurposeOfDisclosureCoded() {
        return purposeOfDisclosureCoded;
    }

    /**
     * Sets the value of the purposeOfDisclosureCoded property.
     * 
     * @param value
     *     allowed object is
     *     {@link CeType }
     *     
     */
    public void setPurposeOfDisclosureCoded(CeType value) {
        this.purposeOfDisclosureCoded = value;
    }

    /**
     * Gets the value of the samlAuthnStatement property.
     * 
     * @return
     *     possible object is
     *     {@link SamlAuthnStatementType }
     *     
     */
    public SamlAuthnStatementType getSamlAuthnStatement() {
        return samlAuthnStatement;
    }

    /**
     * Sets the value of the samlAuthnStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link SamlAuthnStatementType }
     *     
     */
    public void setSamlAuthnStatement(SamlAuthnStatementType value) {
        this.samlAuthnStatement = value;
    }

    /**
     * Gets the value of the samlAuthzDecisionStatement property.
     * 
     * @return
     *     possible object is
     *     {@link SamlAuthzDecisionStatementType }
     *     
     */
    public SamlAuthzDecisionStatementType getSamlAuthzDecisionStatement() {
        return samlAuthzDecisionStatement;
    }

    /**
     * Sets the value of the samlAuthzDecisionStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link SamlAuthzDecisionStatementType }
     *     
     */
    public void setSamlAuthzDecisionStatement(SamlAuthzDecisionStatementType value) {
        this.samlAuthzDecisionStatement = value;
    }

    /**
     * Gets the value of the samlSignature property.
     * 
     * @return
     *     possible object is
     *     {@link SamlSignatureType }
     *     
     */
    public SamlSignatureType getSamlSignature() {
        return samlSignature;
    }

    /**
     * Sets the value of the samlSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SamlSignatureType }
     *     
     */
    public void setSamlSignature(SamlSignatureType value) {
        this.samlSignature = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the relatesToList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatesToList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatesToList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRelatesToList() {
        if (relatesToList == null) {
            relatesToList = new ArrayList<String>();
        }
        return this.relatesToList;
    }

}
