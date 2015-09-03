package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.adt.A01Sender;
import gov.nist.toolkit.testengine.engine.PatientIdAllocator;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class PatientIdentityFeedTransaction extends BasicTransaction {
	private final static Logger logger = Logger.getLogger(PatientIdentityFeedTransaction.class);

	public PatientIdentityFeedTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	String transactionName() {
		return "pif";
	}

	public void run(OMElement request) 
	throws XdsException {

		try {
			String pid = PatientIdAllocator.getNew();
			transactionSettings.patientId = pid;
			testLog.add_name_value(instruction_output, "PatientId", pid);
			String server = testConfig.site.pifHost;
			String port = testConfig.site.pifPort;

			A01Sender.send(server, Integer.parseInt(port), pid);

		}
		catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
		}
	}
	

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		parseBasicInstruction(part);
	}

	@Override
	protected String getRequestAction() {
		return null;
	}

	protected String getBasicTransactionName() {
		return "pif";
	}


}
