package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmetadata.coding.AllCodes;
import gov.nist.toolkit.valregmetadata.coding.Code;
import gov.nist.toolkit.valregmetadata.coding.CodesFactory;
import gov.nist.toolkit.valregmetadata.coding.Uuid;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a script supposed to update all the metadata files living inside the testkit
 * for their codes to match the ones in the configuration file codes.xml.
 * Created by onh2 on 6/24/2015.
 */
public class UpdateCodes {
    File testkit;
    AllCodes allCodes=null;
    boolean error;
    String sections[] = { /*"testdata",*/ "tests"/*, "examples"*//*, "selftest"*/ };
    List<String> metadataFilesPaths=new ArrayList<String>();

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
                    metadataFilesPaths.add(testFile + "/" + metadataFile.getText());
                }
            }
            // the following is probably useless
            Iterator<OMElement> c = node.getChildElements();
            while (c.hasNext()){
                exploreChildren(c, testFile);
            }
        }
    }

    /**
     * This method method reads the different files found previously in the testkit to update non-conforming codes.
     */
    private void processCodes() {
        try {
            for (String filePath:metadataFilesPaths){
                File file=new File(filePath);
                if (file.exists()) {
                    File backupFile = new File(file.toString() + ".bak");
                    if (!backupFile.exists()) {
                        // backup the unmodified file before updating
                        FileUtils.copyFile(file, backupFile);
                    }
                    // read the file
                    OMElement metadataElement = Util.parse_xml(file);
                    List<OMElement> badCodes = findNonConformingCodes(metadataElement);
                    System.out.println(badCodes.size() + " bad codes to update in " + filePath);
                    // update bad codes
                    updateCodes(badCodes);
                    // update the file itself
                    Io.stringToFile(file, new OMFormatter(metadataElement).toString());
                }else{
                    System.err.println("WARNING: "+filePath+" file does not exist in Testkit where it should be.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
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
            String classificationScheme = classification.getAttributeValue(MetadataSupport.classificationscheme_qname);
            if (classificationScheme == null || classificationScheme.equals(""))
                continue;
            Uuid classificationUuid = new Uuid(classificationScheme);
            if (!allCodes.isKnownClassification(classificationUuid))
                continue;
            Code code = getCode(classification);
            if (!allCodes.exists(classificationUuid, code))
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

    /**
     * This method takes care of updating the non-conforming codes.
     * @param badCodes list of non-conforming codes to update.
     * @throws XdsInternalException
     * @throws FactoryConfigurationError
     */
    void updateCodes(List<OMElement> badCodes) throws XdsInternalException, FactoryConfigurationError {
        for (OMElement classification : badCodes) {
            String classificationScheme = classification.getAttributeValue(MetadataSupport.classificationscheme_qname);
            Uuid classificationUuid = new Uuid(classificationScheme);
            // pick a new conforming code out of all the codes available in codes.xml
            Code newCode = allCodes.pick(classificationUuid);
            updateClassification(classification, newCode);
        }
    }

    /**
     * This method updates the classification of the metadata element itself.
     * @param classification classification to update.
     * @param code code that will replace the current non-conforming code.
     */
    void updateClassification(OMElement classification, Code code) {
        classification.getAttribute(MetadataSupport.noderepresentation_qname).setAttributeValue(code.getCode());

        OMElement slot = MetadataSupport.firstChildWithLocalName(classification, "Slot");
        OMElement codeSystemElement = null;
        if (slot != null) {
            OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "ValueList");
            if (valueList!=null) {
                OMElement value = MetadataSupport.firstChildWithLocalName(valueList, "Value");
                codeSystemElement = value;
            }
        }
        if (codeSystemElement != null){
            codeSystemElement.setText(code.getScheme());
        }
        OMElement nameElement = MetadataSupport.firstChildWithLocalName(classification, "Name");
        if (nameElement == null) return;
        OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
        if (localizedStringElement == null) return;
        localizedStringElement.getAttribute(MetadataSupport.value_qname).setAttributeValue(code.getDisplay());
    }

    public static void main(String[] args) {
        UpdateCodes uc=new UpdateCodes();
        // init codes
        uc.allCodes = new CodesFactory().load(new File(args[1]));
        // init testkit
        uc.testkit = new File(args[0]);
        uc.scan();
        uc.processCodes();
    }
}
