package gov.nist.toolkit.registrymsg.common;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

import java.util.HashMap;
import java.util.List;

public class RequestHeader {
//	String home;
//	String queryId;
//	OMElement adhocQueryElement;
//	OMElement adhocQueryRequestElement;
//	OMAttribute homeAtt;
//    String patientId = null;
//	List<String> documentEntryObjectTypeList = null;
	OMElement omElement;
	HashMap<String, OMElement> attributeStatement = new HashMap<>();
	String samlAssertionID = null;
	String samlAssertionIssueInstant = null;
	String samlAssertionVersion = null;
	String samlAssertionIssuer = null;
	String samlCanonicalizationMethodAlgorithm = null;
	String samlSignatureMethodAlgorithm = null;
	String samlDigestMethodAlgorithm = null;
	String samlDigestValue = null;
	String samlSignatureValue = null;
	String samlX509Certificate = null;
	String samlRSAKyValueModulus = null;
	String samlRSAKeyValueExponent = null;
	String samlNHINHomeCommunityID = null;
	String samlIHEHomeCommunityID = null;
	String samlPurposeOfUseCode = null;
	String samlPurposeOfUseCodeSystem = null;
	String samlPurposeOfUseCSP = null;
	String samlPurposeOfUseValidatedAttributes = null;


	/*
	public String getHome() {
		return home;
	}
	public String getQueryId() {
		return queryId;
	}
	public OMElement getAdhocQueryElement() {
		return adhocQueryElement;
	}
	public OMElement getRequestHeaderElement() {
		return adhocQueryRequestElement;
	}
	public OMAttribute getHomeAtt() {
		return homeAtt;
	}
    public String getPatientId() { return patientId; }

	public OMElement getAttributeStatementAttribute(String name) {
		OMElement e = attributeStatement.get(name);
		return e;
	}

	public void setAttributeStatement(HashMap<String, OMElement> attributeStatement) {
		this.attributeStatement = attributeStatement;
	}

	public List<String> getDocumentEntryObjectTypeList() {
		return documentEntryObjectTypeList;
	}

	 */
	public OMElement getAttributeStatementAttribute(String name) {
		OMElement e = attributeStatement.get(name);
		return e;
	}

	public OMElement getOmElement() {
		return omElement;
	}

	public void setOmElement(OMElement omElement) {
		this.omElement = omElement;
	}

	public HashMap<String, OMElement> getAttributeStatement() {
		return attributeStatement;
	}

	public String getSamlAssertionID() {
		return samlAssertionID;
	}

	public void setSamlAssertionID(String samlAssertionID) {
		this.samlAssertionID = samlAssertionID;
	}

	public String getSamlAssertionIssueInstant() {
		return samlAssertionIssueInstant;
	}

	public void setSamlAssertionIssueInstant(String samlAssertionIssueInstant) {
		this.samlAssertionIssueInstant = samlAssertionIssueInstant;
	}

	public String getSamlAssertionVersion() {
		return samlAssertionVersion;
	}

	public void setSamlAssertionVersion(String samlAssertionVersion) {
		this.samlAssertionVersion = samlAssertionVersion;
	}

	public String getSamlAssertionIssuer() {
		return samlAssertionIssuer;
	}

	public void setSamlAssertionIssuer(String samlAssertionIssuer) {
		this.samlAssertionIssuer = samlAssertionIssuer;
	}

	public String getSamlCanonicalizationMethodAlgorithm() {
		return samlCanonicalizationMethodAlgorithm;
	}

	public void setSamlCanonicalizationMethodAlgorithm(String samlCanonicalizationMethodAlgorithm) {
		this.samlCanonicalizationMethodAlgorithm = samlCanonicalizationMethodAlgorithm;
	}

	public String getSamlSignatureMethodAlgorithm() {
		return samlSignatureMethodAlgorithm;
	}

	public void setSamlSignatureMethodAlgorithm(String samlSignatureMethodAlgorithm) {
		this.samlSignatureMethodAlgorithm = samlSignatureMethodAlgorithm;
	}

	public String getSamlDigestMethodAlgorithm() {
		return samlDigestMethodAlgorithm;
	}

	public void setSamlDigestMethodAlgorithm(String samlDigestMethodAlgorithm) {
		this.samlDigestMethodAlgorithm = samlDigestMethodAlgorithm;
	}

	public String getSamlDigestValue() {
		return samlDigestValue;
	}

	public void setSamlDigestValue(String samlDigestValue) {
		this.samlDigestValue = samlDigestValue;
	}

	public String getSamlSignatureValue() {
		return samlSignatureValue;
	}

	public void setSamlSignatureValue(String samlSignatureValue) {
		this.samlSignatureValue = samlSignatureValue;
	}

	public String getSamlX509Certificate() {
		return samlX509Certificate;
	}

	public void setSamlX509Certificate(String samlX509Certificate) {
		this.samlX509Certificate = samlX509Certificate;
	}

	public String getSamlRSAKyValueModulus() {
		return samlRSAKyValueModulus;
	}

	public void setSamlRSAKyValueModulus(String samlRSAKyValueModulus) {
		this.samlRSAKyValueModulus = samlRSAKyValueModulus;
	}

	public String getSamlRSAKeyValueExponent() {
		return samlRSAKeyValueExponent;
	}

	public void setSamlRSAKeyValueExponent(String samlRSAKeyValueExponent) {
		this.samlRSAKeyValueExponent = samlRSAKeyValueExponent;
	}

	public String getSamlNHINHomeCommunityID() {
		return samlNHINHomeCommunityID;
	}

	public void setSamlNHINHomeCommunityID(String samlNHINHomeCommunityID) {
		this.samlNHINHomeCommunityID = samlNHINHomeCommunityID;
	}

	public String getSamlIHEHomeCommunityID() {
		return samlIHEHomeCommunityID;
	}

	public void setSamlIHEHomeCommunityID(String samlIHEHomeCommunityID) {
		this.samlIHEHomeCommunityID = samlIHEHomeCommunityID;
	}

	public String getSamlPurposeOfUseCode() {
		return samlPurposeOfUseCode;
	}

	public void setSamlPurposeOfUseCode(String samlPurposeOfUseCode) {
		this.samlPurposeOfUseCode = samlPurposeOfUseCode;
	}

	public String getSamlPurposeOfUseCodeSystem() {
		return samlPurposeOfUseCodeSystem;
	}

	public void setSamlPurposeOfUseCodeSystem(String samlPurposeOfUseCodeSystem) {
		this.samlPurposeOfUseCodeSystem = samlPurposeOfUseCodeSystem;
	}

	public String getSamlPurposeOfUseCSP() {
		return samlPurposeOfUseCSP;
	}

	public void setSamlPurposeOfUseCSP(String samlPurposeOfUseCSP) {
		this.samlPurposeOfUseCSP = samlPurposeOfUseCSP;
	}

	public String getSamlPurposeOfUseValidatedAttributes() {
		return samlPurposeOfUseValidatedAttributes;
	}

	public void setSamlPurposeOfUseValidatedAttributes(String samlPurposeOfUseValidatedAttributes) {
		this.samlPurposeOfUseValidatedAttributes = samlPurposeOfUseValidatedAttributes;
	}

	public String getAttributeValue(String name) {
		String rtn = null;
		OMElement e = attributeStatement.get(name);
		if (e != null) {
			OMElement child = e.getFirstElement();
			if (child != null) {
				rtn = child.getText();
			}
		}
		return rtn;
	}

	public String toString() {
		return "RequestHeader: ";
	}
}
