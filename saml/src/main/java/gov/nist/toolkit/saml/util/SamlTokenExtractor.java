package gov.nist.toolkit.saml.util;

import gov.nist.toolkit.saml.attributes.AttributeUtil;
import gov.nist.toolkit.saml.bean.AssertionType;
import gov.nist.toolkit.saml.bean.CeType;
import gov.nist.toolkit.saml.bean.HomeCommunityType;
import gov.nist.toolkit.saml.bean.PersonNameType;
import gov.nist.toolkit.saml.bean.SamlAuthnStatementType;
import gov.nist.toolkit.saml.bean.SamlAuthzDecisionStatementEvidenceAssertionType;
import gov.nist.toolkit.saml.bean.SamlAuthzDecisionStatementEvidenceConditionsType;
import gov.nist.toolkit.saml.bean.SamlAuthzDecisionStatementEvidenceType;
import gov.nist.toolkit.saml.bean.SamlAuthzDecisionStatementType;
import gov.nist.toolkit.saml.bean.SamlSignatureKeyInfoType;
import gov.nist.toolkit.saml.bean.SamlSignatureType;
import gov.nist.toolkit.saml.bean.UserType;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Evidence;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.signature.ContentReference;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.util.LazyList;
import org.opensaml.xml.validation.ValidationException;

import sun.security.x509.AVA;
import sun.security.x509.X500Name;

public class SamlTokenExtractor {
	private static Log log = LogFactory.getLog(SamlTokenExtractor.class);
    private static final String X509_FORMAT = "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName";
    private static final String EMPTY_STRING = "";
    public static boolean samlEngineInitialized = false;
    public static Assertion samlAssertion ;


    /**
     * Initialise the SAML library
     */
    public synchronized static void initSamlEngine() {
        if (!samlEngineInitialized) {
            if (log.isDebugEnabled()) {
                log.debug("Initilizing the opensaml2 library...");
            }
            try {
            	DefaultBootstrap.bootstrap();
                samlEngineInitialized = true;
                if (log.isDebugEnabled()) {
                    log.debug("opensaml2 library bootstrap complete");
                }
            } catch (ConfigurationException e) {
                log.error(
                    "Unable to bootstrap the opensaml2 library - all SAML operations will fail", 
                    e
                );
            }
        }
    }

    
    
   public static AssertionType CreateAssertion(OMElement  element  ) throws Exception {
        log.debug("Entering SamlTokenExtractor.CreateAssertion...");

        AssertionType assertion = initializeAssertion();

        try {
        	
        	
        	samlAssertion = unmarshellElement(element);
        	
        	
        	
        	
            // If we know the underlying type, we can get to more of the data we are looking for.
            //------------------------------------------------------------------------------------
           
        	extractSubject(samlAssertion, assertion);
           
            List statements = samlAssertion.getStatements();
            if (statements != null && !statements.isEmpty()) {
                for (int idx = 0; idx < statements.size(); idx++) {
                    if (statements.get(idx) instanceof AttributeStatement) {
                    	AttributeStatement statement = (AttributeStatement) statements.get(idx);
                        extractAttributeInfo(statement, assertion);
                    } else if (statements.get(idx) instanceof AuthzDecisionStatement) {
                    	AuthzDecisionStatement statement = (AuthzDecisionStatement) statements.get(idx);
                        extractDecisionInfo(statement, assertion);
                    } else if (statements.get(idx) instanceof AuthnStatement) {
                    	AuthnStatement statement = (AuthnStatement) statements.get(idx);
                        extractAuthnStatement(statement, assertion);
                    } else {
                        log.warn("Unknown statement type: " + statements.get(idx));
                    }
                }
                
            } else {
                log.error("There were no statements specified in the SAML token.");
                assertion = null;
            }
            if(samlAssertion.getSignature()!=null){
            	
            }
            
        } catch (SAMLException ex) {
            log.error("SAML Exception Thrown: " + ex.getMessage());
            assertion = null;
        } 

        log.debug("Exiting SamlTokenExtractor.CreateAssertion");
        return assertion;
    }
   
   
   
   /**
    * The Authorization Decision Statement is used to convey a form authorizing
    * access to medical records.  It may embed the binary content of the
    * authorization form as well describing the conditions of its validity.
    * This method saves off all values associated with this Evidence.
    * @param authnStatement The authn statement element
    * @param assertOut The Assertion element being written to
    */
   private static void extractAuthnStatement(AuthnStatement authnStatement, AssertionType assertOut) {
       log.debug("Entering SamlTokenExtractor.extractAuthnStatement...");

       if (authnStatement == null) {
           log.debug("authnStatement is null: ");
           return;         // nothing to do...
       }

       SamlAuthnStatementType samlAuthnStatement = assertOut.getSamlAuthnStatement();

       // AuthnInstant
       //-------------
       if (authnStatement.getAuthnInstant() != null) {
           samlAuthnStatement.setAuthInstant(authnStatement.getAuthnInstant().toString());
           log.debug("Assertion.samlAuthnStatement.authnInstant = " + samlAuthnStatement.getAuthInstant());
       }

       // SessionIndex
       if (authnStatement.getSessionIndex() != null) {
           samlAuthnStatement.setSessionIndex(authnStatement.getSessionIndex());
           log.debug("Assertion.samlAuthnStatement.sessionIndex = " + samlAuthnStatement.getSessionIndex());
       }

       // AuthContextClassRef
       //--------------------
       if ((authnStatement.getAuthnContext() != null) &&
               (authnStatement.getAuthnContext().getAuthnContextClassRef() != null)) {
    	   //log.debug("authnContext has " + authnStatement.getAuthnContext().getAuthnContextClassRef().size() + " content entries. ");
    	   samlAuthnStatement.setAuthContextClassRef(authnStatement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef());
           log.debug("Assertion.samlAuthnStatement.authContextClassRef = " + samlAuthnStatement.getAuthContextClassRef());
    	   
    	   
         log.debug("authnContext has " + authnStatement.getAuthnContext().getAuthenticatingAuthorities().size() + " content entries. ");
           List<AuthenticatingAuthority> contents = authnStatement.getAuthnContext().getAuthenticatingAuthorities();
           if ((contents != null) &&
                   (contents.size() > 0)) {
               for (AuthenticatingAuthority jaxElem1 : contents) {
                   // When you look at the actual XML string, it looks as follows:
                   // 		<saml2:AuthnContext>
                   //          <saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:X509</saml2:AuthnContextClassRef>
                   //      </saml2:AuthnContext>
                   // This implies that we should see under the AuthnContext tag another tag.  But for some reason the
                   // unmarshaller is stripping that layer out and what we see directly under the AuthnContext is
                   // a string element that contains the value that is currently in the AuthnContextClassRef tag.  So
                   // that is why we are looking for a string data type here.
                   //----------------------------------------------------------------------------------------------------------------
                   if (jaxElem1.toString() instanceof String) {
                       String sValue = (String) jaxElem1.toString();
                       samlAuthnStatement.setAuthContextClassRef(sValue);
                       log.debug("Assertion.samlAuthnStatement.authContextClassRef = " + samlAuthnStatement.getAuthContextClassRef());
                   }   // if (jaxElem1.getValue() instanceof AuthnContext)
               }   // for (JAXBElement jaxElem1 : contents)
           }   // if ((contents != null) &&*/
       }   // if ((authnStatement.getAuthnContext() != null) &&

       // SubjectLocalityAddress
       //-----------------------
       if ((authnStatement.getSubjectLocality() != null) &&
               (authnStatement.getSubjectLocality().getAddress() != null) &&
               (authnStatement.getSubjectLocality().getAddress().length() > 0)) {
           samlAuthnStatement.setSubjectLocalityAddress(authnStatement.getSubjectLocality().getAddress());
           log.debug("Assertion.samlAuthnStatement.subjectlocalityAddress = " + samlAuthnStatement.getSubjectLocalityAddress());
       }

       // SubjectLocalityDNSName
       //------------------------
       if ((authnStatement.getSubjectLocality() != null) &&
               (authnStatement.getSubjectLocality().getDNSName() != null) &&
               (authnStatement.getSubjectLocality().getDNSName().length() > 0)) {
           samlAuthnStatement.setSubjectLocalityDNSName(authnStatement.getSubjectLocality().getDNSName());
           log.debug("Assertion.samlAuthnStatement.subjectlocalityDNSName = " + samlAuthnStatement.getSubjectLocalityDNSName());
       }

       log.debug("Exiting SamlTokenExtractor.extractAuthnStatement...");
   }
   
   /**
    * The Authorization Decision Statement is used to convey a form authorizing 
    * access to medical records.  It may embed the binary content of the 
    * authorization form as well describing the conditions of its validity.  
    * This method saves off all values associated with this Evidence.
    * @param authzState The authorization decision element
    * @param assertOut The Assertion element being written to
 * @throws Exception 
    */
   private static void extractDecisionInfo(AuthzDecisionStatement authzState, AssertionType assertOut) throws Exception {
       log.debug("Entering SamlTokenExtractor.extractDecisionInfo...");

       SamlAuthzDecisionStatementType oSamlAuthzDecision = assertOut.getSamlAuthzDecisionStatement();

       // @Decision
       //-----------
       if ((authzState.getDecision() != null) &&
               (authzState.getDecision() != null)) {
           oSamlAuthzDecision.setDecision(authzState.getDecision().toString());
           log.debug("Assertion.SamlAuthzDecisionStatement.Decision = " + oSamlAuthzDecision.getDecision());
       }

       // @Resource
       //----------
       if (authzState.getResource() != null) {
           oSamlAuthzDecision.setResource(authzState.getResource());
           log.debug("Assertion.SamlAuthzDecisionStatement.Resource = " + oSamlAuthzDecision.getResource());
       }

       // Action
       // According to the NHIN Specifications we should see exactly one of these.  If there are more than
       // one we will only take the first one.
       //--------------------------------------------------------------------------------------------------
       if ((authzState.getActions() != null) &&
               (authzState.getActions().size() > 0) &&
               (authzState.getActions().get(0) != null) &&
               (authzState.getActions().get(0).getAction() != null)) {
           oSamlAuthzDecision.setAction(authzState.getActions().get(0).getAction());
           oSamlAuthzDecision.setActionNameSpace(authzState.getActions().get(0).getNamespace());
           log.debug("Assertion.SamlAuthzDecisionStatement.Action = " + oSamlAuthzDecision.getAction());
       }

       // Evidence
       //----------
       Evidence evid = authzState.getEvidence();
       extractEvidence(evid, assertOut);
       

       log.debug("Exiting SamlTokenExtractor.extractDecisionInfo...");
   }
   
   /**
    * This extracts the evidence information from the authorization
    * decision statement and places the data into the assertion class.
    *
    * @param evidence The evidence that was part of the authroization decision statement.
    * @param assertOut The class where the data will be placed.
 * @throws Exception 
    */
   private static void extractEvidence(Evidence evidence, AssertionType assertOut) throws Exception {
       log.debug("Entering SamlTokenExtractor.extractEvidence...");
       SamlAuthzDecisionStatementEvidenceAssertionType oSamlEvidAssert = assertOut.getSamlAuthzDecisionStatement().getEvidence().getAssertion();

       List<Assertion> oaAsserts = evidence.getAssertions();
       if ((oaAsserts != null) &&
               (oaAsserts.size() > 0)) {
           // Loop looking for the tags we need.
           //----------------------------------------
           for (Assertion oElement : oaAsserts) {
               // Is this an Assertion?
               //-----------------------
                   // ID
                   //----
                   if (oElement.getID() != null) {
                       oSamlEvidAssert.setId(oElement.getID());
                       log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.Id = " + oSamlEvidAssert.getId());
                   }

                   // Issue Instant
                   //--------------
                   if (oElement.getIssueInstant() != null) {
                       oSamlEvidAssert.setIssueInstant(oElement.getIssueInstant().toString());
                       log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.IssueInstant = " + oElement.getIssueInstant());
                   }

                   // Version
                   //--------
                   if (oElement.getVersion() != null) {
                       oSamlEvidAssert.setVersion(oElement.getVersion().toString());
                       log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.Version = " + oSamlEvidAssert.getVersion());
                   }

                   // Issuer Format
                   //---------------
                   if ((oElement.getIssuer() != null) &&
                           (oElement.getIssuer().getFormat() != null)) {
                       oSamlEvidAssert.setIssuerFormat(oElement.getIssuer().getFormat());
                       log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.IssuerFormat = " + oSamlEvidAssert.getIssuerFormat());
                   }

                   // Issuer
                   //-------
                   if ((oElement.getIssuer() != null) &&
                           (oElement.getIssuer().getValue() != null)) {
                       oSamlEvidAssert.setIssuer(oElement.getIssuer().getValue());
                       log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.Issuer = " + oSamlEvidAssert.getIssuer());
                   }

                   extractConditionsInfo(assertOut, oElement.getConditions());

                   List statements = oElement.getAttributeStatements();
                   if (statements != null && !statements.isEmpty()) {
                       for (int idxState = 0; idxState <
                               statements.size(); idxState++) {
                           if (statements.get(idxState) instanceof AttributeStatement) {
                               AttributeStatement statement = (AttributeStatement) statements.get(idxState);
                               extractAttributeInfo(statement, assertOut);
                           }
                       }
                   } else {
                       log.error("Evidence Statements are missing.");
                   }
               
           }
       } else {
           log.error("Evidence assertion is empty: " + oaAsserts);
       }

       log.debug("Exiting SamlTokenExtractor.extractEvidence...");
   }

   /**
    * This method extracts the dates of validity for the Evidence's 
    * authorization form.  These dates are contained in the Conditions element 
    * and are written out to the storage file by this method.
    * @param assertOut The Assertion element being written to
    * @param conditions The Evidence's Conditions element
    */
   private static void extractConditionsInfo(AssertionType assertOut, Conditions conditions) {
       log.debug("Entering SamlTokenExtractor.extractConditionsInfo...");

       SamlAuthzDecisionStatementEvidenceAssertionType oSamlEvidAssert = assertOut.getSamlAuthzDecisionStatement().getEvidence().getAssertion();

       if (conditions != null) {
           //SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

           DateTime beginTime = conditions.getNotBefore();
           if (beginTime != null && beginTime.toGregorianCalendar() != null && beginTime.toGregorianCalendar().getTime() != null) {
               String formBegin = beginTime.toString();

               if (NullChecker.isNotNullish(formBegin)) {
                   oSamlEvidAssert.getConditions().setNotBefore(formBegin);
                   log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.Conditions.NotBefore = " + oSamlEvidAssert.getConditions().getNotBefore());
               }
           }

           DateTime endTime = conditions.getNotOnOrAfter();
           if (endTime != null && endTime.toGregorianCalendar() != null && endTime.toGregorianCalendar().getTime() != null) {
               String formEnd = endTime.toString();
               //String formEnd = dateForm.format(endTime.toGregorianCalendar().getTime());

               if (NullChecker.isNotNullish(formEnd)) {
                   oSamlEvidAssert.getConditions().setNotOnOrAfter(formEnd);
                   log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.Conditions.NotOnOrAfter = " + oSamlEvidAssert.getConditions().getNotOnOrAfter());
               }
           }
       }
       log.debug("Exiting SamlTokenExtractor.extractConditionsInfo...");
   }
    /**
     * Initializes the assertion model to contain empty strings for all values.
     * These are overwritten in the extraction process with real values if they
     * are available
     * @param assertOut The Assertion element being written to
     */
    private static AssertionType initializeAssertion() {

        //System.out.println("Initializing Assertion to Default: " + EMPTY_STRING);
        AssertionType assertOut = new AssertionType();

        CeType purposeCoded = new CeType();
        UserType user = new UserType();
        PersonNameType userPerson = new PersonNameType();
        CeType userRole = new CeType();
        HomeCommunityType userHc = new HomeCommunityType();
        HomeCommunityType homeCom = new HomeCommunityType();
        SamlAuthnStatementType samlAuthnStatement = new SamlAuthnStatementType();
        SamlAuthzDecisionStatementType samlAuthzDecisionStatement = new SamlAuthzDecisionStatementType();
        SamlAuthzDecisionStatementEvidenceType samlAuthzDecisionStatementEvidence = new SamlAuthzDecisionStatementEvidenceType();
        SamlAuthzDecisionStatementEvidenceAssertionType samlAuthzDecisionStatementAssertion = new SamlAuthzDecisionStatementEvidenceAssertionType();
        SamlAuthzDecisionStatementEvidenceConditionsType samlAuthzDecisionStatementEvidenceConditions = new SamlAuthzDecisionStatementEvidenceConditionsType();
        SamlSignatureType samlSignature = new SamlSignatureType();
        SamlSignatureKeyInfoType samlSignatureKeyInfo = new SamlSignatureKeyInfoType();

        assertOut.setHomeCommunity(homeCom);
        homeCom.setHomeCommunityId(EMPTY_STRING);

        assertOut.getUniquePatientId().clear();

        user.setPersonName(userPerson);
        user.setOrg(userHc);
        user.setRoleCoded(userRole);
        assertOut.setUserInfo(user);
        assertOut.setPurposeOfDisclosureCoded(purposeCoded);

        userPerson.setGivenName(EMPTY_STRING);
        userPerson.setFamilyName(EMPTY_STRING);
        userPerson.setSecondNameOrInitials(EMPTY_STRING);
        userPerson.setFullName(EMPTY_STRING);

        userHc.setName(EMPTY_STRING);
        userHc.setHomeCommunityId(EMPTY_STRING);
        user.setUserName(EMPTY_STRING);
        userRole.setCode(EMPTY_STRING);
        userRole.setCodeSystem(EMPTY_STRING);
        userRole.setCodeSystemName(EMPTY_STRING);
        userRole.setDisplayName(EMPTY_STRING);

        purposeCoded.setCode(EMPTY_STRING);
        purposeCoded.setCodeSystem(EMPTY_STRING);
        purposeCoded.setCodeSystemName(EMPTY_STRING);
        purposeCoded.setDisplayName(EMPTY_STRING);

        assertOut.setSamlAuthnStatement(samlAuthnStatement);
        samlAuthnStatement.setAuthInstant(EMPTY_STRING);
        samlAuthnStatement.setSessionIndex(EMPTY_STRING);
        samlAuthnStatement.setAuthContextClassRef(EMPTY_STRING);
        samlAuthnStatement.setSubjectLocalityAddress(EMPTY_STRING);
        samlAuthnStatement.setSubjectLocalityDNSName(EMPTY_STRING);

        assertOut.setSamlAuthzDecisionStatement(samlAuthzDecisionStatement);
        samlAuthzDecisionStatement.setDecision(EMPTY_STRING);
        samlAuthzDecisionStatement.setResource(EMPTY_STRING);
        samlAuthzDecisionStatement.setAction(EMPTY_STRING);

        samlAuthzDecisionStatement.setEvidence(samlAuthzDecisionStatementEvidence);

        samlAuthzDecisionStatementEvidence.setAssertion(samlAuthzDecisionStatementAssertion);
        samlAuthzDecisionStatementAssertion.setId(EMPTY_STRING);
        samlAuthzDecisionStatementAssertion.setIssueInstant(EMPTY_STRING);
        samlAuthzDecisionStatementAssertion.setVersion(EMPTY_STRING);
        samlAuthzDecisionStatementAssertion.setIssuer(EMPTY_STRING);
        samlAuthzDecisionStatementAssertion.setAccessConsentPolicy(EMPTY_STRING);
        samlAuthzDecisionStatementAssertion.setInstanceAccessConsentPolicy(EMPTY_STRING);

        samlAuthzDecisionStatementAssertion.setConditions(samlAuthzDecisionStatementEvidenceConditions);
        samlAuthzDecisionStatementEvidenceConditions.setNotBefore(EMPTY_STRING);
        samlAuthzDecisionStatementEvidenceConditions.setNotOnOrAfter(EMPTY_STRING);

        byte[] formRaw = EMPTY_STRING.getBytes();
        assertOut.setSamlSignature(samlSignature);
        samlSignature.setSignatureValue(formRaw);

        samlSignature.setKeyInfo(samlSignatureKeyInfo);
        samlSignatureKeyInfo.setRsaKeyValueExponent(formRaw);
        samlSignatureKeyInfo.setRsaKeyValueModulus(formRaw);

        return assertOut;
    }
    
    private static void extractSignatureInfo(Assertion assertion, AssertionType assertOut) {
    	SamlSignatureType samlSignature = assertOut.getSamlSignature() ;
    	SamlSignatureKeyInfoType samlSignatureKeyInfoType = samlSignature.getKeyInfo() ;
    	byte []signatureValue = samlSignature.getSignatureValue();
    	samlSignature.getKeyInfo().getRsaKeyValueExponent();
    	samlSignature.getKeyInfo().getRsaKeyValueModulus() ;
    	
    	Signature signature = assertion.getSignature() ;
    	assertion.getSignature().getCanonicalizationAlgorithm();
    	signature.getSignatureAlgorithm();
    	List<ContentReference> contentReference1 = signature.getContentReferences();
    	
    	ContentReference contentReference = (ContentReference)contentReference1.get(0);
    	signature.getSigningCredential().getPublicKey().getAlgorithm();
    	
    	//signature.getSigningCredential().
    	
    	
    }
    private static String getSubjectNameIDValue(Assertion assertion) {
		String retVal = null;
    	if (assertion.getSubject() != null && 
        	assertion.getSubject().getNameID() != null) {
        		retVal =  assertion.getSubject().getNameID().getValue();
        		//assertion.getSubject().getNameID().get
        }
    	return retVal;
	}
    private static String getSubjectNameIDFormat(Assertion assertion) {
		String retVal = null;
    	if (assertion.getSubject() != null && 
        	assertion.getSubject().getNameID() != null) {
        		retVal =  assertion.getSubject().getNameID().getFormat();
        }
    	return retVal;
	}
/*
 * <saml2:Subject>
	<saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName">UID=CN=</saml2:NameID>
	<saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:holder-of-key">
		<saml2:SubjectConfirmationData>
			<ds:KeyInfo>
				<ds:KeyValue>
					<ds:RSAKeyValue>
						<ds:Modulus>jK0Txf3GWau5TnOA5VSZo4HRHtgJT4h+5ewomSoHupkqFLo0OSojAmn+q5Wc/Q5g02m1KQ9GXzB5WgCUy+jJHTA7q3vhZXdd3WJhed0sOloPVwt9bwjGjJ3dr2zt/YIvvb4gLG8EUFGTkiLyeMCJSQXucA0ZZwBI82iBasDVAac=</ds:Modulus>
						<ds:Exponent>AQAB</ds:Exponent>
					</ds:RSAKeyValue>
				</ds:KeyValue>
			</ds:KeyInfo>
		</saml2:SubjectConfirmationData>
	</saml2:SubjectConfirmation>
</saml2:Subject>
 * 
 * 
 */
    private static void  extractSubject(Assertion assertion, AssertionType assertOut) throws ValidationException {
    	   
      
        UserType userInfo = assertOut.getUserInfo();
        
        if (assertion.getSubject() != null) {
        	org.opensaml.saml2.core.Subject subject = assertion.getSubject();
            List<org.opensaml.saml2.core.SubjectConfirmation> confirmations = 
                subject.getSubjectConfirmations();
            
            String format = getSubjectNameIDFormat(assertion);
            String nameVal = getSubjectNameIDValue(assertion);
            // For X509 format the user identifier is extracted, for others content is taken as is
            String userIdentifier = nameVal;
            if (NullChecker.isNotNullish(format) && NullChecker.isNotNullish(nameVal)) {
                if (format.trim().equals(X509_FORMAT)) {
                    String extractedUID = extract509(nameVal);
                    if (NullChecker.isNotNullish(extractedUID)) {
                        userIdentifier = extractedUID;
                    } else {
                        log.warn("X509 Formatted user identifier cannot be extracted");
                    }
                }
            } else {
                log.warn("Subject's NameId has an invalid format");
            }
            log.info("Setting UserName to: " + userIdentifier);
            userInfo.setUserName(userIdentifier);
            assertOut.setUserInfo(userInfo);
        }
        //System.out.println("Exiting SamlTokenExtractor.extractSubject...");
        
    }

    /**
     * Extracts the value of the UID model identifier of the X509 formatted
     * content.  This method uses Sun proprietary classes to determine if the 
     * X509 is properly formed and to extract the value of the UID. The current 
     * specification for the string representation of a distinguished name is 
     * defined in <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>. 
     * @param in509 The X509 formatted string
     * @return The extracted userid value, null if not defined.
     */
    private static String extract509(String in509) {
        //System.out.println("Entering SamlTokenExtractor.extract509...");

        String userVal = null;

        if (NullChecker.isNotNullish(in509)) {
            try {
                X500Principal prin = new X500Principal(in509);
                X500Name name500 = X500Name.asX500Name(prin);
                for (AVA ava : name500.allAvas()) {
                    if (X500Name.userid_oid == ava.getObjectIdentifier()) {
                        userVal = ava.getValueString();
                    } else {
                        log.warn("Construction of user identifier does not use: " + ava.toString());
                    }
                }
            } catch (IllegalArgumentException iae) {
                log.error("X509 NameId is not properly formed: " + iae.getMessage());
            }
        }

        //System.out.println("Exiting SamlTokenExtractor.extract509...");
        return userVal;
    }
    
    /**
     * This method is responsible to extract the information from both the 
     * AttributeStatements as found in the main Assertion element as well as the 
     * AttributeStatements found in the Evidence element.  The permitted names 
     * of the Attributes in the Assertion element are: UserRole, PurposeForUse, 
     * UserName, UserOrganization. The permitted names of the Attributes in the 
     * Evidence element are: AccessConsentPolicy and InstanceAccessConsentPolicy
     * @param statement The attribute statement to be extracted
     * @param assertOut The Assertion element being written to
     * @throws Exception 
     */
    private static void extractAttributeInfo(AttributeStatement statement, AssertionType assertOut) throws Exception {
        //System.out.println("Entering SamlTokenExtractor.extractAttributeInfo...");
        
        List attribs = statement.getAttributes();

        if (attribs != null && !attribs.isEmpty()) {
            for (int idx = 0; idx < attribs.size(); idx++) {
                if (attribs.get(idx) instanceof Attribute) {
                    Attribute attrib = (Attribute) attribs.get(idx);
                    String nameAttr = attrib.getName();
                    if (nameAttr != null) {
                        if (nameAttr.equals(SamlConstants.USER_ROLE_ATTR)) {
                            log.debug("Extracting Assertion.userInfo.roleCoded:");
                            assertOut.getUserInfo().setRoleCoded(extractNhinCodedElement(attrib, SamlConstants.USER_ROLE_ATTR));
                        } else if (nameAttr.equals(SamlConstants.PURPOSE_ROLE_ATTR)) {
                            log.debug("Extracting Assertion.purposeOfDisclosure:");
                            assertOut.setPurposeOfDisclosureCoded(extractNhinCodedElement(attrib, SamlConstants.PURPOSE_ROLE_ATTR));
                        } else if (nameAttr.equals(SamlConstants.USERNAME_ATTR)) {
                            extractNameParts(attrib, assertOut);
                        } else if (nameAttr.equals(SamlConstants.USER_ORG_ATTR)) {
                            String sUserOrg = AttributeUtil.extractAttributeValueValue(attrib);
                            assertOut.getUserInfo().getOrg().setName(sUserOrg);
                            log.debug("Assertion.userInfo.org.Name = " + sUserOrg);
                        } else if (nameAttr.equals(SamlConstants.USER_ORG_ID_ATTR)) {
                            String sUserOrgId = AttributeUtil.extractAttributeValueValue(attrib);
                            assertOut.getUserInfo().getOrg().setHomeCommunityId(sUserOrgId);
                            log.debug("Assertion.userInfo.org.homeCommunityId = " + sUserOrgId);
                        } else if (nameAttr.equals(SamlConstants.HOME_COM_ID_ATTR)) {
                            String sHomeComId = AttributeUtil.extractAttributeValueValue(attrib);
                            assertOut.getHomeCommunity().setHomeCommunityId(sHomeComId);
                            log.debug("Assertion.homeCommunity.homeCommunityId = " + sHomeComId);
                        } else if (nameAttr.equals(SamlConstants.PATIENT_ID_ATTR)) {
                            String sPatientId = AttributeUtil.extractAttributeValueValue(attrib);
                            assertOut.getUniquePatientId().add(sPatientId);
                            log.debug("Assertion.uniquePatientId = " + sPatientId);
                        } else if (nameAttr.equals(SamlConstants.ACCESS_CONSENT_ATTR)) {
                            String sAccessConsentId = AttributeUtil.extractAttributeValueValue(attrib);
                            assertOut.getSamlAuthzDecisionStatement().getEvidence().getAssertion().setAccessConsentPolicy(sAccessConsentId);
                            log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.AccessConsentPolicy = " + sAccessConsentId);
                        } else if (nameAttr.equals(SamlConstants.INST_ACCESS_CONSENT_ATTR)) {
                            String sInstAccessConsentId = AttributeUtil.extractAttributeValueValue(attrib);
                            assertOut.getSamlAuthzDecisionStatement().getEvidence().getAssertion().setInstanceAccessConsentPolicy(sInstAccessConsentId);
                            log.debug("Assertion.SamlAuthzDecisionStatement.Evidence.Assertion.InstanceAccessConsentPolicy = " + sInstAccessConsentId);
                        } else {
                            log.warn("Unrecognized Name Attribute: " + nameAttr);
                        }
                    } else {
                        log.warn("Improperly formed Name Attribute: " + nameAttr);
                    }
                }
            }
        } else {
            log.error("Expected Attributes are missing.");
        }
    }

    /**
     * This method takes an attribute and extracts the string value of the
     * attribute.  If the attribute has multiple values, then it concatenates
     * all of the values.
     *
     * @param attrib The attribute containing the string value.
     * @return The string value (or if there are multiple values, the concatenated string value.)
     */
    private static String extractAttributeValueString(Attribute attrib) {
        String retValue = "";

        List<XMLObject> attrVals = attrib.getAttributeValues();
        if ((attrVals != null) &&
                (attrVals.size() > 0)) {
            StringBuffer strBuf = new StringBuffer();
            for (Object o : attrVals) {
                strBuf.append(o + " ");
            }
            retValue = strBuf.toString();
        }

        return retValue.trim();

    }
    
    /**
     * This method takes an attribute and extracts the string value of the
     * attribute.  If the attribute has multiple values, then it concatenates
     * all of the values.
     *
     * @param attrib The attribute containing the string value.
     * @return The string value (or if there are multiple values, the concatenated string value.)
     */
    private static String extractAttributeValueString1(Attribute attrib) {
        String retValue = "";

        List attrVals = attrib.getAttributeValues();
        if ((attrVals != null) &&
                (attrVals.size() > 0)) {
            StringBuffer strBuf = new StringBuffer();
            for (Object o : attrVals) {
                strBuf.append(o + " ");
            }
            retValue = strBuf.toString();
        }

        return retValue.trim();

    }

    /**
     * This method takes an attribute and extracts the base64Encoded value from the first
     * attribute value.
     *
     * @param attrib The attribute containing the string value.
     * @return The string value (or if there are multiple values, the concatenated string value.)
     */
    private static byte[] extractFirstAttributeValueBase64Binary(Attribute attrib) {
        byte[] retValue = null;

        List attrVals = attrib.getAttributeValues();
        if ((attrVals != null) &&
                (attrVals.size() > 0)) {
            if (attrVals.get(0) instanceof byte[]) {
                retValue = (byte[]) attrVals.get(0);
            }
        }

        return retValue;
    }

    /**
     * The value of the UserName attribute is assumed to be a user's name in 
     * plain text.  The name parts are extracted in this method as the first 
     * word constitutes the first name, the last word constitutes the last name 
     * and all other text in between these words constitute the middle name. 
     * @param attrib The Attribute that has the user name as its value
     * @param assertOut The Assertion element being written to
     * @throws Exception 
     */
    private static void extractNameParts(Attribute attrib, AssertionType assertOut) throws Exception {
        //System.out.println("Entering SamlTokenExtractor.extractNameParts...");

        // Assumption is that before the 1st space reflects the first name,
        // after the last space is the last name, anything between is the middle name
        String attrV = AttributeUtil.extractAttributeValueValue(attrib);
        
        //System.out.println("");
        List attrVals =   attrib.getAttributeValues();
        if ((attrV != null) &&
                (attrV.length() >= 1)) {
            PersonNameType personName = assertOut.getUserInfo().getPersonName();

            // Although SAML allows for multiple attribute values, the NHIN Specification
            // states that for a name there will be one attribute value.  So we will
            // only look at the first one.  If there are more, the first is the only one
            // that will be used.
            //-----------------------------------------------------------------------------
            //String completeName = attrVals.get(0).toString();
            personName.setFullName(attrV);
            //System.out.println("Assertion.userInfo.personName.FullName = " + attrV);

            String[] nameTokens = attrV.split("\\s");
            ArrayList<String> nameParts = new ArrayList<String>();

            //remove blank tokens
            for (String tok : nameTokens) {
                if (tok.trim() != null && tok.trim().length() > 0) {
                    nameParts.add(tok);
                }
            }

            if (nameParts.size() > 0) {
                if (!nameParts.get(0).contains(EMPTY_STRING)) {
                    personName.setGivenName(nameParts.get(0));
                    nameParts.remove(0);
                    //System.out.println("Assertion.userInfo.personName.givenName = " + personName.getGivenName());
                }
            }

            if (nameParts.size() > 0) {
                if (!nameParts.get(nameParts.size() - 1).contains(EMPTY_STRING)) {
                    personName.setFamilyName(nameParts.get(nameParts.size() - 1));
                    nameParts.remove(nameParts.size() - 1);
                    //System.out.println("Assertion.userInfo.personName.familyName = " + personName.getFamilyName());
                }
            }

            if (nameParts.size() > 0) {
                StringBuffer midName = new StringBuffer();
                for (String name : nameParts) {
                    midName.append(name + " ");
                }
                // take off last blank character
                midName.setLength(midName.length() - 1);
                personName.setSecondNameOrInitials(midName.toString());
                //System.out.println("Assertion.userInfo.personName.secondNameOrInitials = " + personName.getSecondNameOrInitials());
            }
            assertOut.setPersonName(personName);
        } else {
            log.error("User Name attribute is empty: " + attrVals);
        }

        //System.out.println("SamlTokenExtractor.extractNameParts() -- End");
    }
    
    
    private static CeType getAttribCodeData(String attribdata, CeType ceType){
    	String []data = attribdata.split(" ");
    	for (String datastring : data) {
			if( datastring.indexOf("code")>-1){
				ceType.setCode(getExtractString(datastring));
			}else if( datastring.indexOf("codeSystem")>-1){
				ceType.setCode(getExtractString(datastring));
			}else if( datastring.indexOf("codeSystemName")>-1){
				ceType.setCode(getExtractString(datastring));
			}else if( datastring.indexOf("displayName")>-1){
				ceType.setCode(getExtractString(datastring));
			} 
		}
    	return ceType ;
    }
    
    private static String getExtractString(String exdata){
    	String result = "" ;
    	int start = exdata.indexOf("=\"");
    	if( start != -1){
    		start = start+2;
    		int end = exdata.indexOf(":", start);
    		result = exdata.substring(start, end);
    	}
    	return result ;
    }
    /**
     * The value of the UserRole and PurposeForUse attributes are formatted 
     * according to the specifications of an nhin:CodedElement.  This method 
     * parses that expected structure to obtain the code, codeSystem, 
     * codeSystemName, and the displayName attributes of that element.
     * @param attrib The Attribute that has the UserRole or PurposeForUse as its 
     * value
     * @param assertOut The Assertion element being written to
     * @param codeId Identifies which coded element this is parsing
     * @throws Exception 
     */
    private static CeType extractNhinCodedElement(Attribute attrib, String codeId) throws Exception {
    	 //System.out.println("Entering SamlTokenExtractor.parseNhinCodedElement...");

    	 
    	 
    	 
         CeType ce = new CeType();
         ce.setCode(EMPTY_STRING);
         ce.setCodeSystem(EMPTY_STRING);
         ce.setCodeSystemName(EMPTY_STRING);
         ce.setDisplayName(EMPTY_STRING);
         
         Hashtable<String, String> roleData = AttributeUtil.getCodeAttributes( AttributeUtil.extractAttributeValueValue(attrib));         

        	 ce.setCode(roleData.get("code"));
        	 ce.setCodeSystem(roleData.get("codeSystem"));
        	 ce.setCodeSystemName(roleData.get("codeSystemName"));
        	 ce.setDisplayName(roleData.get("displayName"));
         
         return ce;
    }

    private static Assertion unmarshellElement(OMElement omAssertion) throws Exception{
		if(omAssertion.getLocalName().equals("Assertion")){
			try{
			UnmarshallerFactory factory = Configuration.getUnmarshallerFactory ();
			javax.xml.namespace.QName qname = omAssertion.getQName();
            Unmarshaller unmarsh = factory.getUnmarshaller(qname);
            if(unmarsh == null){
            	throw new Exception("Unmarshell Exception");
            }
            Assertion assertion;
            
            	assertion = (Assertion)unmarsh.unmarshall(XMLUtils.toDOM(omAssertion));
            	return assertion;
            } catch(Exception e){
            	throw e ;
            }
		}
		return null ;
	}
    
    private static List<KeyInfo> getSubjectConfirmationKeyInformation(SubjectConfirmation confirmation, Assertion assertion) throws ValidationException {
        SubjectConfirmationData confirmationData = confirmation.getSubjectConfirmationData();
        if (confirmation == null) {
           //System.out.println("Subject Confirmation Data is null !!!!!!!!");
        }

        List<KeyInfo> keyInfos = new LazyList<KeyInfo>();
        for (XMLObject object : confirmationData.getUnknownXMLObjects()) {
            if (object.getElementQName().equals(KeyInfo.DEFAULT_ELEMENT_NAME)) {
	            if (object != null && object.getElementQName().equals(KeyInfo.DEFAULT_ELEMENT_NAME)) {
	                keyInfos.add((KeyInfo) object);
	            }
            }
        }
        if (keyInfos == null || keyInfos.isEmpty()) {
           //System.out.println("KeyInfo Data is null !!!!!!!!");
        }

        return keyInfos;
    }
    
    
}