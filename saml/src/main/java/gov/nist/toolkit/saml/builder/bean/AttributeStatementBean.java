package gov.nist.toolkit.saml.builder.bean;

import java.util.List;
import java.util.ArrayList;


/**
 * @author Srinivasarao.Eadara
 *
 */
public class AttributeStatementBean {
    private SubjectBean subject;
    private List<AttributeBean> attributeBeans;

    /**
     * Constructor SamlAttributeStatement creates a new SamlAttributeStatement instance.
     */
    public AttributeStatementBean() {
        attributeBeans = new ArrayList<AttributeBean>();
    }
    
    /**
     * Constructor SamlAttributeStatement creates a new SamlAttributeStatement instance.
     * @param subject A new SubjectBean instance
     * @param attributeBeans A list of Attributes
     */
    public AttributeStatementBean(
        SubjectBean subject,
        List<AttributeBean> attributeBeans
    ) {
        this.subject = subject;
        this.attributeBeans = attributeBeans;
    }

    /**
     * Method getSamlAttributes returns the samlAttributes of this SamlAttributeStatement object.
     *
     * @return the samlAttributes (type List<SamlAttribute>) of this SamlAttributeStatement object.
     */
    public List<AttributeBean> getSamlAttributes() {
        return attributeBeans;
    }

    /**
     * Method setSamlAttributes sets the samlAttributes of this SamlAttributeStatement object.
     *
     * @param attributeBeans the samlAttributes of this SamlAttributeStatement object.
     *
     */
    public void setSamlAttributes(List<AttributeBean> attributeBeans) {
        this.attributeBeans = attributeBeans;
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
        if (!(o instanceof AttributeStatementBean)) return false;

        AttributeStatementBean that = (AttributeStatementBean) o;

        if (attributeBeans == null && that.attributeBeans != null) {
            return false;
        } else if (attributeBeans != null && !attributeBeans.equals(that.attributeBeans)) {
            return false;
        }
        
        if (subject == null && that.subject != null) {
            return false;
        } else if (subject != null && !subject.equals(that.subject)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (attributeBeans != null ? attributeBeans.hashCode() : 0);
        return result;
    }
}
