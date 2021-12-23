package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.engine.PlanContext;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import java.util.logging.Logger;

import java.util.Map;

/**
 * This "gathers" inputs from the Conformance test UI and represents them as Reports
 */
public class GatherTransaction extends BasicTransaction {
    private final static Logger logger = Logger.getLogger(GatherTransaction.class.getName());

    public GatherTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
    }

    @Override
    protected void run(OMElement request) throws Exception {
        PlanContext planContext = s_ctx.getPlan();
        Map<String, String> params = planContext.getExtraLinkage();
        for (String name : params.keySet()) {
            ReportDTO report = new ReportDTO();
            report.setName(name);
            report.setValue(params.get(name));
            getReportManager().addReport(report);
        }
        reportManagerPostRun();
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {

    }

    @Override
    protected String getRequestAction() {
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        return "Gather";
    }
}
