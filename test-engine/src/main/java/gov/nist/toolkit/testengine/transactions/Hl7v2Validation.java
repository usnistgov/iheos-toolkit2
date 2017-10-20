package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyManager;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.*;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Hl7v2Validation extends BasicTransaction {

    private static Pattern sha256 = Pattern.compile("^[0-9a-fA-F]{64}$");

    private OMElement step;
    private PropertyManager pMgr;
    private Logger log = Logger.getLogger(Hl7v2Validation.class);
    private OMElement testMsg;
    private OMElement testRequest;
    private OMElement testResponse;

    /**
     * @param s_ctx StepContext instance
     * @param step {@code <TestStep>} element from the textplan.xml
     * @param instruction {@code <Hl7v2Validation>} element from the testplan.xml
     * @param instruction_output {@code <Hl7v2Validation>} element from the log.xml file.
     */
    public Hl7v2Validation(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
        pMgr = Installation.instance().propertyServiceManager().getPropertyManager();
        this.step = step;
    }

    @Override
    protected void run(OMElement request) throws Exception {

        testLog.add_name_value(instruction_output, "Request", Util.deep_copy(testRequest));
        if (testResponse != null )
            testLog.add_name_value(instruction_output, "Response", Util.deep_copy(testResponse));

        return;

    }

    // <SimHl7v2Transaction id="rep_reg" transaction="ADT^A01"/>
    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        if (part.getLocalName().equalsIgnoreCase("SimHl7v2Transaction")) {
            String id = part.getAttributeValue(new QName("id"));
            String transaction = part.getAttributeValue(new QName("transaction"));
            SimId simId = new SimId(testConfig.testInstance.getUser(), id);
            SimulatorTransactionHl7v2 trans = null;
            boolean request = !StringUtils.startsWithIgnoreCase(part.getAttributeValue(new QName("msg")),"RES");
            try {
                trans = new SimulatorTransactionHl7v2(simId, transaction);
                testRequest = XmlUtil.strToOM(trans.getMsg("REQUEST.XML"));
                testResponse = XmlUtil.strToOM(trans.getMsg("RESPONSE.XML"));
                if (request) testMsg = testRequest;
                else testMsg = testResponse;
            } catch (Exception e) {
                throw new XdsInternalException(e.getMessage());
            }
            return;
        }
        parseBasicInstruction(part);
    }

    @Override
    protected String getRequestAction() {
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        return "Hl7v2Validation";
    }

    private List<String> errs;

    @Override
    public AssertionEngine runAssertionEngine(OMElement step_output, ErrorReportingInterface eri, OMElement assertion_output) throws XdsInternalException {
        AssertionEngine engine = super.runAssertionEngine(step_output, eri, assertion_output);
        reportManagerPostRun();
        return engine;
    }


    @Override
    public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output)
            throws XdsInternalException {
        XdsInternalException xdsInternalException = null;
        errs = new ArrayList<>();
        String result = "";
        String reportAs = StringUtils.trimToEmpty(a.assertElement.getAttributeValue(new QName("reportAs")));

        String cmd = a.process.substring(0, 1).toLowerCase();
        if (cmd.equals("n") == false) {
            result = getLocationValue(a.id);
            if (reportAs.length() > 0)
                reportManager.addReport(new ReportDTO(reportAs, result));
            if (result == null) return;
        }

        switchLoop:
        switch (cmd) {

            case "n": // NoPHI all non-exempt fields must be empty
                String segName = StringUtils.substringBefore(a.id, " ");
                // exempt fields are in xpath as comma separated list
                String ok[] = a.xpath.split(",");
                Set<Integer> exempt = new HashSet<>();
                for (int i=0; i<ok.length; i++) {
                    exempt.add(Integer.parseInt(ok[i]));
                }
                // get segment, then list of its fields.
                OMElement segEle = XmlUtil.firstChildWithLocalName(testMsg, segName);
                List<OMElement> fieldEles = XmlUtil.childrenWithLocalName(segEle,"field");
                // pass fields, collecting text
                String txt = "";
                for (int i=0; i<fieldEles.size(); i++) {
                    // skip any in exempt list
                    if (exempt.contains(i+1)) continue;
                    OMElement fieldEle = fieldEles.get(i);
                    txt += StringUtils.trimToEmpty(fieldEle.getText());
                }
                String nmsg = a.id + " No PHI";
                if (a.xpath.length() > 0) nmsg += " exept in " + a.xpath;
                nmsg += " found ";
                if (txt.isEmpty()) engine.addDetail(nmsg + "none");
                else errs.add(nmsg + txt);
                break;

            case "o": // OneOf
                String [] opts = a.xpath.split(",");
                String m = a.id +  " Oneof " + a.xpath + " found: " + result;
                for (String opt : opts) {
                    opt = opt.trim();
                    if (result.equals(opt)) {
                        engine.addDetail(m);
                        break switchLoop;
                    }
                }
                errs.add(m);
                break;

            case "s": // SHA256 checksum
                String o = a.id = " SHA256 chksum, found: " + result;
                if (sha256.matcher(result).matches()) engine.addDetail(o);
                else errs.add(o);
                break;

            case "e": // Equals
            case "=":
                String n = a.id +  " Equals " + a.xpath + " found: " + result;
                if (result.equals(a.xpath.trim())) engine.addDetail(n);
                else errs.add(n);
                break;

            case "r": // Required
            default:
                String rmsg = a.id +  " Required, found: " + result;
                if (StringUtils.isNotEmpty(result) ) engine.addDetail(rmsg);
                else errs.add(rmsg);
        }

        if (errs.isEmpty() == false) {
            ILogger testLogger = new TestLogFactory().getLogger();
            testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
            for (String err : errs)
                s_ctx.fail(err);
        }
        if (xdsInternalException != null) throw xdsInternalException;
    } // EO processAssertion method



    private OMElement getTestTrans(Assertion a, boolean request) throws XdsInternalException {
        String piece = (request? "REQUEST.XML": "RESPONSE.XML");
        // Get response from simulator transaction
        OMElement simTransactionElement = XmlUtil.firstChildWithLocalName(a.assertElement, "SimHl7v2Transaction");
        if (simTransactionElement != null) {
            String id = simTransactionElement.getAttributeValue(new QName("id"));
            String trans = simTransactionElement.getAttributeValue(new QName("transaction"));
            TestInstance ti = testConfig.testInstance;
            SimId simId = new SimId(ti.getUser(), id);
            try {
                SimulatorTransactionHl7v2 simTran = new SimulatorTransactionHl7v2(simId, trans);
                return AXIOMUtil.stringToOM(simTran.getMsg(piece));
            } catch (Exception e) {
                throw new XdsInternalException(e.getMessage());
            }
        }
        return null;
    }

    private String getLocationValue(String hl7v2Location) throws XdsInternalException {
        String loc = StringUtils.substringBefore(hl7v2Location.trim(), ":");
        try {
            AXIOMXPath xpath = new AXIOMXPath(locationToXpath(loc));
            String result =  xpath.stringValueOf(testMsg);
            StringUtils.trimToEmpty(result);
            return result;
        } catch (JaxenException je) {
            errs.add(je.getMessage());
        }
        return null;
    }

    private String locationToXpath(String hl7v2Location) throws XdsInternalException {
        String s = hl7v2Location.trim();
        String xPath = "//hl7";
        String segment = StringUtils.substringBefore(s, " ");
        xPath += "/" + segment;
        s = StringUtils.substringAfter(s, " ");
        String flds[] = StringUtils.split(s, "-.");
        for (int i=0; i<flds.length; i++ ) {
            String fld = flds[i].trim();
            if (fld.isEmpty()) break;
            switch (i) {
                case 0:
                    xPath += "/field";
                    // because xml ignores first two fields of MSH segment
                    if (segment.equals("MSH")) {
                        Integer f = Integer.valueOf(fld) - 2;
                        fld = f.toString();
                    }
                    break;
                case 1:
                    xPath += "/component";
                    break;
                case 2:
                    xPath += "/subcomponent";
                    break;
                default:
                    throw new XdsInternalException("invalid hl7v2 location " + hl7v2Location);
            }
            xPath += "[" + fld + "]";
        }
        xPath += "/descendant::text()";
        return xPath;
    }

}