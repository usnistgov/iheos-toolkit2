package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.adt.A40Sender;
import gov.nist.toolkit.testengine.engine.PatientIdAllocator;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.Map;

public class PatientMergeTransaction extends BasicTransaction {
	
	private final static Logger logger = Logger.getLogger(PatientMergeTransaction.class);
	
	boolean createNewPID = false;
	String forcePatientId = null;

	public PatientMergeTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	public void run(OMElement request) throws XdsException {

		try {
			Pid newXADPID = null;
			String newXADPIDString = "";
			String oldXADPIDString = "";
			if (createNewPID) {
				newXADPID = PatientIdAllocator.getNew(transactionSettings.patientIdAssigningAuthorityOid);
				newXADPIDString = newXADPID.asString();
			} else if (forcePatientId != null) {
				newXADPIDString = forcePatientId;
			} else {
				Map<String, String> linkage = getExternalLinkage();
				newXADPIDString = linkage.get("$patientid$");
			}

			newXADPIDString = useReportManager.get("$NewXADPID$");
			oldXADPIDString = useReportManager.get("$OldXADPID$");

			transactionSettings.patientId = newXADPIDString;
			testLog.add_name_value(instruction_output, "NewXADPID", newXADPIDString);
			testLog.add_name_value(instruction_output, "OldXADPID", oldXADPIDString);

			if (testConfig == null)
				throw new Exception("Internal Error - TestConfig not initialized");
			if (testConfig.site == null)
				throw new Exception("Internal Error - TestConfig.site not initialized");

			String server = testConfig.site.pifHost;
			String port = testConfig.site.pifPort;
			s_ctx.addDetail("server", server);
			s_ctx.addDetail("port", port);

			if (server == null || "".equals(server))
				throw new Exception("Site " + testConfig.site.getName() + " has no Patient Merge host configured");
			if (port == null || "".equals(port))
				throw new Exception("Site " + testConfig.site.getName() + " has no Patient Merge Feed port configured");

			new A40Sender().send(server, Integer.parseInt(port), newXADPIDString, oldXADPIDString);

		} catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
		}
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		String part_name = part.getLocalName();
		if (part_name.equals("CreateNewPatientId")) {
			createNewPID = true;
		} else if (part_name.equals("PatientID")) {
			forcePatientId = part.getText();
		} else {
			parseBasicInstruction(part);
		}
	}

	@Override
	protected String getRequestAction() {
		return null;
	}

	@Override
	protected String getBasicTransactionName() {
		return "pA40";
	}

	String transactionName() {
		return "pA40";
	}

}
