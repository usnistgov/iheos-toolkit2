package gov.nist.toolkit.saml.builder.bean;

import java.util.List;
import java.util.ArrayList;


/**
 * @author Srinivasarao.Eadara
 *
 */
public class AttributeBean {
    private String simpleName;
    private String qualifiedName;
    private String nameFormat;
    private List<String> attributeValues;

    /**
     * Constructor SamlAttribute creates a new SamlAttribute instance.
     */
    public AttributeBean() {
        attributeValues = new ArrayList<String>();
    }

    /**
     * Constructor SamlAttribute creates a new SamlAttribute instance.
     * 
     * @param simpleName of type String
     * @param qualifiedName of type String
     * @param attributeValues of type List<String>
     */
    public AttributeBean(String simpleName, String qualifiedName, List<String> attributeValues) {
        this.simpleName = simpleName;
        this.qualifiedName = qualifiedName;
        this.attributeValues = attributeValues;
    }

    /**
     * Method getSimpleName returns the simpleName of this SamlAttribute model.
     *
     * @return the simpleName (type String) of this SamlAttribute model.
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Method setSimpleName sets the simpleName of this SamlAttribute model.
     *
     * @param simpleName the simpleName of this SamlAttribute model.
     */
    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }
    
    /**
     * Method getNameFormat returns the nameFormat of this SamlAttribute model
     * 
     * @return he nameFormat of this SamlAttribute model
     */
    public String getNameFormat() {
        return nameFormat;
    }
    
    /**
     * Method setNameFormat sets the nameFormat of this SamlAttribute model.
     *
     * @param nameFormat the nameFormat of this SamlAttribute model.
     */
    public void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    /**
     * Method getQualifiedName returns the qualifiedName of this SamlAttribute model.
     *
     * @return the qualifiedName (type String) of this SamlAttribute model.
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * Method setQualifiedName sets the qualifiedName of this SamlAttribute model.
     *
     * @param qualifiedName the qualifiedName of this SamlAttribute model.
     */
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    /**
     * Method getAttributeValues returns the attributeValues of this SamlAttribute model.
     *
     * @return the attributeValues (type Map) of this SamlAttribute model.
     */
    public List<String> getAttributeValues() {
        return attributeValues;
    }

    /**
     * Method setAttributeValues sets the attributeValues of this SamlAttribute model.
     *
     * @param attributeValues the attributeValues of this SamlAttribute model.
     */
    public void setAttributeValues(List<String> attributeValues) {
        this.attributeValues = attributeValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeBean)) return false;

        AttributeBean that = (AttributeBean) o;

        if (attributeValues == null && that.attributeValues != null) {
            return false;
        } else if (attributeValues != null && !attributeValues.equals(that.attributeValues)) {
            return false;
        }
        
        if (qualifiedName == null && that.qualifiedName != null) {
            return false;
        } else if (qualifiedName != null && !qualifiedName.equals(that.qualifiedName)) {
            return false;
        }
        
        if (nameFormat == null && that.nameFormat != null) {
            return false;
        } else if (nameFormat != null && !nameFormat.equals(that.nameFormat)) {
            return false;
        }
        
        if (simpleName == null && that.simpleName != null) {
            return false;
        } else if (simpleName != null && !simpleName.equals(that.simpleName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (simpleName != null) {
            result = 31 * result + simpleName.hashCode();
        }
        if (qualifiedName != null) {
            result = 31 * result + qualifiedName.hashCode();
        }
        if (nameFormat != null) {
            result = 31 * result + nameFormat.hashCode();
        }
        if (attributeValues != null) {
            result = 31 * result + attributeValues.hashCode();
        }
        return result;
    }
}
