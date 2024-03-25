package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.server.PropertyManager;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.ILogger;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testengine.engine.TestLogFactory;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/*
 * Handles Validations for Edge Server Transactions
 */
public class EdgeServerTransaction  extends BasicTransaction {

    private OMElement step;
    private PropertyManager pMgr;
    private Logger log = Logger.getLogger(EdgeServerTransaction.class.getName());


    /**
     * @param s_ctx StepContext instance
     * @param step {@code <TestStep>} element from the textplan.xml
     * @param instruction {@code <EdgeServerTransaction>} element from the
     * testplan.xml
     * @param instruction_output {@code <ImgDetailTransaction>} element from the
     * log.xml file.
     */
    public EdgeServerTransaction(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
        pMgr = Installation.instance().propertyServiceManager().getPropertyManager();
        this.step = step;
    }


    @Override
    protected void run(OMElement request) throws Exception {
        return;
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        parseBasicInstruction(part);
    }
    @Override
    protected String getRequestAction() {
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        return "EdgeServer";
    }

    private List<String> errs;

    @Override
    public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output)
            throws XdsInternalException {
        XdsInternalException xdsInternalException = null;
        errs = new ArrayList<>();
        try {
            switch(a.process) {
                case "validatePatientFeed":
                    prsValidatePatientFeed(engine, a, assertion_output);
                    break;
                default:
                    throw new XdsInternalException("ImgDetailTransaction: Unknown assertion.process " + a.process);
            }

    } catch (XdsInternalException ie) {
        xdsInternalException = ie;
        errs.add(ie.getMessage());
    }
      if (errs.isEmpty() == false) {
        ILogger testLogger = new TestLogFactory().getLogger();
        testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
        for (String err : errs)
            s_ctx.fail(err);
    }
      if (xdsInternalException != null) throw xdsInternalException;
    }

    private void prsValidatePatientFeed(AssertionEngine engine, Assertion a, OMElement assertion_output)
            throws XdsInternalException {

    }

}
