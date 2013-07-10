package gov.nist.toolkit.errorrecording.client;


import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public class ErrorRecorderAdapter {
	
	ArrayList<SummaryToken> summary = new ArrayList<SummaryToken>();
	ArrayList<ValidationReportItem> detailed = new ArrayList<ValidationReportItem>();
	int indexEndSummary = 0;
	
	public ErrorRecorderAdapter(ArrayList<ValidatorErrorItem> er) {
		/*ArrayList<ValidatorErrorItem> er = new ArrayList<ValidatorErrorItem>();
		for(int k=0;k<results.getResults().size();k++) {
			for(int l=0;l<results.getResults().get(l).er.size();l++) {
				er.add(results.getResults().get(l).er.get(l));
			}
		}*/
		
		getSummaryFromErrorRecorder(er);
		getReportFromErrorRecorder(er);
		
	}
	
	
	public void getReportFromErrorRecorder(ArrayList<ValidatorErrorItem> er) {
		for(int i=indexEndSummary;i<er.size();i++) {
			if(er.get(i).level.equals(ValidatorErrorItem.ReportingLevel.DETAIL)) {
				if(er.get(i).msg.startsWith("#")) {
					String contentName = er.get(i).msg.replaceAll("#", "");
					String content = er.get(i+1).msg;
					i += 2;
					detailed.add(new ValidationReportItem(contentName, content));
				} else {
					detailed.add(new ValidationReportItem(er.get(i).msg));
				}
			} else {
				if(er.get(i).level.equals(ValidatorErrorItem.ReportingLevel.SECTIONHEADING)) {
					er.get(i).name = er.get(i).msg;
				}
				detailed.add(new ValidationReportItem(er.get(i).level, er.get(i).name,
					er.get(i).dts, er.get(i).found, er.get(i).expected, er.get(i).rfc));
			}
		}
	}
	
	
	public void getSummaryFromErrorRecorder(ArrayList<ValidatorErrorItem> er) {
		for(int i=0;i<er.size();i++) {
			if(er.get(i).msg.contains("Message Content Summary")) {
				int k = i+1;
				while(!er.get(k).msg.contains("Detailed Validation")) {
					int num = 0;
					if(er.get(k).level.equals(ValidatorErrorItem.ReportingLevel.DETAIL)) {
						num = 0;
					} else if(er.get(k).level.equals(ValidatorErrorItem.ReportingLevel.SECTIONHEADING)) {
						num = 1;
					} else if(er.get(k).level.equals(ValidatorErrorItem.ReportingLevel.ERROR)) {
						num = 2;
					} else {
						num = 2;
					}
					
					summary.add(new SummaryToken(er.get(k).msg, num));
					k++;
				}
				this.indexEndSummary = k+1; 
			}
		}
	}


	public ArrayList<SummaryToken> getSummary() {
		return summary;
	}


	public void setSummary(ArrayList<SummaryToken> summary) {
		this.summary = summary;
	}


	public ArrayList<ValidationReportItem> getDetailed() {
		return detailed;
	}


	public void setDetailed(ArrayList<ValidationReportItem> detailed) {
		this.detailed = detailed;
	}

	public String toHTML() {
		// Test velocity
		String res = "";
		try {
			//  first, get and initialize an engine  
			VelocityEngine ve = VelocitySingleton.getVelocityEngine();
			//  next, get the Template
			Template t = ve.getTemplate("DirectValidationReport.vm");
			//  create a context and add data
			VelocityContext context = new VelocityContext();
			context.put("summary", this.getSummary());
			context.put("validationReport", this.getDetailed());
			
			// Path for images
			context.put("path", "/ttt/doc");
			
			// now render the template into a StringWriter
			StringWriter writer = new StringWriter();
			t.merge( context, writer );
			// show the World
			//System.out.println( writer.toString() );
			res = writer.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
