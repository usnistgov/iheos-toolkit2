package gov.nist.toolkit.saml.bean;

public class SamlSignatureType {

    protected SamlSignatureKeyInfoType keyInfo;
    protected byte[] signatureValue;

    /**
     * Gets the value of the keyInfo property.
     * 
     * @return
     *     possible model is
     *     {@link SamlSignatureKeyInfoType }
     *     
     */
    public SamlSignatureKeyInfoType getKeyInfo() {
        return keyInfo;
    }

    /**
     * Sets the value of the keyInfo property.
     * 
     * @param value
     *     allowed model is
     *     {@link SamlSignatureKeyInfoType }
     *     
     */
    public void setKeyInfo(SamlSignatureKeyInfoType value) {
        this.keyInfo = value;
    }

    /**
     * Gets the value of the signatureValue property.
     * 
     * @return
     *     possible model is
     *     byte[]
     */
    public byte[] getSignatureValue() {
        return signatureValue;
    }

    /**
     * Sets the value of the signatureValue property.
     * 
     * @param value
     *     allowed model is
     *     byte[]
     */
    public void setSignatureValue(byte[] value) {
        this.signatureValue = ((byte[]) value);
    }

}
