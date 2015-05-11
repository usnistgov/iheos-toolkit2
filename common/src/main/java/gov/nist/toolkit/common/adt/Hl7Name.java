/*
 * Hl7Name.java
 *
 * Created on August 4, 2005, 4:42 PM
 *
 */

package gov.nist.toolkit.common.adt;

/**
 *
 * @author mccaffrey
 */
public class Hl7Name {
    
    private String parent = null;   //  uuid link to record -- not the patient's biological parent                                    
        
    private String familyName = null;
    private String givenName = null;
    private String secondAndFurtherName = null;
    private String suffix = null;
    private String prefix = null;
    private String degree = null;
    
    /**
     * Creates a new instance of Hl7Name 
     */
    
    public Hl7Name() {
        
    }
    
    public Hl7Name(String parent) {
        this.setParent(parent);
        
    }
    public Hl7Name(String parent, String familyName, String givenName, String secondAndFurtherName,
            String suffix, String prefix, String degree) {
        
        this.setParent(parent);
        this.setFamilyName(familyName);
        this.setGivenName(givenName);
        this.setSecondAndFurtherName(secondAndFurtherName);
        this.setSuffix(suffix);
        this.setPrefix(prefix);
        this.setDegree(degree);
        
        
    }
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSecondAndFurtherName() {
        return secondAndFurtherName;
    }

    public void setSecondAndFurtherName(String secondAndFurtherName) {
        this.secondAndFurtherName = secondAndFurtherName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    
}
