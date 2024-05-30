package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.testkitutilities.Sections;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmetadata.coding.AllCodes;
import gov.nist.toolkit.valregmetadata.coding.Code;
import gov.nist.toolkit.valregmetadata.coding.CodesFactory;
import gov.nist.toolkit.valregmetadata.coding.Uuid;
import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;
import gov.nist.toolkit.valregmsg.registry.SQCodedTerm;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.ParamParser;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.SqParams;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQueryGenerator;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class performs an update on the default testkit for it to match the configuration file
 * (affinity domain) of a given environment.
 *
 * @see #run(String, String, TestSession)
 * @see #getOutput()
 * @see #hasErrors()
 *
 * Created by oherrmann on 1/11/16.
 */
public class CodesUpdater {
    // just to keep track of the testkit structure more easily
    private static Sections MAIN_SECTIONS;
    private static final String[] SECTIONS = { "utilities", "examples","xcpd", "selftest"};
    private static final Logger LOGGER = Logger.getLogger(CodesUpdater.class.getName());

    private File testkit;
    private AllCodes allCodes=null;
    private List<String> filesTreated = new ArrayList<String>();
    private Map<String,Code> replacementMap= new HashMap<String,Code>();

    private String out=new String();
    private boolean error;

    private List<Code> overrideCodes;
    /**
     * goes by the index of overrideCodes. If index value is null or empty, all files are affected.
     */
    private List<String> targetFileByOverrideCodeIndex;
    private boolean backupReplacedFiles = true;
    private boolean dryRun = false;

    /**
     * Normal mode to be used with the run method (replaces bad a Code by picking a new Code by an arbitrary index)
     */
    public CodesUpdater() {
    }

    /**
     * Alternate mode to replacing Code with a supplied replacement
     * @param overrideCodes
     * @param replacementMap
     * @param backupReplacedFiles
     */
    public CodesUpdater(List<String> targetFileByOverrideCodeIndex, List<Code> overrideCodes, Map<String, Code> replacementMap, boolean backupReplacedFiles, boolean dryRun) {
        this.targetFileByOverrideCodeIndex = targetFileByOverrideCodeIndex;
        this.overrideCodes = overrideCodes;
        this.replacementMap = replacementMap;
        this.backupReplacedFiles = backupReplacedFiles;
        this.dryRun = dryRun;
    }

    public static void main(String[] args) {

        /*
        How to use code override feature
        codesXmlFileParent - set to a temporary working directory (outside of toolkit source) with a copy of the environment folder and the codes.xml file
        testkitSourceLocation - set to the toolkit testkit source directory, these tests will be copied to the codesXmlFileParent based location
        testSession - set to default test session
        overrideCodes - the bad code(s) to be replaced by the replacementMap (code.toString() is the map key)
        targetFileByOverrideCodeIndex - only apply the replacement for metadata files which match this string. Note the index matches the index of the overrideCode.
        Can be empty but not null, when overrideCodes is not null
        Example
        If index is 0, overrideCode[index] correlates to targetFileByOverrideCodeIndex[index]
        new HashMap<String, Code>()
        Key is the code.toString, value is the replacement Code.
         */

        // "C:\\Users\\skb1\\myprojects\\iheos-toolkit2\\xdstools2\\src\\main\\webapp\\toolkitx\\environment\\default";
        String codesXmlFileParent = "c:\\temp\\xdscodesupdatertool\\toolkitx\\environment\\default";
        String testkitSourceLocation = "C:\\Users\\skb1\\myprojects\\iheos-toolkit2\\xdstools2\\src\\main\\webapp\\toolkitx\\testkit";
        TestSession testSession = TestSession.DEFAULT_TEST_SESSION;

        List<Code> overrideCodes = Arrays.asList(
                new Code("urn:connectathon:bppc:foundational:policy", "1.3.6.1.4.1.21367.2017.3", "Foundational Connectathon Read-Access Policy")
        );
        List<String> targetFileByOverrideCodeIndex = Arrays.asList(
                "\\idc_init\\XDS-I-idc-a"
        );

        CodesUpdater tool = new CodesUpdater(
                targetFileByOverrideCodeIndex,
                overrideCodes,
                new HashMap<String, Code>() {{
                    /* key Is code + "^" + display + "^" + scheme; */
                    put(overrideCodes.get(0).toString(), new Code("CT", "1.2.840.10008.2.16.4", "Computed Tomography"));
                }},
                false, // false=no bak files
                false); // false=perform update, true=does not update update files
        tool.run(codesXmlFileParent, testkitSourceLocation, testSession);
    }

        /**
         * Reset the class variable to their initial state before the algorithm is run again.
         */
    void reset(){
        filesTreated=new ArrayList<String>();
        replacementMap=new HashMap<String,Code>();
        overrideCodes = null;
    }

    /**
     * This method scans the testkit for metadata files,
     * which will result in the update itself.
     */
    void execute() {
        error = false;
        for (Sections section:MAIN_SECTIONS.values()){
            executeSection(section.getSection());
        }
        for (int i = 0; i< SECTIONS.length; i++) {
            String section = SECTIONS[i];
            executeSection(section);
        }
    }

    void executeSection(String section){
        File sectionFile = new File(testkit + File.separator + section);

        try {
            exploreTests(sectionFile);
        } catch (Exception e) {
            error=true;
            out+="FAILURE.\n"+e.getMessage()+"\n";
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * This method explores a test repository structure.
     *
     * @param testFile path to the testkit folder which contains the test section to explore.
     * @throws XdsInternalException
     */
    void exploreTests(File testFile) throws XdsInternalException {
        File[] dirs = testFile.listFiles();
        if (dirs == null) {
            LOGGER.warning("No tests defined in " + testFile.toString());
            out+="No tests defined in " + testFile.toString() +"\n";
        }else {
            for (int i = 0; i < dirs.length; i++) {
                File testDir = dirs[i];
                if (".svn".equals(testDir.getName()))
                    continue;
                if (testDir.isDirectory()) {
                    exploreTests(testDir);
                } else {
                    if ("testplan.xml".equals(testDir.getName())) {
                        // read testplan.xml
                        String testplanContent = null;
                        try {
                            testplanContent = Io.stringFromFile(testDir);
                            OMElement testplanNode = Util.parse_xml(testplanContent);
                            // retrieve the TestStep nodes
                            Iterator<OMElement> steps = testplanNode.getChildrenWithName(new QName("TestStep"));
                            while (steps.hasNext()) {
                                // find transaction nodes among the nodes under exploration (Under a TestStep)
                                Iterator<OMElement> children = steps.next().getChildElements();
                                exploreChildren(children, testFile);
                            }
                        } catch (IOException e) {
                            LOGGER.warning("File not found : " + testFile.toString() +"\n"+e.getMessage());
                            out+="WARNING: No file found for " + testFile.toString() +"\n";
                        }
                    }
                }
            }
        }
    }

    /**
     *  This method explores all the nodes under a set of nodes node in parameter to find MetadataFile nodes.
     *
     *  @param children Set of nodes to explore
     *  @param testFile Path to the folder being explored
     */
    private void exploreChildren(Iterator<OMElement> children,File testFile) {
        while(children!=null && children.hasNext()){
            OMElement node=children.next();
            // find transaction nodes among the nodes under exploration
            OMElement transaction = null;
            if (node.getLocalName().matches(".*Transaction")){
                transaction=node;
            }
            if (transaction!=null) {
                // getRetrievedDocumentsModel metadata file(s) name
                OMElement metadataFile=transaction.getFirstChildWithName(new QName("MetadataFile"));
                if (metadataFile!=null) {
                    if (transaction.getLocalName().contains("StoredQueryTransaction")) {
                        processQueryFile(testFile, metadataFile.getText());
                    }else {
                        processMetadataFile(testFile,metadataFile.getText());
                    }
                }
            }
        }
    }

    /**
     * This method process a query file found in a testplan by looking for non conforming codes
     * and triggers the update when it find bad codes.
     * @param folderPath path to the directory of the query file.
     * @param fileName name of the query file to process.
     */
    private void processQueryFile(File folderPath, String fileName) {
        File file=null;
        try {
            String filePath = folderPath + File.separator + fileName;
            // check if the file being treated has been processed already
            if (!filesTreated.contains(fileName)) {
                filesTreated.add(filePath);
                file = new File(filePath);
                File errorsFile = new File(filePath.split(file.getName())[0] + "errors.properties");
                boolean testsFails = false;
                if (errorsFile.exists()) {
                    Properties errorsProperties = new Properties();
                    errorsProperties.load(FileUtils.openInputStream(errorsFile));
                    if (errorsProperties.get("not_in_CE_format") != null) {
                        testsFails = true;
                    }
                }
                // TODO check this error and testsFails thing + add comments
                if (file.exists()) {
                    if (!testsFails) {
                        // read the file
                        if (filePath.contains("11897") && filePath.contains("eventcode")) {
                            out += "Found it";
                        }
                        OMElement queryElement = Util.parse_xml(file);
                        ParamParser parser = new ParamParser();
                        SqParams params = parser.parse(queryElement, false);
                        List<SQCodeOr.CodeLet> badCodes = findNonConformingCodes(params);
                        if (!badCodes.isEmpty()) {
                            LOGGER.info(badCodes.size() + " codes to update in query file: " + filePath);
                            out += badCodes.size() + " codes to update in query file: " + filePath + "\n";
                            if (!dryRun && backupReplacedFiles) {
                                File backupFile = new File(file.toString() + ".bak");
                                if (!backupFile.exists()) {
                                    // backup the unmodified file before updating
                                    FileUtils.copyFile(file, backupFile);
                                }
                            }

                            // update bad codes
                            updateCode(badCodes);

                            if (! dryRun) {
                                // update the file itself
                                String returnType = queryElement.getFirstElement().getAttributeValue(new QName("returnType"));
                                Io.stringToFile(file, new OMFormatter(StoredQueryGenerator.generateQueryFile(returnType, params)).toString());
                            }
                        } else {
                            if (! dryRun && overrideCodes == null)
                                out += "No codes to update in query file: " + filePath + "\n";
                        }
                    }
                } else {
                    String warning="WARNING: " + filePath + " file does not exist in Testkit where it should be.";
                    LOGGER.warning(warning);
                    out+=warning;
                }
            }
        }catch(Exception e){
            error=true;
            if (e.getMessage().contains("Could not decode the value")) {
                LOGGER.severe("Error parsing the following file: " + file);
                out+="Error parsing the following file: " + file+"\n";
            }
            out+=e.getMessage();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     *  This method process a metdata file found in a testplan by looking for non conforming codes
     * and triggers the update when it find bad codes.
     * @param folderPath path to the directory containing the metadata file.
     * @param fileName name of the metadata file to process.
     */
    private void processMetadataFile(File folderPath, String fileName) {
        File file=null;
        try {
            String filePath=folderPath+"/"+fileName;
            // test if the file being processed has been treated yet
            if (!filesTreated.contains(filePath)) {
                filesTreated.add(filePath);
                file = new File(filePath);
                if (file.exists()) {
                    // read the file
                    OMElement metadataElement = Util.parse_xml(file);
//                    if (file.toString().contains("IDCDEPT012-a")) {
//                        out += "found the target file.\n";
//                    }
                    List<OMElement> badCodes = findNonConformingCodes(file.toString(), metadataElement);
                    if (!badCodes.isEmpty()) {
                        LOGGER.info(badCodes.size() + " codes to update in " + filePath);
                        out += badCodes.size() + " codes to update in " + filePath + '\n';
                        if (!dryRun && backupReplacedFiles) {
                            File backupFile = new File(file.toString() + ".bak");
                            if (!backupFile.exists()) {
                                // backup the unmodified file before updating
                                FileUtils.copyFile(file, backupFile);
                            }
                        }

                        // update bad codes
                        updateCodes(badCodes);

                        if (! dryRun) {
                            // update the file itself
                            Io.stringToFile(file, new OMFormatter(metadataElement).toString());
                        }
                    } else {
                        if (! dryRun && overrideCodes == null)
                            out += "No codes to update in " + filePath + '\n';
                    }
                } else {
                    LOGGER.warning("WARNING: " + filePath + " file referenced in test does not exist.");
                    out+="WARNING: " + filePath + " file referenced in test does not exist.\n";
                }
            }
        } catch (Exception e) {
            error=true;
            if (e.getMessage().contains("Could not decode the value")){
                LOGGER.severe("Error parsing the following file: "+file);
                out+="Error parsing the following file: "+file+"\n";
            }
            out+=e.getMessage();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * This method scans the parameters of a query file for non conforming codes and returns
     * those found.
     * @param params parameters of a query file
     * @return list of non conforming codes.
     */
    private List<SQCodeOr.CodeLet> findNonConformingCodes(SqParams params){
        List<SQCodeOr.CodeLet> badCodes = new ArrayList<SQCodeOr.CodeLet>();
        Map<String,Object> codes=params.getCodedParms();
        for (Map.Entry<String,Object> code:codes.entrySet()){
            if (code.getValue() instanceof SQCodeOr){
                List<SQCodeOr.CodeLet> codesList=((SQCodeOr) code.getValue()).getCodeValues();
                for (SQCodeOr.CodeLet c:codesList){
                    Code tmpCode=new Code(c.code,c.scheme,"");
                    Uuid classificationUuid=new Uuid(SQCodedTerm.codeUUID(code.getKey()));
                    if (!allCodes.isKnownClassification(classificationUuid)) {
                        error=true;
                        LOGGER.warning("Error: Unknown classification");
                        out+="Error: Unknown classification.\n";
                        // TODO throw an exception?
                        continue;
                    }
                    if (!allCodes.exists(classificationUuid, tmpCode)){
                        c.setClassificationUUID(classificationUuid);
                        badCodes.add(c);
                    }

                }
            }else if(code.getValue() instanceof SQCodeAnd){
                for (SQCodeOr sqCodeOr:((SQCodeAnd) code.getValue()).codeOrs){
                    List<SQCodeOr.CodeLet> codesList=sqCodeOr.getCodeValues();
                    for (SQCodeOr.CodeLet c:codesList){
                        Code tmpCode=new Code(c.code,c.scheme,"");
                        Uuid classificationUuid=new Uuid(SQCodedTerm.codeUUID(code.getKey()));
                        if (!allCodes.isKnownClassification(classificationUuid)) {
                            // TODO Throw an exception?
                            error=true;
                            LOGGER.warning("Error: Unknown classification");
                            out+="Error: Unknown classification\n";
                            continue;
                        }
                        if (!allCodes.exists(classificationUuid, tmpCode)){
                            c.setClassificationUUID(classificationUuid);
                            badCodes.add(c);
                        }
                    }
                }
            }
        }
        return badCodes;
    }

    /**
     * This method change a bad codes inside a query file.
     * @param badCodes list of non conforming codes
     */
    void updateCode(List<SQCodeOr.CodeLet> badCodes){
        for (SQCodeOr.CodeLet sqCodedTerm:badCodes){
            changeCodeOR(sqCodedTerm);
        }
    }

    private void changeCodeOR(SQCodeOr.CodeLet code) {
        Code tmpCode=new Code(code.code,code.scheme,"");
        Code newCode;
        if (replacementMap.containsKey(tmpCode.toString())){
            newCode=replacementMap.get(tmpCode.toString());
            out += "Old mapping: " + tmpCode + " --> " + newCode + "\n";
        }else{
            newCode=allCodes.pick(code.getClassificationUUID());
            replacementMap.put(tmpCode.toString(),newCode);
            out += "Old mapping: " + tmpCode + " --> " + newCode + "\n";
        }
        code.code=newCode.getCode();
        code.scheme=newCode.getScheme();
    }

    /**
     * This method takes care of updating the non-conforming codes inside a metadata file.
     * @param badCodes list of non-conforming codes to update.
     * @throws XdsInternalException
     * @throws FactoryConfigurationError
     */
    void updateCodes(List<OMElement> badCodes) throws XdsInternalException, FactoryConfigurationError {
        for (OMElement classification : badCodes) {
            // create variables of the code to be replaced
            OMAttribute codeToReplace=classification.getAttribute(MetadataSupport.noderepresentation_qname)/*.setAttributeValue(code.getCode())*/;
            OMElement valueToReplace=null;
            OMElement slot = MetadataSupport.firstChildWithLocalName(classification, "Slot");
            if (slot != null) {
                OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "ValueList");
                if (valueList!=null) {
                    valueToReplace = MetadataSupport.firstChildWithLocalName(valueList, "Value");
                }
            }
            OMElement nameElement = MetadataSupport.firstChildWithLocalName(classification, "Name");
            if (nameElement == null) return;
            OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
            if (localizedStringElement == null) return;
            OMAttribute nameToReplace = localizedStringElement.getAttribute(MetadataSupport.value_qname);
            Code oldCode = asCode(codeToReplace.getAttributeValue(), valueToReplace.getText(), nameToReplace.getAttributeValue());
            // check if the code to be replaced as already been changed before.
            Code replacementCode=replacementMap.get(oldCode.toString());
            if (replacementCode==null){
                // if not, assign a new code
                String classificationScheme = classification.getAttributeValue(MetadataSupport.classificationscheme_qname);
                Uuid classificationUuid = new Uuid(classificationScheme);
                // pick a new conforming code out of all the codes available in codes.xml
                replacementCode=allCodes.pick(classificationUuid);
                replacementMap.put(oldCode.toString(),replacementCode);
                out += "New mapping: " + oldCode + " --> " + replacementCode + "\n";
            } else {
                if (overrideCodes != null && ! overrideCodes.isEmpty()) {
                    String classificationScheme = classification.getAttributeValue(MetadataSupport.classificationscheme_qname);
                    Uuid classificationUuid = new Uuid(classificationScheme);
                    if (! allCodes.exists(classificationUuid, replacementCode)) {
                        throw new RuntimeException("Override replacement code does not exist in the loaded code map: " + replacementCode.toString());
                    }
                }
                out += "Old mapping: " + oldCode + " --> " + replacementCode + "\n";
            }
            // replace the code
            codeToReplace.setAttributeValue(replacementCode.getCode());
            nameToReplace.setAttributeValue(replacementCode.getDisplay());
            valueToReplace.setText(replacementCode.getScheme());
        }
    }

    private Code asCode(String attributeValue, String text, String display) {
        if (overrideCodes != null && ! overrideCodes.isEmpty()) {
            return new Code(attributeValue, text, display);
        }
        return new Code(attributeValue, text, "");
    }


    /**
     * This method explore a parsed document and looks for non-conforming codes with codes.xml file.
     * @param metadataElement parsed document to analyze
     * @return list of non-confirming code elements
     */
    private List<OMElement> findNonConformingCodes(String file, OMElement metadataElement) {
        List<OMElement> badCodes = new ArrayList<OMElement>();
        List<OMElement> classifications = MetadataSupport.decendentsWithLocalName(metadataElement, "Classification");
        for (OMElement classification : classifications) {
            // Determine the type of code (classification uuid).
            String classificationScheme = classification.getAttributeValue(MetadataSupport.classificationscheme_qname);
            if (classificationScheme == null || "".equals(classificationScheme))
                continue;
            Uuid classificationUuid = new Uuid(classificationScheme);
            // Check if the type of code exists.
            if (allCodes.isKnownClassification(classificationUuid)) {
                Code code = getCode(classification);
                if (overrideCodes != null && ! overrideCodes.isEmpty()) {
                    int oCodeIndex = overrideCodes.indexOf(code);
                    if (oCodeIndex > -1
                            && targetFileByOverrideCodeIndex != null
                            && targetFileByOverrideCodeIndex.get(oCodeIndex) != null
                            && file.contains(targetFileByOverrideCodeIndex.get(oCodeIndex))) {
                        out += "Overriding code: " + code.getCode() + ". ";
                        badCodes.add(classification);
                    }
                }
                // check if the code exists in the environment codes.xml file
                else if (!allCodes.exists(classificationUuid, code)) {
                    // if it does not add the code to the list of bad codes.
                    badCodes.add(classification);
                }
            }
        }
        return badCodes;
    }

    /**
     * This method returns a Code out of a classification element.
     * @param classificationElement classification element to extract
     * @return code model
     */
    private Code getCode(OMElement classificationElement){
        // getRetrievedDocumentsModel coding scheme
        String value = classificationElement.getAttributeValue(MetadataSupport.noderepresentation_qname);
        // getRetrievedDocumentsModel display name
        String displayName;
        OMElement nameElement = MetadataSupport.firstChildWithLocalName(classificationElement, "Name");
        OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
        if (nameElement == null || localizedStringElement == null)
            displayName="";
        else
            displayName=localizedStringElement.getAttributeValue(MetadataSupport.value_qname);
        // getRetrievedDocumentsModel code
        String codeSystem = "";
        OMElement codeSystemElement;
        OMElement slot = MetadataSupport.firstChildWithLocalName(classificationElement, "Slot");
        OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "ValueList");
        OMElement v = MetadataSupport.firstChildWithLocalName(valueList, "Value");
        if (slot == null || valueList == null || v==null){
            codeSystemElement= null;
        } else{
            codeSystemElement= v;
        }

        // return the code model
        if (codeSystemElement == null) return new Code(value, codeSystem, displayName);
        codeSystem = codeSystemElement.getText();
        return new Code(value, codeSystem, displayName);
    }

    /**
     * The method run the code update procedure on a copied version of testkit in the environment specified
     * in parameter.
     * @param pathToEnvironment destination environment for the testkit (containing codes.xml).
     * @return execution log.
     */
    public void run(String pathToEnvironment, String pathToTestkit, TestSession testSession) {
        String outputSeparator = new String("----------------------------------------------------");
        // init environment dir
        File environment = new File(pathToEnvironment);
        // init testkit dir
        testkit=new File(environment.getPath()+File.separator+"testkits" + File.separator + testSession);
        // init codes
        allCodes = new CodesFactory().load(new File(environment.getPath() + File.separator + "codes.xml"));
        try {
            LOGGER.info("Copying testkit to " + testkit + "...");
            FileUtils.copyDirectory(new File(pathToTestkit), testkit);
            LOGGER.info("... testkit copied.");
            out += "Testkit of referenced copied successfully to " + testkit + "\n";
        } catch (IOException e) {
            error = true;
            out += "FAILURE. Could not copy testkit into environment.\n" ;
            LOGGER.severe(e.getMessage());
            return;
        }
        execute();
        if (error) return;
        out += outputSeparator + "\n";
        out += "Mappings\n\n";
        for (String from : replacementMap.keySet()) {
            String to = replacementMap.get(from).toString();
            out += (from + "  ===>   " + to + "\n");
        }
        if (! dryRun) {
            // Check own work
            reset();
            out += outputSeparator + "\n";
            out += "Pass 2\n\n";
            execute();
            if (error) return;
            out = outputSeparator + outputSeparator + "\n" + "   SUCCESS on generating testkit in environment in " +
                    pathToEnvironment.split("/")[pathToEnvironment.split("/").length - 1] + "\n" +
                    outputSeparator + outputSeparator + "\n\n" + out;
            out += outputSeparator + "\n";
            out += "Mappings\n\n";
            for (String from : replacementMap.keySet()) {
                String to = replacementMap.get(from).toString();
                out += (from + "  ===>   " + to + "\n");
            }
        }
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
            File logDirectory = new File(pathToEnvironment, "Testkit update logs");
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }
            File f = new File(logDirectory, dateFormatter.format(new Date()) + ".out");
            LOGGER.info("Creating output log file in " + f.getPath() + "...");
            Io.stringToFile(f, out);
            LOGGER.info("... file created.");
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * @return execution output (log) of the update.
     */
    public String getOutput(){
        return out;
    }

    /**
     * @return if errors happened during the execution of the update.
     */
    public boolean hasErrors(){
        return error;
    }
}
