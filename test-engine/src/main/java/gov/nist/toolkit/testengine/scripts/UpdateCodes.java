package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.registrysupport.MetadataSupport;
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
import java.util.*;

/**
 * Created by oherrmann on 1/11/16.
 */
public class UpdateCodes {
    File testkit;
    AllCodes allCodes=null;
    boolean error;
    static String sections[] = { "tests", "testdata",  "examples"/*, "selftest"*/ };
    List<String> filesTreated = new ArrayList<String>();
    List<String> metadataFilesPaths=new ArrayList<String>();
    List<String> queryFilesPaths=new ArrayList<String>();
    Map<String,Code> replacementMap= new HashMap<String,Code>();
    String out=new String();

    void reset(){
        filesTreated=new ArrayList<String>();
        metadataFilesPaths=new ArrayList<String>();
        replacementMap=new HashMap<String,Code>();
    }

    /**
     * This method scans the testkit for metadata files.
     */
    void scan() {
        error = false;
        for (int i=0; i<sections.length; i++) {
            String section = sections[i];

            File sectionFile = new File(testkit + File.separator + section);

            try {
                exploreTests(sectionFile);
            } catch (IOException e) {
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
                System.out.println("No tests defined in " + dirs);
                error = true;
            }
            for (int i = 0; i < dirs.length; i++) {
                File testDir = dirs[i];
                if (testDir.getName().equals(".svn"))
                    continue;
                if (testDir.isDirectory()) {
                    exploreTests(testDir);
                } else {
//                    if (testFile.getPath().contains("12346") || testFile.getPath().contains("11903") )
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
        }catch (Exception e){
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
                // get metadata file(s) name
                OMElement metadataFile=transaction.getFirstChildWithName(new QName("MetadataFile"));
                if (metadataFile!=null) {
                    if (transaction.getLocalName().contains("StoredQueryTransaction")) {
                        queryFilesPaths.add(testFile + "/" + metadataFile.getText());
                        processQueryFile(testFile, metadataFile.getText());
                    }else {
                        metadataFilesPaths.add(testFile + "/" + metadataFile.getText());
                        processMetadataFile(testFile,metadataFile.getText());
                    }
                }
            }
            // the following is probably useless
//            Iterator<OMElement> c = node.getChildElements();
//            while (c.hasNext()){
//                exploreChildren(c, testFile);
//            }
        }
    }

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
                if (file.exists() && !testsFails) {
                    // read the file
                    OMElement queryElement = Util.parse_xml(file);
                    ParamParser parser = new ParamParser();
                    SqParams params = parser.parse(queryElement, false);
                    List<SQCodeOr.CodeLet> badCodes = findNonConformingCodes(params);
                    System.out.println(badCodes.size() + " bad codes to update in query file: " + filePath);
                    if (!badCodes.isEmpty()) {
                        File backupFile = new File(file.toString() + ".bak");
                        if (!backupFile.exists()) {
                            // backup the unmodified file before updating
                            FileUtils.copyFile(file, backupFile);
                        }
                        // update bad codes
                        updateCode(badCodes, filePath);
                        // update the file itself
                        String returnType=queryElement.getFirstElement().getAttributeValue(new QName("returnType"));
                        Io.stringToFile(file, new OMFormatter(StoredQueryGenerator.generateQueryFile(returnType,params)).toString());
                    }
                } else {
                    System.err.println("WARNING: " + filePath + " file does not exist in Testkit where it should be.");
                }
            }
        }catch(Exception e){
            if (e.getMessage().contains("Could not decode the value")) {
                System.err.println("Error parsing the following file: " + file);
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void processMetadataFile(File folderPath, String fileName) {
        File file=null;
        try {
            String filePath=folderPath+"/"+fileName;
            // test if the file being processed has been treated yet
            if (!filesTreated.contains(filePath)) {
                file = new File(filePath);
                if (file.exists()) {
                    File backupFile = new File(file.toString() + ".bak");
                    if (!backupFile.exists()) {
                        // backup the unmodified file before updating
                        FileUtils.copyFile(file, backupFile);
                    }
                    // TODO something is probably missing here (else?)
                    // read the file
                    OMElement metadataElement = Util.parse_xml(file);
                    List<OMElement> badCodes = findNonConformingCodes(metadataElement);
                    System.out.println(badCodes.size() + " bad codes to update in " + filePath);
                    // update bad codes
                    updateCodes(badCodes, filePath);
                    // update the file itself
                    Io.stringToFile(file, new OMFormatter(metadataElement).toString());
                    filesTreated.add(filePath);
                } else {
                    System.err.println("WARNING: " + filePath + " file does not exist in Testkit where it should be.");
                }
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Could not decode the value")){
                System.err.println("Error parsing the following file: "+file);
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // FIXME this is the method causing the error (also see updateCode method)
    private List<SQCodeOr.CodeLet> findNonConformingCodes(SqParams params){
        List<SQCodeOr.CodeLet> badCodes = new ArrayList<SQCodeOr.CodeLet>();
        Map<String,SQCodedTerm> codes=params.getCodedParms();
        for (String key:codes.keySet()){
            if (codes.get(key) instanceof SQCodeOr){
                List<SQCodeOr.CodeLet> codesList=((SQCodeOr) codes.get(key)).getCodeValues();
                for (SQCodeOr.CodeLet c:codesList){
//                    System.out.println("CodeLetOR "+c);
                    Code tmpCode=new Code(c.code,c.scheme,"");
                    Uuid classificationUuid=new Uuid(SQCodedTerm.codeUUID(key));
                    if (!allCodes.isKnownClassification(classificationUuid)) {
                        System.err.println("Error: Unknown classification");
                        continue;
                    }
                    if (!allCodes.exists(classificationUuid, tmpCode)){
//                        System.out.println(c);
                        c.setClassificationUUID(classificationUuid);
                        badCodes.add(c);
//                        System.out.println(codes.get(key));
                    }
                }
            }else if(codes.get(key) instanceof SQCodeAnd){
                for (SQCodeOr sqCodeOr:((SQCodeAnd) codes.get(key)).codeOrs){
                    List<SQCodeOr.CodeLet> codesList=sqCodeOr.getCodeValues();
                    for (SQCodeOr.CodeLet c:codesList){
//                        System.out.println("CodeLetAND "+c);
                        Code tmpCode=new Code(c.code,c.scheme,"");
                        Uuid classificationUuid=new Uuid(SQCodedTerm.codeUUID(key));
                        if (!allCodes.isKnownClassification(classificationUuid)) {
                            System.err.println("Error: Unknown classification");
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

    void updateCode(List<SQCodeOr.CodeLet> badCodes,String filePath){
        for (SQCodeOr.CodeLet sqCodedTerm:badCodes){
            changeCodeOR(sqCodedTerm,filePath);
        }
    }

    /**
     * This method takes care of updating the non-conforming codes.
     * @param badCodes list of non-conforming codes to update.
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
            if (filePath.contains("11992")||filePath.contains("11993")||filePath.contains("11994")){
                System.out.println(filePath+" : "+classification.getAttributeValue(MetadataSupport.classificationscheme_qname));
            }
            out+=oldCode.toString() + " REPLACE BY "+replacementCode.toString()+" in "+filePath+"\n";
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
        out+=tmpCode.toString() + " REPLACED BY "+newCode.toString()+" in " + filePath + "\n";
//        System.out.println(newCode.toString());
        code.code=newCode.getCode();
        code.scheme=newCode.getScheme();
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
            if (!allCodes.exists(classificationUuid, code))
                // if it does not add the code to the list of bad codes.
                badCodes.add(classification);
        }
        return badCodes;
    }

    /**
     * This method returns a Code out of a classification element.
     * @param classificationElement classification element to extract
     * @return code object
     */
    public Code getCode(OMElement classificationElement){
        // get coding scheme
        String value = classificationElement.getAttributeValue(MetadataSupport.noderepresentation_qname);

        // get display name
        String displayName = null;
        OMElement nameElement = MetadataSupport.firstChildWithLocalName(classificationElement, "Name");
        OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
        if (nameElement == null || localizedStringElement == null) displayName="";
        displayName=localizedStringElement.getAttributeValue(MetadataSupport.value_qname);

        // get code
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

        // return the code object
        if (codeSystemElement == null) return new Code(value, codeSystem, displayName);
        codeSystem = codeSystemElement.getText();
        return new Code(value, codeSystem, displayName);
    }

    public static void main(String[] args) {
        UpdateCodes uc=new UpdateCodes();
        // init codes
        uc.allCodes = new CodesFactory().load(new File(args[1]));
        // init testkit
        uc.testkit = new File(args[0]);
        uc.scan();
        uc.reset();
        uc.scan();
        File f = new File("xdstools2"+File.separator+"target"+File.separator+"CodeUpdateLog.out");
        try {
            Io.stringToFile(f, uc.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(uc.out);
    }
}
