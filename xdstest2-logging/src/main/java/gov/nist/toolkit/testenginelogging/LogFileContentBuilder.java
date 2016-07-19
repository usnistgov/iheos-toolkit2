package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.SectionGoalsDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gov.nist.toolkit.adt.AdtRecordBean.firstNChars;

/**
 *
 */
public class LogFileContentBuilder {
    LogFileContentDTO c = new LogFileContentDTO();
    OMElement log;

    public LogFileContentDTO build(File logfile) throws Exception {
        return build(logfile, false);
    }

    public LogFileContentDTO build(File logfile, boolean incompleteOk) throws Exception {
//        c.setInputFile(logfile);
        log = Util.parse_xml(logfile);
        init(false);
        return c;
    }

    public LogFileContentDTO build(OMElement testresults) throws Exception {
        return build(testresults, false);
    }

    public LogFileContentDTO build(OMElement testresults, boolean incompleteOk) throws Exception {
        log = Util.parse_xml(testresults);
        init(false);
        return c;
    }

    private void init(boolean incompleteOk) throws NotALogFileException, Exception {
        try {
            parseStatus();
        } catch (Exception e) {
            if ( ! incompleteOk )
                throw new NotALogFileException(e.getMessage() + " log is [" + firstNChars(log.toString(), 20) + "]");
        }
        parseTest();
        parseTestSteps();
        calcGoals();
        parseFatalError();
        parseReports();
    }

    private List<OMElement> testStepsEle = new ArrayList<>();

    private void parseReports() throws XdsInternalException {
        for (int i=0; i<c.getSteps().size(); i++) {
            OMElement stepEle = testStepsEle.get(i);
            OMElement reportsEle = getRawReports(stepEle);
            if (reportsEle != null) {
                List<ReportDTO> reportDTOs = ReportBuilder.parseReports(reportsEle);
                c.getReportDTOs().addAll(reportDTOs);
            }
        }
    }

    private OMElement getRawReports(OMElement ele) {
        try {
            return XmlUtil.firstDecendentWithLocalName(ele, "Reports");
        } catch (Exception e) {
            return null;
        }
    }

    private void parseFatalError() {
        OMElement ele = XmlUtil.firstChildWithLocalName(log, "FatalError");
        if (ele == null)
            return;
        c.setFatalError(ele.getText());
    }

    private void calcGoals() {
        SectionGoalsDTO goals = new SectionGoalsDTO(c.getTestAttribute());

        for (String stepName : c.getStepMap().keySet()) {
            goals.stepGoalDTOs.add(c.getStepMap().get(stepName).getStepGoalsDTO());
        }

        c.setSectionGoalsDTO(goals);
    }

    private void parseTestSteps() throws Exception {
//        steps = new ArrayList<TestStepLogContentDTO>();
//        stepMap = new HashMap<String, TestStepLogContentDTO>();
        testStepsEle = XmlUtil.childrenWithLocalName(log, "TestStep");
        for (OMElement step : testStepsEle) {
            TestStepLogContentDTO stepLog = new TestStepLogContentBuilder(step).build();
            c.getSteps().add(stepLog);
            c.getStepMap().put(stepLog.getId(), stepLog);
        }
    }


    private void parseTest() {
        try {
            c.setTestAttribute(XmlUtil.firstChildWithLocalName(log, "Test").getText());
            if (c.getTestAttribute() == null) {
                c.setTest(null);
                c.setSection(null);
            } else {
                String[] parts = c.getTestAttribute().split("/");
                if (parts.length == 2) {
                    c.setTest(parts[0]);
                    c.setSection(parts[1]);
                } else {
                    c.setTest(c.getTestAttribute());
                    c.setSection(null);
                }
            }
        } catch (Exception e) {
            c.setTestAttribute("Unknown");
        }
    }


    private void parseStatus() throws Exception {
        String stat = log.getAttributeValue(new QName("status"));
        if (stat == null)
            throw new Exception("Log file status not available. Log element is " + log.getLocalName());
        if (stat.equals("Pass"))
            c.setSuccess(true);
        else
            c.setSuccess(false);
    }


}
