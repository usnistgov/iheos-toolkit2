package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmsg.validation.schematron.ReportProcessor;
import gov.nist.toolkit.valregmsg.validation.schematron.schematronValidation;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.axiom.om.OMElement;
import org.apache.xmlbeans.XmlObject;

public class SchematronValidator extends MessageValidator {
	ErrorRecorder er;
	OMElement message;

	public SchematronValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.message = xml;
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

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;

		int schematronValidationType = vc.getSchematronValidationType();
		
		String[] validationType = vc.getSchematronValidationTypeName(schematronValidationType);
	    
		try {

			String warHome = System.getProperty("warHome");
			System.out.print("warHome[SchematronValidator]: " + warHome + "\n");
			String path = warHome + File.separator + "toolkitx" + File.separator + "schematron" + File.separator + "xcpd" + File.separator + "files" + File.separator + "schematronValidationConfig.xml";
			File config = new File(path);
		    if (!config.exists()) {
		    	System.out.print("file: " + path + "does not exist");
		    }	
	
			if (message.getLocalName() == "Message" || message.getLocalName() == "ClinicalDocument" || message.getLocalName() =="PRPA_IN201305UV02" || message.getLocalName() =="PRPA_IN201306UV02") 
			{	
			String xmlstring = message.toString();
			// RegistryUtility.schema_validate_local(xml, schemaValidationType);
			for (int m = 0; m < validationType.length; m++) {
				String messageType = validationType[m];
				//if (validationType == "C32") {
					XmlObject createdReport = schematronValidation.createReport(warHome, config, xmlstring, messageType);
					er.detail("<br><br><b>----------" + messageType.toUpperCase() + "  VALIDATION ----------</b>");
					reportFormatter(createdReport, er);
			}
			} else {
					er.detail("This doesn't appear to be a Patient Discovery message or CDA document");
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			er.detail("Problem generating your report");
		}

	}

	public void reportFormatter(XmlObject report, ErrorRecorder er) {

		// String formattedReport = null;
		String reportAsString = report.xmlText();
		// Utility class that breaks the report into 3 sections for reporting
		// back
		ReportProcessor rp = new ReportProcessor();
		rp.documentParser(reportAsString);

		// Sets header reporting
		HashMap<String, String> reportHeader = rp.getHeaderValues();
		er.detail("ValidationStatus: " + reportHeader.get("ValidationStatus"));
		er.detail("DateOfTest: " + reportHeader.get("DateOfTest"));
		er.detail("ResultOfTest: " + reportHeader.get("ResultOfTest"));
		er.detail("ErrorCount: " + reportHeader.get("ErrorCount"));

		// Sets schema reporting
		er.detail("<br><b>Schema Report</b>");
		ArrayList<String> schemaErrors = rp.getSchemaErrors();
		if (schemaErrors == null) {
			er.detail("No Schema Errors Found");
		} else {
			for (int i = 0; i < schemaErrors.size(); i++) {
				err(schemaErrors.get(i), "");
			}
		}

		// Sets schematron reporting
		er.detail("<br><b>Schematron Report</b>");
		ArrayList<HashMap> schematronErrors = rp.getSchematronErrors();
		if (schematronErrors == null) {
			er.detail("No Schematron Errors Found");
		} else {
			for (int j = 0; j < schematronErrors.size(); j++) {
				HashMap errorMsg = (HashMap) schematronErrors.get(j);
				err("<br>" + "Message: " + errorMsg.get("Message") + "<br>"
						+ "Context: " + errorMsg.get("Context") + "<br>"
						+ "Test: " + errorMsg.get("Test"), "");
			}
		}
	}


}
