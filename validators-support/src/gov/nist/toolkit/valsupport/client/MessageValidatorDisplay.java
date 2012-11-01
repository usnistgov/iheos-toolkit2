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
		f.setDetail("   ");
		f.incRow();

		f.setDetail("Time of validation: " + timeAndDate);
		f.incRow();

		f.setDetail("Client IP Address: " + clientIP);
		f.incRow();

		if (uploadFilename != null) {
			f.setDetail("File validated: " + uploadFilename);
			f.incRow();
		}
		f.hr();

		f.setDetail(f.h2("Detail"));
		f.setReference(f.h2("Reference"));
		f.setStatus(f.h2("Status"));
		f.incRow();

		for (ValidationStepResult result : results.getResults()) {
			f.hr();
			f.addCell(f.h3(result.stepName), 0);
			f.incRow();

			List<ValidatorErrorItem> ers = result.er;
			for (ValidatorErrorItem er : ers)  {
				boolean row_advance = true;
				switch (er.level) {
				case SECTIONHEADING:
					f.setDetail(f.bold(er.msg));
					break;

				case CHALLENGE:
					if (!lessdetail) 
						f.setDetail(er.msg);
					else
						row_advance = false;
					break;

				case EXTERNALCHALLENGE:
					f.setDetail(er.msg);
					break;

				case DETAIL:
					f.setDetail(er.msg);
					break;

				case ERROR:
					f.setDetail(f.red(er.msg));
					f.setReference(f.red(er.resource));
					foundErrors = true;
					f.setStatus(f.red("error"));
					break;

				case WARNING:
					f.setDetail(f.blue(er.msg));
					f.setReference(f.red(er.resource));
					foundErrors = false;
					f.setStatus(f.blue("warning"));
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

		if (foundErrors)
			f.setCell(f.red("Summary: Errors were found"), summaryRow, 0);
//			resultsTable.setWidget(summaryRow, 0, html(f.red("Summary: Errors were found")));
		else
			f.setCell("Summary: No error were found", summaryRow, 0);
//			resultsTable.setWidget(summaryRow, 0, html("Summary: No error were found"));

	}

}
