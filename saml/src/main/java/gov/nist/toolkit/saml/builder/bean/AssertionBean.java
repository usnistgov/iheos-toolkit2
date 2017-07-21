package gov.nist.toolkit.saml.builder.bean;

import gov.nist.toolkit.saml.builder.OpenSamlBootStrap;
import gov.nist.toolkit.saml.builder.SAMLAssertionWrapper;
import gov.nist.toolkit.saml.util.SAML2CallbackHandler;
import gov.nist.toolkit.saml.util.SAMLCallback;
import gov.nist.toolkit.saml.util.SAMLParms;
import gov.nist.toolkit.saml.util.SamlConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * @author Srinivasarao.Eadara
 *
 */
public class AssertionBean {
	
	private String issuer = null;
	private String subjectNameIDFormat =null ;
	private List<AuthenticationStatementBean> authenStateBean = null ;
	private List<AttributeStatementBean> attrBean = null;
	private SubjectBean subjectBean = null ;
	private List<AuthDecisionStatementBean> authzBean = null;
	private ConditionsBean conditionsBean = null ;
	public AssertionBean(){
		authenStateBean = new ArrayList<AuthenticationStatementBean>();
		attrBean = new  ArrayList<AttributeStatementBean>() ;
		subjectBean = new SubjectBean();
		authzBean = new ArrayList<AuthDecisionStatementBean>();
		conditionsBean = null ;
	}
	
	public ConditionsBean getConditionsBean() {
		return conditionsBean;
	}

	public void setConditionsBean(ConditionsBean conditionsBean) {
		this.conditionsBean = conditionsBean;
	}

	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getSubjectNameIDFormat() {
		return subjectNameIDFormat;
	}
	public void setSubjectNameIDFormat(String subjectNameIDFormat) {
		this.subjectNameIDFormat = subjectNameIDFormat;
	}
	public List<AuthenticationStatementBean> getAuthenStateBean() {
		return authenStateBean;
	}
	public void setAuthenStateBean(List<AuthenticationStatementBean> authenStateBean) {
		this.authenStateBean = authenStateBean;
	}
	public List<AttributeStatementBean> getAttrBean() {
		return attrBean;
	}
	public void setAttrBean(List<AttributeStatementBean> attrBean) {
		this.attrBean = attrBean;
	}
	public SubjectBean getSubjectBean() {
		return subjectBean;
	}
	public void setSubjectBean(SubjectBean subjectBean) {
		this.subjectBean = subjectBean;
	}
	public List<AuthDecisionStatementBean> getAuthzBean() {
		return authzBean;
	}
	public void setAuthzBean(List<AuthDecisionStatementBean> authzBean) {
		this.authzBean = authzBean;
	}
	
	
	
	
	
/*
 *     	SAML2CallbackHandler callbackHandler = new SAML2CallbackHandler();
        callbackHandler.setStatement(SAML2CallbackHandler.Statement.AUTHN);
        callbackHandler.setIssuer("O=Social Security Administration,L=Baltimore,ST=Maryland,C=US");
        callbackHandler.setSubjectNameIDFormat(SamlConstants.NAMEID_FORMAT_EMAIL_ADDRESS);
        
        AttributeStatementBean attrBean = new AttributeStatementBean();
        SubjectBean subjectBean = 
            new SubjectBean(
                "uid=joe,ou=people,ou=saml-demo,o=example.com", "www.example.com", SamlConstants.CONF_HOLDER_KEY
            );
        if (subjectBean != null) {
            attrBean.setSubject(subjectBean);
        }
        AttributeBean attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:oasis:names:tc:xspa:1.0:subject:subject-id");
        attributeBean.setAttributeValues(Collections.singletonList("MEGAHIT"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        
        attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:oasis:names:tc:xspa:1.0:subject:organization");
        attributeBean.setAttributeValues(Collections.singletonList("Social Security Administration"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        
        attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:oasis:names:tc:xspa:1.0:subject:organization-id");
        attributeBean.setAttributeValues(Collections.singletonList("2.16.840.1.113883.3.184.6.1"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        
        attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:nhin:names:saml:homeCommunityId");
        attributeBean.setAttributeValues(Collections.singletonList("2.16.840.1.113883.3.184.6.1"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        
        attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:oasis:names:tc:xacml:2.0:subject:role");
        
        attributeBean.setAttributeValues(Collections.singletonList("2.16.840.1.113883.3.184.6.1"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        
        
        attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse");
        attributeBean.setAttributeValues(Collections.singletonList("2.16.840.1.113883.3.184.6.1"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        
        attributeBean = new AttributeBean();
        attributeBean.setQualifiedName("urn:oasis:names:tc:xacml:2.0:resource:resource-id");
        attributeBean.setAttributeValues(Collections.singletonList("2.16.840.1.113883.3.184.6.1"));
        attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
        //Attribute Statements
        samlCallBack = new SAMLCallback() ;
        samlCallBack.setAttributeStatementData(Collections.singletonList(attrBean));
        
        //Authentication Statements
        AuthenticationStatementBean authenStateBean = new AuthenticationStatementBean() ;
        authenStateBean.setAuthenticationInstant(new DateTime());
        authenStateBean.setAuthenticationMethod("x509");
        authenStateBean.setSubject(subjectBean);
        samlCallBack.setAuthenticationStatementData(Collections.singletonList(authenStateBean));
        
        //Authorization Statements 
        AuthDecisionStatementBean authzBean = new AuthDecisionStatementBean();
        ActionBean actionBean = new ActionBean();
        actionBean.setContents("Execute");
        actionBean.setActionNamespace("urn:oasis:names:tc:SAML:1.0:action:rwedc");
        authzBean.setActions(Collections.singletonList(actionBean));
        authzBean.setDecision(authzBean.getDecision().PERMIT);
        authzBean.setResource("https://nhinri2c23.aegis.net:443/RespondingGateway_Query_Service/DocQuery");
        authzBean.setSubject(subjectBean);
        samlCallBack.setAuthDecisionStatementData(Collections.singletonList(authzBean));
        
        SAMLParms samlParms = new SAMLParms();
        samlParms.setCallbackHandler(callbackHandler);
        OpenSamlBootStrap.initSamlEngine();
        SAMLAssertionWrapper assertion = new SAMLAssertionWrapper(samlParms);
        String assertionString = assertion.assertionToString();
 */
}
