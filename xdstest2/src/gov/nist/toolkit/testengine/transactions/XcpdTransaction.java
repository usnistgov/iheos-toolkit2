package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.valregmsg.validation.schematron.ReportProcessor;
import gov.nist.toolkit.valregmsg.validation.schematron.schematronValidation;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.xmlbeans.XmlObject;
import org.jaxen.JaxenException;


public class XcpdTransaction extends BasicTransaction {

	boolean clean_params = true;
	
	public XcpdTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	protected void run(OMElement request) throws Exception {
		useAddressing = true;
		soap_1_2 = true;
		
		if (metadata_filename == null)
			throw new XdsInternalException("No MetadataFile element found for XcpdTransaction instruction within step " + this.s_ctx.get("step_id"));

		
		if (clean_params)
			cleanSqParams(request);
		
		try {
			soapCall(request);
			OMElement result = getSoapResult();
			if (result != null) {
				testLog.add_name_value(instruction_output, "Result", result);
				
				// here get validation call and put here
				
				
				validate_response(result);
			} else {
				testLog.add_name_value(instruction_output, "Result", "None");
				s_ctx.set_error("Result was null");
			}

		} 
		catch (Exception e) {
			fail(e.getMessage());
		}

	}

	private void validate_response(OMElement result) throws XdsInternalException {
		
	try {
		    String warHome = System.getProperty("warHome");
		    System.out.print("warHome[xdstest2:validate_response]: " + warHome + "\n");
		    String path = warHome + File.separator + "toolkitx" + File.separator + "schematron" + File.separator + "xcpd" + File.separator + "files" + File.separator + "schematronValidationConfig.xml";
		   
		    File config = new File(path);
		    if (!config.exists()) {
		    	System.out.print("file: " + path + "does not exist");
		    }
		    
			String xmlstring = result.toString();
			String messageType = "IHE_XCPD_306";
			XmlObject createdReport = schematronValidation.createReport(warHome, config, xmlstring, messageType);
			//s_ctx.set_error(createdReport.toString());
			reportFormatter(createdReport);
		} catch (Exception e) {
			e.printStackTrace();
			s_ctx.set_error("Problem generating your report");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void reportFormatter(XmlObject report) {

		// String formattedReport = null;
		String reportAsString = report.xmlText();
	//	String headerErrorsReport = new String();
	//	String schemaErrorsReport = new String();
	//	String schematronErrorsReport = new String();
		
		// Utility class that breaks the xml report into 3 sections for formatting and reporting back
		ReportProcessor rp = new ReportProcessor();
		rp.documentParser(reportAsString);

		// Sets header reporting
		HashMap<String, String> reportHeader = rp.getHeaderValues();
		testLog.add_name_value(instruction_output, "Detail", "ValidationStatus: " + reportHeader.get("ValidationStatus"));
		testLog.add_name_value(instruction_output, "Detail", "DateOfTest: " + reportHeader.get("DateOfTest"));
		testLog.add_name_value(instruction_output, "Detail", "ResultOfTest: " + reportHeader.get("ResultOfTest"));
		testLog.add_name_value(instruction_output, "Detail", "TotalErrorCount: " + reportHeader.get("ErrorCount"));
		
		// Sets schema reporting
		testLog.add_name_value(instruction_output, "Detail", "");
		testLog.add_name_value(instruction_output, "Detail", "---- Schema Report ----");
		ArrayList<String> schemaErrors = rp.getSchemaErrors();
		if (schemaErrors == null) {
			testLog.add_name_value(instruction_output, "Detail", "No Schema Errors Found");
		} else {
			for (int i = 0; i < schemaErrors.size(); i++) {
				try {
					s_ctx.set_error(i + ") " + schemaErrors.get(i).toString());
				} catch (XdsInternalException e) {
				}
		}	
		// Sets schematron reporting
			testLog.add_name_value(instruction_output, "Detail", "");
			testLog.add_name_value(instruction_output, "Detail", "---- Schematron Report ----");
		@SuppressWarnings("rawtypes")
		ArrayList<HashMap> schematronErrors = rp.getSchematronErrors();
		if (schematronErrors == null) {
			testLog.add_name_value(instruction_output, "Detail", "No Schematron Errors Found");
		} else {
			for (int j = 0; j < schematronErrors.size(); j++) {
				@SuppressWarnings("rawtypes")
				HashMap errorMsg = (HashMap) schematronErrors.get(j);
				testLog.add_name_value(instruction_output, "Detail", "Message: " + errorMsg.get("Message").toString());
				testLog.add_name_value(instruction_output, "Detail", "Context: " + errorMsg.get("Context").toString());
				testLog.add_name_value(instruction_output, "Detail", "Test: " + errorMsg.get("Test").toString());
				testLog.add_name_value(instruction_output, "Detail", "");
			}
		}
	}	
	}


	protected void parseInstruction(OMElement part)
			throws XdsInternalException, MetadataException {
		parseBasicInstruction(part);
	}

	protected String getRequestAction() {
		return "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery";
	}

	protected String getResponseAction() {
		return "urn:hl7-org:v3:PRPA_IN201306UV02:CrossGatewayPatientDiscovery";
	}
	
	protected String getBasicTransactionName() {
		return "xcpd";
	}

	void cleanSqParams(OMElement ele) {
		AXIOMXPath xpathExpression;
		try {
			xpathExpression = new AXIOMXPath ("//*[local-name() = 'parameterList']");
			OMElement adhocQuery = (OMElement) xpathExpression.selectSingleNode(ele);
			if (adhocQuery == null)
				return;
			
            // Clean livingSubjectAdministrativeGender
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "livingSubjectAdministrativeGender")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					    System.out.println("detatched");
					}
				}
				
			} // Cleaned LivingSubjectsAdministrativeGender
			
			//livingSubjectsBirthPlaceAddress
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "livingSubjectBirthPlaceAddress")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "streetAddressLine")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
					System.out.println("detatched");
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "city")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "state")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "postalCode")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "country")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			} // End of Address
			
			//livingSubjectsBirthPlaceName
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "livingSubjectBirthPlaceName")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					    System.out.println("Gavin: Detatched" );
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "Value")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
			

			//livingSubjectsID --SSN  -- need to figure out representation and value
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "livingSubjectId")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "Value")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
			//livingSubjectsID -- patientID --- need to figure out what we are putting where
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "livingSubjectId")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "Value")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
			//livingSubjectID
			
			//livingSubjectName 
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "livingSubjectName")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				//System.out.println("Gavin: " + slot);
				//System.out.println("Gavin: " + valueList);
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "given")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "suffix")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
			//livingSubjectID
			
			//mothersMaidenName
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "mothersMaidenName")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "given")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "family")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "suffix")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
			
			//patientAddress
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "patientAddress")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
				
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "streetAddressLine")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "city")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "state")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "postalCode")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "country")) {
					//check if a value exist anywhere within the address
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
			//patient telecom
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "patientTelecom")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
			}
			//patient telecom
			
			//principleCarePrividerID
			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "principalCareProviderId")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "value");
				for (Iterator<OMAttribute> it=valueList.getAllAttributes(); it.hasNext(); ) {
					OMAttribute at = it.next();
					if ((at.getAttributeValue().indexOf("$") != -1) || (at.getAttributeValue().equals(null)))  {
						adhocQuery.removeAttribute(at);
					    valueList.detach();
					    slot.detach();
					}
				}
			}
			
		} catch (JaxenException e) {
		}

	}
}
