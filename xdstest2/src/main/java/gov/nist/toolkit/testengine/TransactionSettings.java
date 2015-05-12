package gov.nist.toolkit.testengine;

import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.testengine.logrepository.LogRepository;

public class TransactionSettings {
	/**
	 * Should Patient ID be assigned from xdstest config on Submissions?
	 */
	public Boolean assignPatientId = null;   // allows for null (unknown)
	public String patientId = null;
	public String altPatientId = null;
//	public File logDir = null;
	public LogRepository logRepository = null;
	public boolean writeLogs = false;
	public SiteSpec siteSpec;
	public AssertionResults res = null;
	public String user = null;
	
	public SecurityParams securityParams = null;
	
	public TransactionSettings() {
		res = new AssertionResults();
	}
}
