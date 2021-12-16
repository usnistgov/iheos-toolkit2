package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.adt.A43Sender;
import gov.nist.toolkit.testengine.engine.PatientIdAllocator;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import java.util.logging.Logger;

import java.util.Map;

public class PatientMoveTransaction extends BasicTransaction {
	
	private final static Logger logger = Logger.getLogger(PatientMoveTransaction.class.getName());
	
	boolean createNewPID = false;
	String forcePatientId = null;

	public PatientMoveTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	public void run(OMElement request) throws XdsException {

		try {
			Pid newXADPID = null;
			String newXADPIDString = "";
			String oldXADPIDString = "";
			String newLocalPIDString = "";
			String oldLocalPIDString = "";
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
			newLocalPIDString = useReportManager.get("$NewLocalPID$");
			oldLocalPIDString = useReportManager.get("$OldLocalPID$");

			transactionSettings.patientId = newXADPIDString;
			testLog.add_name_value(instruction_output, "NewXADPID", newXADPIDString);
			testLog.add_name_value(instruction_output, "OldXADPID", oldXADPIDString);
			testLog.add_name_value(instruction_output, "NewLocalPID", newLocalPIDString);
			testLog.add_name_value(instruction_output, "OldLocalPID", oldLocalPIDString);

			if (testConfig == null)
				throw new Exception("Internal Error - TestConfig not initialized");
			if (testConfig.site == null)
				throw new Exception("Internal Error - TestConfig.site not initialized");

			String server = testConfig.site.pifHost;
			String port = testConfig.site.pifPort;
			s_ctx.addDetail("server", server);
			s_ctx.addDetail("port", port);

			if (server == null || "".equals(server))
				throw new Exception("Site " + testConfig.site.getName() + " has no Patient Move host configured");
			if (port == null || "".equals(port))
				throw new Exception("Site " + testConfig.site.getName() + " has no Patient Move Feed port configured");

			new A43Sender().send(server, Integer.parseInt(port), newXADPIDString, oldXADPIDString, newLocalPIDString,
					oldLocalPIDString);

		} catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
			logger.severe(ExceptionUtil.exception_details(e));
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
		return "pA43";
	}

	String transactionName() {
		return "pA43";
	}

}
