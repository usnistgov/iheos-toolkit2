package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction;
import gov.nist.toolkit.testengine.engine.ILogReporting;
import gov.nist.toolkit.testengine.engine.ILogger;
import gov.nist.toolkit.testengine.engine.SimReference;
import gov.nist.toolkit.testengine.engine.SimulatorTransaction;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testengine.engine.TestLogFactory;
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction;
import gov.nist.toolkit.testengine.engine.validations.ValidationPluginRunner;
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

public class NullTransaction extends BasicTransaction {
	ILogReporting logReport;
	private List <String> errs;

	public NullTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		defaultEndpointProcessing = false;
		noMetadataProcessing = true;
	}

	// for IT testing only
	// where ILogReporting is adequate
	public NullTransaction(ILogReporting logReport, OMElement instruction, OMElement instruction_output) {
		super(null, instruction, instruction_output);
		this.logReport = logReport;
		defaultEndpointProcessing = false;
		parse_metadata = false;
		noMetadataProcessing = true;
	}


	protected String getBasicTransactionName() {
		return "Null";
	}

	protected String getRequestAction() {
		return null;
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		parseBasicInstruction(part);
	}

	protected void run(OMElement request) throws XdsException {
        reportManagerPreRun(request);
	}


	@Override
	public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output) throws XdsInternalException {
		List<String> errs = new ArrayList<>();
		try {
			SimReference simReference = getSimReference(errs, a);
			if (a.hasValidations()) {
			    if (TestKit.PluginType.FHIR_ASSERTION.equals(a.validations.getPluginType())) {
					List<FhirSimulatorTransaction> transactions = new FhirSimulatorTransaction(simReference.getSimId(),simReference.getTransactionType()).get();
					List<ValidaterResult> passing = new ValidationPluginRunner(getStepContext())
							.run(new SimDbTransactionInstanceBuilder(new SimDb(simReference.getSimId()),null)
									, simReference
									, a
									, assertion_output
									, transactions);
					if (passing.isEmpty())
						errs.add("No " + simReference.getTransactionType() + " Transactions match requirements");
				} else if (TestKit.PluginType.XDS_ASSERTION.equals(a.validations.getPluginType())) {
					SoapSimulatorTransaction sst = new SoapSimulatorTransaction(simReference);
					List<SoapSimulatorTransaction> transactions = sst.get();
					List<ValidaterResult> passing = new ValidationPluginRunner<SoapSimulatorTransaction>(getStepContext())
							.run(new SimDbTransactionInstanceBuilder(new SimDb(simReference.getSimId()),null)
									, simReference
									, a
									, assertion_output
									, transactions);

					if (passing.isEmpty()) {
						if (transactions!=null) {
							if (transactions.size()==1 && transactions.get(0).getSimDbEvent()!=null) {
								errs.add(String.format("Found 1 simEventId %s", transactions.get(0).getSimDbEvent().getEventId()));
							} else {
								errs.add(String.format("Found %s " , transactions.size() + " transactions"));
							}
						}
						errs.add("No SOAP " + simReference.getTransactionType() + " Transactions match requirements");
					}
				}
			} else
				throw new XdsInternalException("NullTransaction: Unknown Assertion clause with not Assert statements");
		} catch (Exception ex) {
			errs.add(ex.getMessage());
		}
		if (!errs.isEmpty()) {
			ILogger testLogger = new TestLogFactory().getLogger();
			testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
			for (String err : errs)
				this.fail(err);
		}
	}

	public ILogReporting getLogReport() {
		return logReport;
	}

	public List<String> getErrs() {
		return errs;
	}
}
