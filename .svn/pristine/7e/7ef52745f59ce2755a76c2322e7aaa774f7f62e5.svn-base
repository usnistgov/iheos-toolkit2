package gov.nist.toolkit.saml.builder.bean;

import gov.nist.toolkit.saml.util.SamlConstants;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SubjectBean {
    private String subjectName;
    private String subjectNameIDFormat = SamlConstants.NAMEID_FORMAT_UNSPECIFIED;
    private String subjectNameQualifier;
    private String subjectConfirmationMethod;
    private KeyInfoBean keyInfo;

    /**
     * Constructor SubjectBean creates a new SubjectBean instance.
     */
    public SubjectBean() {
    }

    /**
     * Constructor SubjectBean creates a new SubjectBean instance.
     *
     * @param subjectName of type String
     * @param subjectNameQualifier of type String
     * @param subjectConfirmationMethod of type String
     */
    public SubjectBean(
        String subjectName, 
        String subjectNameQualifier, 
        String subjectConfirmationMethod
    ) {
        this.subjectName = subjectName;
        this.subjectNameQualifier = subjectNameQualifier;
        this.subjectConfirmationMethod = subjectConfirmationMethod;
    }
    
    /**
     * Constructor SubjectBean creates a new SubjectBean instance.
     *
     * @param subjectName of type String
     * @param subjectNameQualifier of type String
     * @param subjectConfirmationMethod of type String
     * @param subjectNameIDFormat of type String
     */
    public SubjectBean(
        String subjectName, 
        String subjectNameQualifier, 
        String subjectConfirmationMethod,
        String subjectNameIDFormat
    ) {
        this(subjectName, subjectNameQualifier, subjectConfirmationMethod);
        this.subjectNameIDFormat = subjectNameIDFormat;
    }

    /**
     * Method getSubjectName returns the subjectName of this SubjectBean object.
     *
     * @return the subjectName (type String) of this SubjectBean object.
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Method setSubjectName sets the subjectName of this SubjectBean object.
     *
     * @param subjectName the subjectName of this SubjectBean object.
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    
    /**
     * Method getSubjectNameQualifier returns the subjectNameQualifier of this SubjectBean object.
     *
     * @return the subjectNameQualifier (type String) of this SubjectBean object.
     */
    public String getSubjectNameQualifier() {
        return subjectNameQualifier;
    }

    /**
     * Method setSubjectNameQualifier sets the subjectNameQualifier of this SubjectBean object.
     *
     * @param subjectNameQualifier the subjectNameQualifier of this SubjectBean object.
     */
    public void setSubjectNameQualifier(String subjectNameQualifier) {
        this.subjectNameQualifier = subjectNameQualifier;
    }
    
    /**
     * Method getSubjectConfirmationMethod returns the subjectConfirmationMethod of
     * this SubjectBean object.
     *
     * @return the subjectConfirmationMethod (type String) of this SubjectBean object.
     */
    public String getSubjectConfirmationMethod() {
        return subjectConfirmationMethod;
    }

    /**
     * Method setSubjectConfirmationMethod sets the subjectConfirmationMethod of
     * this SubjectBean object.
     *
     * @param subjectConfirmationMethod the subjectConfirmationMethod of this 
     *        SubjectBean object.
     */
    public void setSubjectConfirmationMethod(String subjectConfirmationMethod) {
        this.subjectConfirmationMethod = subjectConfirmationMethod;
    }
    
    /**
     * Method getSubjectNameIDFormat returns the subjectNameIDFormat of this SubjectBean 
     * object.
     *
     * @return the subjectNameIDFormat (type String) of this SubjectBean object.
     */
    public String getSubjectNameIDFormat() {
        return subjectNameIDFormat;
    }

    /**
     * Method setSubjectNameIDFormat sets the subjectNameIDFormat of this SubjectBean 
     * object.
     *
     * @param subjectNameIDFormat the subjectNameIDFormat of this SubjectBean object.
     */
    public void setSubjectNameIDFormat(String subjectNameIDFormat) {
        this.subjectNameIDFormat = subjectNameIDFormat;
    }
    
    /**
     * Method getKeyInfo returns the keyInfo of this SubjectBean object.
     *
     * @return the keyInfo (type KeyInfoBean) of this SubjectBean object.
     */
    public KeyInfoBean getKeyInfo() {
        return keyInfo;
    }

    /**
     * Method setKeyInfo sets the keyInfo of this SubjectBean object.
     *
     * @param keyInfo the keyInfo of this SubjectBean object.
     */
    public void setKeyInfo(KeyInfoBean keyInfo) {
        this.keyInfo = keyInfo;
    }
    
    /**
     * Method equals ...
     *
     * @param o of type Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectBean)) return false;

        SubjectBean that = (SubjectBean) o;

        if (subjectName == null && that.subjectName != null) {
            return false;
        } else if (subjectName != null && !subjectName.equals(that.subjectName)) {
            return false;
        }
        
        if (subjectNameQualifier == null && that.subjectNameQualifier != null) {
            return false;
        } else if (subjectNameQualifier != null && 
            !subjectNameQualifier.equals(that.subjectNameQualifier)) {
            return false;
        }
        
        if (subjectConfirmationMethod == null && that.subjectConfirmationMethod != null) {
            return false;
        } else if (subjectConfirmationMethod != null && 
            !subjectConfirmationMethod.equals(that.subjectConfirmationMethod)) {
            return false;
        }
        
        if (subjectNameIDFormat == null && that.subjectNameIDFormat != null) {
            return false;
        } else if (subjectNameIDFormat != null 
            && !subjectNameIDFormat.equals(that.subjectNameIDFormat)) {
            return false;
        }
        
        if (keyInfo == null && that.keyInfo != null) {
            return false;
        } else if (keyInfo != null && !keyInfo.equals(that.keyInfo)) {
            return false;
        }

        return true;
    }

    /**
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        int result = 0;
        if (subjectName != null) {
            result = subjectName.hashCode();
        }
        if (subjectNameQualifier != null) {
            result = 31 * result + subjectNameQualifier.hashCode();
        }
        if (subjectConfirmationMethod != null) {
            result = 31 * result + subjectConfirmationMethod.hashCode();
        }
        if (subjectNameIDFormat != null) {
            result = 31 * result + subjectNameIDFormat.hashCode();
        }
        if (keyInfo != null) {
            result = 31 * result + keyInfo.hashCode();
        }
        return result;
    }
}
