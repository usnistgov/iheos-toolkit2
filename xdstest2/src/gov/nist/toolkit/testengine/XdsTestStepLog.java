package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testengine.errormgr.ErrorManager;

import org.apache.axiom.om.OMElement;

public class XdsTestStepLog {
	boolean status;
	OMElement transaction;
	OMElement stepEle;

	public XdsTestStepLog(OMElement stepEle) {
		this.stepEle = stepEle;
		String statusStr = stepEle.getAttributeValue(MetadataSupport.id_qname);
		status = ("Pass".equals(statusStr));
		
		loadTransaction(stepEle);
	}
	
	void loadTransaction(OMElement stepEle) {
		transaction = MetadataSupport.firstChildWithLocalNameEndingWith(stepEle, "Transaction");
	}
	
	/**
	 * Get pass/fail  
	 * @return
	 */
	public boolean getStatus()  {
		return status;
	}
	
	OMElement getTransactionSection(String sectionName) {
		if (transaction == null)
			return null;
		OMElement ele = MetadataSupport.firstChildWithLocalName(transaction, sectionName);
		if (ele == null)
			return null;
		return ele.getFirstElement();
	}
	
	String getTransactionSectionValue(String sectionName) {
		if (transaction == null)
			return null;
		OMElement ele = MetadataSupport.firstChildWithLocalName(transaction, sectionName);
		if (ele == null)
			return null;
		return ele.getText();
	}
	
	OMElement getSection(String sectionName) {
		OMElement ele = MetadataSupport.firstChildWithLocalName(stepEle, sectionName);
		if (ele == null)
			return null;
		return ele.getFirstElement();
	}

	/**
	 * Get SOAP Headers sent
	 * @return
	 */
	public OMElement getSendSoapHeader() {
		return getTransactionSection("OutHeader");
	}
	
	/**
	 * Get SOAP Headers received
	 * @return
	 */
	public OMElement getRcvSoapHeader() {
		return getTransactionSection("InHeader");
	}
	
	/**
	 * Get XML body of transaction request
	 * @return
	 */
	public OMElement getRequest() {
		return getTransactionSection("InputMetadata");
	}
	
	/**
	 * Get XML body of transaction response
	 * @return
	 */
	public OMElement getResponse() {
		return getTransactionSection("Result");
	}
	
	/**
	 * Get WS endpoint used
	 * @return
	 */
	public String getEndpoint() {
		return getTransactionSectionValue("Endpoint");
	}

	/**
	 * Get errors
	 * 
	 * @return
	 */
	public ErrorManager getErrors() {
		ErrorManager em = new ErrorManager();
		if (transaction == null)
			return null;
		 for (OMElement err : MetadataSupport.childrenWithLocalName(transaction, "Error")) {
			 em.add(err.getText());
		 }
		 for (OMElement err : MetadataSupport.childrenWithLocalName(stepEle, "FatalError")) {
			 em.add(err.getText());
			 em.setFatal();
		 }
		 return em;
	}

}
