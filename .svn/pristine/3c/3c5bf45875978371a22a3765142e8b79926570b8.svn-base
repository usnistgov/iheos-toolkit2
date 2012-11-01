package gov.nist.toolkit.saml.builder;

import edu.emory.mathcs.backport.java.util.Collections;
import gov.nist.toolkit.saml.builder.bean.AttributeBean;
import gov.nist.toolkit.saml.builder.bean.AttributeStatementBean;
import gov.nist.toolkit.saml.builder.bean.SubjectBean;

public class AttributeStatementBuilder {
	
	private AttributeStatementBean attributeStatement;
	
	public AttributeStatementBuilder(){
		attributeStatement = new AttributeStatementBean();
	}
	
	public AttributeStatementBuilder withSubject(SubjectBean subject){
		attributeStatement.setSubject(subject);
		return this;
	}
	
	public AttributeStatementBuilder withSingleAttribute(AttributeBean attribute){
		attributeStatement.setSamlAttributes(Collections.singletonList(attribute));
		return this;
	}
	
	public AttributeStatementBean build(){
		return attributeStatement;
	}

}
