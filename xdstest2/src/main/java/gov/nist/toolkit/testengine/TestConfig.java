package gov.nist.toolkit.testengine;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.logrepository.LogRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestConfig {
	
	// current test number
	public String testNum;
	
	// location of testkit
	public File testkitHome = null;
	
	// location of log directory
	public LogRepository logRepository = null;
	
	// Directory holding testplan.xml and its support files
	 public File testplanDir = null;
	
	// Output directory for log.xml files (organized in same dir structure as testkit)
	// public String log_dir = null;
	 public File logFile = null;
	
	// Full path to xdstoolkit/xdstest directory
	 public String testmgmt_dir = null;
	
	// REST service to call to allocate a patient id for testing 
	// Configured in actors.xml
	 public String pid_allocate_endpoint = null;
	
	 public Site site = null;
	 
	 public Site allRepositoriesSite = null;
	
	 public String configHome = null;
		
	// XPath (with no trailing /) to element defining selected site
	 public String siteXPath = null;
	
	 public String currentStep = null;
	
	 public boolean endpointOverride = false;
	
	 public boolean verbose  = true;
	
	 public boolean trace = false;
	
	 public boolean secure = false;
	 
	 public boolean saml = false;
	
	 public String version = "0.0";
	
	 public boolean prepare_only = false;
	
	 public String args = "";
	
	 public Soap soap = null;
	
	 public String testkitVersion = null;
	
	 public void rememberPatientId(String pid) throws FileNotFoundException, IOException {
		if (testmgmt_dir == null) return;
		
		FileOutputStream fos = new FileOutputStream(new File(testmgmt_dir + File.separatorChar + "patientid_base.txt"));
		fos.write(pid.substring(0,pid.indexOf("^")).getBytes());
		fos.close();

		fos = new FileOutputStream(new File(testmgmt_dir + File.separatorChar + "assigning_authority.txt"));
		fos.write(pid.substring(pid.lastIndexOf("^")+1).getBytes());
		fos.close();
	}
}
