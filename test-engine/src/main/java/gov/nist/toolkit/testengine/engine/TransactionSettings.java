package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.testengine.logrepository.LogRepository;
import gov.nist.toolkit.testengine.transactions.TransactionTransport;

/**
 * Control the execution of a transaction.  Does not include the spec
 * of what transaction to run.  This only holds the environment for the
 * transaction.  That way it can be reused.
 */
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
	public TransactionTransport transactionTransport = null;
	
	public SecurityParams securityParams = null;
	
	public TransactionSettings() {
		res = new AssertionResults();
	}
}
