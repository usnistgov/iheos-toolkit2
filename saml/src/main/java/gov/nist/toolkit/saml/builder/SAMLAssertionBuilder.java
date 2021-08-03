package gov.nist.toolkit.saml.builder;

import gov.nist.toolkit.dsig.KeyStoreAccessObject;
import gov.nist.toolkit.saml.builder.bean.ActionBean;
import gov.nist.toolkit.saml.builder.bean.AssertionBean;
import gov.nist.toolkit.saml.builder.bean.AttributeBean;
import gov.nist.toolkit.saml.builder.bean.AttributeStatementBean;
import gov.nist.toolkit.saml.builder.bean.AuthDecisionStatementBean;
import gov.nist.toolkit.saml.builder.bean.AuthenticationStatementBean;
import gov.nist.toolkit.saml.builder.bean.ConditionsBean;
import gov.nist.toolkit.saml.builder.bean.KeyInfoBean;
import gov.nist.toolkit.saml.builder.bean.SubjectBean;
import gov.nist.toolkit.saml.util.SamlConstants;
import gov.nist.toolkit.saml.util.UUIDGenerator;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.X509KeyInfoGeneratorFactory;
import org.opensaml.xml.signature.KeyInfo;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SAMLAssertionBuilder {
	private static SAMLObjectBuilder<Assertion> assertionBuilder;
    
    private static SAMLObjectBuilder<Issuer> issuerBuilder;
    
    private static SAMLObjectBuilder<Subject> subjectBuilder;
    
    private static SAMLObjectBuilder<NameID> nameIdBuilder;
    
    private static SAMLObjectBuilder<SubjectLocality> subjectLocalityBuilder;
    
    private static SAMLObjectBuilder<SubjectConfirmation> subjectConfirmationBuilder;
    
    private static SAMLObjectBuilder<Conditions> conditionsBuilder;
    
    private static SAMLObjectBuilder<SubjectConfirmationData> subjectConfirmationDataBuilder;
    
    private static SAMLObjectBuilder<KeyInfoConfirmationDataType> keyInfoConfirmationDataBuilder;
    
    private static SAMLObjectBuilder<AuthnStatement> authnStatementBuilder;
    
    private static SAMLObjectBuilder<AuthnContext> authnContextBuilder;
    
    private static SAMLObjectBuilder<AuthnContextClassRef> authnContextClassRefBuilder;
    
    private static SAMLObjectBuilder<AttributeStatement> attributeStatementBuilder;
    
    private static SAMLObjectBuilder<Attribute> attributeBuilder;
    
    private static XSStringBuilder stringBuilder;
    
    private static SAMLObjectBuilder<AudienceRestriction> audienceRestrictionBuilder;
    
    private static SAMLObjectBuilder<Audience> audienceBuilder;
    
    private static SAMLObjectBuilder<AuthzDecisionStatement> authorizationDecisionStatementBuilder;
    
    private static SAMLObjectBuilder<Action> actionElementBuilder;
    
    private static SAMLObjectBuilder<Evidence> evidenceElementBuilder;
    
    
    private static XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
    public static final String AUTH_CONTEXT_CLASS_REF_X509 = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
    public static final String CONF_BEARER = 
        "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    
    public static final String CONF_HOLDER_KEY = 
        "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key";
    
    public static final String CONF_SENDER_VOUCHES = 
        "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";
    
    public static String assertionId = null ;
    
    
    
    
    /**
     * Create a SAML 2 assertion
     *
     * @return a SAML 2 assertion
     */
    @SuppressWarnings("unchecked")
    public static Assertion createAssertion() {
        if (assertionBuilder == null) {
            assertionBuilder = (SAMLObjectBuilder<Assertion>) 
                builderFactory.getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
            if (assertionBuilder == null) {
                throw new IllegalStateException(
                    "OpenSaml engine not initialized. Please make sure to initialize the OpenSaml engine "
                    + "prior using it"
                );
            }
        }
        Assertion assertion = 
            assertionBuilder.buildObject(Assertion.DEFAULT_ELEMENT_NAME, Assertion.TYPE_NAME);
        assertionId = UUIDGenerator.getUUID();
        assertion.setID(assertionId);
        assertion.setVersion(SAMLVersion.VERSION_20);
        assertion.setIssueInstant(new DateTime());
        return assertion;
    }
    
    
    /**
     * Create an Issuer model
     *
     * @param issuerValue of type String
     * @return an Issuer model
     */
    @SuppressWarnings("unchecked")
    public static Issuer createIssuer(String issuerValue) {
        if (issuerBuilder == null) {
            issuerBuilder = (SAMLObjectBuilder<Issuer>) 
                builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
            
        }
        Issuer issuer = issuerBuilder.buildObject();
        //
        // The SAML authority that is making the claim(s) in the assertion. The issuer SHOULD 
        // be unambiguous to the intended relying parties.
        issuer.setFormat(SamlConstants.NAMEID_FORMAT_X509_SUBJECT_NAME);
        issuer.setValue(issuerValue);
        return issuer;
    }
    
    /**
     * Create SAML 2 Authentication Statement(s).
     *
     * @param authBeans A list of AuthenticationStatementBean instances
     * @return SAML 2 Authentication Statement(s).
     */
    @SuppressWarnings("unchecked")
    public static List<AuthnStatement> createAuthnStatement(
        List<AuthenticationStatementBean> authBeans
    ) {
        List<AuthnStatement> authnStatements = new ArrayList<AuthnStatement>();
        
        if (authnStatementBuilder == null) {
            authnStatementBuilder = (SAMLObjectBuilder<AuthnStatement>) 
                builderFactory.getBuilder(AuthnStatement.DEFAULT_ELEMENT_NAME);
        }
        if (authnContextBuilder == null) {
            authnContextBuilder = (SAMLObjectBuilder<AuthnContext>) 
                builderFactory.getBuilder(AuthnContext.DEFAULT_ELEMENT_NAME);
        }
        if (authnContextClassRefBuilder == null) {
            authnContextClassRefBuilder = (SAMLObjectBuilder<AuthnContextClassRef>) 
                builderFactory.getBuilder(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        }
        

        if (authBeans != null && authBeans.size() > 0) {
            for (AuthenticationStatementBean statementBean : authBeans) {
                AuthnStatement authnStatement = authnStatementBuilder.buildObject();
                //authnStatement.setAuthnInstant(statementBean.getAuthenticationInstant());
                authnStatement.setAuthnInstant(new DateTime());
                authnStatement.setSessionIndex("12345");
                
                SubjectLocality subjectLocality =  createSubjectLocality("158.147.185.10", "ssa.gov");
                authnStatement.setSubjectLocality(subjectLocality);
                
                AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject();
                authnContextClassRef.setAuthnContextClassRef(
                    transformAuthenticationMethod(statementBean.getAuthenticationMethod())
                );
                AuthnContext authnContext = authnContextBuilder.buildObject();
                authnContext.setAuthnContextClassRef(authnContextClassRef);
                authnStatement.setAuthnContext(authnContext);

                authnStatements.add(authnStatement);
            }
        }

        return authnStatements;
    }
    /**
     * Transform the user-supplied authentication method value into one of the supported 
     * specification-compliant values.
     * NOTE: Only "Password" is supported at this time.
     *
     * @param sourceMethod of type String
     * @return String
     */
    private static String transformAuthenticationMethod(String sourceMethod) {
        String transformedMethod = "";

        if ("x509".equalsIgnoreCase(sourceMethod)) {
            transformedMethod = AUTH_CONTEXT_CLASS_REF_X509;
        }

        return transformedMethod;
    }
    /**
     * Create a Subject.
     *
     * @param subjectBean of type SubjectBean
     * @return a Subject
     */
    @SuppressWarnings("unchecked")
    public static Subject createSaml2Subject(SubjectBean subjectBean) 
        throws org.opensaml.xml.security.SecurityException, Exception {
        if (subjectBuilder == null) {
            subjectBuilder = (SAMLObjectBuilder<Subject>) 
                builderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME);
        }
        Subject subject = subjectBuilder.buildObject();
        
        NameID nameID = SAMLAssertionBuilder.createNameID(subjectBean);
        subject.setNameID(nameID);
        
        
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM"); /* Removed argument for internal API: , new org.jcp.xml.dsig.internal.dom.XMLDSigRI() */
		List envelopedTransform = Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        Reference ref = fac.newReference(assertionId, fac.newDigestMethod(DigestMethod.SHA1, null),envelopedTransform, null, null);

        //Create the SignedInfo.
        SignedInfo signInfo = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
				  	fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

        //Load the KeyStore and get the signing key and certificate.
        
        KeyStoreAccessObject ksAccessObj = KeyStoreAccessObject.getInstance(null);
		X509Certificate cert = ksAccessObj.getX509Certificate();
		//Create the KeyInfo containing the Public Key.
		PublicKey publicKey  = ksAccessObj.getPublicKey();
		KeyInfoBean keyInfoBean = new KeyInfoBean() ;
		keyInfoBean.setCertificate(cert);
		keyInfoBean.setElement(null);
		keyInfoBean.setPublicKey(publicKey);
		subjectBean.setKeyInfo(keyInfoBean);
		//System.out.println("public key ::["+publicKey.toString()+"]");
		KeyInfoFactory keyInfoFac = fac.getKeyInfoFactory();	   
		KeyValue keyVal = keyInfoFac.newKeyValue(publicKey);
		//KeyInfo keyInf = keyInfoFac.newKeyInfo(Collections.singletonList(keyVal));
        
        
        SubjectConfirmationData subjectConfData = null;
        if (subjectBean.getKeyInfo() != null) {
            subjectConfData = 
                SAMLAssertionBuilder.createSubjectConfirmationData(
                    null, 
                    null, 
                    null, 
                    subjectBean.getKeyInfo() 
                );
        }
        
        String confirmationMethodStr = subjectBean.getSubjectConfirmationMethod();
        if (confirmationMethodStr == null) {
            confirmationMethodStr = SamlConstants.CONF_HOLDER_KEY;
        }
        SubjectConfirmation subjectConfirmation = 
            createSubjectConfirmation(
                confirmationMethodStr, subjectConfData
            );
        
        subject.getSubjectConfirmations().add(subjectConfirmation);
        return subject;
    }
    
    
    /**
     * Create a SubjectConfirmationData model
     *
     * @param inResponseTo of type String
     * @param recipient    of type String
     * @param notOnOrAfter of type DateTime
     * @param keyInfoBean of type KeyInfoBean
     * @return a SubjectConfirmationData model
     */
    @SuppressWarnings("unchecked")
    public static SubjectConfirmationData createSubjectConfirmationData(
        String inResponseTo, 
        String recipient, 
        DateTime notOnOrAfter,
        KeyInfoBean keyInfoBean
    ) throws org.opensaml.xml.security.SecurityException, Exception {
        SubjectConfirmationData subjectConfirmationData = null;
        KeyInfo keyInfo = null;
        if (keyInfoBean == null) {
            if (subjectConfirmationDataBuilder == null) {
                subjectConfirmationDataBuilder = (SAMLObjectBuilder<SubjectConfirmationData>) 
                    builderFactory.getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
            }
            subjectConfirmationData = subjectConfirmationDataBuilder.buildObject();
        } else {
            if (keyInfoConfirmationDataBuilder == null) {
                keyInfoConfirmationDataBuilder = (SAMLObjectBuilder<KeyInfoConfirmationDataType>) 
                    builderFactory.getBuilder(KeyInfoConfirmationDataType.TYPE_NAME);
            }
            subjectConfirmationData = keyInfoConfirmationDataBuilder.buildObject();
            keyInfo = createKeyInfo(keyInfoBean);
            ((KeyInfoConfirmationDataType)subjectConfirmationData).getKeyInfos().add(keyInfo);
        }
        
        if (inResponseTo != null) {
            subjectConfirmationData.setInResponseTo(inResponseTo);
        }
        if (recipient != null) {
            subjectConfirmationData.setRecipient(recipient);
        }
        if (notOnOrAfter != null) {
            subjectConfirmationData.setNotOnOrAfter(notOnOrAfter);
        }
        
        return subjectConfirmationData;
    }
    
    /**
     * Create a SubjectConfirmation model
     * One of the following subject confirmation methods MUST be used:
     *   urn:oasis:names:tc:SAML:2.0:cm:holder-of-key
     *   urn:oasis:names:tc:SAML:2.0:cm:sender-vouches
     *   urn:oasis:names:tc:SAML:2.0:cm:bearer
     *
     * @param method of type String
     * @param subjectConfirmationData of type SubjectConfirmationData
     * @return a SubjectConfirmation model
     */
    @SuppressWarnings("unchecked")
    public static SubjectConfirmation createSubjectConfirmation(
        String method,
        SubjectConfirmationData subjectConfirmationData
    ) {
        if (subjectConfirmationBuilder == null) {
            subjectConfirmationBuilder = (SAMLObjectBuilder<SubjectConfirmation>) 
                builderFactory.getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        }
        
        SubjectConfirmation subjectConfirmation = subjectConfirmationBuilder.buildObject();
        subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key");
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        return subjectConfirmation;
    }

    /**
     * Create a NameID model
     * One of the following formats MUST be used:
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:entity
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:persistent
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:transient
     *
     * @param subject A SubjectBean instance
     * @return NameID
     */
    @SuppressWarnings("unchecked")
    public static NameID createNameID(SubjectBean subject) {
        if (nameIdBuilder == null) {
            nameIdBuilder = (SAMLObjectBuilder<NameID>) 
                builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        }
        NameID nameID = nameIdBuilder.buildObject();
        //nameID.setNameQualifier(subject.getSubjectNameQualifier());
        nameID.setFormat(SamlConstants.NAMEID_FORMAT_X509_SUBJECT_NAME);
        nameID.setValue(subject.getSubjectName());
        return nameID;
    }
    /**
     * Create a SubjectLocality model
     * One of the following formats MUST be used:
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName
     *   urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:entity
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:persistent
     *   urn:oasis:names:tc:SAML:2.0:nameid-format:transient
     *
     * @param address
     * @param dnsName
     * @return NameID
     */

    @SuppressWarnings("unchecked")
    public static SubjectLocality createSubjectLocality(String address, String dnsName) {
        if (subjectLocalityBuilder == null) {
        	subjectLocalityBuilder = (SAMLObjectBuilder<SubjectLocality>) 
                builderFactory.getBuilder(SubjectLocality.DEFAULT_ELEMENT_NAME);
        }
        SubjectLocality subjectLocality = subjectLocalityBuilder.buildObject();
        //nameID.setNameQualifier(subject.getSubjectNameQualifier());
        subjectLocality.setAddress(address);
        
        subjectLocality.setDNSName(dnsName);
        return subjectLocality;
    }
    
    
    /**
     * Create an Opensaml KeyInfo model from the parameters
     * @param keyInfo the KeyInfo bean from which to extract security credentials
     * @return the KeyInfo model
     * @throws org.opensaml.xml.security.SecurityException
     */
    public static KeyInfo createKeyInfo(KeyInfoBean keyInfo) 
        throws org.opensaml.xml.security.SecurityException, Exception {
        if (keyInfo.getElement() != null) {
            return (KeyInfo)fromDom(keyInfo.getElement());
        } else {
            // Set the certificate or public key
            BasicX509Credential keyInfoCredential = new BasicX509Credential();
            if (keyInfo.getCertificate() != null) {
                keyInfoCredential.setEntityCertificate(keyInfo.getCertificate());
            } else if (keyInfo.getPublicKey() != null) {
                keyInfoCredential.setPublicKey(keyInfo.getPublicKey());
            }
            
            // Configure how to emit the certificate
            X509KeyInfoGeneratorFactory kiFactory = new X509KeyInfoGeneratorFactory();
            KeyInfoBean.CERT_IDENTIFIER certIdentifier = keyInfo.getCertIdentifer();
            switch (certIdentifier) {
                case X509_CERT: {
                    kiFactory.setEmitEntityCertificate(true);
                    break;
                }
                case KEY_VALUE: {
                    kiFactory.setEmitPublicKeyValue(true);
                    break;
                }
                case X509_ISSUER_SERIAL: {
                    kiFactory.setEmitX509IssuerSerial(true);
                }
            }
            return kiFactory.newInstance().generate(keyInfoCredential);
        }
    }
    /**
     * Convert a SAML Assertion from a DOM Element to an XMLObject
     *
     * @param root of type Element
     * @return XMLObject
     * @throws UnmarshallingException
     */
    public static XMLObject fromDom(Element root) throws Exception {
        Unmarshaller unmarshaller = OpenSamlBootStrap.getUnmarshallerFactory().getUnmarshaller(root);
        try {
            return unmarshaller.unmarshall(root);
        } catch (UnmarshallingException ex) {
            throw new Exception("Error unmarshalling a SAML assertion", ex);
        }
    }
    
    /**
     * Create SAML2 Attribute Statement(s)
     *
     * @param attributeData A list of AttributeStatementBean instances
     * @return SAML2 Attribute Statement(s)
     */
    @SuppressWarnings("unchecked")
    public static List<AttributeStatement> createAttributeStatement(
        List<AttributeStatementBean> attributeData
    ) {
        List<AttributeStatement> attributeStatements = new ArrayList<AttributeStatement>();
        if (attributeStatementBuilder == null) {
            attributeStatementBuilder = (SAMLObjectBuilder<AttributeStatement>) 
            builderFactory.getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
        }

        if (attributeData != null && attributeData.size() > 0) {
            for (AttributeStatementBean statementBean : attributeData) {
                AttributeStatement attributeStatement = attributeStatementBuilder.buildObject();
                for (AttributeBean values : statementBean.getSamlAttributes()) {
                    Attribute samlAttribute = 
                        createAttribute(
                            values.getSimpleName(), 
                            values.getQualifiedName(),
                            values.getNameFormat(),
                            values.getAttributeValues()
                        );
                    attributeStatement.getAttributes().add(samlAttribute);
                }
                // Add the completed attribute statementBean to the collection
                attributeStatements.add(attributeStatement);
            }
        }

        return attributeStatements;
    }
    
    /**
     * Create an Attribute model.
     *
     * @param friendlyName of type String
     * @param name of type String
     * @param nameFormat of type String
     * @return an Attribute model
     */
    @SuppressWarnings("unchecked")
    public static Attribute createAttribute(String friendlyName, String name, String nameFormat) {
        if (attributeBuilder == null) {
            attributeBuilder = (SAMLObjectBuilder<Attribute>)
                builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        }
        
        Attribute attribute = attributeBuilder.buildObject();
        attribute.setFriendlyName(friendlyName);
        if (nameFormat == null) {
            attribute.setNameFormat(SamlConstants.ATTRNAME_FORMAT_URI);
        } else {
            attribute.setNameFormat(nameFormat);
        }
        
        attribute.setName(name);
        return attribute;
    }
    /**
     * Create a SAML2 Attribute
     *
     * @param friendlyName of type String
     * @param name         of type String
     * @param nameFormat   of type String
     * @param values       of type ArrayList
     * @return a SAML2 Attribute
     */
    public static Attribute createAttribute(
        String friendlyName, String name, String nameFormat, List<String> values
    ) {
        if (stringBuilder == null) {
            stringBuilder = (XSStringBuilder)builderFactory.getBuilder(XSString.TYPE_NAME);
        }
        Attribute attribute = createAttribute(friendlyName, name, nameFormat);
        for (String value : values) {
            XSString attributeValue = 
                stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
            attributeValue.setValue(value);
            attribute.getAttributeValues().add(attributeValue);
            
        }

        return attribute;
    }
    /**
     * Create SAML2 AuthorizationDecisionStatement(s)
     *
     * @param decisionData A list of AuthDecisionStatementBean instances
     * @return SAML2 AuthorizationDecisionStatement(s)
     */
    @SuppressWarnings("unchecked")
    public static List<AuthzDecisionStatement> createAuthorizationDecisionStatement(
        List<AuthDecisionStatementBean> decisionData
    ) {
        List<AuthzDecisionStatement> authDecisionStatements = new ArrayList();
        if (authorizationDecisionStatementBuilder == null) {
            authorizationDecisionStatementBuilder = 
                (SAMLObjectBuilder<AuthzDecisionStatement>)
                    builderFactory.getBuilder(AuthzDecisionStatement.DEFAULT_ELEMENT_NAME);
        }

        if (decisionData != null && decisionData.size() > 0) {
            for (AuthDecisionStatementBean decisionStatementBean : decisionData) {
                AuthzDecisionStatement authDecision = 
                    authorizationDecisionStatementBuilder.buildObject();
                authDecision.setResource(decisionStatementBean.getResource());
                authDecision.setDecision(
                    transformDecisionType(decisionStatementBean.getDecision())
                );

                for (ActionBean actionBean : decisionStatementBean.getActions()) {
                    Action actionElement = createSamlAction(actionBean);
                    authDecision.getActions().add(actionElement);
                }

                
                authDecision.setEvidence(createSamlEvidence()) ;
                
                authDecisionStatements.add(authDecision);
            }
        }

        return authDecisionStatements;
    }
    
    /**
     * Create an Evidence model
     *
     * @return an Action model
     */
    @SuppressWarnings("unchecked")
    public static Evidence createSamlEvidence() {
    	AssertionBean assertionBean = SamlAssertionData.getEvidenceInfo();
        if (evidenceElementBuilder == null) {
        	evidenceElementBuilder = (SAMLObjectBuilder<Evidence>)
                builderFactory.getBuilder(Evidence.DEFAULT_ELEMENT_NAME);
        }
        //SAMLCallback[] samlCallbacks = new SAMLCallback[] { OpenSamlBootStrap.samlCallBack };
        
        Evidence evidenceElement = evidenceElementBuilder.buildObject();
        String issuer = assertionBean.getIssuer();//samlCallbacks[0].getIssuer();
        Assertion assertion = createAssertion();
        Issuer samlIssuer = SAMLAssertionBuilder.createIssuer(issuer);
     // Attribute statement(s)
        List<org.opensaml.saml2.core.AttributeStatement> attributeStatements = 
            SAMLAssertionBuilder.createAttributeStatement(
                assertionBean.getAttrBean()
            );
        assertion.setIssuer(samlIssuer);
        org.opensaml.saml2.core.Conditions conditions = 
            SAMLAssertionBuilder.createConditions(assertionBean.getConditionsBean());
        assertion.setConditions(conditions);
        assertion.getAttributeStatements().addAll(attributeStatements);
        evidenceElement.getAssertions().add(assertion);
        
        return evidenceElement;
    }
    
    
    /**
     * Create an Action model
     *
     * @param actionBean An ActionBean instance
     * @return an Action model
     */
    @SuppressWarnings("unchecked")
    public static Action createSamlAction(ActionBean actionBean) {
        if (actionElementBuilder == null) {
            actionElementBuilder = (SAMLObjectBuilder<Action>)
                builderFactory.getBuilder(Action.DEFAULT_ELEMENT_NAME);
        }
        Action actionElement = actionElementBuilder.buildObject();
        actionElement.setNamespace(actionBean.getActionNamespace());
        actionElement.setAction(actionBean.getContents());

        return actionElement;
    }

    /**
     * Create a DecisionTypeEnumeration model
     *
     * @param decision of type Decision
     * @return a DecisionTypeEnumeration model
     */
    private static DecisionTypeEnumeration transformDecisionType(
        AuthDecisionStatementBean.Decision decision
    ) {
        DecisionTypeEnumeration decisionTypeEnum = DecisionTypeEnumeration.DENY;
        if (decision.equals(AuthDecisionStatementBean.Decision.PERMIT)) {
            decisionTypeEnum = DecisionTypeEnumeration.PERMIT;
        } else if (decision.equals(AuthDecisionStatementBean.Decision.INDETERMINATE)) {
            decisionTypeEnum = DecisionTypeEnumeration.INDETERMINATE;
        }

        return decisionTypeEnum;
    }
    /**
     * Create a Conditions model
     *
     * @param conditionsBean A ConditionsBean model
     * @return a Conditions model
     */
    @SuppressWarnings("unchecked")
    public static Conditions createConditions(ConditionsBean conditionsBean) {
        if (conditionsBuilder == null) {
            conditionsBuilder = (SAMLObjectBuilder<Conditions>) 
                builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
        }
        
        Conditions conditions = conditionsBuilder.buildObject();
        
        if (conditionsBean == null) {
            DateTime newNotBefore = new DateTime();
            conditions.setNotBefore(newNotBefore);
            conditions.setNotOnOrAfter(newNotBefore.plusMinutes(5));
            return conditions;
        }
        
        int tokenPeriodMinutes = conditionsBean.getTokenPeriodMinutes();
        DateTime notBefore = conditionsBean.getNotBefore();
        DateTime notAfter = conditionsBean.getNotAfter();
        
        if (notBefore != null && notAfter != null) {
            if (notBefore.isAfter(notAfter)) {
                throw new IllegalStateException(
                    "The value of notBefore may not be after the value of notAfter"
                );
            }
            conditions.setNotBefore(notBefore);
            conditions.setNotOnOrAfter(notAfter);
        } else {
            DateTime newNotBefore = new DateTime();
            conditions.setNotBefore(newNotBefore);
            conditions.setNotOnOrAfter(newNotBefore.plusMinutes(tokenPeriodMinutes));
        }
        
        if (conditionsBean.getAudienceURI() != null) {
            AudienceRestriction audienceRestriction = 
                createAudienceRestriction(conditionsBean.getAudienceURI());
            conditions.getAudienceRestrictions().add(audienceRestriction);
        }
        
        return conditions;
    }
    /**
     * Create an AudienceRestriction model
     *
     * @param audienceURI of type String
     * @return an AudienceRestriction model
     */
    @SuppressWarnings("unchecked")
    public static AudienceRestriction createAudienceRestriction(String audienceURI) {
        if (audienceRestrictionBuilder == null) {
            audienceRestrictionBuilder = (SAMLObjectBuilder<AudienceRestriction>) 
                builderFactory.getBuilder(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        }
        if (audienceBuilder == null) {
            audienceBuilder = (SAMLObjectBuilder<Audience>) 
                builderFactory.getBuilder(Audience.DEFAULT_ELEMENT_NAME);
        }
       
        AudienceRestriction audienceRestriction = audienceRestrictionBuilder.buildObject();
        Audience audience = audienceBuilder.buildObject();
        audience.setAudienceURI(audienceURI);
        audienceRestriction.getAudiences().add(audience);
        return audienceRestriction;
    }
}
