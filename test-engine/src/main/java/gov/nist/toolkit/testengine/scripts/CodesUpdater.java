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
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by oherrmann on 1/11/16.
 */
public class CodesUpdater {
    private final static String sections[] = { "tests", "testdata",  "examples"/*, "selftest"*/ };

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
        for (int i=0; i<sections.length; i++) {
            String section = sections[i];

            File sectionFile = new File(testkit + File.separator + section);

            try {
                exploreTests(sectionFile);
            } catch (IOException e) {
                out+=e.getMessage();
                e.printStackTrace();
            }
        }
    }

    /**
     * This method explores a test repository structure.
     *
     * @param testFile path to the testkit folder which contains the test section to explore.
     * @throws IOException
     */
    void exploreTests(File testFile) throws IOException {
        try {
            File[] dirs = testFile.listFiles();
            if (dirs == null) {
                System.out.println("No tests defined in " + testFile.toString());
                // TODO throw an exception?!
                out+="No tests defined in " + testFile.toString() +"\n";
                error = true;
            }else {
                for (int i = 0; i < dirs.length; i++) {
                    File testDir = dirs[i];
                    if (testDir.getName().equals(".svn"))
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
        }catch (Exception e){
            out+=e.getMessage();
            error=true;
            throw new RuntimeException(e.getMessage());
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
                            System.out.println(badCodes.size() + " bad codes to update in query file: " + filePath);
                            out += badCodes.size() + " bad codes to update in query file: " + filePath + "\n";
                            File backupFile = new File(file.toString() + ".bak");
                            if (!backupFile.exists()) {
                                // backup the unmodified file before updating
                                FileUtils.copyFile(file, backupFile);
                            }
                            // update bad codes
                            updateCode(badCodes, filePath);
                            // update the file itself
                            String returnType = queryElement.getFirstElement().getAttributeValue(new QName("returnType"));
                            Io.stringToFile(file, new OMFormatter(StoredQueryGenerator.generateQueryFile(returnType, params)).toString());
                        }
                    }
                } else {
                    System.err.println("WARNING: " + filePath + " file does not exist in Testkit where it should be.");
                }
            }
        }catch(Exception e){
            error=true;
            if (e.getMessage().contains("Could not decode the value")) {
                System.err.println("Error parsing the following file: " + file);
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
                    // TODO something is probably missing here (else?)
                    // read the file
                    OMElement metadataElement = Util.parse_xml(file);
                    List<OMElement> badCodes = findNonConformingCodes(metadataElement);
                    if (!badCodes.isEmpty()) {
                        System.out.println(badCodes.size() + " bad codes to update in " + filePath);
                        out += badCodes.size() + " bad codes to update in query file: " + filePath + '\n';
                        File backupFile = new File(file.toString() + ".bak");
                        if (!backupFile.exists()) {
                            // backup the unmodified file before updating
                            FileUtils.copyFile(file, backupFile);
                        }
                        // update bad codes
                        updateCodes(badCodes, filePath);
                        // update the file itself
                        Io.stringToFile(file, new OMFormatter(metadataElement).toString());
                    }
                } else {
                    System.err.println("WARNING: " + filePath + " file does not exist in Testkit where it should be.");
                    out+="WARNING: " + filePath + " file does not exist in Testkit where it should be.\n";
                }
            }
        } catch (Exception e) {
            error=true;
            if (e.getMessage().contains("Could not decode the value")){
                System.err.println("Error parsing the following file: "+file);
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
        Map<String,SQCodedTerm> codes=params.getCodedParms();
        for (String key:codes.keySet()){
            if (codes.get(key) instanceof SQCodeOr){
                List<SQCodeOr.CodeLet> codesList=((SQCodeOr) codes.get(key)).getCodeValues();
                for (SQCodeOr.CodeLet c:codesList){
                    Code tmpCode=new Code(c.code,c.scheme,"");
                    Uuid classificationUuid=new Uuid(SQCodedTerm.codeUUID(key));
                    if (!allCodes.isKnownClassification(classificationUuid)) {
                        error=true;
                        System.err.println("Error: Unknown classification");
                        out+="Error: Unknown classification.\n";
                        // TODO throw an exception?
                        continue;
                    }
                    if (!allCodes.exists(classificationUuid, tmpCode)){
                        c.setClassificationUUID(classificationUuid);
                        badCodes.add(c);
                    }
                }
            }else if(codes.get(key) instanceof SQCodeAnd){
                for (SQCodeOr sqCodeOr:((SQCodeAnd) codes.get(key)).codeOrs){
                    List<SQCodeOr.CodeLet> codesList=sqCodeOr.getCodeValues();
                    for (SQCodeOr.CodeLet c:codesList){
                        Code tmpCode=new Code(c.code,c.scheme,"");
                        Uuid classificationUuid=new Uuid(SQCodedTerm.codeUUID(key));
                        if (!allCodes.isKnownClassification(classificationUuid)) {
                            // TODO Throw an exception?
                            error=true;
                            System.err.println("Error: Unknown classification");
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
     * @param filePath path to the query file containing these non conforming codes.
     */
    void updateCode(List<SQCodeOr.CodeLet> badCodes,String filePath){
        for (SQCodeOr.CodeLet sqCodedTerm:badCodes){
            changeCodeOR(sqCodedTerm,filePath);
        }
    }

    private void changeCodeOR(SQCodeOr.CodeLet code,String filePath) {
        Code tmpCode=new Code(code.code,code.scheme,"");
        Code newCode;
        if (replacementMap.containsKey(tmpCode.toString())){
            newCode=replacementMap.get(tmpCode.toString());
        }else{
            newCode=allCodes.pick(code.getClassificationUUID());
            replacementMap.put(tmpCode.toString(),newCode);
        }
//        out+=tmpCode.toString() + " REPLACED BY "+newCode.toString()+" in " + filePath + "\n";
        code.code=newCode.getCode();
        code.scheme=newCode.getScheme();
    }

    /**
     * This method takes care of updating the non-conforming codes inside a metadata file.
     * @param badCodes list of non-conforming codes to update.
     * @param filePath path to the metadata file containing these non conforming codes
     * @throws XdsInternalException
     * @throws FactoryConfigurationError
     */
    void updateCodes(List<OMElement> badCodes,String filePath) throws XdsInternalException, FactoryConfigurationError {
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
//            out+=oldCode.toString() + " REPLACE BY "+replacementCode.toString()+" in "+filePath+"\n";
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
            if (classificationScheme == null || classificationScheme.equals(""))
                continue;
            Uuid classificationUuid = new Uuid(classificationScheme);
            // Check if the type of code exists.
            if (!allCodes.isKnownClassification(classificationUuid))
                continue;
            Code code = getCode(classification);
            // check if the code exists in the environment codes.xml file
            if (!allCodes.exists(classificationUuid, code)) {
                // if it does not add the code to the list of bad codes.
                badCodes.add(classification);
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
        String displayName = null;
        OMElement nameElement = MetadataSupport.firstChildWithLocalName(classificationElement, "Name");
        OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
        if (nameElement == null || localizedStringElement == null) displayName="";
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
        testkit = new File(environment.getPath()+File.separator+"testkit");
        // init codes
        allCodes = new CodesFactory().load(new File(environment.getPath()+File.separator+"codes.xml"));
        try {
            System.out.println("Copying testkit to "+testkit+"...");
            FileUtils.copyDirectory(new File(pathToTestkit), testkit);
            System.out.println("... testkit copied.");
            out+="Testkit of referenced copied successfully to "+testkit;
        } catch (IOException e) {
            e.printStackTrace();
        }
        execute();
        reset();
        execute();
        String outputSeparator=new String("----------------------------------------------------");
        out=outputSeparator+outputSeparator+"\n"+"   SUCCESS on generating testkit in environment in " +
                pathToEnvironment.split("/")[pathToEnvironment.split("/").length-1] + "\n" +
                outputSeparator+outputSeparator +"\n\n"+out;
        try {
            SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyyMMddHHmmss");
            File logDirectory=new File(pathToEnvironment,"Testkit update logs");
            if (!logDirectory.exists()){
                logDirectory.mkdir();
            }
            File f = new File(logDirectory,dateFormatter.format(new Date())+".out");
            System.out.println("Creating output log file in "+f.getPath()+"...");
            Io.stringToFile(f, out);
            System.out.println("... file created.");
        } catch (IOException e) {
            e.printStackTrace();
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
