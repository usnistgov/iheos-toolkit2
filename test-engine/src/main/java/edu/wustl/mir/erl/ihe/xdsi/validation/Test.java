package edu.wustl.mir.erl.ihe.xdsi.validation;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import gov.nist.toolkit.testengine.engine.ReportManager;
import gov.nist.toolkit.testengine.engine.UseReportManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

/**
 * Base class for Validation Tests 
 * A collector of Validation Step objects.
 */
@SuppressWarnings("javadoc")
public abstract class Test  {
   
	protected ArrayList<Step> steps = new ArrayList<Step>();
	protected String reportFolder = null;
	
	public String getReportFolder() {
		return reportFolder;
	}

	public void setReportFolder(String reportFolder) {
		this.reportFolder = reportFolder;
	}

	protected void addStep(Step step) {
		steps.add(step);
	}
	
	public abstract void initializeTest(String[] args) throws Exception;
	
	public void runTest() throws Exception {
	   runSteps();
	}
	
	public void runSteps() throws Exception {
	   for (Step step : steps) {
		   step.runStep();
	   }
	}
	
	public Results getResults(String name) {
	   Results results = new Results(name);
	   for(Step step : steps) {
		   step.getResults(results);
	   }
	   return results;
	}
	
	public void reportResults(String testName) throws Exception {
		System.out.println("Reporting results: " + reportFolder);
		Results results = getResults(testName);
		writeSummary      (reportFolder + Utility.fs + "summary.txt", testName, results);
		writeSuccess      (reportFolder + Utility.fs + "success.txt", testName, results);
		writeWarning      (reportFolder + Utility.fs + "warnings.txt", testName, results);
		writeError        (reportFolder + Utility.fs + "errors.txt", testName, results);
		writeUncategorized(reportFolder + Utility.fs + "uncategorized.txt", testName, results);
		writeFullReport   (reportFolder + Utility.fs + "full_report.txt", testName, results);

	}
	
	private void writeSummary(String path, String testName, Results results) throws Exception {
		String nl = "\n";
		File f = new File(path);
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		writer.write("Test Summary\n");
		writer.write("Test name:               " + testName + nl);
		writer.write("Report time stamp:       " + timeLog + nl);
		writer.write("Steps tested succefully: " + results.getSuccessCount() + nl);
		writer.write("Warning count:           " + results.getWarningCount() + nl);
		writer.write("Error count:             " + results.getErrorCount() + nl);
		writer.write("Uncategorized results:   " + results.getUncategorizedCount() + nl);
		writer.write("End of report\n");
		writer.close();
	}
	
	private void writeSuccess(String path, String testName, Results results) throws Exception {
		String nl = "\n";
		File f = new File(path);
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		writer.write("Success Summary\n");
		writer.write("Test name:                 " + testName + nl);
		writer.write("Report time stamp:         " + timeLog + nl);
		writer.write("Steps tested successfully: " + results.getSuccessCount() + nl);
		int i = 1;
		for (String detail: results.getSuccessDetails()) {
			writer.write("" + i + " " + detail + nl);
			i++;
		}

		writer.write("End of report\n");
		writer.close();
	}
	
	
	private void writeError(String path, String testName, Results results) throws Exception {
		String nl = "\n";
		File f = new File(path);
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		writer.write("Error Summary\n");
		writer.write("Test name:          " + testName + nl);
		writer.write("Report time stamp:  " + timeLog + nl);
		writer.write("Errors:             " + results.getErrorCount() + nl);
		int i = 1;
		for (String detail: results.getErrorDetails()) {
			writer.write("" + i + " " + detail + nl);
			i++;
		}

		writer.write("End of report\n");
		writer.close();
	}
	
	
	private void writeWarning(String path, String testName, Results results) throws Exception {
		String nl = "\n";
		File f = new File(path);
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		writer.write("Warning Summary\n");
		writer.write("Test name:          " + testName + nl);
		writer.write("Report time stamp:  " + timeLog + nl);
		writer.write("Warnings:           " + results.getWarningCount() + nl);
		int i = 1;
		for (String detail: results.getWarningDetails()) {
			writer.write("" + i + " " + detail + nl);
			i++;
		}

		writer.write("End of report\n");
		writer.close();
	}
	
	private void writeUncategorized(String path, String testName, Results results) throws Exception {
		String nl = "\n";
		File f = new File(path);
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		writer.write("Uncategorized Results Summary\n");
		writer.write("Test name:             " + testName + nl);
		writer.write("Report time stamp:     " + timeLog + nl);
		writer.write("Uncategorized results: " + results.getUncategorizedCount() + nl);
		int i = 1;
		for (String detail: results.getUncategorizedDetails()) {
			writer.write("" + i + " " + detail + nl);
			i++;
		}

		writer.write("End of report\n");
		writer.close();
	}
	
	private void writeFullReport(String path, String testName, Results results) throws Exception {
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		writer.println("Full Report");
		writer.println("Test name: " + testName);
		writer.println("Report time stamp: " + timeLog);
		writer.println(results.toString());
		writer.close();
	}
}
