package gov.nist.toolkit.saml.builder;

import edu.emory.mathcs.backport.java.util.Collections;
import gov.nist.toolkit.saml.builder.bean.AttributeBean;

public class AttributeBuilder {
	
	private AttributeBean attribute;
	
	public AttributeBuilder(){
		attribute = new AttributeBean();
	}
	
	public AttributeBuilder withQualifiedName(String name){
		attribute.setQualifiedName(name);
		return this;
	}
	
	public AttributeBuilder withSingleValue(String value){
		attribute.setAttributeValues(Collections.singletonList(value));
		return this;
	}
	
	public AttributeBean build(){
		return attribute;
	}

}
