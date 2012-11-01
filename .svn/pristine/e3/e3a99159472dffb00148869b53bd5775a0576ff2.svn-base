package gov.nist.toolkit.saml.builder.bean;

import org.joda.time.DateTime;


/**
 * @author Srinivasarao.Eadara
 *
 */
public class AuthenticationStatementBean {
    private SubjectBean subject;
    DateTime authenticationInstant;
    private String authenticationMethod;

    /**
     * Default constructor
     */
    public AuthenticationStatementBean() {
    }

    /**
     * Construct a new AuthenticationStatementBean
     * 
     * @param subject the Subject to set 
     * @param authenticationMethod the Authentication Method to set
     * @param authenticationInstant the Authentication Instant to set
     */
    public AuthenticationStatementBean(
        SubjectBean subject, 
        String authenticationMethod,
        DateTime authenticationInstant
    ) {
        this.subject = subject;
        this.authenticationMethod = authenticationMethod;
        this.authenticationInstant = authenticationInstant;
    }

    /**
     * Get the Subject
     * @return the subject
     */
    public SubjectBean getSubject() {
        return subject;
    }

    /**
     * Set the subject
     * @param subject the SubjectBean instance to set
     */
    public void setSubject(SubjectBean subject) {
        this.subject = subject;
    }

    /**
     * Get the authentication method
     * @return the authentication method
     */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * Set the authentication method
     * @param authenticationMethod the authentication method
     */
    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    /**
     * Get the authentication instant
     * @return the authentication instant
     */
    public DateTime getAuthenticationInstant() {
        return authenticationInstant;
    }

    /**
     * Set the authentication instant
     * @param authenticationInstant the authentication instant
     */
    public void setAuthenticationInstant(DateTime authenticationInstant) {
        this.authenticationInstant = authenticationInstant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthenticationStatementBean)) return false;

        AuthenticationStatementBean that = (AuthenticationStatementBean) o;

        if (authenticationInstant == null && that.authenticationInstant != null) {
            return false;
        } else if (authenticationInstant != null 
            && !authenticationInstant.equals(that.authenticationInstant)) {
            return false;
        }
        
        if (authenticationMethod == null && that.authenticationMethod != null) {
            return false;
        } else if (authenticationMethod != null 
            && !authenticationMethod.equals(that.authenticationMethod)) {
            return false;
        }
        
        if (subject == null && that.subject != null) {
            return false;
        } else if (subject != null 
            && !subject.equals(that.subject)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (authenticationInstant != null ? authenticationInstant.hashCode() : 0);
        result = 31 * result + (authenticationMethod != null ? authenticationMethod.hashCode() : 0);
        return result;
    }
}
