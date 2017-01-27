package gov.nist.toolkit.session.server.testlog;

import gov.nist.toolkit.actortransaction.shared.SimId;
import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.StepOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.BasicTestOverview;
import gov.nist.toolkit.testengine.engine.SimulatorTransaction;
import gov.nist.toolkit.testengine.engine.TransactionStatus;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Find and translate sim log entry into BasicTestOverview (subset of TestOverviewDTO)
 */
public class SimLogBuilder {

    class ErrorStatus {
        TransactionStatus s;
        List<String> errors = new ArrayList<String>();
    }

    /**
     * See SimulatorTransaction#get() for details of calling parameters.
     * @param simId
     * @param transactionType
     * @param pid
     * @param timeStamp
     * @return
     * @throws XdsInternalException
     */
    BasicTestOverview get(SimId simId, TransactionType transactionType, Pid pid, Date timeStamp) throws XdsInternalException {
        SimulatorTransaction transLog = SimulatorTransaction.get(simId, transactionType, pid.asString(), timeStamp);

        StepOverviewDTO stepOverview = new StepOverviewDTO();
        stepOverview.setTransaction(transactionType.getName());
        ErrorStatus errorStatus = parseResponse(transLog.getResponseBody());
        for (String error : errorStatus.errors) {
            stepOverview.addError(error);
        }

        SectionOverviewDTO sectionOverview = new SectionOverviewDTO();
        sectionOverview.setRun(true);

        sectionOverview.setSite(simId.getSiteSpec().getName());
        sectionOverview.setName("none");
        sectionOverview.addStep(stepOverview.getName(), stepOverview);
        sectionOverview.setPass(!errorStatus.s.isSuccess());
        sectionOverview.setHl7Time(new Hl7Date().from(transLog.getTimeStamp()));


        TestOverviewDTO testOverview = new TestOverviewDTO();

        testOverview.setName("none");
        testOverview.addSection(sectionOverview);

        return testOverview;
    }

    private static final QName CODE_CONTEXT_QNAME = new QName("codeContext");
    private static final QName STATUS_QNAME = new QName("status");

    private ErrorStatus parseResponse(String soapbody)  {
        ErrorStatus status = new ErrorStatus();
        try {
            OMElement soapbodyEle = Util.parse_xml(soapbody);
            OMElement adhocQueryResponse = XmlUtil.firstDecendentWithLocalName(soapbodyEle, "AdhocQueryResponse");
            status.s = new TransactionStatus(adhocQueryResponse.getAttributeValue(STATUS_QNAME));

            List<OMElement> registryErrors = XmlUtil.decendentsWithLocalName(adhocQueryResponse, "RegistryError");
            for (OMElement registryError : registryErrors) {
                status.errors.add(registryError.getAttributeValue(CODE_CONTEXT_QNAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

}
