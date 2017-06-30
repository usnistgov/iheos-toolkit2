package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.testengine.transactions.TransactionTransport;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;

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
	public String patientIdAssigningAuthorityOid = null;
//	public File logDir = null;
	public LogRepository logRepository = null;
	public boolean writeLogs = false;
	public SiteSpec siteSpec;
	public String testSession;
	public String environmentName;
	/**
	 *Origin, null or default value is interpreted as "TestClient". For other Toolkit to Toolkit uses, use the originating simulator name/site name here.
	 */
	public String origin;
	public AssertionResults res = null;
	public String user = null;
	public TransactionTransport transactionTransport = null;
	
	public SecurityParams securityParams = null;
	
	public TransactionSettings() {
		res = new AssertionResults();
	}

	@Override
	public TransactionSettings clone() {
		TransactionSettings ts = new TransactionSettings();
		ts.assignPatientId = assignPatientId;
		ts.patientId = patientId;
		ts.altPatientId = altPatientId;
		ts.patientIdAssigningAuthorityOid = patientIdAssigningAuthorityOid;
		ts.logRepository = logRepository;
		ts.writeLogs = writeLogs;
		ts.siteSpec = siteSpec;
		ts.res = res;
		ts.user = user;
		ts.transactionTransport = transactionTransport;
		ts.securityParams = securityParams;
		ts.testSession = testSession;
		ts.environmentName = environmentName;
		return ts;
	}
}
