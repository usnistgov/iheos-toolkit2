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


		// leave as summary row (plus a blank for separation)
		summaryRow = f.getRow();
		f.setName("   ");
		f.incRow();

		f.setName("Time of validation: " + timeAndDate);
		f.incRow();

		f.setName("Client IP Address: " + clientIP);
		f.incRow();

		if (uploadFilename != null) {
			f.setName("File validated: " + uploadFilename);
			f.incRow();
		}
		f.hr();

		f.setName(f.h2("Name"));
		f.setStatus(f.h2("Status"));
		f.setDetail(f.h2("DTS"));
		f.setReference(f.h2("Found"));
		f.setExpected(f.h2("Expected"));
		f.setRFC(f.h2("RFC"));
		f.incRow();

		for (ValidationStepResult result : results.getResults()) {
			f.hr();
			f.addCell(f.h3(result.stepName), 0);
			f.incRow();

			List<ValidatorErrorItem> ers = result.er;
			for (ValidatorErrorItem er : ers)  {
				boolean row_advance = true;
				lessdetail = false;
				switch (er.level) {
				case SECTIONHEADING:
					f.setName(f.bold(er.msg));
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
					f.setName(er.msg);
					f.setColSpan(0, 5);
					f.setDetail(f.green("Success"));
					lessdetail = true;
					break;

				case ERROR:
					f.setName(f.red(er.msg));
					f.setReference(f.red(er.resource));
					foundErrors = true;
					f.setStatus(f.red("Error"));
					break;

				case WARNING:
					f.setName(f.blue(er.msg));
					f.setReference(f.blue(er.resource));
					f.setStatus(f.blue("Warning"));
					break;
				
				case D_SUCCESS:
					f.setName(er.name);
					f.setDetail(er.dts);
					f.setReference(er.found);
					f.setExpected(er.expected);
					f.setRFC(f.htm_link(er.rfc));
					f.setStatus(f.green(er.status));
					lessdetail = true;
					break;
					
				case D_INFO:
					f.setName(er.name);
					f.setDetail(er.dts);
					f.setReference(er.found);
					f.setExpected(er.expected);
					f.setRFC(f.htm_link(er.rfc));
					f.setStatus(f.purple(er.status));
					lessdetail = true;
					break;
					
				case D_ERROR:
					f.setName(f.red(er.name));
					f.setDetail(f.red(er.dts));
					f.setReference(f.red(er.found));
					f.setExpected(f.red(er.expected));
					f.setRFC(f.htm_link(er.rfc));
					f.setStatus(f.red(er.status));
					lessdetail = true;
					break;
					
					
				case D_WARNING:
					f.setName(f.blue(er.name));
					f.setDetail(f.blue(er.dts));
					f.setReference(f.blue(er.found));
					f.setExpected(f.blue(er.expected));
					f.setRFC(f.htm_link(er.rfc));
					f.setStatus(f.blue(er.status));
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

}
