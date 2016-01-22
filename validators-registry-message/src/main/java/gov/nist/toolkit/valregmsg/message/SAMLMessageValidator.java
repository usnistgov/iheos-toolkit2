package gov.nist.toolkit.valregmsg.message;


import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.saml.bean.*;
import gov.nist.toolkit.saml.security.XMLSignatureValidatorUtil;
import gov.nist.toolkit.saml.subject.HolderOfKeySubjectConfirmationValidator;
import gov.nist.toolkit.saml.util.*;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.util.XMLUtils;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.validation.ValidationException;
import sun.net.util.IPAddressUtil;

import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class SAMLMessageValidator extends AbstractMessageValidator {
	OMElement envlope ;
	OMElement assertion;
	OMElement header;
	OMElement body;
	OMElement security;
	OMElement timestamp ;
	OMElement messagebody = null;
	String wsaction = null;
	String reqMessageId = null; 
	ErrorRecorder er;
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	RegistryValidationInterface rvi;
	Schema schema = null ;
	AssertionType assertionType ;
	
	public SAMLMessageValidator(ValidationContext vc, OMElement xml, ErrorRecorderBuilder erBuilder, 
			MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		
		this.envlope = xml;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}
	
	// needed for junit testing
	public SAMLMessageValidator(OMElement messagebody) {
		super(DefaultValidationContextFactory.validationContext());
		this.messagebody = messagebody;
	}
	void err(String msg, String ref) {
		er.err(XdsErrorCode.Code.NoCode, msg, this, ref);
	}

	void err(String msg) {
		er.err(XdsErrorCode.Code.NoCode, msg, this, null);
	}

	void err(Exception e) {
		er.err(XdsErrorCode.Code.NoCode, e);
	}
	public ErrorRecorder getErrorRecorder() {
		return er;
	}
	void parse() {
		header = firstChildWithLocalName(envlope, "Header");
		security = firstChildWithLocalName(header, "Security");
		timestamp = firstChildWithLocalName(security, "Timestamp");
		body = firstChildWithLocalName(envlope, "Body");
		assertion = firstChildWithLocalName(security, "Assertion");
	}
	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		this.envlope = envlope;
		try {
			parse();
			
			SamlTokenExtractor.initSamlEngine();
			assertionType = SamlTokenExtractor.CreateAssertion(assertion);
	    	validateAssertion(SamlTokenExtractor.samlAssertion, "", "");
			
			// Digital Signature Validation
			
			//validateDigitalSignature();
			
			
			//schema validation
			parseWSAddressing();
			
			
			//timestamp validation
			er.sectionHeading("wsu:Timestamp Validation");
			/*<wsu:Timestamp xmlns:ns17="http://docs.oasis-open.org/ws-sx/ws-secureconversation/200512" xmlns:ns16="http://schemas.xmlsoap.org/soap/envelope/" wsu:Id="_1">
				<wsu:Created>2010-08-26T15:53:11Z</wsu:Created>
				<wsu:Expires>2010-08-26T15:58:11Z</wsu:Expires>
			</wsu:Timestamp>*/
			TimeStamp timeStamp = new  TimeStamp(XMLUtils.toDOM(timestamp));
			timeStamp = TimeStampValidate.validate(timeStamp);
			er.detail("Timestamp created: "+timeStamp.getCreated().toString());
			er.detail("Timestamp expires: "+timeStamp.getExpires().toString());
			if(timeStamp.getErrorVal() == 1){
				err(timeStamp.getValidateResult(), timeStamp.getExpires().toString());
			}else{
				er.detail(timeStamp.getValidateResult());
			}
			
	    	er.sectionHeading("Digital Signature Validation");
	    	
	    	boolean signatureValue = XMLSignatureValidatorUtil.verifySignatureForXMLDocument(XMLUtils.toDOM(assertion));
	    	if( signatureValue){
	    		er.detail("Valid Assertion Digital Signature. ");
	    	}else{
	    		err("Invalid Digital Signature.");
	    	}
	    	
			
			//Subject Validation
	    	er.sectionHeading("Subject Confirmation Data Validate");
			if(assertionType.getUserInfo()!=null){
				if( assertionType.getUserInfo().getUserName()!=null){
					er.detail("Subject UserName is ==> "+assertionType.getUserInfo().getUserName());
				}
			}else{
				err("Subject is required.", "saml:SubjectType");
			}
			
	
			// SAMLAssertion samlasertn = new SAMLAssertion(assertion);
			// validate(samlasertn, "", "");
	
			//AttributeStatement Validation
	    	er.sectionHeading("AttributeStatement Validation");
	    	//subject-id validation
	    	er.challenge("Attribute Subject-id Validation");
	    	if( assertionType.getPersonName().getFullName()!=null){
	    		er.detail("Attribute Subject-id is ==> "+assertionType.getPersonName().getFullName());
	    	}else{
	    		err("Attribute Subject-id is required.", "Attribute Subject-id");
	    	}
	    	//Oraganization Validation
	    	er.challenge("Attribute Oraganization Validation");
	    	if( assertionType.getUserInfo().getOrg().getName()!=null){
	    		er.detail("Attribute Oraganization is ==> "+assertionType.getUserInfo().getOrg().getName());
	    	}else{
	    		err("Attribute Oraganization is required.", "Attribute Oraganization");
	    	}
	    	
	    	// - REMOVE THIS!!!
	    	//OraganizationId Validation
	    	er.challenge("Attribute OraganizationId Validation");
	    	if( assertionType.getUserInfo().getOrg().getHomeCommunityId()!=null){
	    		er.detail("Attribute OraganizationId is ==> "+assertionType.getUserInfo().getOrg().getHomeCommunityId());
	    	}else{
	    		err("Attribute OraganizationId is required.", "Attribute OraganizationId");
	    	}
	    	
	    	//HomeCommunityId Validation
	    	er.challenge("Attribute HomeCommunityId Validation");
	    	if( assertionType.getHomeCommunity().getHomeCommunityId()!=null){
	    		er.detail("Attribute HomeCommunityId is ==> "+assertionType.getHomeCommunity().getHomeCommunityId());
	    	}else{
	    		err("Attribute HomeCommunityId is required.", "Attribute HomeCommunityId");
	    	}
	    	
	    	//needs CodeUtils to getRetrievedDocumentsModel access to codes.xml
	    	CodesUtil codesUtil = new CodesUtil(vc);
	    	
	    	
	    	//purposeofuse validation
	    	er.challenge("Attribute PurposeOfUse Validation");
	    	if( assertionType.getPurposeOfDisclosureCoded()!=null){
	    		CeType ceType = assertionType.getPurposeOfDisclosureCoded() ;
	    		
	    		CodeTypeBean codetype = codesUtil.getCodeTypeList().get("purposeofuse");
	    		
	    		if(ceType.getCodeSystem()!=null){
	    			if (! ceType.getCodeSystem().equalsIgnoreCase(codetype.getClassScheme()))
	    				//er.detail("Assertion Attribute Role CodeSystem(hl7:Role CodeSystem) is  ==>"+ceType.getCodeSystem());
	    				err("CodeSystem is not valid: " +ceType.getCodeSystem(), "Attribute purposeofuse");
	    			else {
	    				//check hl7:Role code
	    	    		if(ceType.getCode()!=null){
	    	    			CodeBean codebean = codetype.getCode().get(ceType.getCode());
	    	    			
	    	    			if (codebean == null)
	    	    				err("The code \""+ceType.getCode()+"\" does not exist.");
	    	    			else {
	    		        		if(ceType.getCodeSystemName()!=null){
	    		        			if (! ceType.getCodeSystemName().equalsIgnoreCase(codebean.getCodingScheme()))
	    		        			err("Code System Name \""+ceType.getCodeSystemName()+"\" does not exist.", ceType.getCodeSystemName());
	    		        		}else{
	    		        			err("Assertion Attribute Role CodeSystemName(hl7:Role CodeSystemName) is required", "purposeofuse hl7:Role ");
	    		        		}
	    		        		
	    		        		if(ceType.getDisplayName()!=null){
	    		        			if (! ceType.getDisplayName().equalsIgnoreCase(codebean.getDisplay()))
	    		        			err("Display Name \""+ceType.getDisplayName() +"\" does not exist.", ceType.getDisplayName());
	    		        		}else{
	    		        			err("Assertion Attribute Role DisplayName(hl7:Role DisplayName) is required", "purposeofuse displayName");
	    		        		}
	    	    			}
	    	        		
	    	    		} else {
	    	    			err("Assertion Attribute Role code(hl7:Role Code) is required", "hl7:Role Code");
	    	    		}
	    			}
	    		} else {
	    			err("Assertion Attribute Role CodeSystem(hl7:Role CodeSystem) is required","hl7:Role CodeSystem");
	    		}
	    	}else{
	    		err("Assertion UserInfo PurposeOfUse (:PurposeOfUse) is required.", "PurposeOfUse");
	    	}
	    	//role Validation
	    	er.challenge("Attribute Role Validation");
	    	
	    	if( assertionType.getUserInfo().getRoleCoded()!=null){
	    		CeType ceType = assertionType.getUserInfo().getRoleCoded() ;
	    		
	    		CodeTypeBean codetype = codesUtil.getCodeTypeList().get("role");
	    		
	    		if(ceType.getCodeSystem()!=null){
	    			if (! ceType.getCodeSystem().equalsIgnoreCase(codetype.getClassScheme()))
	    				//er.detail("Assertion Attribute Role CodeSystem(hl7:Role CodeSystem) is  ==>"+ceType.getCodeSystem());
	    				err("CodeSystem is not valid: " +ceType.getCodeSystem());
	    			else {
	    				//check hl7:Role code
	    	    		if(ceType.getCode()!=null){
	    	    			CodeBean codebean = codetype.getCode().get(ceType.getCode());
	    	    			
	    	    			if (codebean == null)
	    	    				err("The code \""+ceType.getCode()+"\" does not exist.",ceType.getCode());
	    	    			else {
	    		        		if(ceType.getCodeSystemName()!=null){
	    		        			if (! ceType.getCodeSystemName().equalsIgnoreCase(codebean.getCodingScheme()))
	    		        			err("Code System Name \""+ceType.getCodeSystemName()+"\" does not exist.", ceType.getCodeSystemName());
	    		        		}else{
	    		        			err("Assertion Attribute Role CodeSystemName(hl7:Role CodeSystemName) is required", "hl7:Role CodeSystemName");
	    		        		}
	    		        		
	    		        		if(ceType.getDisplayName()!=null){
	    		        			if (! ceType.getDisplayName().equalsIgnoreCase(codebean.getDisplay()))
	    		        			err("Display Name \""+ceType.getDisplayName() +"\" does not exist.", "ceType.getDisplayName()");
	    		        		}else{
	    		        			err("Assertion Attribute Role DisplayName(hl7:Role DisplayName) is required", "hl7:Role DisplayName");
	    		        		}
	    	    			}
	    	        		
	    	    		} else {
	    	    			err("Assertion Attribute Role code(hl7:Role Code) is required", "hl7:Role Code");
	    	    		}
	    			}
	    		} else {
	    			err("Assertion Attribute Role CodeSystem(hl7:Role CodeSystem) is required", "hl7:Role CodeSystem");
	    		}
	    		
	    	} else {
	    		err("Assertion UserInfo RoleCoded (:subject:role) is required.", "subject:role");
	    	}
	    	
	    	
			//AuthnStatement Validation
			er.sectionHeading("AuthnStatement Validation");
			SamlAuthnStatementType samlAuthnStatementType = assertionType.getSamlAuthnStatement();
	
			
	
	
			if( samlAuthnStatementType != null ){
				//AuthInstant Validation
				if( samlAuthnStatementType.getAuthInstant()!=null){
					er.detail("AuthInstant is ==> "+samlAuthnStatementType.getAuthInstant());
				}else{
					err("AuthnInstant is required field", "AuthnStatement AuthnInstant");
				}
				
				//SessionIndex Validation
				if( samlAuthnStatementType.getSessionIndex()!=null){
					er.detail("SessionIndex is ==> "+samlAuthnStatementType.getSessionIndex());
				}else{
					err("SessionIndex is required field", "AuthnStatement SessionIndex");
				}
				//Address Validation
				if( samlAuthnStatementType.getSubjectLocalityAddress()!=null){
					if ( samlAuthnStatementType.getSubjectLocalityAddress().matches( "^.[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}" ) == false ){
						if( IPAddressUtil.isIPv6LiteralAddress(samlAuthnStatementType.getSubjectLocalityAddress())){
							err("Address is an invalid format!", "IPv6 addresses");
						}else{
							err("Address is an invalid format! Must be: [0-255].[0-255].[0-255].[0-255]", "IPv4 addresses");						
						}
					}else{
						er.detail("Subject Location Address is ==> "+samlAuthnStatementType.getSubjectLocalityAddress());
					}
				}else{
					err("Address is required field", "AuthnStatement Address");
				}
				//DNSName Validation
				if( samlAuthnStatementType.getSubjectLocalityDNSName()!=null){
					er.detail("Subject Location DNSName is ==> "+samlAuthnStatementType.getSubjectLocalityDNSName());
				}else{
					err("DNSName is required field", "AuthnStatement DNSName");
				}
				
				//AuthnContextClassRef Validation
				if( samlAuthnStatementType.getAuthContextClassRef()!=null){
					er.detail("AuthnContextClassRef is ==> "+ samlAuthnStatementType.getAuthContextClassRef());
				}else{
					err("AuthnContextClassRef is required field", "AuthnStatement AuthnContextClassRef");
				}
	
			}else{
				err("AuthnStatement is required", "AuthnStatement");
			}
			
			
			
			//AuthzDecisionStatement Validation
			/*
			 * <saml2:AuthzDecisionStatement Decision="Permit" Resource="https://nhinri2c23.aegis.net:443/RespondingGateway_Query_Service/DocQuery">
					<saml2:Action Namespace="urn:oasis:names:tc:SAML:1.0:action:rwedc">Execute</saml2:Action>
					<saml2:Evidence>
						<saml2:Assertion ID="40df7c0a-ff3e-4b26-baeb-f2910f6d05a9" IssueInstant="2010-08-26T15:53:10.859Z" Version="2.0">
							<saml2:Issuer Format="urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName">O=Social Security Administration,L=Baltimore,ST=Maryland,C=US</saml2:Issuer>
							<saml2:Conditions NotBefore="2010-08-26T15:53:10.860Z" NotOnOrAfter="2010-08-26T15:53:10.861Z"/>
							<saml2:AttributeStatement>
								<saml2:Attribute Name="AccessConsentPolicy" NameFormat="http://www.hhs.gov/healthit/nhin"/>
								<saml2:Attribute Name="InstanceAccessConsentPolicy" NameFormat="http://www.hhs.gov/healthit/nhin">
									<saml2:AttributeValue xmlns:ns6="http://www.w3.org/2001/XMLSchema-instance" xmlns:ns7="http://www.w3.org/2001/XMLSchema" ns6:type="ns7:string">urn:oid:2.16.840.1.113883.3.184.6.1.2222</saml2:AttributeValue>
								</saml2:Attribute>
							</saml2:AttributeStatement>
						</saml2:Assertion>
					</saml2:Evidence>
				</saml2:AuthzDecisionStatement>
			 * 
			 */
			er.sectionHeading("AuthzDecisionStatement Validation");
			SamlAuthzDecisionStatementType samlAuthzDecisionStatementType = assertionType.getSamlAuthzDecisionStatement() ;
			
			if( samlAuthzDecisionStatementType != null ){
				if(samlAuthzDecisionStatementType.getDecision()!=null){
					if(!samlAuthzDecisionStatementType.getDecision().equalsIgnoreCase("Permit")){
						err("AuthzDecisionStatement Decision statement must be Permit.", "AuthzDecisionStatement Decision");
					}else{
						er.detail("AuthzDecisionStatement Decision attribute is ==> "+samlAuthzDecisionStatementType.getDecision());	
					}
				}else{
					err("AuthzDecisionStatement Decision attribute is required.", "AuthzDecisionStatement Decision");
				}
				if(samlAuthzDecisionStatementType.getResource()!=null){
					er.detail("AuthzDecisionStatement Resource attribute is ==> "+samlAuthzDecisionStatementType.getResource());
				}else{
					err("AuthzDecisionStatement Resource attribute is required.","AuthzDecisionStatement Resource");
				}
				if(samlAuthzDecisionStatementType.getAction()!=null&&samlAuthzDecisionStatementType.getActionNameSpace()!=null){
					if(samlAuthzDecisionStatementType.getAction().equalsIgnoreCase("Execute")&&samlAuthzDecisionStatementType.getActionNameSpace().equalsIgnoreCase(SamlConstants.ACTION)){
						er.detail("AuthzDecisionStatement Action is ==> "+samlAuthzDecisionStatementType.getAction());
					}else{
						err("AuthzDecisionStatement Action must be specified using a Namespace of urn:oasis:names:tc:SAML:1.0:action:rwedc and a value of Execute.","AuthzDecisionStatement Resource");
					}
				}else{
					err("AuthzDecisionStatement Action is required.", "AuthzDecisionStatement Action");
				}
				
				SamlAuthzDecisionStatementEvidenceType samlAuthzDecisionStatementEvidenceType = samlAuthzDecisionStatementType.getEvidence();
				
				if( samlAuthzDecisionStatementEvidenceType!=null){
					if(samlAuthzDecisionStatementEvidenceType.getAssertion()!=null){
						SamlAuthzDecisionStatementEvidenceAssertionType samlAuthzDecisionStatementEvidenceAssertionType = samlAuthzDecisionStatementEvidenceType.getAssertion();
						if(samlAuthzDecisionStatementEvidenceAssertionType.getId()!=null){
							er.detail("AtuhzDecisionStatementEvidenceAssertion ID is ==> "+ samlAuthzDecisionStatementEvidenceAssertionType.getId()) ;
						}else{
							err("AtuhzDecisionStatementEvidenceAssertion ID is required.", "AtuhzDecisionStatementEvidenceAssertion ID");
						}
						if(samlAuthzDecisionStatementEvidenceAssertionType.getIssueInstant()!=null){
							er.detail("AtuhzDecisionStatementEvidenceAssertion IssueInstant is ==> "+ samlAuthzDecisionStatementEvidenceAssertionType.getIssueInstant()) ;
						}else{
							err("AtuhzDecisionStatementEvidenceAssertion IssueInstant is required.", "AtuhzDecisionStatementEvidenceAssertion IssueInstant");
						}
						if(samlAuthzDecisionStatementEvidenceAssertionType.getVersion()!=null){
							er.detail("AtuhzDecisionStatementEvidenceAssertion Version is ==> "+ samlAuthzDecisionStatementEvidenceAssertionType.getVersion()) ;
						}else{
							err("AtuhzDecisionStatementEvidenceAssertion Version is required.", "AtuhzDecisionStatementEvidenceAssertion Version");
						}
						
						if(samlAuthzDecisionStatementEvidenceAssertionType.getIssuer()!=null){
							er.detail("AtuhzDecisionStatementEvidenceAssertion Issuer is ==> "+ samlAuthzDecisionStatementEvidenceAssertionType.getIssuer()) ;
						}else{
							err("AtuhzDecisionStatementEvidenceAssertion Issuer is required.", "AtuhzDecisionStatementEvidenceAssertion Issuer");
						}
						if(samlAuthzDecisionStatementEvidenceAssertionType.getConditions()!=null){
							SamlAuthzDecisionStatementEvidenceConditionsType samlAuthzDecisionStatementEvidenceConditionsType = samlAuthzDecisionStatementEvidenceAssertionType.getConditions();
							if( samlAuthzDecisionStatementEvidenceConditionsType.getNotBefore()!=null){
								er.detail("AtuhzDecisionStatementEvidenceAssertion Conditions notBefore is ==> "+samlAuthzDecisionStatementEvidenceConditionsType.getNotBefore());
							}else{
								err("AtuhzDecisionStatementEvidenceAssertion Conditions notBefore is required.", "AtuhzDecisionStatementEvidenceAssertion Conditions notBefore");
							}
							if( samlAuthzDecisionStatementEvidenceConditionsType.getNotOnOrAfter()!=null){
								er.detail("AtuhzDecisionStatementEvidenceAssertion Conditions NotOnOrAfter is ==> "+samlAuthzDecisionStatementEvidenceConditionsType.getNotOnOrAfter());
							}else{
								err("AtuhzDecisionStatementEvidenceAssertion Conditions NotOnOrAfter is required.", "AtuhzDecisionStatementEvidenceAssertion Conditions NotOnOrAfter");
							}
						}else{
							err("AtuhzDecisionStatementEvidenceAssertion Conditions is required.", "AtuhzDecisionStatementEvidenceAssertion Conditions");
						}
						
						/*if(samlAuthzDecisionStatementEvidenceAssertionType.getAccessConsentPolicy()!=null){
							er.detail("samlAuthzDecisionStatementEvidenceAssertionType AccessConsentPolicy is ==> "+samlAuthzDecisionStatementEvidenceAssertionType.getAccessConsentPolicy());
						}else{
							err("samlAuthzDecisionStatementEvidenceAssertionType AccessConsentPolicy is required.");
						}*/
						
						if(samlAuthzDecisionStatementEvidenceAssertionType.getInstanceAccessConsentPolicy()!=null){
							er.detail("samlAuthzDecisionStatementEvidenceAssertionType InstanceAccessConsentPolicy is ==> "+samlAuthzDecisionStatementEvidenceAssertionType.getInstanceAccessConsentPolicy());
						}else{
							err("samlAuthzDecisionStatementEvidenceAssertionType InstanceAccessConsentPolicy is required.", "samlAuthzDecisionStatementEvidenceAssertionType InstanceAccessConsentPolicy");
						}					
					}else{
						err("samlAuthzDecisionStatementEvidenceAssertion is required.", "samlAuthzDecisionStatementEvidenceAssertion");
					}
				}else{
					err("AuthzDecisionStatementEvidence is required.", "AuthzDecisionStatementEvidence");
				}
			
			}

	    	}catch (Exception e1) {
				System.out.println(e1.getMessage());
			} 
    	
    	
		
		//return er ;
    	
    
	}
	static String wsaddresingNamespace = "http://www.w3.org/2005/08/addressing";
	static String wsaddressingRef = "http://www.w3.org/TR/ws-addr-core/";
	public static QName must_understand_qname = new QName("http://www.w3.org/2003/05/soap-envelope","mustUnderstand");
	void parseWSAddressing() {
		if (header == null)
			return;
		er.challenge("WS-Addressing");
		List<OMElement> messageId = childrenWithLocalName(header, "MessageID");
		List<OMElement> relatesTo = childrenWithLocalName(header, "RelatesTo");
		List<OMElement> to = childrenWithLocalName(header, "To");
		List<OMElement> action = childrenWithLocalName(header, "Action");
		List<OMElement> from = childrenWithLocalName(header, "From");
		List<OMElement> replyTo = childrenWithLocalName(header, "ReplyTo");
		List<OMElement> faultTo = childrenWithLocalName(header, "FaultTo");

		// check for namespace
		validateNamespace(messageId, wsaddresingNamespace);
		validateNamespace(relatesTo, wsaddresingNamespace);
		validateNamespace(to,        wsaddresingNamespace);
		validateNamespace(action,    wsaddresingNamespace);
		validateNamespace(from,      wsaddresingNamespace);
		validateNamespace(replyTo,   wsaddresingNamespace);
		validateNamespace(faultTo,   wsaddresingNamespace);

		// check for repeating and required elements
		// this does not take async into consideration
		if (to.size() > 1)
			err("Multiple WS-Addressing To headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (from.size() > 1)
			err("Multiple WS-Addressing From headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (replyTo.size() > 1)
			err("Multiple WS-Addressing ReplyTo headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (faultTo.size() > 1)
			err("Multiple WS-Addressing FaultTo headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (action.size() == 0)
			err("WS-Addressing Action header is required",wsaddressingRef + "#msgaddrpropsinfoset");
		if (action.size() > 1)
			err("Multiple WS-Addressing Action headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (messageId.size() > 	1) 
			err("Multiple WS-Addressing MessageID headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");

		List<OMElement> hdrs = new ArrayList<OMElement> ();
		hdrs.addAll(messageId);
		hdrs.addAll(relatesTo);
		hdrs.addAll(to);
		hdrs.addAll(action);
		hdrs.addAll(from);
		hdrs.addAll(replyTo);
		hdrs.addAll(faultTo);

		boolean mufound = false;
		for (OMElement hdr : hdrs) {
			String mu = hdr.getAttributeValue(must_understand_qname);
			if (mu == null)
				continue;
			mufound = true;
			if ("true".equals(mu))
				break;
			if ("1".equals(mu))
				break;
			er.detail("The WS-Addressing SOAP header " + hdr.getLocalName() + " has value other than \"1\""/*,"ITI TF-2x: V.3.2.2"*/);
		}
		if (action.size() > 0) {
			OMElement a = action.get(0);
			String mu = a.getAttributeValue(must_understand_qname);
			if (!"1".equals(mu))
				err("A WS-Addressing SOAP header element must have attribute soapenv:mustUnderstand=\"1\"","ITI TF-2x: V.3.2.2");
		}

		//		if (action.size() > 0) {
		//			OMElement a = action.getRetrievedDocumentsModel(0);
		//			String mu = a.getAttributeValue(MetadataSupport.must_understand_qname);
		//			if (!"1".equals(mu))
		//				er.err("The WS-Action SOAP header element must have attribute wsa:mustUnderstand=\"1\"","ITI TF-2x: V.3.2.2");
		//		}

		// check for endpoint format
		endpointCheck(replyTo, false);
		endpointCheck(from, true);
		endpointCheck(faultTo, false);

		// check for simple http style endpoint
		httpCheck(to);

		// return WSAction

		wsaction = null;

		if (action.size() > 0) {
			OMElement aEle = action.get(0);
			wsaction = aEle.getText();
		}

		if (messageId.size() > 0) {
			OMElement mid = messageId.get(0);
			reqMessageId = mid.getText();
		}

	}
	void validateNamespace(List<OMElement> eles, String namespace) {
		for (OMElement ele : eles) {
			OMNamespace omns = ele.getNamespace();
			String nsuri = omns.getNamespaceURI();
			if (!namespace.equals(nsuri)){
				err("Namespace on element " + ele.getLocalName() + " must be " +
						namespace + " - found instead " + nsuri, 
						wsaddressingRef);
			}else{
				er.detail(ele.getLocalName()+"--> "+ele.getText());
			}
		}
	}
	String validateEndpoint(OMElement endpoint, boolean anyURIOk) {
		if (endpoint == null)
			return "null value";
		if (!endpoint.getLocalName().equals("Address"))
			return "found " + endpoint.getLocalName() + " but expected Address";
		if (!endpoint.getNamespace().getNamespaceURI().equals(wsaddresingNamespace))
			return "found namespace" + endpoint.getNamespace().getNamespaceURI() + " but expected " + wsaddresingNamespace;
		String value = endpoint.getText();
		
		if (anyURIOk && value.startsWith("urn:"))
			return null;
		
		if (!value.startsWith("http"))
			return "not HTTP style endpoint";
		return null;
	}
	void httpCheck(List<OMElement> eles) {
		for (OMElement ele : eles) {
			String value = ele.getText();
			if (!value.startsWith("http"))
				err("Value of " + ele.getLocalName() + " must be http endpoint - found instead " + value,
						wsaddressingRef);
		}
	}

	void endpointCheck(List<OMElement> eles, boolean anyURIOk) {
		for (OMElement ele : eles) {
			OMElement first = ele.getFirstElement();
			if (first == null) {
				err("Validating contents of " + ele.getLocalName() + ": " + "not HTTP style endpoint"
						,wsaddressingRef);
				
			} else {
				String valError = validateEndpoint(first, anyURIOk);
				if (valError != null){
					err("Validating contents of " + ele.getLocalName() + ": " + valError
							,wsaddressingRef);
				}else{
					er.challenge("ReplayTo - Address is : "+ first.getText());
				}
			}
		}
	}
	
	public static OMElement firstChildWithLocalName(OMElement ele, String localName) {
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				return child;
		}
		return null;
	}
	public static  List<OMElement> childrenWithLocalName(OMElement ele, String localName) {
		List<OMElement> al = new ArrayList<OMElement>();
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				al.add(child);
		}
		return al;
	}
	
	public void validate(Assertion a, String spEntityId, String spAssertionConsumerURL) throws Exception {
		basicvalidate(a, spEntityId, spAssertionConsumerURL);
		
		
		
		
		
		DateTime confirmationTime = getConfirmationTime(a);
		if (confirmationTime == null || !confirmationTime.isAfterNow()) {
			err("Subject Confirmation Data is expired: " + confirmationTime + " before " + new DateTime(), "Subject Confirmation Data");
		}
		// Subject confirmation 
		HolderOfKeySubjectConfirmationValidator hok = new HolderOfKeySubjectConfirmationValidator() ;
		if(hok.isValidConfirmationDataType(a.getSubject().getSubjectConfirmations().get(0))== false )
			err("SubjectConfirmationData xsi:type was non-null and did not match ", "xsi:type");
		
		
		
		
		// There must be only be one AuthnStatement within the assertion
    	if (a.getAuthnStatements().size() != 1) {  
    		err("The assertion must contain exactly one AuthnStatement. Was " + a.getAuthnStatements().size());
    	}
    	// AssuranceLevel and AuthnStatement/AuthnContext/AuthnContextClassRef must be consistent
    	int assuranceLevel = getAssuranceLevel(a);
    	String authnContextClassRefValue = null;
    	AuthnStatement authnStatement = (AuthnStatement) a.getAuthnStatements().get(0);
    	AuthnContext authnContext = authnStatement.getAuthnContext();
    	if (authnContext != null) {
    		AuthnContextClassRef authnContextClassRef = authnContext.getAuthnContextClassRef();
    		if (authnContextClassRef != null) {
    			authnContextClassRefValue = authnContextClassRef.getAuthnContextClassRef();
    		}
    	}
    	if (assuranceLevel == 2 && 
    		!"urn:oasis:names:tc:SAML:2.0:ac:classes:Password".equals(authnContextClassRefValue)) {
    		err("The assuranceLevel attribute " + assuranceLevel + "  in the assertion does not correspond with the value of AuthnStatement/AuthnContext/AuthnContextClassRef: " + authnContextClassRefValue,"Password");
    	} else if (assuranceLevel == 3 && 
    		!"urn:oasis:names:tc:SAML:2.0:ac:classes:X509".equals(authnContextClassRefValue)) {
    		err("The assuranceLevel attribute " + assuranceLevel + "  in the assertion does not correspond with the value of AuthnStatement/AuthnContext/AuthnContextClassRef: " + authnContextClassRefValue, "X509");
       	}
    	
		
    	
    	// There must be a SessionIndex
    	if (getSessionIndex(a) == null) {  
    		err("The assertion must contain a AuthnStatement@SessionIndex", "AuthnStatement@SessionIndex");
    	}
    	// There must be exactly one AttributeStatement within the assertion
    	if (a.getAttributeStatements().size() != 1) {  
    		err("The assertion must contain exactly one AttributeStatement. Contains " + a.getAttributeStatements().size(), "AttributeStatement");
    	}
    	// There must not be a AttributeStatement within the assertion
    	if (a.getAuthzDecisionStatements().size() != 1) {  
    		err("The assertion must contain a AuthzDecisionStatement. Contains " + a.getAuthzDecisionStatements().size(), "AuthzDecisionStatement");
    	}

    	// There must be a valid recipient
    	if (!checkRecipient(a, spAssertionConsumerURL)) {
    		err("The assertion must contain the recipient url"+ spAssertionConsumerURL, "valid recipient");
    	}
    	
    	// Session must not have expired
    	DateTime dateTime = authnStatement.getSessionNotOnOrAfter();
    	
    	if (authnStatement.getSessionNotOnOrAfter() != null &&
    		authnStatement.getSessionNotOnOrAfter().isAfterNow()) {  
    		err("The assertion must have a AuthnStatement@SessionNotOnOrAfter and it must not have expired. SessionNotOnOrAfter: " + authnStatement.getSessionNotOnOrAfter(), "SessionNotOnOrAfter");
    	}
	}
		
	public boolean checkRecipient(Assertion assertion, String assertionConsumerURL) {
		if (assertionConsumerURL == null) return false;
		if (assertion.getSubject() == null) return false;
		if (assertion.getSubject().getSubjectConfirmations() == null) return false;
		
		
		for (SubjectConfirmation subjectConfirmation : assertion.getSubject().getSubjectConfirmations()) {
			SubjectConfirmationData subjectConfirmationData = subjectConfirmation.getSubjectConfirmationData();
			if (subjectConfirmationData == null) continue;
			
			if (assertionConsumerURL.equals(subjectConfirmationData.getRecipient())) {
				return true;
			}
		}
		return false;
	}	
	
	public DateTime getConfirmationTime(Assertion assertion) {
		if (assertion.getSubject() == null) return null;
		if (assertion.getSubject().getSubjectConfirmations() == null || 
				assertion.getSubject().getSubjectConfirmations().isEmpty()) return null;

		for (SubjectConfirmation subjectConfirmation : assertion.getSubject().getSubjectConfirmations()) {
			SubjectConfirmationData data = subjectConfirmation.getSubjectConfirmationData();

			if (data != null && data.getNotOnOrAfter() != null) {
				return data.getNotOnOrAfter();
			}
		}
		return null;
	}
	public static final String ATTRIBUTE_ASSURANCE_LEVEL_NAME = "dk:gov:saml:attribute:AssuranceLevel";

	public static final String ATTRIBUTE_ASSURANCE_LEVEL_FRIENDLY_NAME = "AssuranceLevel";

	 public int getAssuranceLevel(Assertion assertion) throws Exception {
		 int value1 = 0 ;
	    	for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
	    		for (Attribute attribute : attributeStatement.getAttributes()) {
					if (ATTRIBUTE_ASSURANCE_LEVEL_NAME.equals(attribute.getName())) {
						String value = SamlUtil.extractAttributeValueValue(attribute);
						try {
							value1 = Integer.parseInt(value);
						} catch (NumberFormatException e) {
							value1 = 3;
						}
						
					}
				}
			}
	    	return value1;
	    }
	 /**
	 * Return the value of the /Subject/NameID element in an assertion
	 * 
	 * @return The value. <code>null</code>, if the assertion does not
	 *         contain the element.
	 */
	public String getSubjectNameIDValue(Assertion assertion) {
		String retVal = null;
    	if (assertion.getSubject() != null && 
        	assertion.getSubject().getNameID() != null) {
        		retVal =  assertion.getSubject().getNameID().getValue();
        }
    	return retVal;
	}
	
	
	/**
	 * Return the value of the /AuthnStatement@SessionIndex element in an assertion
	 * 
	 * @return The value. <code>null</code>, if the assertion does not
	 *         contain the element.
	 */
	public String getSessionIndex(Assertion assertion) {
		String retVal = null;
    	if (assertion != null && assertion.getAuthnStatements() != null) {
    		if (assertion.getAuthnStatements().size() > 0) {
    			// We only look into the first AuthnStatement
    			AuthnStatement authnStatement = assertion.getAuthnStatements().get(0);
    			retVal = authnStatement.getSessionIndex();
    		}
    	}
    	return retVal;
	}

	/**
	 * Check whether an assertion contains an expired sessionIndex within a
	 * AuthnStatement (i.e. AuthnStatement@SessionNotOnOrAfter &lt;= now)
	 * 
	 * @return <code>true</code>, if the assertion has expired. <code>false</code>
	 *         otherwise.
	 */
	public boolean hasSessionExpired(Assertion assertion) {
		boolean retVal = false;
    	if (assertion != null && assertion.getAuthnStatements() != null) {
			if (assertion.getAuthnStatements().size() > 0) {
				// We only look into the first AuthnStatement
				AuthnStatement authnStatement = (AuthnStatement) assertion.getAuthnStatements().get(0);
				if (authnStatement.getSessionNotOnOrAfter() != null) {
					retVal = authnStatement.getSessionNotOnOrAfter().isBeforeNow();
				} else {
					retVal = false;
				}
			}
		}
		return retVal;
	}

	/**
	 * Return the value of the /AuthnStatement/AuthnContext/AuthnContextClassRef
	 * element in an assertion
	 * 
	 * @return The value. null, if the assertion does not
	 *         contain the element.
	 */
	public String getAuthnContextClassRef(Assertion assertion) {
		String retVal = null;
    	if (assertion.getAuthnStatements() != null) {
    		if (assertion.getAuthnStatements().size() > 0) {
    			// We only look into the first AuthnStatement
    			AuthnStatement authnStatement = (AuthnStatement) assertion.getAuthnStatements().get(0);
    			AuthnContext authnContext = authnStatement.getAuthnContext();
    			if (authnContext != null) {
    				AuthnContextClassRef authnContextClassRef = authnContext.getAuthnContextClassRef();
    				if (authnContextClassRef != null) {
    					retVal = authnContextClassRef.getAuthnContextClassRef();
    				}
    			}
    		}
    	}
    	return retVal;
	}
	/**
     * Validate whether a SAML assertion contains the expected elements
     * @param spEntityID The entityID of the service provider
     * @param spAssertionConsumerURL The assertion consumer URL of the service provider
	 * @throws Exception 
     */
    public void validateAssertion(Assertion assertion, String spEntityID, String spAssertionConsumerURL) throws Exception {
    	try {
			assertion.validate(false);
		} catch (org.opensaml.xml.validation.ValidationException e) {
			err( "Assertion Validation exception", "Assertion");
		}
		// The SAML version must be 2.0
		if (!SAMLVersion.VERSION_20.equals(assertion.getVersion())) {  
			err("The assertion must be version 2.0. Was " + assertion.getVersion(), "Version 2.0");
		}
    	// There must be an ID
    	if (assertion.getID() == null) {  
    		err("The assertion must contain a ID", "Assertion ID");
    	}
    	
    	
    		validate(assertion, spEntityID, spAssertionConsumerURL);
    	//}
    }
	
    public void basicvalidate(Assertion a, String spEntityId, String spAssertionConsumerURL) throws ValidationException {
		
    	// There must be an IssueInstant
    	if (a.getIssueInstant() == null) {  
    		err("The assertion must contain a IssueInstant", "Assertion IssueInstant");
    	}

    	// There must be an Issuer
    	if (a.getIssuer() == null ||
    		a.getIssuer().getValue() == null) {  
    		err("The assertion must contain an Issuer", "Assertion Issuer");
    	}

    	// There must be a Subject/NameID
    	if (a.getSubject().getNameID() == null) {  
    		err("The assertion must contain a Subject/NameID", "Assertion Subject/NameID");
    	}
		
    	// There must be a valid audience
    	if (!getAudience(a).contains(spEntityId)) {
    		err("The assertion must contain the service provider "+spEntityId+" within the Audience list: " + getAudience(a), "Audience");
    	}

    	DateTime conditionTime = getConditionTime(a);
    	if (conditionTime == null || !conditionTime.isAfterNow()) {
    		err("Condition NotOnOrAfter is after now: " + conditionTime, "Condition NotOnOrAfter");
    	}
	}
    public Collection<String> getAudience(Assertion assertion) {
		List<String> audiences = new ArrayList<String>();
		
		if (assertion.getConditions() == null) return audiences;
		
		for (AudienceRestriction audienceRestriction : assertion.getConditions().getAudienceRestrictions()) {
			for (Audience audience : audienceRestriction.getAudiences()) {
				audiences.add(audience.getAudienceURI());
			}
		}
		
		return audiences;
	}
    public DateTime getConditionTime(Assertion assertion) {
		if (assertion.getConditions() == null) return null;
		
		DateTime notOnOrAfter = assertion.getConditions().getNotOnOrAfter();
		return notOnOrAfter;
	}
	
		
	
}
