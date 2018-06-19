package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.adt.A01Sender;
import gov.nist.toolkit.testengine.engine.PatientIdAllocator;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.Map;

public class PatientIdentityFeedTransaction extends BasicTransaction {
	private final static Logger logger = Logger.getLogger(PatientIdentityFeedTransaction.class);
	boolean createNewPID = false;
	String forcePatientId = null;

	public PatientIdentityFeedTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	String transactionName() {
		return "pif";
	}

	public void run(OMElement request)
			throws XdsException {


		try {
			Pid pid;
			String pidString;
			if (createNewPID) {
				pid = PatientIdAllocator.getNew(transactionSettings.patientIdAssigningAuthorityOid);
				pidString = pid.asString();
			} else if (forcePatientId != null) {
				pidString = forcePatientId;
			} else {
				Map<String, String> linkage = getExternalLinkage();
				pidString = linkage.get("$patientid$");
			}
			transactionSettings.patientId = pidString;
			testLog.add_name_value(instruction_output, "PatientId", pidString);

			if (testConfig == null) throw new Exception("Internal Error - TestConfig not initialized");
			if (testConfig.site == null) throw new Exception("Internal Error - TestConfig.site not initialized by a prior step");

			String server = testConfig.site.pifHost;
			String port = testConfig.site.pifPort;
            s_ctx.addDetail("gov/nist/toolkit/installation/server", server);
            s_ctx.addDetail("port", port);

			if (server == null || "".equals(server)) throw new Exception("Site " + testConfig.site.getName() + " has no Patient Identity Feed host configured");
			if (port == null || "".equals(port)) throw new Exception("Site " + testConfig.site.getName() + " has no Patient Identity Feed port configured");

			A01Sender.send(server, Integer.parseInt(port), pidString);

			reportManager.add("$patientid$", pidString);
		}
		catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
		}
	}


	protected void parseInstruction(OMElement part) throws XdsInternalException {
		noMetadataProcessing = true;
		String part_name = part.getLocalName();
		if (part_name.equals("CreateNewPatientId")) {
			createNewPID = true;
		}
		else if (part_name.equals("PatientID")) {
			forcePatientId = part.getText();
		}
		else {
			parseBasicInstruction(part);
		}
	}

	@Override
	protected String getRequestAction() {
		return null;
	}

	@Override
	protected String getBasicTransactionName() {
		return "pif";
	}


}
