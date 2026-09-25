package gov.nist.toolkit.saml.builder;

import gov.nist.toolkit.dsig.KeyStoreAccessObject;
import gov.nist.toolkit.saml.builder.bean.AssertionBean;
import gov.nist.toolkit.saml.util.DOM2Writer;
import gov.nist.toolkit.saml.util.SAMLKeyInfo;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenSAML 5.1.4-compatible SAML Assertion wrapper for IHE XDS/XUA.
 *
 * <h3>Key fix vs. previous version</h3>
 * OpenSAML 5 requires an explicit bootstrap call before any registry access.
 * The static initializer below calls {@link OpenSAMLInitializer#ensureInitializedUnchecked()}
 * which invokes {@code InitializationService.initialize()} exactly once,
 * regardless of how many threads or constructors reach this class first.
 *
 * <p>Without that call every factory returned by
 * {@link XMLObjectProviderRegistrySupport} is {@code null} and you see:
 * <pre>
 *   ⚠ ServiceLoader discovery: Not finding providers
 *   ⚠ Registry from support: Returns null
 *   ⚠ Factory instances: All NULL (BuilderFactory, MarshallerFactory, UnmarshallerFactory)
 * </pre>
 *
 * @author Srinivasarao.Eadara
 */
public class SAMLAssertionWrapper {

    private static final Logger logger = LoggerFactory.getLogger(SAMLAssertionWrapper.class);

    // -------------------------------------------------------------------------
    // FIX 1 — Bootstrap OpenSAML 5 the first time this class is loaded.
    //
    // The static block runs exactly once per ClassLoader, before any constructor
    // or static method.  OpenSAMLInitializer uses double-checked locking so
    // repeated class-loads across multiple ClassLoaders are also idempotent.
    // -------------------------------------------------------------------------
    static {
        OpenSAMLInitializer.ensureInitializedUnchecked();
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Raw SAML assertion data. */
    private XMLObject xmlObject = null;

    /** Typed SAML v2.0 assertion. */
    private Assertion saml2 = null;

    /** SAML specification version (only v2.0 is supported for IHE XUA). */
    private SAMLVersion samlVersion;

    /** The Assertion as a DOM element (cached after the first marshal). */
    private Element assertionElement;

    /** SAMLKeyInfo model associated with the Subject KeyInfo. */
    private SAMLKeyInfo subjectKeyInfo;

    /** SAMLKeyInfo model associated with the Signature on the Assertion. */
    private SAMLKeyInfo signatureKeyInfo;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructor from a DOM Element.
     *
     * @param element the assertion element
     * @throws Exception when unmarshalling fails
     */
    public SAMLAssertionWrapper(Element element) throws Exception {
        this.xmlObject = fromDom(element);
        if (xmlObject instanceof Assertion) {
            this.saml2 = (Assertion) xmlObject;
            samlVersion = SAMLVersion.VERSION_20;
        } else {
            logger.warn(
                    "AssertionWrapper: found unexpected type {}",
                    (xmlObject != null ? xmlObject.getClass().getName() : "null")
            );
        }
        assertionElement = element;
    }

    /**
     * Constructor from a SAML 2.0 Assertion.
     *
     * @param saml2 the typed assertion
     */
    public SAMLAssertionWrapper(Assertion saml2) {
        this((XMLObject) saml2);
    }

    /**
     * Primary constructor. All other constructors should route through this
     * to ensure the wrapper is initialized correctly.
     *
     * @param xmlObject the raw XMLObject
     */
    public SAMLAssertionWrapper(XMLObject xmlObject) {
        this.xmlObject = xmlObject;
        if (xmlObject instanceof Assertion) {
            this.saml2 = (Assertion) xmlObject;
            samlVersion = SAMLVersion.VERSION_20;
        } else {
            logger.warn(
                    "AssertionWrapper: found unexpected type {}",
                    (xmlObject != null ? xmlObject.getClass().getName() : "null")
            );
        }
    }

    /**
     * Constructor from an {@link AssertionBean}. Used on the client side to
     * build an assertion from configuration.
     *
     * <p>OpenSAML is guaranteed to be initialized before the body of this
     * constructor runs because the {@code static} block fires when the class
     * is first loaded.
     *
     * @param params the assertion configuration bean
     * @throws Exception on build failure
     */
    public SAMLAssertionWrapper(AssertionBean params) throws Exception {
        SAMLVersion samlVersion = SAMLVersion.VERSION_20;

        String issuer = params.getIssuer();
        if (issuer == null) {
            throw new IllegalArgumentException("Issuer must not be null in AssertionBean");
        }

        if (samlVersion.equals(SAMLVersion.VERSION_20)) {
            saml2 = SAMLAssertionBuilder.createAssertion();
            Issuer samlIssuer = SAMLAssertionBuilder.createIssuer(issuer);

            List<AuthnStatement> authnStatements =
                    SAMLAssertionBuilder.createAuthnStatement(params.getAuthenStateBean());
            saml2.getAuthnStatements().addAll(authnStatements);

            List<AttributeStatement> attributeStatements =
                    SAMLAssertionBuilder.createAttributeStatement(params.getAttrBean());
            saml2.getAttributeStatements().addAll(attributeStatements);

            List<AuthzDecisionStatement> authDecisionStatements =
                    SAMLAssertionBuilder.createAuthorizationDecisionStatement(params.getAuthzBean());
            saml2.getAuthzDecisionStatements().addAll(authDecisionStatements);

            saml2.setIssuer(samlIssuer);

            try {
                org.opensaml.saml.saml2.core.Subject subject =
                        SAMLAssertionBuilder.createSaml2Subject(params.getSubjectBean(), saml2.getID());
                saml2.setSubject(subject);
            } catch (Exception ex) {
                throw new Exception("Error generating KeyInfo from signing credential", ex);
            }

            xmlObject = saml2;
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Get the assertion as a DOM Element (marshals if necessary).
     *
     * @return the assertion element
     * @throws Exception on marshaling failure
     */
    public Element getAssertionElement() throws Exception {
        return toDOM(null);
    }

    /**
     * Set an enveloped {@link Signature} on the underlying assertion.
     * Releases the cached DOM so the next marshal picks up the signature.
     *
     * <p><b>Note:</b> This is the single canonical implementation.
     * The previous version had a duplicate {@code setSignatureToAssertion}
     * method with identical logic — that duplicate has been removed.
     *
     * @param signature the signature to attach
     */
    public void setSignature(Signature signature) {
        if (xmlObject instanceof SignableSAMLObject) {
            SignableSAMLObject signableObject = (SignableSAMLObject) xmlObject;
            signableObject.setSignature(signature);
            // Release cached DOM so the next marshal includes the signature placeholder.
            signableObject.releaseDOM();
            signableObject.releaseChildrenDOM(true);
        } else {
            logger.error(
                    "Attempt to sign an unsignable object {}",
                    xmlObject.getClass().getName()
            );
        }
    }

    /**
     * Build an OpenSAML {@link Signature} object.
     *
     * @return Signature, or {@code null} if the builder cannot be resolved
     */
    @SuppressWarnings("unchecked")
    public static Signature buildSignature() {
        QName qName = Signature.DEFAULT_ELEMENT_NAME;

        XMLObjectBuilder<Signature> builder =
                (XMLObjectBuilder<Signature>)
                        XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(qName);

        if (builder == null) {
            logger.error("Unable to retrieve builder for QName {}", qName);
            return null;
        }
        return builder.buildObject(
                qName.getNamespaceURI(), qName.getLocalPart(), qName.getPrefix()
        );
    }

    /**
     * Create an enveloped XML-DSig signature on the assertion using the
     * credential resolved from the {@link KeyStoreAccessObject}.
     *
     * <p>Per IHE XUA profile requirements, SHA-1 is prohibited; this method
     * uses SHA-256 minimum for both RSA and DSA keys.
     *
     * <p>In OpenSAML 5 {@link KeyInfoGenerator#generate} is {@code @Nullable}
     * and throws {@link SecurityException}; both cases are handled explicitly.
     *
     * @param issuerKeyName     alias used for diagnostics / future key lookup
     * @param issuerKeyPassword reserved for future use
     * @param sendKeyValue      {@code true} to emit the raw public-key value;
     *                          {@code false} to emit the entity certificate
     * @throws Exception on signing or KeyInfo generation failure
     */
    public void signAssertion(
            String issuerKeyName,
            String issuerKeyPassword,
            boolean sendKeyValue
    ) throws Exception {

        Signature signature = buildSignature();
        if (signature == null) {
            throw new Exception(
                    "Failed to build Signature object — OpenSAML may not be fully initialized. "
                            + "Check that OpenSAMLInitializer.ensureInitialized() completed without errors."
            );
        }

        signature.setCanonicalizationAlgorithm(
                SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS
        );

        KeyStoreAccessObject ksAccessObj = KeyStoreAccessObject.getInstance(null);
        X509Certificate issuerCerts = ksAccessObj.getX509Certificate();
        if (issuerCerts == null) {
            throw new Exception(
                    "No issuer certs were found to sign the SAML Assertion using issuer name: "
                            + issuerKeyName
            );
        }

        // IHE XUA: SHA-1 is prohibited — use SHA-256 minimum.
        String pubKeyAlgo = issuerCerts.getPublicKey().getAlgorithm();
        logger.debug("Detected public key algorithm: {}", pubKeyAlgo);

        String sigAlgo;
        if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
            sigAlgo = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
        } else {
            sigAlgo = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;
        }

        PrivateKey privateKey = ksAccessObj.getPrivateKey();
        signature.setSignatureAlgorithm(sigAlgo);

        BasicX509Credential signingCredential =
                new BasicX509Credential(issuerCerts, privateKey);
        signature.setSigningCredential(signingCredential);

        // generate() is @Nullable in OpenSAML 5 — guard against null explicitly.
        X509KeyInfoGeneratorFactory kiFactory = new X509KeyInfoGeneratorFactory();
        if (sendKeyValue) {
            kiFactory.setEmitPublicKeyValue(true);
        } else {
            kiFactory.setEmitEntityCertificate(true);
        }

        try {
            KeyInfoGenerator generator = kiFactory.newInstance();
            KeyInfo keyInfo = generator.generate(signingCredential);
            if (keyInfo == null) {
                throw new Exception(
                        "KeyInfoGenerator returned null for the provided signing credential"
                );
            }
            signature.setKeyInfo(keyInfo);
        } catch (SecurityException ex) {
            throw new Exception("Error generating KeyInfo from signing credential", ex);
        }

        // Use the single canonical setSignature() — not the removed duplicate.
        setSignature(signature);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** @return the SAML 2.0 Assertion */
    public Assertion getSaml2() {
        return saml2;
    }

    /** @return the raw XMLObject */
    public XMLObject getXmlObject() {
        return xmlObject;
    }

    /** @return {@code true} if the SAML 2.0 assertion has been created */
    public boolean isCreated() {
        return saml2 != null;
    }

    /**
     * Marshal the current XMLObject to a DOM Element.
     * If {@code doc} is not null the returned Element is reparented into that
     * document.
     *
     * @param doc optional target Document
     * @return Element
     * @throws Exception on marshaling failure
     */
    public Element toDOM(Document doc) throws Exception {
        assertionElement = toDom(xmlObject, doc);
        return assertionElement;
    }

    /**
     * Serialize the assertion to a String.
     *
     * @return XML string
     * @throws Exception on serialization failure
     */
    public String assertionToString() throws Exception {
        Element element = toDOM(null);
        return DOM2Writer.nodeToString(element);
    }

    /**
     * Get the assertion ID.
     *
     * @return ID string, or {@code null} if not set
     */
    public String getId() {
        if (saml2 != null) {
            return saml2.getID();
        }
        logger.warn("AssertionWrapper: unable to return ID — no SAML assertion object");
        return null;
    }

    /**
     * Get the issuer string.
     *
     * @return issuer string, or {@code null} if not available
     */
    public String getIssuerString() {
        if (saml2 != null && saml2.getIssuer() != null) {
            return saml2.getIssuer().getValue();
        }
        logger.warn("AssertionWrapper: unable to return Issuer string — assertion or issuer is null");
        return null;
    }

    /**
     * Get the confirmation methods from the assertion Subject.
     *
     * @return list of confirmation method URIs (may be empty, never null)
     */
    public List<String> getConfirmationMethods() {
        List<String> methods = new ArrayList<>();
        if (saml2 != null) {
            org.opensaml.saml.saml2.core.Subject subject = saml2.getSubject();
            if (subject != null) {
                for (SubjectConfirmation confirmation : subject.getSubjectConfirmations()) {
                    methods.add(confirmation.getMethod());
                }
            }
        }
        return methods;
    }

    /** @return {@code true} if the assertion carries a populated Signature element */
    public boolean isSigned() {
        if (saml2 != null) {
            return saml2.isSigned() || saml2.getSignature() != null;
        }
        return false;
    }

    /** @return the SAML version (never null after construction) */
    public SAMLVersion getSamlVersion() {
        if (samlVersion == null) {
            logger.debug("SAML version was null in getSamlVersion(). Recomputing…");
            if (saml2 != null) {
                samlVersion = SAMLVersion.VERSION_20;
            } else {
                throw new IllegalStateException(
                        "Could not determine the SAML version number. "
                                + "Check your configuration and try again."
                );
            }
        }
        return samlVersion;
    }

    /** @return the cached assertion DOM element */
    public Element getElement() {
        return assertionElement;
    }

    /** @return SAMLKeyInfo associated with the signature */
    public SAMLKeyInfo getSignatureKeyInfo() {
        return signatureKeyInfo;
    }

    /** @return SAMLKeyInfo associated with the Subject KeyInfo */
    public SAMLKeyInfo getSubjectKeyInfo() {
        return subjectKeyInfo;
    }

    // -------------------------------------------------------------------------
    // Static helpers — use XMLObjectProviderRegistrySupport (OpenSAML 5 API)
    // -------------------------------------------------------------------------

    /**
     * Convert a DOM {@link Element} to an OpenSAML {@link XMLObject}.
     *
     * @param root the assertion element
     * @return XMLObject
     * @throws Exception on unmarshalling failure or missing unmarshaller
     */
    public static XMLObject fromDom(Element root) throws Exception {
        Unmarshaller unmarshaller =
                XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                        .getUnmarshaller(root);

        if (unmarshaller == null) {
            throw new Exception(
                    "No Unmarshaller registered for element {"
                            + root.getNamespaceURI() + "}" + root.getLocalName()
                            + ". Verify OpenSAML is initialized and all *-impl JARs are present."
            );
        }

        try {
            return unmarshaller.unmarshall(root);
        } catch (UnmarshallingException ex) {
            throw new Exception("Error unmarshalling a SAML assertion", ex);
        }
    }

    /**
     * Convert an OpenSAML {@link XMLObject} to a DOM {@link Element}.
     *
     * <p>For signed assertions the flow is:
     * <ol>
     *   <li>Set the {@link Signature} object <em>before</em> calling this method
     *       (via {@link #setSignature}).</li>
     *   <li>The marshaller outputs the {@code <ds:Signature>} placeholder.</li>
     *   <li>{@link Signer#signObject} fills in the actual signature value over
     *       the already-serialized DOM.</li>
     * </ol>
     *
     * @param xmlObject the object to marshal
     * @param doc       optional Document to reparent the element into
     * @return Element
     * @throws Exception on marshalling, signing, or missing marshaller
     */
    public static Element toDom(XMLObject xmlObject, Document doc) throws Exception {
        Marshaller marshaller =
                XMLObjectProviderRegistrySupport.getMarshallerFactory()
                        .getMarshaller(xmlObject);

        if (marshaller == null) {
            throw new Exception(
                    "No Marshaller registered for XMLObject type: "
                            + xmlObject.getClass().getName()
                            + ". Verify OpenSAML is initialized and all *-impl JARs are present."
            );
        }

        Element element;
        try {
            element = marshaller.marshall(xmlObject);
        } catch (MarshallingException ex) {
            throw new Exception("Error marshalling a SAML assertion", ex);
        }

        // Sign after marshalling so the signature covers the serialized DOM.
        if (xmlObject instanceof Assertion) {
            Assertion saml2 = (Assertion) xmlObject;
            if (saml2.getSignature() != null) {
                logger.debug("Signing SAML v2.0 assertion…");
                try {
                    Signer.signObject(saml2.getSignature());
                } catch (SignatureException ex) {
                    throw new Exception("Error signing a SAML assertion", ex);
                }
            }
        }

        if (doc != null) {
            logger.debug(
                    "Reparenting SAML token DOM to document type: {}",
                    doc.getClass().getName()
            );
            Node importedNode = doc.importNode(element, true);
            element = (Element) importedNode;
        }

        return element;
    }
}