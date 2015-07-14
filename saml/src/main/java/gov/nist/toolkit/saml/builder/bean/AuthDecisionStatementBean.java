package gov.nist.toolkit.saml.builder.bean;

import java.util.List;
import java.util.ArrayList;


/**
 * @author Srinivasarao.Eadara
 *
 */
public class AuthDecisionStatementBean {

    /** 
     * The SAML subject  
     */
    private SubjectBean subject;

    /** 
     * enum representing the possible decision types as specified in the SAML spec 
     */
    public enum Decision {PERMIT, INDETERMINATE, DENY}

    /** 
     * The decision rendered by the SAML authority with respect to the specified resource 
     */
    private Decision decision;

    /** 
     * A URI reference identifying the resource to which access authorization is sought 
     */
    private String resource;

    /** 
     * The set of actions authorized to be performed on the specified resource (one or more) 
     */
    private List<ActionBean> actionBeans;

    /** 
     * A set of assertions that the SAML authority relied on in making the decision (optional) 
     */
    private Object evidence;

    /**
     * Constructor SamlDecision creates a new SamlDecision instance.
     */
    public AuthDecisionStatementBean() {
        actionBeans = new ArrayList<ActionBean>();
    }

    /**
     * Constructor SamlDecision creates a new SamlDecision instance.
     *
     * @param decision of type Decision
     * @param resource of type String
     * @param subject of type SubjectBean
     * @param evidence of type Object
     * @param actionBeans of type List<SamlAction>
     */
    public AuthDecisionStatementBean(
        Decision decision, 
        String resource, 
        SubjectBean subject,
        Object evidence,
        List<ActionBean> actionBeans
    ) {
        this.decision = decision;
        this.resource = resource;
        this.subject = subject;
        this.evidence = evidence;
        this.actionBeans = actionBeans;
    }

    /**
     * Method getResource returns the resource of this SamlDecision object.
     *
     * @return the resource (type String) of this SamlDecision object.
     */
    public String getResource() {
        return resource;
    }

    /**
     * Method setResource sets the resource of this SamlDecision object.
     *
     * @param resource the resource of this SamlDecision object.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Method getActions returns the actions of this SamlDecision object.
     *
     * @return the actions (type List<SamlAction>) of this SamlDecision object.
     */
    public List<ActionBean> getActions() {
        return actionBeans;
    }

    /**
     * Method setActions sets the actions of this SamlDecision object.
     *
     * @param actionBeans the actions of this SamlDecision object.
     */
    public void setActions(List<ActionBean> actionBeans) {
        this.actionBeans = actionBeans;
    }

    /**
     * Method getDecision returns the decision of this SamlDecision object.
     *
     * @return the decision (type Decision) of this SamlDecision object.
     */
    public Decision getDecision() {
        return decision;
    }

    /**
     * Method setDecision sets the decision of this SamlDecision object.
     *
     * @param decision the decision of this SamlDecision object.
     */
    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    /**
     * Method getEvidence returns the evidence of this SamlDecision object.
     *
     * @return the evidence (type Object) of this SamlDecision object.
     */
    public Object getEvidence() {
        return evidence;
    }

    /**
     * Method setEvidence sets the evidence of this SamlDecision object.
     *
     * @param evidence the evidence of this SamlDecision object.
     */
    public void setEvidence(Object evidence) {
        this.evidence = evidence;
    }

    /**
     * Get the Subject
     * @return the Subject
     */
    public SubjectBean getSubject() {
        return subject;
    }

    /**
     * Set the Subject
     * @param subject the SubjectBean instance to set
     */
    public void setSubject(SubjectBean subject) {
        this.subject = subject;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthDecisionStatementBean)) return false;

        AuthDecisionStatementBean that = (AuthDecisionStatementBean) o;

        if (subject == null && that.subject != null) {
            return false;
        } else if (subject != null && !subject.equals(that.subject)) {
            return false;
        }
        
        if (decision == null && that.decision != null) {
            return false;
        } else if (decision != null && !decision.equals(that.decision)) {
            return false;
        }
        
        if (evidence == null && that.evidence != null) {
            return false;
        } else if (evidence != null && !evidence.equals(that.evidence)) {
            return false;
        }
        
        if (actionBeans == null && that.actionBeans != null) {
            return false;
        } else if (actionBeans != null && !actionBeans.equals(that.actionBeans)) {
            return false;
        }
        
        if (resource == null && that.resource != null) {
            return false;
        } else if (resource != null && !resource.equals(that.resource)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (decision != null ? decision.hashCode() : 0);
        result = 31 * result + (evidence != null ? evidence.hashCode() : 0);
        result = 31 * result + (actionBeans != null ? actionBeans.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        return result;
    }
}
