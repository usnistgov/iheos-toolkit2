package gov.nist.toolkit.saml.builder;

import org.apache.axiom.om.util.UUIDGenerator;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.saml1.core.AttributeStatement;
import org.opensaml.saml1.core.AuthenticationStatement;
import org.opensaml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml1.core.ConfirmationMethod;
import org.opensaml.saml1.core.Subject;
import org.opensaml.saml1.core.SubjectConfirmation;
import org.opensaml.saml1.core.SubjectStatement;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.X509KeyInfoGeneratorFactory;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.validation.ValidationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nist.toolkit.dsig.KeyStoreAccessObject;
import gov.nist.toolkit.saml.bean.SamlUtil;
import gov.nist.toolkit.saml.builder.bean.AssertionBean;
import gov.nist.toolkit.saml.util.CryptoType;
import gov.nist.toolkit.saml.util.DOM2Writer;
import gov.nist.toolkit.saml.util.SAMLCallback;
import gov.nist.toolkit.saml.util.SAMLKeyInfo;
import gov.nist.toolkit.saml.util.SAMLParms;
import gov.nist.toolkit.saml.util.SamlTokenExtractor;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.xml.*;
import org.opensaml.xml.io.*;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SAMLAssertionWrapper {
	 /**
     * Field log
     */
    private static final org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(SAMLAssertionWrapper.class);

    /**
     * Raw SAML assertion data
     */
    private XMLObject xmlObject = null;

    
    /**
     * Typed SAML v2.0 assertion
     */
    private org.opensaml.saml2.core.Assertion saml2 = null;

    /**
     * Which SAML specification to use (currently, only v1.1 and v2.0 are supported)
     */
    private SAMLVersion samlVersion;

    /**
     * Fully qualified class name of the SAML callback handler implementation to use.
     * NOTE: Each application should provide a unique implementation of this 
     * <code>CallbackHandler</code> that is able to extract any dynamic data from the
     * local environment that should be included in the generated SAML statements.
     */
    private CallbackHandler samlCallbackHandler = null;
    
    /**
     * The Assertion as a DOM element
     */
    private Element assertionElement;
    
    /**
     * The SAMLKeyInfo model associated with the Subject KeyInfo
     */
    private SAMLKeyInfo subjectKeyInfo;
    
    /**
     * The SAMLKeyInfo model associated with the Signature on the Assertion
     */
    private SAMLKeyInfo signatureKeyInfo;

    /**
     * Constructor AssertionWrapper creates a new AssertionWrapper instance.
     *
     * @param element of type Element
     * @throws UnmarshallingException when
     */
    public SAMLAssertionWrapper(Element element) throws Exception {
        //OpenSAMLUtil.initSamlEngine();
        
        this.xmlObject = fromDom(element);
        if (xmlObject instanceof org.opensaml.saml2.core.Assertion) {
            this.saml2 = (org.opensaml.saml2.core.Assertion) xmlObject;
            samlVersion = SAMLVersion.VERSION_20;
        } else {
            log.error(
                "AssertionWrapper: found unexpected type " 
                + (xmlObject != null ? xmlObject.getClass().getName() : xmlObject)
            );
        }
        
        assertionElement = element;
    }

    /**
     * Constructor AssertionWrapper creates a new AssertionWrapper instance.
     *
     * @param saml2 of type Assertion
     */
    public SAMLAssertionWrapper(org.opensaml.saml2.core.Assertion saml2) {
        this((XMLObject)saml2);
    }

    /**
     * Constructor AssertionWrapper creates a new AssertionWrapper instance.
     * This is the primary constructor.  All other constructor calls should
     * be routed to this method to ensure that the wrapper is initialized
     * correctly.
     *
     * @param xmlObject of type XMLObject
     */
    public SAMLAssertionWrapper(XMLObject xmlObject) {
        //OpenSAMLUtil.initSamlEngine();
        
        this.xmlObject = xmlObject;
        if (xmlObject instanceof org.opensaml.saml2.core.Assertion) {
            this.saml2 = (org.opensaml.saml2.core.Assertion) xmlObject;
            samlVersion = SAMLVersion.VERSION_20;
        } else {
            log.error(
                "AssertionWrapper: found unexpected type " 
                + (xmlObject != null ? xmlObject.getClass().getName() : xmlObject)
            );
        }
    }

    
    
    
    /**
     * Constructor AssertionWrapper creates a new AssertionWrapper instance.
     * This constructor is primarily called on the client side to initialize
     * the wrapper from a configuration file. <br>
     * NOTE: The OpenSaml library MUST be initialized prior to constructing an AssertionWrapper
     *
     * @param parms of type SAMLParms
     */
    public SAMLAssertionWrapper(AssertionBean params) throws Exception {
    	SAMLVersion samlVersion = SAMLVersion.VERSION_20;
        String issuer = params.getIssuer();
        if (issuer == null && params.getIssuer() != null) {
            issuer = params.getIssuer();
        }
        if (samlVersion.equals(SAMLVersion.VERSION_20)) {
            // Build a SAML v2.0 assertion
            saml2 = SAMLAssertionBuilder.createAssertion();
            Issuer samlIssuer = SAMLAssertionBuilder.createIssuer(issuer);

            // Authn Statement(s)
            List<AuthnStatement> authnStatements = 
                SAMLAssertionBuilder.createAuthnStatement(
                    params.getAuthenStateBean()
                );
            saml2.getAuthnStatements().addAll(authnStatements);

            // Attribute statement(s)
            List<org.opensaml.saml2.core.AttributeStatement> attributeStatements = 
                SAMLAssertionBuilder.createAttributeStatement(
                    params.getAttrBean()
                );
            saml2.getAttributeStatements().addAll(attributeStatements);

            // AuthzDecisionStatement(s)
            List<AuthzDecisionStatement> authDecisionStatements =
                    SAMLAssertionBuilder.createAuthorizationDecisionStatement(
                        params.getAuthzBean()
                    );
            saml2.getAuthzDecisionStatements().addAll(authDecisionStatements);

            // Build the SAML v2.0 assertion
            saml2.setIssuer(samlIssuer);
            
            try {
                org.opensaml.saml2.core.Subject subject = 
                    SAMLAssertionBuilder.createSaml2Subject(params.getSubjectBean());
                saml2.setSubject(subject);
            } catch (org.opensaml.xml.security.SecurityException ex) {
                throw new Exception(
                    "Error generating KeyInfo from signing credential", ex
                );
            }
            
            

            // Set the OpenSaml2 XMLObject instance
            xmlObject = saml2;
            
        }
    }
    
    
    public Element getAssertionElement() throws Exception{
    	
        	return toDOM(null);
            //return DOM2Writer.nodeToString(element);
    }
    
    
    /**
     * Method setSignature sets the signature of this AssertionWrapper model.
     *
     * @param signature the signature of this AssertionWrapper model.
     */
    public void setSignatureToAssertion(Signature signature) {
        if (xmlObject instanceof SignableSAMLObject) {
            SignableSAMLObject signableObject = (SignableSAMLObject) xmlObject;
            signableObject.setSignature(signature);
            signableObject.releaseDOM();
            signableObject.releaseChildrenDOM(true);
        } else {
            log.error("Attempt to sign an unsignable model " + xmlObject.getClass().getName());
        }
    }
    
    /**
     * Method buildSignature ...
     *
     * @return Signature
     */
    @SuppressWarnings("unchecked")
    public static Signature buildSignature() {
        QName qName = Signature.DEFAULT_ELEMENT_NAME;
        XMLObjectBuilder<Signature> builder = OpenSamlBootStrap.getBuilderFactory().getBuilder(qName);
        if (builder == null) {
            log.error(
                "Unable to retrieve builder for model QName "
                + qName
            );
            return null;
        }
        return
                builder.buildObject(
                     qName.getNamespaceURI(), qName.getLocalPart(), qName.getPrefix()
                 );
    }
    
      
    /**
     * Create an enveloped signature on the assertion that has been created.
     * 
     * @param issuerKeyName the Issuer KeyName to use with the issuerCrypto argument
     * @param issuerKeyPassword the Issuer Password to use with the issuerCrypto argument
     * @param issuerCrypto the Issuer Crypto instance
     * @param sendKeyValue whether to send the key value or not
     * @throws WSSecurityException
     */
    public void signAssertion(
        String issuerKeyName,
        String issuerKeyPassword,
        boolean sendKeyValue
    ) throws Exception {
        //
        // Create the signature
        //
        Signature signature = buildSignature();
        signature.setCanonicalizationAlgorithm(
            SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS
        );
        
        // prepare to sign the SAML token
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        
        cryptoType.setAlias(issuerKeyName);
        KeyStoreAccessObject ksAccessObj = KeyStoreAccessObject.getInstance(null);
		X509Certificate issuerCerts = ksAccessObj.getX509Certificate();
        if (issuerCerts == null) {
            throw new Exception(
                "No issuer certs were found to sign the SAML Assertion using issuer name: "
                + issuerKeyName
            );
        }

        String sigAlgo = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
        String pubKeyAlgo = issuerCerts.getPublicKey().getAlgorithm();
        if (log.isDebugEnabled()) {
            log.debug("automatic sig algo detection: " + pubKeyAlgo);
        }
        if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
            sigAlgo = SignatureConstants.ALGO_ID_SIGNATURE_DSA;
        }
        PrivateKey privateKey = ksAccessObj.getPrivateKey();
        
        signature.setSignatureAlgorithm(sigAlgo);

        BasicX509Credential signingCredential = new BasicX509Credential();
        signingCredential.setEntityCertificate(issuerCerts);
        signingCredential.setPrivateKey(privateKey);

        signature.setSigningCredential(signingCredential);

        X509KeyInfoGeneratorFactory kiFactory = new X509KeyInfoGeneratorFactory();
        if (sendKeyValue) {
            kiFactory.setEmitPublicKeyValue(true);
        } else {
            kiFactory.setEmitEntityCertificate(true);
        }
        try {
            KeyInfo keyInfo = kiFactory.newInstance().generate(signingCredential);
            signature.setKeyInfo(keyInfo);
        } catch (org.opensaml.xml.security.SecurityException ex) {
            throw new Exception(
                "Error generating KeyInfo from signing credential", ex
            );
        }

        // add the signature to the assertion
        setSignatureToAssertion(signature);
    }
    /**
     * Method getSaml2 returns the saml2 of this AssertionWrapper model.
     *
     * @return the saml2 (type Assertion) of this AssertionWrapper model.
     */
    public org.opensaml.saml2.core.Assertion getSaml2() {
        return saml2;
    }

    /**
     * Method getXmlObject returns the xmlObject of this AssertionWrapper model.
     *
     * @return the xmlObject (type XMLObject) of this AssertionWrapper model.
     */
    public XMLObject getXmlObject() {
        return xmlObject;
    }

    /**
     * Method isCreated returns the created of this AssertionWrapper model.
     *
     * @return the created (type boolean) of this AssertionWrapper model.
     */
    public boolean isCreated() {
        return saml2 != null;
    }


    /**
     * Create a DOM from the current XMLObject content. If the user-supplied doc is not null,
     * reparent the returned Element so that it is compatible with the user-supplied document.
     *
     * @param doc of type Document
     * @return Element
     */
    public Element toDOM(Document doc) throws Exception {
        assertionElement = toDom(xmlObject, doc);
        return assertionElement;
    }

    /**
     * Method assertionToString ...
     *
     * @return String
     */
    public String assertionToString() throws Exception {
        Element element = toDOM(null);
        return DOM2Writer.nodeToString(element);
    }

    /**
     * Method getId returns the id of this AssertionWrapper model.
     *
     * @return the id (type String) of this AssertionWrapper model.
     */
    public String getId() {
        String id = null;
        if (saml2 != null) {
            id = saml2.getID();
        } else {
            log.error("AssertionWrapper: unable to return ID - no saml assertion model");
        }
        if (id == null || id.length() == 0) {
            log.error("AssertionWrapper: ID was null, seeting a new ID value");
            id = UUIDGenerator.getUUID();
            if (saml2 != null) {
                saml2.setID(id);
            } 
        }
        return id;
    }

    /**
     * Method getIssuerString returns the issuerString of this AssertionWrapper model.
     *
     * @return the issuerString (type String) of this AssertionWrapper model.
     */
    public String getIssuerString() {
        if (saml2 != null && saml2.getIssuer() != null) {
            return saml2.getIssuer().getValue();
        }
        log.error(
            "AssertionWrapper: unable to return Issuer string - no saml assertion "
            + "model or issuer is null"
        );
        return null;
    }

    /**
     * Method getConfirmationMethods returns the confirmationMethods of this 
     * AssertionWrapper model.
     *
     * @return the confirmationMethods of this AssertionWrapper model.
     */
    public List<String> getConfirmationMethods() {
        List<String> methods = new ArrayList<String>();
        if (saml2 != null) {
            org.opensaml.saml2.core.Subject subject = saml2.getSubject();
            List<org.opensaml.saml2.core.SubjectConfirmation> confirmations = 
                subject.getSubjectConfirmations();
            for (org.opensaml.saml2.core.SubjectConfirmation confirmation : confirmations) {
                methods.add(confirmation.getMethod());
            }
        } 
        return methods;
    }

    /**
     * Method isSigned returns the signed of this AssertionWrapper model.
     *
     * @return the signed (type boolean) of this AssertionWrapper model.
     */
    public boolean isSigned() {
        if (saml2 != null) {
            return saml2.isSigned() || saml2.getSignature() != null;
        } 
        return false;
    }

    /**
     * Method setSignature sets the signature of this AssertionWrapper model.
     *
     * @param signature the signature of this AssertionWrapper model.
     */
    public void setSignature(Signature signature) {
        if (xmlObject instanceof SignableSAMLObject) {
            SignableSAMLObject signableObject = (SignableSAMLObject) xmlObject;
            signableObject.setSignature(signature);
            signableObject.releaseDOM();
            signableObject.releaseChildrenDOM(true);
        } else {
            log.error("Attempt to sign an unsignable model " + xmlObject.getClass().getName());
        }
    }
    
    /**
     * Method getSamlVersion returns the samlVersion of this AssertionWrapper model.
     *
     * @return the samlVersion (type SAMLVersion) of this AssertionWrapper model.
     */
    public SAMLVersion getSamlVersion() {
        if (samlVersion == null) {
            // Try to set the version.
            if (log.isDebugEnabled()) {
                log.debug(
                    "The SAML version was null in getSamlVersion(). Recomputing SAML version..."
                );
            }
            if (saml2 != null) {
                samlVersion = SAMLVersion.VERSION_20;
            } else {
                // We are only supporting SAML v1.1 or SAML v2.0 at this time.
                throw new IllegalStateException(
                    "Could not determine the SAML version number. Check your "
                    + "configuration and try again."
                );
            }
        }
        return samlVersion;
    }

    /**
     * Get the Assertion as a DOM Element.
     * @return the assertion as a DOM Element
     */
    public Element getElement() {
        return assertionElement;
    }
    
    /**
     * Get the SAMLKeyInfo associated with the signature of the assertion
     * @return the SAMLKeyInfo associated with the signature of the assertion
     */
    public SAMLKeyInfo getSignatureKeyInfo() {
        return signatureKeyInfo;
    }
    
    /**
     * Get the SAMLKeyInfo associated with the Subject KeyInfo
     * @return the SAMLKeyInfo associated with the Subject KeyInfo
     */
    public SAMLKeyInfo getSubjectKeyInfo() {
        return subjectKeyInfo;
    }
    /**
     * Convert a SAML Assertion from a DOM Element to an XMLObject
     *
     * @param root of type Element
     * @return XMLObject
     * @throws UnmarshallingException
     */
    public static XMLObject fromDom(Element root) throws Exception {
        Unmarshaller unmarshaller = OpenSamlBootStrap.unmarshallerFactory.getUnmarshaller(root);
        try {
            return unmarshaller.unmarshall(root);
        } catch (UnmarshallingException ex) {
            throw new Exception("Error unmarshalling a SAML assertion", ex);
        }
    }

    /**
     * Convert a SAML Assertion from a XMLObject to a DOM Element
     *
     * @param xmlObject of type XMLObject
     * @param doc  of type Document
     * @return Element
     * @throws MarshallingException
     * @throws SignatureException
     */
    public static Element toDom(
        XMLObject xmlObject, 
        Document doc
    ) throws Exception {
        Marshaller marshaller = OpenSamlBootStrap.marshallerFactory.getMarshaller(xmlObject);
        Element element = null;
        try {
            element = marshaller.marshall(xmlObject);
        } catch (MarshallingException ex) {
            throw new Exception("Error marshalling a SAML assertion", ex);
        }

        // Sign the assertion if the signature element is present.
        if (xmlObject instanceof org.opensaml.saml2.core.Assertion) {
            org.opensaml.saml2.core.Assertion saml2 = 
                (org.opensaml.saml2.core.Assertion) xmlObject;
            // if there is a signature, but it hasn't already been signed
            if (saml2.getSignature() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Signing SAML v2.0 assertion...");
                }
                try {
                    Signer.signObject(saml2.getSignature());
                } catch (SignatureException ex) {
                    throw new Exception("Error signing a SAML assertion", ex);
                }
            }
        } 

        // Reparent the document. This makes sure that the resulting element will be compatible
        // with the user-supplied document in the future (for example, when we want to add this
        // element that dom).
        if (doc != null) {
            if (log.isDebugEnabled()) {
                log.debug("Reparenting the SAML token dom to type: " + doc.getClass().getName());
            }
            Node importedNode = doc.importNode(element, true);
            element = (Element) importedNode;
        }

        return element;
    }
}