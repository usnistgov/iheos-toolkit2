package gov.nist.toolkit.saml.builder.bean;

import org.joda.time.DateTime;


/**
 * @author Srinivasarao.Eadara
 *
 */
public class ConditionsBean {
    private DateTime notBefore;
    private DateTime notAfter;
    private int tokenPeriodMinutes;
    private String audienceURI;

    /**
     * Constructor ConditionsBean creates a new ConditionsBean instance.
     */
    public ConditionsBean() {
    }

    /**
     * Constructor ConditionsBean creates a new ConditionsBean instance.
     *
     * @param notBefore The notBefore instance
     * @param notAfter The notAfter instance
     */
    public ConditionsBean(
        DateTime notBefore, 
        DateTime notAfter
    ) {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }
    
    /**
     * Constructor ConditionsBean creates a new ConditionsBean instance.
     *
     * @param tokenPeriodMinutes how long the token is valid for in minutes
     */
    public ConditionsBean(
        int tokenPeriodMinutes
    ) {
        this.tokenPeriodMinutes = tokenPeriodMinutes;
    }
    
    /**
     * Get the notBefore instance
     *
     * @return the notBefore instance
     */
    public DateTime getNotBefore() {
        return notBefore;
    }

    /**
     * Set the notBefore instance
     *
     * @param notBefore the notBefore instance to set
     */
    public void setNotBefore(DateTime notBefore) {
        this.notBefore = notBefore;
    }
    
    /**
     * Get the notAfter instance
     *
     * @return the notAfter instance
     */
    public DateTime getNotAfter() {
        return notAfter;
    }

    /**
     * Set the notAfter instance
     *
     * @param notAfter the notAfter instance to set
     */
    public void setNotAfter(DateTime notAfter) {
        this.notAfter = notAfter;
    }
    
    /**
     * Get the tokenPeriodMinutes of this object.
     *
     * @return the tokenPeriodMinutes (type int)
     */
    public int getTokenPeriodMinutes() {
        return tokenPeriodMinutes;
    }

    /**
     * Set the tokenPeriodMinutes.
     *
     * @param tokenPeriodMinutes the tokenPeriodMinutes to set
     */
    public void setTokenPeriodMinutes(int tokenPeriodMinutes) {
        this.tokenPeriodMinutes = tokenPeriodMinutes;
    }
    
    /**
     * Get the audienceURI instance
     *
     * @return the audienceURI instance
     */
    public String getAudienceURI() {
        return audienceURI;
    }

    /**
     * Set the audienceURI instance
     *
     * @param audienceURI the audienceURI instance to set
     */
    public void setAudienceURI(String audienceURI) {
        this.audienceURI = audienceURI;
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
        if (!(o instanceof ConditionsBean)) return false;

        ConditionsBean that = (ConditionsBean) o;

        if (tokenPeriodMinutes != that.tokenPeriodMinutes) return false;
        
        if (notBefore == null && that.notBefore != null) {
            return false;
        } else if (notBefore != null && !notBefore.equals(that.notBefore)) {
            return false;
        }
        
        if (notAfter == null && that.notAfter != null) {
            return false;
        } else if (notAfter != null && !notAfter.equals(that.notAfter)) {
            return false; 
        }
        
        if (audienceURI == null && that.audienceURI != null) {
            return false;
        } else if (audienceURI != null && !audienceURI.equals(that.audienceURI)) {
            return false; 
        }

        return true;
    }

    /**
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        int result = tokenPeriodMinutes;
        if (notBefore != null) {
            result = 31 * result + notBefore.hashCode();
        }
        if (notAfter != null) {
            result = 31 * result + notAfter.hashCode();
        }
        if (audienceURI != null) {
            result = 31 * result + audienceURI.hashCode();
        }
        return result;
    }
}
