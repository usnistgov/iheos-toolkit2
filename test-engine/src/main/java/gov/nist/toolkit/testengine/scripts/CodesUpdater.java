package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
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
 * @see #run(String, String)
 * @see #getOutput()
 * @see #hasErrors()
 *
 * Created by oherrmann on 1/11/16.
 */
public class CodesUpdater {
    private static final String[] SECTIONS = { "tests", "testdata", "testdata-registry","testdata-repository","testdata-xdr","utilities", "examples","xcpd", "selftest"};
    private static final Logger LOGGER = Logger.getLogger(CodesUpdater.class.getName());

    private File testkit;
    private AllCodes allCodes=null;
    private List<String> filesTreated = new ArrayList<String>();
    private Map<String,Code> replacementMap= new HashMap<String,Code>();

    private String out=new String();
    private boolean error;

    /**
     * Reset the class variable to their initial state before the algorithm is run again.
     */
    void reset(){
        filesTreated=new ArrayList<String>();
        replacementMap=new HashMap<String,Code>();
    }

    /**
     * This method scans the testkit for metadata files,
     * which will result in the update itself.
     */
    void execute() {
        error = false;
        for (int i = 0; i< SECTIONS.length; i++) {
            String section = SECTIONS[i];

            File sectionFile = new File(testkit + File.separator + section);

            try {
                exploreTests(sectionFile);
            } catch (Exception e) {
                error=true;
                out+="FAILURE.\n"+e.getMessage();
                LOGGER.severe(e.getMessage());
            }
        }
    }

    /**
     * This method explores a test repository structure.
     *
     * @param testFile path to the testkit folder which contains the test section to explore.
     * @throws IOException
     */
    void exploreTests(File testFile) throws IOException,XdsInternalException {
        File[] dirs = testFile.listFiles();
        if (dirs == null) {
            LOGGER.warning("No tests defined in " + testFile.toString());
            error = true;
            out+="No tests defined in " + testFile.toString() +"\n";
            throw new IOException("No tests defined in " +testFile.toString());
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
                        String testplanContent = Io.stringFromFile(testDir);
                        OMElement testplanNode = Util.parse_xml(testplanContent);
                        // retrieve the TestStep nodes
                        Iterator<OMElement> steps = testplanNode.getChildrenWithName(new QName("TestStep"));
                        while (steps.hasNext()) {
                            // find transaction nodes among the nodes under exploration (Under a TestStep)
                            Iterator<OMElement> children = steps.next().getChildElements();
                            exploreChildren(children, testFile);
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
                        OMElement queryElement = Util.parse_xml(file);
                        ParamParser parser = new ParamParser();
                        SqParams params = parser.parse(queryElement, false);
                        List<SQCodeOr.CodeLet> badCodes = findNonConformingCodes(params);
                        if (!badCodes.isEmpty()) {
                            LOGGER.info(badCodes.size() + " bad codes to update in query file: " + filePath);
                            out += badCodes.size() + " bad codes to update in query file: " + filePath + "\n";
                            File backupFile = new File(file.toString() + ".bak");
                            if (!backupFile.exists()) {
                                // backup the unmodified file before updating
                                FileUtils.copyFile(file, backupFile);
                            }
                            // update bad codes
                            updateCode(badCodes);
                            // update the file itself
                            String returnType = queryElement.getFirstElement().getAttributeValue(new QName("returnType"));
                            Io.stringToFile(file, new OMFormatter(StoredQueryGenerator.generateQueryFile(returnType, params)).toString());
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
                    List<OMElement> badCodes = findNonConformingCodes(metadataElement);
                    if (!badCodes.isEmpty()) {
                        LOGGER.info(badCodes.size() + " bad codes to update in " + filePath);
                        out += badCodes.size() + " bad codes to update in query file: " + filePath + '\n';
                        File backupFile = new File(file.toString() + ".bak");
                        if (!backupFile.exists()) {
                            // backup the unmodified file before updating
                            FileUtils.copyFile(file, backupFile);
                        }
                        // update bad codes
                        updateCodes(badCodes);
                        // update the file itself
                        Io.stringToFile(file, new OMFormatter(metadataElement).toString());
                    }
                } else {
                    LOGGER.warning("WARNING: " + filePath + " file does not exist in Testkit where it should be.");
                    out+="WARNING: " + filePath + " file does not exist in Testkit where it should be.\n";
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
        }else{
            newCode=allCodes.pick(code.getClassificationUUID());
            replacementMap.put(tmpCode.toString(),newCode);
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
            Code oldCode=new Code(codeToReplace.getAttributeValue(),valueToReplace.getText(),""/*nameToReplace.getAttributeValue()*/);
            // check if the code to be replaced as already been changed before.
            Code replacementCode=replacementMap.get(oldCode.toString());
            if (replacementCode==null){
                // if not, assign a new code
                String classificationScheme = classification.getAttributeValue(MetadataSupport.classificationscheme_qname);
                Uuid classificationUuid = new Uuid(classificationScheme);
                // pick a new conforming code out of all the codes available in codes.xml
                replacementCode=allCodes.pick(classificationUuid);
                replacementMap.put(oldCode.toString(),replacementCode);
            }
            // replace the code
            codeToReplace.setAttributeValue(replacementCode.getCode());
            nameToReplace.setAttributeValue(replacementCode.getDisplay());
            valueToReplace.setText(replacementCode.getScheme());
        }
    }


    /**
     * This method explore a parsed document and looks for non-conforming codes with codes.xml file.
     * @param metadataElement parsed document to analyze
     * @return list of non-confirming code elements
     */
    private List<OMElement> findNonConformingCodes(OMElement metadataElement) {
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
                // check if the code exists in the environment codes.xml file
                if (!allCodes.exists(classificationUuid, code)) {
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
    public void run(String pathToEnvironment, String pathToTestkit) {
        // init environment dir
        File environment = new File(pathToEnvironment);
        // init testkit dir
        testkit = new File(environment.getPath()+File.separator+"testkits"+File.separator+"default");
        // init codes
        allCodes = new CodesFactory().load(new File(environment.getPath()+File.separator+"codes.xml"));
        try {
            LOGGER.info("Copying testkit to "+testkit+"...");
            FileUtils.copyDirectory(new File(pathToTestkit), testkit);
            LOGGER.info("... testkit copied.");
            out+="Testkit of referenced copied successfully to "+testkit;
        } catch (IOException e) {
            error=true;
            out+="FAILURE. Could not copy testkit into environment.";
            LOGGER.severe(e.getMessage());
            return;
        }
        execute();
        if (error) return;
        reset();
        execute();
        if (error) return;
        String outputSeparator = new String("----------------------------------------------------");
        out = outputSeparator + outputSeparator + "\n" + "   SUCCESS on generating testkit in environment in " +
                pathToEnvironment.split("/")[pathToEnvironment.split("/").length - 1] + "\n" +
                outputSeparator + outputSeparator + "\n\n" + out;
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
