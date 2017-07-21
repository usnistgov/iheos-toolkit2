/**
 * 
 */
package gov.nist.toolkit.saml.builder;

import gov.nist.toolkit.saml.builder.bean.ActionBean;
import gov.nist.toolkit.saml.builder.bean.AssertionBean;
import gov.nist.toolkit.saml.builder.bean.AttributeBean;
import gov.nist.toolkit.saml.builder.bean.AttributeStatementBean;
import gov.nist.toolkit.saml.builder.bean.AuthDecisionStatementBean;
import gov.nist.toolkit.saml.builder.bean.AuthenticationStatementBean;
import gov.nist.toolkit.saml.builder.bean.SubjectBean;
import gov.nist.toolkit.saml.util.SAML2CallbackHandler;
import gov.nist.toolkit.saml.util.SAMLCallback;
import gov.nist.toolkit.saml.util.SAMLParms;
import gov.nist.toolkit.saml.util.SamlConstants;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Srinivasarao.Eadara
 * 
 * @author agerardin // Reviewed the creation of assertionBean to fix a bug.
 */
public class SamlAssertionData {
	public static SAMLCallback samlCallBack = null;

	// (MA 1028) fix. issuer has to be a person -Antoine
	private static String issuerId = "CN=lambdaMan@nist.gov,O=Nist Test Lab,L=Gaithersburg,ST=Maryland,C=US";

	public static Element createAssertionRequest() throws Exception {

		List<AttributeStatementBean> attributeStatementList = new ArrayList();

		String subjectId = "uid=joe,ou=people,ou=saml-demo,o=example.com";

		SubjectBean subject = new SubjectBean(subjectId, "www.example.com",
				SamlConstants.CONF_HOLDER_KEY);

		String subjectNameIDFormat = SamlConstants.NAMEID_FORMAT_EMAIL_ADDRESS;

		AssertionBean assertionBean = new AssertionBean();

		assertionBean.setIssuer(issuerId);
		assertionBean.setSubjectNameIDFormat(subjectNameIDFormat);
		assertionBean.setSubjectBean(subject);

		// (MA1082). Fix. should match the subject id in the subject -Antoine
		AttributeStatementBean attrStatement1 = createAttributeStatement(
				subject, "urn:oasis:names:tc:xspa:1.0:subject:subject-id",
				subjectId);
		// (MA1084,85..) Fic. Values modified -Antoine
		AttributeStatementBean attrStatement2 = createAttributeStatement(
				subject, "urn:oasis:names:tc:xspa:1.0:subject:organization",
				"Social Security Administration");
		AttributeStatementBean attrStatement3 = createAttributeStatement(
				subject, "urn:oasis:names:tc:xspa:1.0:subject:organization-id",
				"urn:oid:2.2");
		AttributeStatementBean attrStatement4 = createAttributeStatement(
				subject, "urn:nhin:names:saml:homeCommunityId",
				"urn:oid:2.2");

		// Those values are not valid
		AttributeStatementBean attrStatement5 = createAttributeStatement(
				subject, "urn:oasis:names:tc:xacml:2.0:subject:role",
				"2.16.840.1.113883.3.18.6.1.15");
		AttributeStatementBean attrStatement6 = createAttributeStatement(
				subject, "urn:oasis:names:tc:xspa:1.0:subject:purposeofuse",
				"2.16.840.1.113883.3.184.6.1");
		AttributeStatementBean attrStatement7 = createAttributeStatement(
				subject, "urn:oasis:names:tc:xacml:2.0:resource:resource-id",
				"D123401^^^&1.1&ISO");
		
		
		
		List<AttributeStatementBean> attrStatements = Arrays.asList(
				attrStatement1, attrStatement2, attrStatement3, attrStatement4,
				attrStatement5, attrStatement6, attrStatement7);
		assertionBean.setAttrBean(attrStatements);

		// Authentication Statements
		AuthenticationStatementBean authenStateBean = new AuthenticationStatementBean();
		authenStateBean.setAuthenticationInstant(new DateTime());
		authenStateBean.setAuthenticationMethod("x509");
		authenStateBean.setSubject(subject);
		assertionBean.setAuthenStateBean(Collections
				.singletonList(authenStateBean));

		// Authorization Statements
		AuthDecisionStatementBean authzBean = new AuthDecisionStatementBean();
		ActionBean actionBean = new ActionBean();
		actionBean.setContents("Execute");
		actionBean
				.setActionNamespace("urn:oasis:names:tc:SAML:1.0:action:rwedc");
		authzBean.setActions(Collections.singletonList(actionBean));
		authzBean.setDecision(AuthDecisionStatementBean.Decision.PERMIT);
		authzBean
				.setResource("https://nhinri2c23.aegis.net:443/RespondingGateway_Query_Service/DocQuery");
		authzBean.setSubject(subject);
		assertionBean.setAuthzBean(Collections.singletonList(authzBean));
		OpenSamlBootStrap.initSamlEngine();
		SAMLAssertionWrapper assertion = new SAMLAssertionWrapper(assertionBean);
		String assertionString = assertion.assertionToString();
		System.out.println(assertionString);
		return assertion.getAssertionElement();
		// return loadXMLFrom(assertionString);

	}

	private static AttributeStatementBean createAttributeStatement(
			SubjectBean subject, String qualifiedName, String value) {
		AttributeBean attribute1 = new AttributeBuilder()
				.withQualifiedName(qualifiedName).withSingleValue(value)
				.build();
		AttributeStatementBean attrStatement = new AttributeStatementBuilder()
				.withSubject(subject).withSingleAttribute(attribute1).build();
		return attrStatement;

	}

	public static org.w3c.dom.Document loadXMLFrom(String xml)
			throws org.xml.sax.SAXException, java.io.IOException {
		return loadXMLFrom(new java.io.ByteArrayInputStream(xml.getBytes()));
	}

	public static org.w3c.dom.Document loadXMLFrom(java.io.InputStream is)
			throws org.xml.sax.SAXException, java.io.IOException {
		javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException ex) {
		}
		org.w3c.dom.Document doc = builder.parse(is);
		is.close();
		return doc;
	}

	public static AssertionBean getEvidenceInfo() {
		AssertionBean assertionBean = new AssertionBean();

		assertionBean
				.setIssuer(issuerId);
		assertionBean
				.setSubjectNameIDFormat(SamlConstants.NAMEID_FORMAT_EMAIL_ADDRESS);

		AttributeStatementBean attrBean = new AttributeStatementBean();
		AttributeBean attributeBean = new AttributeBean();
		attributeBean.setQualifiedName("AccessConsentPolicy");
		attributeBean.setNameFormat("http://www.hhs.gov/healthit/nhin");
		attrBean.setSamlAttributes(Collections.singletonList(attributeBean));

		attributeBean = new AttributeBean();
		attributeBean.setQualifiedName("InstanceAccessConsentPolicy");
		attributeBean.setNameFormat("http://www.hhs.gov/healthit/nhin");
		attributeBean.setAttributeValues(Collections
				.singletonList("urn:oid:2.16.840.1.113883.3.184.6.1.2222"));
		attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
		assertionBean.setAttrBean(Collections.singletonList(attrBean));
		return assertionBean;
	}

}
