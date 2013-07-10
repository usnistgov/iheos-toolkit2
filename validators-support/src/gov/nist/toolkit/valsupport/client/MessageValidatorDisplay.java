package gov.nist.toolkit.valsupport.client;

import gov.nist.toolkit.errorrecording.client.ValidationStepResult;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem.ReportingCompletionType;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem.ReportingLevel;

import java.util.List;

public class MessageValidatorDisplay {
	ValFormatter f;
	//	int row = 0;
	String timeAndDate = "";
	String clientIP = "0.0.0.0";
	String uploadFilename = null;
	boolean lessdetail = false;
	boolean isDirect = false;
	boolean isSummry = false;

	public void setTimeAndDate(String td) { timeAndDate = td; }
	public void setClientIP(String ip) { clientIP = ip; }
	public void setUploadFilename(String fn) { uploadFilename = fn; }
	public void setLessDetail(boolean less) { lessdetail = less; }

	public MessageValidatorDisplay(ValFormatter f) {
		this.f = f;
	}

	public void displayResults(MessageValidationResults results) {
		int summaryRow;
		boolean foundErrors = false;

		if(isDirectReport(results)) {
			isDirect = true;
		}
		
		// leave as summary row (plus a blank for separation)
		summaryRow = f.getRow();
		f.setName("   ");
		if(isDirect)
			f.setColSpan(0, 5);
		f.incRow();

		f.setName("Time of validation: " + timeAndDate);
		if(isDirect)
			f.setColSpan(0, 5);
		f.incRow();

		//f.setName("Client IP Address: " + clientIP);
		//f.incRow();

		if (uploadFilename != null) {
			f.setName("File validated: " + uploadFilename);
			if(isDirect)
				f.setColSpan(0, 5);
			f.incRow();
		}
		f.hr();

		if(isDirectReport(results)) {
			
			f.setName(f.h2("Name"));
			f.setStatus(f.h2("Status"));
			f.setDTS(f.h2("DTS"));
			f.setFound(f.h2("Found"));
			f.setExpected(f.h2("Expected"));
			f.setRFC(f.h2("RFC"));
			f.incRow();
		} else {
			f.setDetail(f.h2("Detail"));
			f.setReference(f.h2("Reference"));
			f.setStatus(f.h2("Status"));
			f.incRow();
		}

		for (ValidationStepResult result : results.getResults()) {
			f.hr();
			f.addCell(f.h3(result.stepName), 0);
			f.incRow();

			List<ValidatorErrorItem> ers = result.er;
			for (ValidatorErrorItem er : ers)  {
				
				// Summary Detection
				//TODO Need to change that
				if(er.msg.contains("Message Content Summary")) {
					isSummry = true;
				} else if(er.msg.contains("Detailed Validation")) {
					isSummry = false;
				}
				
				boolean row_advance = true;
				lessdetail = false;
				switch (er.level) {
				case SECTIONHEADING:
					f.setDetail(f.bold(er.msg));
					if(isDirect)
						f.setColSpan(0, 5);
					lessdetail = true;
					break;

				case CHALLENGE:
					if (!lessdetail) 
						f.setName(er.msg);
					else
						row_advance = false;
					break;

				case EXTERNALCHALLENGE:
					f.setName(er.msg);
					break;

				case DETAIL:
					if(isDirect) {
						f.setColSpan(0, 5);
						if(isSummry) {
							f.setDetail(er.msg);
							f.setStatus(f.green("Success"));
						} else {
							f.setDetail(f.purple(er.msg));
						}
							
						lessdetail = true;
					} else {
						f.setDetail(er.msg);
					}
					break;

				case ERROR:
					if(isDirect) {
						f.setDetail(f.red(er.msg));
						f.setStatus(f.red("Error"));
						f.setColSpan(0, 5);
					} else {
						f.setDetail(f.red(er.msg));
						f.setReference(f.red(er.resource));
						foundErrors = true;
						f.setStatus(f.red("Error"));
					}
					break;

				case WARNING:
					f.setDetail(f.blue(er.msg));
					f.setReference(f.blue(er.resource));
					f.setStatus(f.blue("Warning"));
					break;

				case D_SUCCESS:
					f.setName(er.name);
					f.setDTS(er.dts);
					f.setFound(er.found);
					f.setExpected(er.expected);
					f.setRFC(f.rfc_link(er.rfc));
					f.setStatus(f.green("Success"));
					lessdetail = true;
					break;

				case D_INFO:
					f.setName(er.name);
					f.setDTS(er.dts);
					f.setFound(er.found);
					f.setExpected(er.expected);
					f.setRFC(f.rfc_link(er.rfc));
					f.setStatus(f.purple("Info"));
					lessdetail = true;
					break;

				case D_ERROR:
					f.setName(f.red(er.name));
					f.setDTS(f.red(er.dts));
					f.setFound(f.red(er.found));
					f.setExpected(f.red(er.expected));
					f.setRFC(f.rfc_link(er.rfc));
					f.setStatus(f.red("Error"));
					lessdetail = true;
					break;


				case D_WARNING:
					f.setName(f.blue(er.name));
					f.setDTS(f.blue(er.dts));
					f.setFound(f.blue(er.found));
					f.setExpected(f.blue(er.expected));
					f.setRFC(f.rfc_link(er.rfc));
					f.setStatus(f.blue("Warning"));
					lessdetail = true;
					break;

				}

				if (!lessdetail) {
					if (er.completion == ReportingCompletionType.OK)
						f.setStatus("ok");
					else if (ReportingLevel.CHALLENGE == er.level && er.completion == ReportingCompletionType.ERROR)
						f.setStatus(f.red("error"));
				}
				if (row_advance)
					f.incRow();
			}
		}

		if (foundErrors) {
			f.setCell(f.red("Summary: Errors were found"), summaryRow, 0);
			//			resultsTable.setWidget(summaryRow, 0, html(f.red("Summary: Errors were found")));
		} else {
			f.setCell("Summary: No errors were found", summaryRow, 0);
			//			resultsTable.setWidget(summaryRow, 0, html("Summary: No error were found"));
		}

	}

	public boolean isDirectReport(MessageValidationResults results) {
		for (ValidationStepResult result : results.getResults()) {

			List<ValidatorErrorItem> ers = result.er;
			for (ValidatorErrorItem er : ers)  {
				switch (er.level) {				
				case D_SUCCESS:
					return true;
				case D_INFO:
					return true;
				case D_ERROR:
					return true;
				case D_WARNING:
					return true;
				}
			}

		}
		return false;

	}
}