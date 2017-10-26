package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyManager;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.*;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class XMLValidation extends BasicTransaction {

    private static Pattern sha256 = Pattern.compile("^[0-9a-fA-F]{64}$");
    private static SimpleDateFormat[] validDateTimeFormats;
    static {
        validDateTimeFormats = new SimpleDateFormat[] {
                new SimpleDateFormat("yyyyMMdd"),
                new SimpleDateFormat("yyyyMMddHHmm"),
                new SimpleDateFormat("yyyyMMddHHmmss"),
        };
    }

    private OMElement step;
    private PropertyManager pMgr;
    private Logger log = Logger.getLogger(Hl7v2Validation.class);
    private OMElement root;
    private String rootXpath;
    private List<String> details;
    private List<String> errs;
    private StrSubstitutor paramsSubstitutor;
    private StrSubstitutor reportParamsSubstitutor;

    private static Map<String, String> defaultNamespaces = new HashMap<>();
    static {
        defaultNamespaces.put("xdsb","urn:ihe:iti:xds-b:2007");
        defaultNamespaces.put("xdsiB","urn:ihe:rad:xdsi-b:2009");
        defaultNamespaces.put("lcm","urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0");
        defaultNamespaces.put("rim","urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
    }
    private Map<String,String> namespaces = new HashMap<>();

    /**
     * @param s_ctx StepContext instance
     * @param step {@code <TestStep>} element from the textplan.xml
     * @param instruction {@code <XmlValidation>} element from the testplan.xml
     * @param instruction_output {@code <ImgDetailTransaction>} element from the log.xml file.
     */
    public XMLValidation(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
        pMgr = Installation.instance().propertyServiceManager().getPropertyManager();
        this.step = step;
        rootXpath = StringUtils.trimToEmpty(instruction.getAttributeValue(new QName("rootXpath")));
        namespaces.putAll(defaultNamespaces);
    }

    @Override
    protected void run(OMElement request) throws Exception {
        Map<String, String> pars = new HashMap<>();
        for (UseReport useReport : useReportManager.get()) {
            pars.put(useReport.useAs, useReport.value);
        }
        reportParamsSubstitutor = new StrSubstitutor(pars, "", "");
        return;
    }


    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        switch (part.getLocalName()) {
            // <SimTransaction id="rep_reg" transaction="prb"/>
            case "SimTransaction":
                SimulatorTransaction simTran = getSimulatorTransaction(part);
                String rBody = simTran.getRequestBody();
                try {
                    root = AXIOMUtil.stringToOM(rBody);
                } catch (XMLStreamException e) {
                    throw new XdsInternalException(e.getMessage());
                }
                if (rootXpath.isEmpty() == false) {
                    try {
                        AXIOMXPath xpath = new AXIOMXPath(rootXpath);
                        List<Object> results = (List<Object>) xpath.evaluate(root);
                        root = (OMElement) results.get(0);
                    } catch (JaxenException e) {
                        throw new XdsInternalException(e.getMessage());
                    }
                }
                break;
            // Each line of text in the <Parameters> element is considered to be a
            // parameter of the form name=value and is added to the parameter list.
            // xpath expressions will insert parameter values for ${name} entries.
            // see org.apache.commons.text.StrSubstitutor for details.
            case "Parameters":
                Map<String, String> pars = new HashMap<>();
                for (String line : part.getText().split("\\r?\\n")) {
                    String[] tokens = line.split("=", 2);
                    if (tokens.length == 2)
                        pars.put(tokens[0].trim(), tokens[1].trim());
                }
                paramsSubstitutor = new StrSubstitutor(pars);
                break;
            // Each line of text in the <NameSpaces> element is considered to be a
            // namespace of the form prefix=uri and is added to the namespace list.
            // namespaces are added to the xpath expression. The following namespaces
            // are provided by default:
            //    lcm=urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0
            //    rim=urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0
            //    xdsb=urn:ihe:iti:xds-b:2007
            //    xdsiB=urn:ihe:rad:xdsi-b:2009
            // and may be added to or overidden in the testplan.xml
            case "NameSpaces":
                for (String line : part.getText().split("\\r?\\n")) {
                    String[] tokens = line.split("=", 2);
                    if (tokens.length == 2)
                        namespaces.put(tokens[0].trim(), tokens[1].trim());
                }
                break;
            default:
                parseBasicInstruction(part);
        }
        return;
    }

    @Override
    protected String getRequestAction() {
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        return "XMLValidation";
    }

    @Override
    public AssertionEngine runAssertionEngine(OMElement step_output, ErrorReportingInterface eri,
                                              OMElement assertion_output) throws XdsInternalException {
        AssertionEngine engine = super.runAssertionEngine(step_output, eri, assertion_output);
        afterLastAssertion(engine);
        return engine;
    }

    @Override
    public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output)
            throws XdsInternalException {
        XdsInternalException xdsInternalException = null;
        String forEach = StringUtils.trimToNull(a.assertElement.getAttributeValue(new QName("forEach")));

        if (forEach == null) {
            // This is for single Asserts, that is, not having a forEach attributed
            String value = StringUtils.trimToEmpty(a.assertElement.getAttributeValue(new QName("value")));
            // This allows value attribute to contain UseReport variables.
            value = reportParamsSubstitutor.replace(value);
            // This resolves <Parameters> element references in xpath
            String xpath = paramsSubstitutor.replace(a.xpath.trim());

            prsExpression(a.id, xpath, a.process, value);
            for (String detail : details) engine.addDetail(detail);
            if (errs.isEmpty() == false) {
                ILogger testLogger = new TestLogFactory().getLogger();
                testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
                for (String err : errs)
                    s_ctx.fail(err);
            }
            if (xdsInternalException != null) throw xdsInternalException;
            return;
        }
        // this is for repeated asserts, having a forEach attribute
        forEach = paramsSubstitutor.replace(forEach);
        String value = StringUtils.trimToEmpty(a.assertElement.getAttributeValue(new QName("value")));
        // This allows value attribute to contain UseReport variables.
        value = reportParamsSubstitutor.replace(value);
        // This resolves <Parameters> element references in xpath
        String xpath = paramsSubstitutor.replace(a.xpath.trim());
        String instanceId = StringUtils.trimToNull(a.assertElement.getAttributeValue(new QName("instanceId")));
        if (instanceId == null) throw new XdsInternalException("Assert " + a.id + " has forEach, must have instanceId");
        instanceId = paramsSubstitutor.replace(instanceId);

        // Pass through forEach for this Assert
        Integer i = 1;
        do {
            // build substitutor for counter i
            Map<String, String> imap = new HashMap<>();
            imap.put("i", i.toString());
            StrSubstitutor iSubstitutor = new StrSubstitutor(imap);

            // substitute counter and pull forEach value, if no result, we're done
            String iForEach = iSubstitutor.replace(forEach);
            List<Object> node = getXPath(root, iForEach);
            if (node == null || node.isEmpty()) break;

            // substitute counter and pull instance id
            String iInstanceId = iSubstitutor.replace(instanceId);
            String resultInstanceId = getXPathString(root, iInstanceId);
            if (resultInstanceId.isEmpty()) resultInstanceId = "no id";

            // substitute counter
            String iXpath = iSubstitutor.replace(xpath);

            // run assertion for this instance
            prsExpression(a.id, iXpath, a.process, value);

            // store results in Grouper
            postGroup(forEach, resultInstanceId, a.id, details);

        } while (i++ > 0);

        if (errs.isEmpty() == false) {
            ILogger testLogger = new TestLogFactory().getLogger();
            testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
            for (String err : errs)
                s_ctx.fail(err);
        }
        if (xdsInternalException != null) throw xdsInternalException;

    } // EO processAssertion method

    public void afterLastAssertion(AssertionEngine engine) throws XdsInternalException {

        // Pass Groups in rational order for detail listing.
        for (Map.Entry<String, Grouper> grouperEntry : groups.entrySet()) {
            Grouper group = grouperEntry.getValue();
            if (group.results.isEmpty()) continue;
            engine.addDetail("For instances of: " + group.group);
            Iterator<String> instanceIterator = group.instances.iterator();
            while (instanceIterator.hasNext()) {
                String nxtInstanceId = instanceIterator.next();
                errs = new ArrayList<>();
                boolean instanceIdAdded = false;
                Iterator<String> assertIterator = group.asserts.iterator();
                while (assertIterator.hasNext()) {
                    String nxtAssertId = assertIterator.next();
                    List<String> rstlDtls = group.results.get(new Pair<String, String>(nxtInstanceId, nxtAssertId));
                    if (rstlDtls == null || rstlDtls.isEmpty()) continue;
                    if (!instanceIdAdded){
                        engine.addDetail("  for instance: " + nxtInstanceId);
                        instanceIdAdded = true;
                    }
                    for (String rstlDtl : rstlDtls) engine.addDetail("    " + rstlDtl);
                } // EO assertions for instance
            } // EO instances in group
        } // EO group
    }

    /**
     * Evaluate assertion, params are attributes of Assert element.
     * Parameter substitution, UseReport substitution (and forEach instance
     * substitution, if applicable) are already done.
     * On return, {@link #details} and {@link #errs} contain results,
     * which have NOT been added to the test log.
     * If errs is not empty, the assertion failed.
     * @param id
     * @param xpath
     * @param process
     * @param value
     * @throws XdsInternalException on error, usually invalid xpath expression
     */
    private void prsExpression(String id, String xpath, String process, String value) throws XdsInternalException {

        details = new ArrayList<>();
        errs = new ArrayList<>();

        String result = getXPathString(root, xpath);

        switch (process.substring(0,1).toLowerCase()) {

            case "m": // matches regex in value
                String mmsg = id +  " Matches " + value + " found: " + result;
                if (result.matches(value)) details.add(mmsg);
                else errs.add(mmsg);
                break;

            case "b": // Begins with value
                String bmsg = id + " Begins with " + value + " found: " + result;
                if (result.startsWith(value)) details.add(bmsg);
                else errs.add(bmsg);
                break;

            case "e": // Equals
            case "=":
                String emsg = id +  " Equals " + value + " found: " + result;
                // Special cases based on types in value attribute
                switch (value.toUpperCase()) {

                    case "UUID":
                        if (result.toLowerCase().matches("^urn:uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) details.add(emsg);
                        else errs.add(emsg);
                        break;

                    case "OID": // Under the IANA enterprise tree
                        if (result.matches("^1\\.3\\.6\\.1\\.4(\\.([1-9][0-9]*|0))*$")) details.add(emsg);
                        else errs.add(emsg);
                        break;

                    case "DATETIME":
                        boolean hasValidDateBeenFound = false;
                        for (SimpleDateFormat df : validDateTimeFormats) {
                            if (df.parse(result, new ParsePosition(0)) == null) continue;
                            hasValidDateBeenFound = true;
                            break;
                        }
                        if (hasValidDateBeenFound) details.add(emsg);
                        else errs.add(emsg);
                        break;
                    // no special case, result must equal value
                    default:
                        if (result.equals(value)) details.add(emsg);
                        else errs.add(emsg);
                }
                break;

            case "r": // Required
            default:
                String rmsg = id +  " Required, found: " + result;
                if (StringUtils.isNotEmpty(result) ) details.add(rmsg);
                else errs.add(rmsg);
        }

    }

    /**
     * Instantiates a {@link SimulatorTransaction} object for a specific simulator transaction.<br/>
     * The SimulatorTransaction element MUST have an id attribute, whose value is the simulator id
     * (not including the test session, which is added) and a transaction element, whose value
     * is the transaction type code. It MAY have a pid attribute, which, if present, must be
     * the complete PID-3 representation of the patient id.<br/>
     * This method is for SOAP transactions only. For Hl7v2 transactions, see {@link SimulatorTransactionHl7v2}.
     * @param part A SimulatorTransaction element from the testplan.xml. For example: {@code
     *             <SimTransaction id="rep_reg" transaction="prb" pid="BR-549^^^$1.3.4.1.4.14356.9.1.1$ISO"/>}
     * @return A fully populated SimulatorTransaction instance for the most recent transaction of
     * the given type processed by the simulator. If the pid attribute is present, only transactions
     * for that patient are considered.
     * @throws XdsInternalException on error, usually caused by an invalid attribute value
     */
    private SimulatorTransaction getSimulatorTransaction(OMElement part) throws XdsInternalException {
        try {
            String id = part.getAttributeValue(new QName("id"));
            String trans = part.getAttributeValue(new QName("transaction"));
            String pid = part.getAttributeValue(new QName("pid"));
            TransactionType tType = TransactionType.find(trans);
            if (tType == null) throw new XdsInternalException(part.toString() + " invalid transaction");
            ActorType aType = ActorType.getActorType(tType);
            TestInstance ti = testConfig.testInstance;
            SimId simId = new SimId(ti.getUser(), id, aType.getShortName());
            return SimulatorTransaction.get(simId, tType, pid, null);
        } catch (XdsInternalException ie) {
            errs.add("Error loading simulator transaction" + ie.getMessage());
            throw ie;
        }
    }

    /**
     * Get string result of xpath *** not for element results ***
     * @param object element to run xpath on.
     * @param xpath String xpath expression
     * @return String value, trimmed to empty string; never null
     * @throws XdsInternalException on invalid xpath expression
     */
    private String getXPathString(OMElement object, String xpath) throws XdsInternalException {
        try {
            AXIOMXPath axiomxPath = new AXIOMXPath(xpath);
            for (String key : namespaces.keySet()) {
                axiomxPath.addNamespace(key, namespaces.get(key));
            }
            return StringUtils.trimToEmpty(axiomxPath.stringValueOf(object));
        } catch (JaxenException je) {
            throw new XdsInternalException(je.getMessage());
        }
    }

    /**
     * Get object array result of xpath
     * @param object element to run xpath on.
     * @param xpath String xpath expression
     * @return List of objects
     * @throws XdsInternalException on invalid xpath expression
     */
    private List<Object> getXPath(OMElement object, String xpath) throws XdsInternalException {
        try {
            AXIOMXPath axiomxPath = new AXIOMXPath(xpath);
            for (String key : namespaces.keySet()) {
                axiomxPath.addNamespace(key, namespaces.get(key));
            }
            return (List<Object>) axiomxPath.evaluate(object);
        } catch (JaxenException je) {
            throw new XdsInternalException(je.getMessage());
        }
    }

    /*
     * Allows collecting of results for Assert elements with forEach attribute,
     * for later display grouped by each matching instance.
     */
    private static Map<String, Grouper> groups = new LinkedHashMap<>();
    public static class Grouper {

        public String group;
        public Set<String> instances = new LinkedHashSet<>();
        public Set<String> asserts = new LinkedHashSet<>();
        // key is instance, assert. content details list
        public Map<Pair<String, String>,List<String>> results = new HashMap<>();

        public Grouper(String group) {
            this.group = group;
        }
    }
    public static Grouper group(String forEach, String instanceId, String assertId) {
        Grouper group = groups.get(forEach);
        if (group == null) {
            group = new Grouper(forEach);
            groups.put(forEach, group);
        }
        if (instanceId != null) group.instances.add(instanceId);
        if (assertId != null) group.asserts.add(assertId);
        return group;
    }
    public static Grouper postGroup(String forEach, String instanceId, String assertId, List<String> detail) {
        Grouper group = group(forEach, instanceId, assertId);
        group.results.put(new Pair<String, String>(instanceId,assertId),detail);
        return group;
    }
}