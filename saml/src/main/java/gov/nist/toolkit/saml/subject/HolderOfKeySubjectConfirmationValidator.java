package gov.nist.toolkit.saml.subject;

import java.util.List;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
	
import javax.xml.namespace.QName;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.keyinfo.KeyInfoHelper;
import org.opensaml.xml.signature.DSAKeyValue;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyValue;
import org.opensaml.xml.signature.RSAKeyValue;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.util.LazyList;
import org.opensaml.xml.util.Pair;
import org.opensaml.xml.validation.ValidationException;

import javax.xml.namespace.QName;
/**
 * Validates a Holder of Key subject confirmation. A subject confirmation is considered confirmed if one of the
 * following checks has passed:
 * <ul>
 * <li>the presenter's public key (either given explicitly or extracted from the given certificate) matches a
 * {@link KeyValue} within one of the {@link KeyInfo} entries in the confirmation data</li>
 * <li>the presenter's public cert matches an {@link org.opensaml.xml.signature.XCertificate} within one of the
 * {@link KeyInfo} entries in the confirmation data</li>
 * </ul>
 * In both cases a "match" is determined by a straight byte-level comparison.
 * 
 * This validator requires the {@link ValidationContext#getStaticParameters()} to carry the presenter's certificate or
 * public key. The certificate must be bound to the property {@link #PRESENTER_CERT_PARAM} and the key must be bound to
 * the property {@link #PRESENTER_KEY_PARAM}. If both are present the public key of the given certificate must match the
 * given key.
 * 
 * This validator populates the {@link ValidationContext#getDynamicParameters()} property
 * {@link #CONFIRMED_KEY_INFO_PARAM} with the {@link KeyInfo} that confirmed the subject.
 */
public class HolderOfKeySubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {
	 /**
	  * The name of the {@link ValidationContext#getStaticParameters()} carrying the {@link PublicKey} used by the
	  presenter.
	     */
	    public static final String PRESENTER_KEY_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
	            + ".PresenterKey";
	
	    /**
	     * The name of the {@link ValidationContext#getStaticParameters()} carrying the {@link X509Certificate} used by the
	     * presenter.
	     */
	    public static final String PRESENTER_CERT_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
	            + ".PresenterCertificate";
	
	    /**
	     * The name of the {@link ValidationContext#getDynamicParameters()} carrying the {@link KeyInfo} that confirmed the
	     * subject.
	     */
	    public static final String CONFIRMED_KEY_INFO_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
	            + ".ConfirmedKeyInfo";
	
	    
	
	   
	/**
		     * Checks to see whether the schema type of the subject confirmation data, if present, is the required
		     * {@link KeyInfoConfirmationDataType#TYPE_NAME}.
		     * 
		     * @param confirmation subject confirmation bearing the confirmation data to be checked
		     * 
		     * @return true if the confirmation data's schema type is correct, false otherwise
		     */
		    public boolean isValidConfirmationDataType(SubjectConfirmation confirmation) {
		        QName confirmationDataSchemaType = confirmation.getSubjectConfirmationData().getSchemaType();
                return confirmationDataSchemaType == null
                        || confirmationDataSchemaType.equals(KeyInfoConfirmationDataType.TYPE_NAME);
            }

	@Override
	protected ValidationResult doValidate(SubjectConfirmation confirmation,
			Assertion assertion, ValidationContext context)
			throws ValidationException {
		   if (!isValidConfirmationDataType(confirmation)) {
	            String msg = String.format(
	                    "Subject confirmation data is not of type '%s'", KeyInfoConfirmationDataType.TYPE_NAME);
	            context.setValidationFailureMessage(msg);
	            return ValidationResult.INVALID;
	        }
	
	        List<KeyInfo> possibleKeys = getSubjectConfirmationKeyInformation(confirmation, assertion, context);
	        if (possibleKeys.isEmpty()) {
	            String msg = String.format(
	                    "No key information for holder of key subject confirmation in assertion '%s'", assertion.getID());
	            context.setValidationFailureMessage(msg);
	            return ValidationResult.INVALID;
	        }
	
	        Pair<PublicKey, X509Certificate> keyCertPair = getKeyAndCertificate(context);
	        if (keyCertPair.getFirst() == null && keyCertPair.getSecond() == null) {
	            context.setValidationFailureMessage("Neither the presenter's certificate nor its public key were provided");
	            return ValidationResult.INDETERMINATE;
	        }
	
	        for (KeyInfo keyInfo : possibleKeys) {
	            if (matchesKeyValue(keyCertPair.getFirst(), keyInfo)) {
	                context.getDynamicParameters().put(CONFIRMED_KEY_INFO_PARAM, keyInfo);
	                return ValidationResult.VALID;
	            } else if (matchesX509Certificate(keyCertPair.getSecond(), keyInfo)) {
	                context.getDynamicParameters().put(CONFIRMED_KEY_INFO_PARAM, keyInfo);
	                return ValidationResult.VALID;
	            }
	        }
	
	        return ValidationResult.INVALID;
		}
	
	/**
     * Extracts the {@link KeyInfo}s from the given subject confirmation data.
     * 
     * @param confirmation subject confirmation data
     * @param assertion assertion bearing the subject to be confirmed
     * @param context current message processing context
     * 
     * @return list of key informations available in the subject confirmation data, never null
     * 
     * @throws ValidationException if there is a problem processing the SubjectConfirmation
     *
     */
    protected List<KeyInfo> getSubjectConfirmationKeyInformation(SubjectConfirmation confirmation, Assertion assertion,
            ValidationContext context) throws ValidationException {
        SubjectConfirmationData confirmationData = confirmation.getSubjectConfirmationData();

        List<KeyInfo> keyInfos = new LazyList<KeyInfo>();
        for (XMLObject object : confirmationData.getUnknownXMLObjects()) {
            if (object != null && object.getElementQName().equals(KeyInfo.DEFAULT_ELEMENT_NAME)) {
            	keyInfos.add((KeyInfo) object);
            }
        }

        return keyInfos;
    }
    
    /**
    	     * Extracts the presenter's key and/or certificate from the validation context.
    	     * 
    	     * @param context current validation context
    	     * 
    	     * @return the presenter's key/cert pair, information not available in the context is null
    	     */
    	    public Pair<PublicKey, X509Certificate> getKeyAndCertificate(ValidationContext context) {
    	        PublicKey presenterKey = null;
    	        try {
    	            presenterKey = (PublicKey) context.getStaticParameters().get(PRESENTER_KEY_PARAM);
    	        } catch (ClassCastException e) {
    	            throw new IllegalArgumentException(String.format(
    	                    "The value of the static validation parameter '%s' was not of the required type '%s'",
    	                    PRESENTER_KEY_PARAM, PublicKey.class.getName()));
    	        }
    	
    	        X509Certificate presenterCert = null;
    	        try {
    	            presenterCert = (X509Certificate) context.getStaticParameters().get(PRESENTER_CERT_PARAM);
    	            if (presenterCert != null) {
    	                if (presenterKey != null) {
    	                    if (!presenterKey.equals(presenterCert.getPublicKey())) {
    	                        throw new IllegalArgumentException(
    	                                "Presenter's certificate contains a different public key " 
    	                                + "than the one explicitly given");
    	                    }
    	                } else {
    	                    presenterKey = presenterCert.getPublicKey();
    	                }
    	            }
    	        } catch (ClassCastException e) {
    	            throw new IllegalArgumentException(String.format(
    	                    "The value of the static validation parameter '%s' was not of the required type '%s'",
    	                    PRESENTER_CERT_PARAM, X509Certificate.class.getName()));
    	        }
    	
    	        return new Pair<PublicKey, X509Certificate>(presenterKey, presenterCert);
    	    }
    	    /**
    	         * Checks to see if the DSA or RSA key (depending on what is used in the certificate) matches one of the keys in the
    	         * given KeyInfo.
    	         * 
    	         * @param key public key presenter of the assertion
    	         * @param keyInfo key info from subject confirmation of the assertion
    	         * 
    	         * @return true if the public key in the certificate matches one of the key values in the key info, false otherwise
    	         */
    	        protected boolean matchesKeyValue(PublicKey key, KeyInfo keyInfo) {
    	            if (key == null) {
    	                return false;
    	            }
    	    
    	            List<KeyValue> keyValues = keyInfo.getKeyValues();
    	            if (keyValues == null || keyValues.isEmpty()) {
    	                System.out.println("KeyInfo contained no KeyValue children, skipping KeyValue match");
    	                return false;
    	            }
    	            
    	            
    	            try {
    	                String keyAlgo = key.getAlgorithm();
    	                if ("DSA".equals(keyAlgo)) {
    	                    DSAKeyValue dsaKeyValue;
    	                    for (KeyValue keyValue : keyValues) {
    	                        dsaKeyValue = keyValue.getDSAKeyValue();
    	                        if (dsaKeyValue != null && key.equals(KeyInfoHelper.getDSAKey(dsaKeyValue))) {
    	                            System.out.println("Matched DSAKeyValue");
    	                            return true;
    	                        }
    	                    }
    	                }
    	    
    	                if ("RSA".equals(keyAlgo)) {
    	                    RSAKeyValue rsaKeyValue;
    	                    for (KeyValue keyValue : keyValues) {
    	                        rsaKeyValue = keyValue.getRSAKeyValue();
    	                        if (rsaKeyValue != null && key.equals(KeyInfoHelper.getRSAKey(rsaKeyValue))) {
    	                            System.out.println("Matched DSAKeyValue");
    	                            return true;
    	                        }
    	                    }
    	                }
    	            } catch (KeyException e) {
    	                //System.out.println("KeyInfo contained DSA/RSA key value that can not be parsed"+ e);
    	            }
    	    
    	           // System.out.println("Failed to match a KeyInfo KeyValue against supplied PublicKey param");
    	            return false;
    	        }
    	    
    	        /**
    	         * Checks to see if the presenter's certificate matches a certificate described by the X509Data within the KeyInfo.
    	         * Matches are performed via a byte-level comparison.
    	         * 
    	         * @param cert certificate of the presenter of the assertion
    	         * @param keyInfo key info from subject confirmation of the assertion
    	         * 
    	         * @return true if the presenter's certificate matches the key described by an X509Data within the KeyInfo, false
    	         *         otherwise.
    	         */
    	        protected boolean matchesX509Certificate(X509Certificate cert, KeyInfo keyInfo) {
    	            if (cert == null) {
    	                System.out.println("X509Certificate was null, skipping certificate match");
    	                return false;
    	            }
    	    
    	            List<X509Data> x509Datas = keyInfo.getX509Datas();
    	            if (x509Datas == null || x509Datas.isEmpty()) {
    	                System.out.println("KeyInfo contained no X509Data children, skipping certificate match");
    	                return false;
    	            }
    	            
    	            System.out.println("Attempting to match KeyInfo X509Data to supplied X509Certificate param");
    	    
    	            List<org.opensaml.xml.signature.X509Certificate> xmlCertificates;
    	            for (X509Data data : x509Datas) {
    	                xmlCertificates = data.getX509Certificates();
    	                if (xmlCertificates == null || xmlCertificates.isEmpty()) {
    	                    System.out.println("X509Data contained no X509Certificate children, skipping certificate match");
    	                    continue;
    	                }
    	    
    	                try {
    	                    for (org.opensaml.xml.signature.X509Certificate xmlCertificate : xmlCertificates) {
    	                        if (cert.equals(KeyInfoHelper.getCertificate(xmlCertificate))) {
    	                            System.out.println("Matched X509Certificate");
    	                            return true;
    	                        }
    	                    }
    	                } catch (CertificateException e) {
    	                    System.out.println("KeyInfo contained Certificate value that can not be parsed"+ e);
    	                }
    	            }
    	    
    	            System.out.println("Failed to match a KeyInfo X509Data against supplied X509Certificate param");
    	            return false;
    	        }
	}
