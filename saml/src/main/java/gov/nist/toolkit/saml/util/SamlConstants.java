package gov.nist.toolkit.saml.util;

import java.math.BigInteger;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.Namespace;
/**
 * @author Srinivasarao.Eadara
 *
 */
public class SamlConstants {
	//
    // NAME ID FORMAT
    //
    
    public static final String NAMEID_FORMAT_UNSPECIFIED = 
        "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
    
    public static final String NAMEID_FORMAT_EMAIL_ADDRESS = 
        "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";
    
    public static final String NAMEID_FORMAT_X509_SUBJECT_NAME = 
        "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName";
    
    public static final String NAMEID_FORMAT_WINDOWS_DQN = 
        "urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName";
    
    public static final String NAMEID_FORMAT_KERBEROS = 
        "urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos";
    
    public static final String NAMEID_FORMAT_ENTITY = 
        "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";
    
    public static final String NAMEID_FORMAT_PERSISTENT = 
        "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";
    
    public static final String NAMEID_FORMAT_TRANSIENT = 
        "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";

    //
    // SUBJECT CONFIRMATION
    //
    
    public static final String CONF_BEARER = 
        "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    
    public static final String CONF_HOLDER_KEY = 
        "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key";
    
    public static final String CONF_SENDER_VOUCHES = 
        "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";
    
    //
    // AUTH CONTEXT CLASS REF
    //
    
    public static final String AUTH_CONTEXT_CLASS_REF_INTERNET_PROTOCOL = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocol";
    
    public static final String AUTH_CONTEXT_CLASS_REF_INTERNET_PROTOCOL_PASSWORD = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocolPassword";
    
    public static final String AUTH_CONTEXT_CLASS_REF_KERBEROS = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos";
    
    public static final String AUTH_CONTEXT_CLASS_REF_MOBILE_ONE_FACTOR_UNREGISTERED = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:MobileOneFactorUnregistered";
    
    public static final String AUTH_CONTEXT_CLASS_REF_MOBILE_TWO_FACTOR_UNREGISTERED = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorUnregistered";
    
    public static final String AUTH_CONTEXT_CLASS_REF_MOBILE_ONE_FACTOR_CONTRACT = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:MobileOneFactorContract";
    
    public static final String AUTH_CONTEXT_CLASS_REF_MOBILE_TWO_FACTOR_CONTRACT = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorContract";
    
    public static final String AUTH_CONTEXT_CLASS_REF_PASSWORD = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";
    
    public static final String AUTH_CONTEXT_CLASS_REF_PASSWORD_PROTECTED_TRANSPORT = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    
    public static final String AUTH_CONTEXT_CLASS_REF_PREVIOUS_SESSION = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession";
    
    public static final String AUTH_CONTEXT_CLASS_REF_X509 = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
    
    public static final String AUTH_CONTEXT_CLASS_REF_PGP = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:PGP";
    
    public static final String AUTH_CONTEXT_CLASS_REF_SPKI = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:SPKI";
    
    public static final String AUTH_CONTEXT_CLASS_REF_XMLDSIG = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:XMLDSig";
    
    public static final String AUTH_CONTEXT_CLASS_REF_SMARTCARD = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard";
    
    public static final String AUTH_CONTEXT_CLASS_REF_SMARTCARD_PKI = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI";
    
    public static final String AUTH_CONTEXT_CLASS_REF_SOFTWARE_PKI = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:SoftwarePKI";
    
    public static final String AUTH_CONTEXT_CLASS_REF_TELEPHONY = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:Telephony";
    
    public static final String AUTH_CONTEXT_CLASS_REF_NOMAD_TELEPHONY = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:NomadTelephony";
    
    public static final String AUTH_CONTEXT_CLASS_REF_PERSONAL_TELEPHONY = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:PersonalTelephony";
    
    public static final String AUTH_CONTEXT_CLASS_REF_AUTHENTICATED_TELEPHONY = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:AuthenticatedTelephony";
    
    public static final String AUTH_CONTEXT_CLASS_REF_SECURED_REMOTE_PASSWORD = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:SecureRemotePassword";
    
    public static final String AUTH_CONTEXT_CLASS_REF_TLS_CLIENT = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:TLSClient";
    
    public static final String AUTH_CONTEXT_CLASS_REF_TIME_SYNC_TOKEN = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:TimeSyncToken";
    
    public static final String AUTH_CONTEXT_CLASS_REF_UNSPECIFIED = 
        "urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified";

    //
    // ATTRIBUTE NAME FORMAT
    //
    
    public static final String ATTRNAME_FORMAT_UNSPECIFIED = 
        "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified";
    
    public static final String ATTRNAME_FORMAT_URI = 
        "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
    
    public static final String ATTRNAME_FORMAT_BASIC = 
        "urn:oasis:names:tc:SAML:2.0:attrname-format:basic";
	public static final String WSU_NS = 
        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final String WSU_PREFIX = "wsu";
    public static final String TIMESTAMP_TOKEN_LN = "Timestamp";

    public static final String CREATED_LN = "Created";
    public static final String EXPIRES_LN = "Expires";
    
    public static final String DS="http://www.w3.org/2000/09/xmldsig#";
    public static final String S11="http://schemas.xmlsoap.org/soap/envelope/";
    public static final String S12="http://www.w3.org/2003/05/soap-envelope/";
    public static final String WSSE="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WSSE11="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd";
    public static final String WSU="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final String XENC="http://www.w3.org/2001/04/xmlenc#";
    
    public static final String ACTION="urn:oasis:names:tc:SAML:1.0:action:rwedc";
    public static final String DECISION="urn:oasis:names:tc:SAML:1.0:action:rwedc";
       
    // SAML Constants
    public static final String ACTION_PROP = "action";
    public static final String RESOURCE_PROP = "resource";
    public static final String PURPOSE_CODE_PROP = "purposeForUseRoleCode";
    public static final String PURPOSE_SYST_PROP = "purposeForUseCodeSystem";
    public static final String PURPOSE_SYST_NAME_PROP = "purposeForUseCodeSystemName";
    public static final String PURPOSE_DISPLAY_PROP = "purposeForUseDisplayName";
    public static final String USER_FIRST_PROP = "userFirstName";
    public static final String USER_MIDDLE_PROP = "userMiddleName";
    public static final String USER_LAST_PROP = "userLastName";
    public static final String USER_NAME_PROP = "userName";
    public static final String USER_ORG_PROP = "userOrganization";
    public static final String USER_ORG_ID_PROP = "userOrganizationID";
    public static final String HOME_COM_PROP = "homeCommunity";
    public static final String PATIENT_ID_PROP = "patientID";
    public static final String USER_CODE_PROP = "userRoleCode";
    public static final String USER_SYST_PROP = "userRoleCodeSystem";
    public static final String USER_SYST_NAME_PROP = "userRoleCodeSystemName";
    public static final String USER_DISPLAY_PROP = "userRoleCodeDisplayName";
    public static final String AUTHN_INSTANT_PROP = "authnInstant";
    public static final String AUTHN_SESSION_INDEX_PROP = "authnSessionIndex";
    public static final String AUTHN_CONTEXT_CLASS_PROP = "authnContextClass";
    public static final String SUBJECT_LOCALITY_ADDR_PROP = "subjectLocalityAddress";
    public static final String SUBJECT_LOCALITY_DNS_PROP = "subjectLocalityDNS";
    public static final String AUTHZ_DECISION_PROP = "authzDecision";
    public static final String EVIDENCE_ID_PROP = "evidenceAssertionId";
    public static final String EVIDENCE_INSTANT_PROP = "evidenceAssertionInstant";
    public static final String EVIDENCE_VERSION_PROP = "evidenceAssertionVersion";
    public static final String EVIDENCE_ISSUER_PROP = "evidenceAssertionIssuer";
    public static final String EVIDENCE_ISSUER_FORMAT_PROP = "evidenceAssertionIssuerFormat";
    public static final String EVIDENCE_CONDITION_NOT_BEFORE_PROP = "evidenceConditionNotBefore";
    public static final String EVIDENCE_CONDITION_NOT_AFTER_PROP = "evidenceConditionNotAfter";
    public static final String EVIDENCE_ACCESS_CONSENT_PROP = "evidenceAccessConsent";
    public static final String EVIDENCE_INST_ACCESS_CONSENT_PROP = "evidenceInstanceAccessConsent";
    public static final String AUDIT_QUERY_ACTION = "queryAuditLog";
    public static final String NOTIFY_ACTION = "notify";
    public static final String SUBSCRIBE_ACTION = "subscribe";
    public static final String UNSUBSCRIBE_ACTION = "unsubscribe";
    public static final String DOC_QUERY_ACTION = "queryDocuments";
    public static final String DOC_RETRIEVE_ACTION = "retrieveDocuments";
    public static final String SUBJECT_DISCOVERY_ACTION = "subjectDiscovery";
    public static final String PATIENT_DISCOVERY_ACTION = "patientDiscovery";
    public static final String ADMIN_DIST_ACTION = "administrativedistribution";
    public static final String ADAPTER_XDR_ACTION = "adapterXDRSecured";
    public static final String ADAPTER_XDRREQUEST_SECURED_ACTION = "adapterXDRRequestSecured";
    public static final String ADAPTER_XDRREQUEST_ACTION = "adapterXDRRequest";
    public static final String ADAPTER_XDRRESPONSE_SECURED_ACTION = "adapterXDRResponseSecured";
    public static final String ADAPTER_XDRRESPONSE_ACTION = "adapterXDRResponse";
    public static final String ENTITY_XDR_SECURED_RESPONSE_ACTION = "entityXDRSecuredResponse";
    public static final String AUDIT_REPO_ACTION = "auditrepository";
    public static final String POLICY_ENGINE_ACTION = "policyengine";
    public static final String POLICY_INFORMATION_POINT_ACTION = "policyinformationpoint";
    public static final String PAT_CORR_ACTION = "patientcorrelation";
    public static final String ADAPTER_MPI_ACTION = "mpi";
    public static final String XDR_ACTION = "xdr";
    public static final String XDR_REQUEST_ACTION = "xdrrequest";
    public static final String XDR_RESPONSE_ACTION = "xdrresponse";
    public static final String USERNAME_ATTR = "urn:oasis:names:tc:xspa:1.0:subject:subject-id";
    public static final String USER_ORG_ATTR = "urn:oasis:names:tc:xspa:1.0:subject:organization";
    public static final String USER_ORG_ID_ATTR = "urn:oasis:names:tc:xspa:1.0:subject:organization-id";
    public static final String HOME_COM_ID_ATTR = "urn:nhin:names:saml:homeCommunityId";
    public static final String USER_ROLE_ATTR = "urn:oasis:names:tc:xacml:2.0:subject:role";
    public static final String PURPOSE_ROLE_ATTR = "urn:oasis:names:tc:xspa:1.0:subject:purposeofuse";
    public static final String PATIENT_ID_ATTR = "urn:oasis:names:tc:xacml:2.0:resource:resource-id";
    public static final String ACCESS_CONSENT_ATTR = "AccessConsentPolicy";
    public static final String INST_ACCESS_CONSENT_ATTR = "InstanceAccessConsentPolicy";
    public static final String CE_CODE_ID = "code";
    public static final String CE_CODESYS_ID = "codeSystem";
    public static final String CE_CODESYSNAME_ID = "codeSystemName";
    public static final String CE_DISPLAYNAME_ID = "displayName";
    // Async Property Constants
    public static final String ASYNC_MESSAGE_ID_PROP = "messageId";
    public static final String ASYNC_RELATES_TO_PROP = "relatesToId";
    public static final String ASYNC_MSG_TYPE_PROP = "msgType";
    public static final String ASYNC_REQUEST_MSG_TYPE_VAL = "request";
    public static final String ASYNC_RESPONSE_MSG_TYPE_VAL = "response";
    public static final String ASYNC_DB_REC_EXP_VAL_PROP = "asyncDbRecExpValue";
    public static final String ASYNC_DB_REC_EXP_VAL_UNITS_PROP = "asyncDbRecExpUnits";
    public static final String ASYNC_DB_REC_EXP_VAL_UNITS_SEC = "seconds";
    public static final String ASYNC_DB_REC_EXP_VAL_UNITS_MIN = "minutes";
    public static final String ASYNC_DB_REC_EXP_VAL_UNITS_HOUR = "hours";
    public static final String ASYNC_DB_REC_EXP_VAL_UNITS_DAY = "days";
    public static final String NS_ADDRESSING_2005 = "http://www.w3.org/2005/08/addressing";
    public static final String HEADER_MESSAGEID = "MessageID";
    public static final String HEADER_RELATESTO = "RelatesTo";
    
    // SOAP Headers
    public static final String HTTP_REQUEST_ATTRIBUTE_SOAPMESSAGE = "SoapMessage";
    public static final String HIEM_SUBSCRIBE_SOAP_HDR_ATTR_TAG = "subscribeSoapMessage";
    public static final String HIEM_UNSUBSCRIBE_SOAP_HDR_ATTR_TAG = "unsubscribeSoapMessage";
    public static final String HIEM_NOTIFY_SOAP_HDR_ATTR_TAG = "notifySoapMessage";
    public static final String WS_ADDRESSING_URL = "http://www.w3.org/2005/08/addressing";
    public static final String WS_ADDRESSING_URL_ANONYMOUS = "http://www.w3.org/2005/08/addressing/anonymous";
    public static final String WS_SOAP_HEADER_ACTION = "Action";
    public static final String WS_RETRIEVE_DOCUMENT_ACTION = "urn:ihe:iti:2007:RetrieveDocumentSet";
    public static final String WS_PROVIDE_AND_REGISTER_DOCUMENT_ACTION = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
    public static final String WS_SOAP_HEADER_TO = "To";
    public static final String WS_SOAP_HEADER_REPLYTO = "ReplyTo";
    public static final String WS_SOAP_HEADER_ADDRESS = "Address";
    public static final String WS_SOAP_HEADER_MESSAGE_ID = "MessageID";
    public static final String WS_SOAP_HEADER_MESSAGE_ID_PREFIX = "urn:uuid:";

    
    public final static String NIST_NS = "http://www.eogs.dk/2007/07/brs";
    public final static String XS_NS = "http://www.w3.org/2001/XMLSchema";
    
    /** BRS SAML 2.0 QName prefix */
    public final static String BRS_PREFIX ="brs";
    public final static String XS_PREFIX ="xs";
    
    /** Used name spaces */
	public static final Namespace SAML20_NAMESPACE = new Namespace(SAMLConstants.SAML20_NS,SAMLConstants.SAML20_PREFIX);	
    
	/** AunthContextClassRef urn */
	public static final String PASSWORD_AUTHN_CONTEXT_CLASS_REF = "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";
	public static final String X509_AUTHN_CONTEXT_CLASS_REF = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";

	/** Subject Confirmation Methods */
	public static final String METHOD_BEARER = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
	public static final String METHOD_HOK = "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key";
	
	public static final String RETRIEVAL_METHOD_ENCRYPTED_KEY = "http://www.w3.org/2001/04/xmlenc#EncryptedKey";
	
	
	/** Format of NameId */
	public static final String PERSISTENT = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";

	/** Hashing algorithms */
    public static final String SHA_HASH_ALGORHTM = "SHA-1";
	public static final String SHA1_WITH_RSA = "SHA1withRSA";
    
    /** Code format */
    public static final String UTF_8 = "UTF-8";
    
	public static final String URI_ATTRIBUTE_NAME_FORMAT = Attribute.BASIC;

	/** Name and friendly name of all the feasible BRS SAML Attributes */
	public static final String ATTRIBUTE_SURNAME_NAME = "urn:oid:2.5.4.4";

	public static final String ATTRIBUTE_SURNAME_FRIENDLY_NAME = "surName";

	public static final String ATTRIBUTE_COMMON_NAME_NAME = "urn:oid:2.5.4.3";

	public static final String ATTRIBUTE_COMMON_NAME_FRIENDLY_NAME = "CommonName";

	public static final String ATTRIBUTE_UID_NAME = "urn:oid:0.9.2342.19200300.100.1.1";

	public static final String ATTRIBUTE_UID_FRIENDLY_NAME = "uid";

	public static final String ATTRIBUTE_MAIL_NAME = "urn:oid:0.9.2342.19200300.100.1.3";

	public static final String ATTRIBUTE_MAIL_FRIENDLY_NAME = "mail";

	public static final String ATTRIBUTE_TELEPHONE_NUMBER_IDENTIFIER_NAME = "dk:gov:virk:saml:attribute:TelephoneNumberIdentifier";

	public static final String ATTRIBUTE_TELEPHONE_NUMBER_IDENTIFIER_FRIENDLY_NAME = "TelephoneNumberIdentifier";

	public static final String ATTRIBUTE_MOBILE_NUMBER_IDENTIFIER_NAME = "dk:gov:virk:saml:attribute:MobileNumberIdentifier";

	public static final String ATTRIBUTE_MOBILE_NUMBER_IDENTIFIER_FRIENDLY_NAME = "MobileNumberIdentifier";

	public static final String ATTRIBUTE_CVR_NUMBER_IDENTIFIER_NAME = "dk:gov:saml:attribute:CvrNumberIdentifier";

	public static final String ATTRIBUTE_CVR_NUMBER_IDENTIFIER_FRIENDLY_NAME = "CVRnumberIdentifier";

	public static final String ATTRIBUTE_PRODUCTION_UNIT_IDENTIFIER_NAME = "dk:gov:virk:saml:attribute:ProductionUnitIdentifier";

	public static final String ATTRIBUTE_PRODUCTION_UNIT_IDENTIFIER_FRIENDLY_NAME = "ProductionUnitIdentifier";

	public static final String ATTRIBUTE_SERIAL_NUMBER_NAME = "urn:oid:2.5.4.5";

	public static final String ATTRIBUTE_SERIAL_NUMBER_FRIENDLY_NAME = "serialNumber";

	public static final String ATTRIBUTE_PID_NUMBER_IDENTIFIER_NAME = "dk:gov:saml:attribute:PidNumberIdentifier";

	public static final String ATTRIBUTE_PID_NUMBER_IDENTIFIER_FRIENDLY_NAME = "PidNumberIdentifier";

	public static final String ATTRIBUTE_RID_NUMBER_IDENTIFIER_NAME = "dk:gov:saml:attribute:RidNumberIdentifier";

	public static final String ATTRIBUTE_RID_NUMBER_IDENTIFIER_FRIENDLY_NAME = "RidNumberIdentifier";

	public static final String ATTRIBUTE_USER_CERTIFICATE_NAME = "urn:oid:1.3.6.1.4.1.1466.115.121.1.8";

	public static final String ATTRIBUTE_USER_CERTIFICATE_FRIENDLY_NAME = "userCertificate";

	public static final String ATTRIBUTE_ASSURANCE_LEVEL_NAME = "dk:gov:saml:attribute:AssuranceLevel";

	public static final String ATTRIBUTE_ASSURANCE_LEVEL_FRIENDLY_NAME = "AssuranceLevel";

	public static final String ATTRIBUTE_CURRENT_CVR_NUMBER_IDENTIFIER_NAME = "dk:gov:virk:saml:attribute:CurrentCVRnumberIdentifier";

	public static final String ATTRIBUTE_CURRENT_CVR_NUMBER_IDENTIFIER_FRIENDLY_NAME = "CurrentCVRnumberIdentifier";

	public static final String ATTRIBUTE_ORGANISATION_NAME_NAME = "urn:oid:2.5.4.10";
	
	public static final String ATTRIBUTE_ORGANISATION_UNIT_NAME = "urn:oid:2.5.4.11";
	
	public static final String ATTRIBUTE_POSTAL_ADDRESS_NAME = "urn:oid:2.5.4.16";
	
	public static final String ATTRIBUTE_SPECVER_NAME = "dk:gov:saml:attribute:SpecVer";
	
	public static final String ATTRIBUTE_TITLE_NAME = "urn:oid:2.5.4.12";
	
	public static final String ATTRIBUTE_UNIQUE_ACCOUNT_KEY_NAME = "dk:gov:saml:attribute:UniqueAccountKey";
	
	public static final String ATTRIBUTE_CPR_NUMBER_NAME = "dk:gov:saml:attribute:CprNumberIdentifier";
	
	public static final String ATTRIBUTE_PSEUDONYM_NAME = "urn:oid:2.5.4.65";
	
	public static final String ATTRIBUTE_YOUTH_CERTIFICATE_NAME = "dk:gov:saml:attribute:IsYouthCert";

    
    

}
