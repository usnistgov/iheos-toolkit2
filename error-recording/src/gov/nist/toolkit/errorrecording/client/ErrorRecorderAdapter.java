package gov.nist.toolkit.errorrecording.client;


import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;


public class ErrorRecorderAdapter {

	ArrayList<SummaryToken> summary = new ArrayList<SummaryToken>();
	ArrayList<ValidationReportItem> detailed = new ArrayList<ValidationReportItem>();
	ArrayList<ArrayList<CCDAValidationReportItem>> ccda = new ArrayList<ArrayList<CCDAValidationReportItem>>();
	ArrayList<ArrayList<XDMValidationReportItem>> xdm = new ArrayList<ArrayList<XDMValidationReportItem>>();
	int indexEndSummary = 0;
	boolean hasCCDA = false;
	boolean hasXDM = false;
	boolean isOnlyCCDA = false;
	boolean isOnlyXDM = false;
	boolean isDirect = false;

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

	public ErrorRecorderAdapter(List<ValidatorErrorItem> er) {
		ArrayList<ValidatorErrorItem> erVal = new ArrayList<ValidatorErrorItem>();
		for(int i=0;i<er.size();i++) {
			erVal.add(er.get(i));
		}
		getSummaryFromErrorRecorder(erVal);
		getReportFromErrorRecorder(erVal);

	}


	public void getReportFromErrorRecorder(ArrayList<ValidatorErrorItem> er) {
		for(int i=indexEndSummary;i<er.size();i++) {
			if(er.get(i).level.equals(ValidatorErrorItem.ReportingLevel.DETAIL)) {
				if(er.get(i).msg.startsWith("#")) {
					String contentName = er.get(i).msg.replaceAll("#", "");
					String content = er.get(i+1).msg;
					i += 2;
					detailed.add(new ValidationReportItem(contentName, content));
				} else if(er.get(i).msg.contains("Input is CDA R2, try validation as CCDA")) {
					if(i == 1) {
						isOnlyCCDA = true;
					}
					i = getCCDAFromErrorRecorder(er, i+1);
					hasCCDA = true;
				} else if(er.get(i).msg.contains("Try validation as XDM")) {
					if(i == 0) {
						isDirect = true;
						isOnlyXDM = true;
					}
					i = getXDMFromErrorRecorder(er, i);
					hasXDM = true;
				} else if(er.get(i).msg.contains("**Metadata Validation**")) {
					if(i == 0) {
						isDirect = true;
						isOnlyXDM = true;
					}
					i = getXDMFromErrorRecorder(er, i);
					hasXDM = true;
				} else {
					detailed.add(new ValidationReportItem(er.get(i).msg));
				}
			} else {
				if(er.get(i).level.equals(ValidatorErrorItem.ReportingLevel.SECTIONHEADING)) {
					er.get(i).name = er.get(i).msg;
				}
				detailed.add(new ValidationReportItem(er.get(i).level, er.get(i).name,
						er.get(i).dts, er.get(i).found, er.get(i).expected, er.get(i).rfc));
				isDirect = true;
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

	public int getCCDAFromErrorRecorder(ArrayList<ValidatorErrorItem> er, int index) {
		int k = index; 
		ArrayList<CCDAValidationReportItem> ccdaList = new ArrayList<CCDAValidationReportItem>();
		while(!er.get(k).msg.contains("CCDA Validation done")) {
			ccdaList.add(new CCDAValidationReportItem(er.get(k).msg, er.get(k).resource, er.get(k).level));
			k++;
		}
		
		ccda.add(ccdaList);

		return k++;
	}

	public int getXDMFromErrorRecorder(ArrayList<ValidatorErrorItem> er, int index) {
		int k = index; 
		ArrayList<XDMValidationReportItem> xdmList = new ArrayList<XDMValidationReportItem>();
		while(k < er.size() && !er.get(k).msg.contains("XDM Validation done")) {
			xdmList.add(new XDMValidationReportItem(er.get(k).msg, er.get(k).level));
			k++;
		}

		xdm.add(xdmList);
		
		return k++;
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
		// velocity
		String res = "";
		if(isDirect) {
			try {

				String ccdaString = "";
				if(hasCCDA) {
					for(int e=0;e<this.ccda.size();e++) {
						ccdaString += CcdaToHtml(this.ccda.get(e));
					}
				}

				String xdmString = "";
				if(hasXDM) {
					for(int e=0;e<this.xdm.size();e++) {
						xdmString += XdmToHtml(this.xdm.get(e));
					}
				}

				//  first, get and initialize an engine  
				VelocityEngine ve = VelocitySingleton.getVelocityEngine();
				//  next, get the Template
				Template t = ve.getTemplate("DirectValidationReport.vm");
				//  create a context and add data
				VelocityContext context = new VelocityContext();

				if(isOnlyCCDA || isOnlyXDM) {
					context.put("summary", new ArrayList<SummaryToken>());
					context.put("validationReport", new ArrayList<ValidationReportItem>());
				} else {
					context.put("summary", this.getSummary());
					context.put("validationReport", this.getDetailed());
				}

				// Path for images
				String absolutePath = new File(Thread.currentThread().getContextClassLoader().getResource("").getFile()).getParentFile().getParentFile().getPath();//this goes to webapps directory
				String pattern = Pattern.quote(File.separator);
				String webappsDir = absolutePath.split(pattern)[absolutePath.split(pattern).length-1];
				webappsDir = "/" + webappsDir + "/doc";
				context.put("path", webappsDir);

				// CCDA
				context.put("ccda", ccdaString);

				// XDM
				context.put("xdm", xdmString);

				// now render the template into a StringWriter
				StringWriter writer = new StringWriter();
				t.merge( context, writer );
				// show the World
				//System.out.println( writer.toString() );
				res = writer.toString();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public String CcdaToHtml(ArrayList<CCDAValidationReportItem> ccdaItem) {
		String res = "";
		//  first, get and initialize an engine  
		try {
			VelocityEngine ve = VelocitySingleton.getVelocityEngine();
			Template t2 = ve.getTemplate("CCDAValidationReport.vm");
			VelocityContext context = new VelocityContext();
			context.put("validationReport", ccdaItem);

			StringWriter writer = new StringWriter();
			t2.merge( context, writer );

			res = writer.toString();

		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String XdmToHtml(ArrayList<XDMValidationReportItem> xdmItem) {
		String res = "";
		//  first, get and initialize an engine  
		try {
			VelocityEngine ve = VelocitySingleton.getVelocityEngine();
			Template t2 = ve.getTemplate("XDMValidationReport.vm");
			VelocityContext context = new VelocityContext();
			context.put("validationReport", xdmItem);
			
			boolean metadata = false;
			if(xdmItem.get(0).getMsg().contains("Metadata")) {
				xdmItem.remove(0);
				metadata = true;
			}
			
			context.put("metadata", metadata);

			StringWriter writer = new StringWriter();
			t2.merge( context, writer );

			res = writer.toString();

		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public boolean isOnlyCCDA() {
		return isOnlyCCDA;
	}

}
