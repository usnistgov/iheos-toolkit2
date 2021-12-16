package gov.nist.toolkit.saml.util;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.security.SAML2HTTPPostSimpleSignRule;
import org.opensaml.saml2.binding.security.SAML2HTTPRedirectDeflateSignatureRule;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.security.MetadataCredentialResolver;
import org.opensaml.security.MetadataCriteria;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.ChainingCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.StaticCredentialResolver;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.signature.impl.ExplicitKeySignatureTrustEngine;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.validation.ValidationException;
/**
 * @author Srinivasarao.Eadara
 *
 */
public class SamlResponseValidator {

	private Logger _logger = null;
    private Credential _credential = null;
    //private CryptoManager _cryptoManager = null;
    private Organization _organization = null;
    private KeyInfoCredentialResolver _keyInfoCredResolver = null;
    private SAMLSignatureProfileValidator _profileValidator = null;
    private BasicParserPool _pool = null;
    
    private ChainingCredentialResolver _chainingCredentialResolver = null;
    private SignatureTrustEngine _sigTrustEngine = null;
    private String _sEntityID;
    private String _issuer;
    
    private boolean _signatureRequired = false;
    
    
    
    // Verify type
    
    
    // Verify status
    
    
    // Verify signature of the response if present
    
    
    // Verify issue time
    

    // Verify response to field if present, set request if correct
    
    
    // Verify destination
    
    
    // Verify issuer
    
    // Verify assertions
    		
    // Make sure that at least one assertion contains authentication statement and subject cofirmation
    
    ///////////////////////////
    // 	Verify assertion time skew
    
    // Verify validity of assertion
    // Advice is ignored, core 574
    
    
    // Assertion with authentication statement must contain audience restriction
    
    
    
    
    /**
     * default constructor
     * 
     * @param sEntityID The Entity ID of the OA server. 
     * @param organization The model containing organization information.
     * @param requireSignature Indicates if a signature is mandatory
     */
    public SamlResponseValidator(String sEntityID, Organization organization, boolean requireSignature){
        _logger = Logger.getLogger(SamlResponseValidator.class.getName());
        
        //Engine engine = Engine.getInstance();
        //_cryptoManager = engine.getCryptoManager();
        
        _sEntityID = sEntityID;
        _organization = organization;
        _signatureRequired = requireSignature;
        
        try
        {
           // _credential = SAML2CryptoUtils.retrieveMySigningCredentials(
            //    _cryptoManager, _sEntityID);
        }
        catch(Exception e)
        {          
           //Logged in SAML2CryptoUtils
        }
        
        _keyInfoCredResolver =
            Configuration.getGlobalSecurityConfiguration(
                ).getDefaultKeyInfoCredentialResolver();
        
        _profileValidator = new SAMLSignatureProfileValidator();
        
        _pool = new BasicParserPool();
        _pool.setNamespaceAware(true);
        
        //Create ChainingCredentialResolver
        _chainingCredentialResolver =  
            new ChainingCredentialResolver();           
           
        //  -MG: EVB, JRE, RDV: define order of credential resolvers and test
                     
        //Metadata credentials
        if(_organization != null)
        {
            _issuer = _organization.getID();
            MetadataProvider mdProvider= null;
            try
            {
                mdProvider = _organization.getMetadataProvider();
            }
            catch (Exception e)
            {
                _logger.fine(
                    "Could not resolve Metadata provider found for issuer: " + _issuer);
            }
            
            if(mdProvider != null) //Metadata provider available
            {
                _logger.fine(
                    "Metadata provider found for issuer: " + _issuer);
                MetadataCredentialResolver mdCredResolver = 
                    new MetadataCredentialResolver(mdProvider);
                _chainingCredentialResolver.getResolverChain().add(mdCredResolver);
            }
        }
        
        //OA Engine credentials
        try
        {               
            if(_credential != null) //OA Signing enabled
            {
                Credential signingCred = null;
                   /// SAML2CryptoUtils.retrieveSigningCredentials(
                      //  _cryptoManager, _issuer);                   
                StaticCredentialResolver oaResolver = 
                    new StaticCredentialResolver(signingCred);
                _chainingCredentialResolver.getResolverChain().add(oaResolver);
            }
        }
        catch(Exception e) //No certificate found
        {
            _logger.fine(
                "No trusted certificate found for issuer: " + _issuer);
            //Ignore
        }
        
        _sigTrustEngine = 
            new ExplicitKeySignatureTrustEngine(
                _chainingCredentialResolver, _keyInfoCredResolver);
    }
    
    /**
     * Validate the decoded SAML2 response message.
     *
     * Performs basic message verification:  
     * <dl>
     *  <dd>{@link #validateSignature(SAMLMessageContext)}
     *  </dd>
     *      <dt>Validate signature</dt>
     *  <dd>Mandatory signing verification</dd>
     *      <dt>Verify if request MUST be signed</dt>
     * <dl>     
     * 
     * @param context The message context containing decoded message
     * @throws SAML2SecurityException If message should be rejected.
     * @throws OAException If an internal error occurs
     */
    public void validateResponse(SAMLMessageContext<SignableSAMLObject, 
        SignableSAMLObject, SAMLObject> context) 
        throws Exception
    {        
        context.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        validateMessage(context);
    }
    
    /**
     * Validates a SAML model.
     * 
     * @param context Message context.
     * @param obj The SAML model
     * @return true if not signed or if signature is correct.
     * @throws OAException If security error occurs
     */
    public boolean validateMessage(SAMLMessageContext<SignableSAMLObject, 
        SignableSAMLObject, SAMLObject> context, SignableSAMLObject obj)
    throws Exception
    {
        boolean bValid = false;
        
        Signature signature = obj.getSignature();
        
        if(obj.isSigned()) //Validate XML signature (if applicable)
        {    
            //create criteria set            
            CriteriaSet criteriaSet = new CriteriaSet();
            criteriaSet.add(new EntityIDCriteria(_issuer));
            MetadataCriteria mdCriteria = new MetadataCriteria(
                context.getPeerEntityRole(), 
                context.getInboundSAMLProtocol());
            criteriaSet.add(mdCriteria);
            criteriaSet.add(new UsageCriteria(UsageType.SIGNING) );
            try
            {
                bValid = _sigTrustEngine.validate(signature, criteriaSet);
            }
            catch (SecurityException e) //Internal processing error
            {
                _logger.log(Level.SEVERE, "Processing error evaluating the signature", e);
                throw new Exception("Processing error evaluating the signature");
            }
        }   
        else
            bValid = !_signatureRequired; //Message itself not signed
        
        return bValid;
    }
    
    /**
     * Validate a inbound message signature and/or simple signature.
     * @param context The message context.
     * @return <code>true</code> if signature is valid, otherwise <code>false</code>. 
     * @throws OAException If validation fails due to internal error.
     */
    protected boolean validateSignature( SAMLMessageContext<SignableSAMLObject, 
        SignableSAMLObject, SAMLObject> context) throws Exception 
    {       
        boolean bValid = false;
        try
        {
            // requestMessage == null is checked
            SignableSAMLObject message = context.getInboundSAMLMessage();
            
            Signature signature = message.getSignature(); 
            if(message.isSigned())
            {               
                //Validate against profile in order to prevent certain types 
                //of denial-of-service attacks associated with signature verification
                _profileValidator.validate(signature);                               
            }                                
            
            // -MG: EVB, JRE, RDV: define order of credential resolvers and test
            if(_chainingCredentialResolver.getResolverChain().isEmpty())
            {
                _logger.warning(
                    "No trusted certificate or metadata found for issuer: " + _issuer);
                //bValid = false already    
            }
            else
            {               
                //Create trust engine                
                // -MG: EVB: trust engine and resolver creation can be placed in one-time init code (e.g. SAML2Requestor)
                
                
                bValid = validateMessage(context, message);
                
                if(bValid) //Message not signed or valid signature
                {
                    //Validate simple signature for GET (if applicable)
                    SAML2HTTPRedirectDeflateSignatureRule ruleGET = 
                        new SAML2HTTPRedirectDeflateSignatureRule(_sigTrustEngine);
                    ruleGET.evaluate(context);
                    //Validate simple signature for POST (if applicable)
                    SAML2HTTPPostSimpleSignRule rulePOST = 
                        new SAML2HTTPPostSimpleSignRule(_sigTrustEngine, 
                            _pool, _keyInfoCredResolver);
                    rulePOST.evaluate(context);
                }
             }                                   
        }       
        catch(SecurityPolicyException e)
        {
            // Indicates signature was not cryptographically valid, or possibly a processing error
            _logger.log(Level.FINE, "Invalid signature", e);
            bValid = false;    
        }
        catch (ValidationException e) 
        {
            // Indicates signature was not cryptographically valid, or possibly a processing error
            _logger.log(Level.FINE, "Invalid signature", e);
            bValid = false;                
        }
        return bValid;
    }

    // Validate the decoded SAML2 message.     
    private void validateMessage(SAMLMessageContext<SignableSAMLObject, 
        SignableSAMLObject, SAMLObject> context) 
        throws Exception
    {        
        // requestMessage == null is checked
        SignableSAMLObject message = context.getInboundSAMLMessage();
        
        //Validate requestor
        String sRequestor = context.getInboundMessageIssuer();
        
        //DD We are aware of the fact that AuthZQuery responses MUST contain Issuer
        //Not SAML correct!
        if (sRequestor == null) sRequestor = _organization.getID();                  
            
        //Validate signature  
        HTTPInTransport inTransport = (HTTPInTransport) context.getInboundMessageTransport();
        String sigParam = inTransport.getParameterValue("Signature");
        boolean bSignatureParam = !DatatypeHelper.isEmpty(sigParam);
        if(bSignatureParam || message.isSigned())
        {           
            if (!validateSignature(context))
            {
                _logger.fine("Invalid XML signature received for message");
                throw new Exception("Invalid XML signature received for message");
            }     
            
            _logger.fine("XML signature validation okay");
            
        }
        else if (_signatureRequired)
        {
            //no signature, but was required: error:
            _logger.fine("No signature received for message, which is required");
            throw new Exception("No signature received for message, which is required");
        }
    }
    /**
     * Maximum time between assertion creation and current time when the assertion is usable
     */
    private static int MAX_ASSERTION_TIME = 3000;
    
   
    
    
    
    private void verifyAssertion(Assertion assertion, AuthnRequest request, BasicSAMLMessageContext context) throws  SAMLException, org.opensaml.xml.security.SecurityException, ValidationException, Exception {
        // Verify assertion time skew
        if (!isDateTimeSkewValid(MAX_ASSERTION_TIME, assertion.getIssueInstant())) {
            System.out.println("Authentication statement is too old to be used"+assertion.getIssueInstant());
            throw new Exception("Users authentication credential is too old to be used");
        }

        // Verify validity of assertion
        // Advice is ignored, core 574
        verifyIssuer(assertion.getIssuer(), context);
        verifyAssertionSignature(assertion.getSignature(), context);
        verifySubject(assertion.getSubject(), request, context);

        // Assertion with authentication statement must contain audience restriction
        if (assertion.getAuthnStatements().size() > 0) {
            verifyAssertionConditions(assertion.getConditions(), context, true);
            for (AuthnStatement statement : assertion.getAuthnStatements()) {
                verifyAuthenticationStatement(statement, context);
            }
        } else {
            verifyAssertionConditions(assertion.getConditions(), context, false);
        }
    }
    /**
     * Trust engine used to verify SAML signatures
     */
    private ExplicitKeySignatureTrustEngine trustEngine;


    protected static final String BEARER_CONFIRMATION = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    /**
     * Verifies validity of Subject element, only bearer confirmation is validated.
     * @param subject subject to validate
     * @param request request
     * @param context context
     * @throws SAMLException error validating the model
     */
    protected void verifySubject(Subject subject, AuthnRequest request, BasicSAMLMessageContext context) throws SAMLException {
        boolean confirmed = false;
        for (SubjectConfirmation confirmation : subject.getSubjectConfirmations()) {
            if (BEARER_CONFIRMATION.equals(confirmation.getMethod())) {

                SubjectConfirmationData data = confirmation.getSubjectConfirmationData();

                // Bearer must have confirmation 554
                if (data == null) {
                    System.out.println("Assertion invalidated by missing confirmation data");
                    throw new SAMLException("SAML Assertion is invalid");
                }

                // Not before forbidden by core 558
                if (data.getNotBefore() != null) {
                    System.out.println("Assertion contains not before in bearer confirmation, which is forbidden");
                    throw new SAMLException("SAML Assertion is invalid");
                }

                // Validate not on or after
                if (data.getNotOnOrAfter().isBeforeNow()) {
                    confirmed = false;
                    continue;
                }

                // Validate in response to
                if (request != null) {
                    if (data.getInResponseTo() == null) {
                        System.out.println("Assertion invalidated by subject confirmation - missing inResponseTo field");
                        throw new SAMLException("SAML Assertion is invalid");
                    } else {
                        if (!data.getInResponseTo().equals(request.getID())) {
                            System.out.println("Assertion invalidated by subject confirmation - invalid in response to");
                            throw new SAMLException("SAML Assertion is invalid");
                        }
                    }
                }

                // Validate recipient
                if (data.getRecipient() == null) {
                    System.out.println("Assertion invalidated by subject confirmation - recipient is missing in bearer confirmation");
                    throw new SAMLException("SAML Assertion is invalid");
                } else {
                    SPSSODescriptor spssoDescriptor = (SPSSODescriptor) context.getLocalEntityRoleMetadata();
                    for (AssertionConsumerService service : spssoDescriptor.getAssertionConsumerServices()) {
                        if (context.getInboundSAMLProtocol().equals(service.getBinding()) && service.getLocation().equals(data.getRecipient())) {
                            confirmed = true;
                        }
                    }
                }
            }
            // Was the subject confirmed by this confirmation data? If so let's store the subject in context.
            if (confirmed) {
                context.setSubjectNameIdentifier(subject.getNameID());
                return;
            }
        }

        System.out.println("Assertion invalidated by subject confirmation - can't be confirmed by bearer method");
        throw new SAMLException("SAML Assertion is invalid");
    }

    /**
     * Verifies signature of the assertion. In case signature is not present and SP required signatures in metadata
     * the exception is thrown.
     * @param signature signature to verify
     * @param context context
     * @throws SAMLException signature missing although required
     * @throws org.opensaml.xml.security.SecurityException signature can't be validated
     * @throws ValidationException signature is malformed
     */
    protected void verifyAssertionSignature(Signature signature, BasicSAMLMessageContext context) throws SAMLException, org.opensaml.xml.security.SecurityException, ValidationException {
        SPSSODescriptor roleMetadata = (SPSSODescriptor) context.getLocalEntityRoleMetadata();
        boolean wantSigned = roleMetadata.getWantAssertionsSigned();
        if (signature != null && wantSigned) {
            verifySignature(signature, context.getPeerEntityMetadata().getEntityID());
        } else if (wantSigned) {
            System.out.println("Assertion must be signed, but is not");
            throw new SAMLException("SAML Assertion is invalid");
        }
    }

    protected void verifyIssuer(Issuer issuer, BasicSAMLMessageContext context) throws SAMLException {
        // Validat format of issuer
        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY)) {
            System.out.println("Assertion invalidated by issuer type"+issuer.getFormat());
            throw new SAMLException("SAML Assertion is invalid");
        }

        // Validate that issuer is expected peer entity
        if (!context.getPeerEntityMetadata().getEntityID().equals(issuer.getValue())) {
            System.out.println("Assertion invalidated by unexpected issuer value"+ issuer.getValue());
            throw new SAMLException("SAML Assertion is invalid");
        }
    }

    protected void verifySignature(Signature signature, String IDPEntityID) throws org.opensaml.xml.security.SecurityException, ValidationException {
        SAMLSignatureProfileValidator validator = new SAMLSignatureProfileValidator();
        validator.validate(signature);
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIDCriteria(IDPEntityID));
        criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
        criteriaSet.add(new UsageCriteria(UsageType.SIGNING));
        System.out.println("Verifying signature"+ signature);
        trustEngine.validate(signature, criteriaSet);
    }

    protected void verifyAssertionConditions(Conditions conditions, BasicSAMLMessageContext context, boolean audienceRequired) throws SAMLException {
        // If no conditions are implied, assertion is deemed valid
        if (conditions == null) {
            return;
        }

        if (conditions.getNotBefore() != null) {
            if (conditions.getNotBefore().isAfterNow()) {
                System.out.println("Assertion is not yet valid, invalidated by condition notBefore"+ conditions.getNotBefore());
                throw new SAMLException("SAML response is not valid");
            }
        }
        if (conditions.getNotOnOrAfter() != null) {
            if (conditions.getNotOnOrAfter().isBeforeNow()) {
                System.out.println("Assertion is no longer valid, invalidated by condition notOnOrAfter"+ conditions.getNotOnOrAfter());
                throw new SAMLException("SAML response is not valid");
            }
        }

        if (audienceRequired && conditions.getAudienceRestrictions().size() == 0) {
            System.out.println("Assertion invalidated by missing audience restriction");
            throw new SAMLException("SAML response is not valid");
        }

        audience:
        for (AudienceRestriction rest : conditions.getAudienceRestrictions()) {
            if (rest.getAudiences().size() == 0) {
                System.out.println("No audit audience specified for the assertion");
                throw new SAMLException("SAML response is invalid");
            }
            for (Audience aud : rest.getAudiences()) {
                if (context.getLocalEntityId().equals(aud.getAudienceURI())) {
                    continue audience;
                }
            }
            System.out.println("Our entity is not the intended audience of the assertion");
            throw new SAMLException("SAML response is not intended for this entity");
        }

        /** ? BUG
         if (conditions.getConditions().size() > 0) {
         System.out.println("Assertion contain not understood conditions");
         throw new SAMLException("SAML response is not valid");
         }
         */
    }
    /**
     * Maximum time between user's authentication and current time
     */
    private static int MAX_AUTHENTICATION_TIME = 7200;
    protected void verifyAuthenticationStatement(AuthnStatement auth, BasicSAMLMessageContext context) throws Exception {
        // Validate that user wasn't authenticated too long time ago
        if (!isDateTimeSkewValid(MAX_AUTHENTICATION_TIME, auth.getAuthnInstant())) {
            System.out.println("Authentication statement is too old to be used"+auth.getAuthnInstant());
            throw new Exception("Users authentication data is too old");
        }

        // Validate users session is still valid
        if (auth.getSessionNotOnOrAfter() != null && auth.getSessionNotOnOrAfter().isAfter(new Date().getTime())) {
            System.out.println("Authentication session is not valid anymore"+auth.getSessionNotOnOrAfter());
            throw new Exception("Users authentication is expired");
        }

        if (auth.getSubjectLocality() != null) {
            HTTPInTransport httpInTransport = (HTTPInTransport) context.getInboundMessageTransport();
            if (auth.getSubjectLocality().getAddress() != null) {
                if (!httpInTransport.getPeerAddress().equals(auth.getSubjectLocality().getAddress())) {
                    throw new Exception("User is accessing the service from invalid address");
                }
            }
        }
    }

    private boolean isDateTimeSkewValid(int skewInSec, DateTime time) {
        return time.isAfter(new Date().getTime() - skewInSec * 1000) && time.isBeforeNow();
    }


       
    
}
