/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nist.toolkit.valregmsg.validation.schematron;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author gunn
 */
public class ReportProcessor {

    private HashMap headerRptMap = null;
    private ArrayList<String> schemaRptErrors = null;
    private ArrayList<HashMap> schematronRptErrors = null;

    //creates of hashmap to get the header information
    public HashMap loadHeaderValues(NodeList header) {
        HashMap<String, String> headerMap = new HashMap<String, String>();

        for (int j = 0; j < header.getLength(); j++) {

            if (header.item(j).getNodeName().equals("ValidationStatus")) {
                headerMap.put("ValidationStatus", header.item(j).getTextContent());
            }
            if (header.item(j).getNodeName().equals("ServiceName")) {
                headerMap.put("ServiceName", header.item(j).getTextContent());
            }
            if (header.item(j).getNodeName().equals("DateOfTest")) {
                headerMap.put("DateOfTest", header.item(j).getTextContent());
            }
            if (header.item(j).getNodeName().equals("TimeOfTest")) {
                headerMap.put("TimeOfTest", header.item(j).getTextContent());
            }
            if (header.item(j).getNodeName().equals("ResultOfTest")) {
                headerMap.put("ResultOfTest", header.item(j).getTextContent());
            }
            if (header.item(j).getNodeName().equals("ErrorCount")) {
                headerMap.put("ErrorCount", header.item(j).getTextContent());
            }
        }
        return headerMap;
    }

    public void setHeaderValues(HashMap headerRtValues) {
        headerRptMap = headerRtValues;
    }

    public HashMap getHeaderValues() {
        return headerRptMap;
    }

    public String getHeaderKeys() {
        String headerKeys = "ValidationStatus ServiceName DateOfTest TimeOfTest ResultOfTest ErrorCount";

        return headerKeys;
    }

    //This is a little more hard coded than a like but the report is well formatted and consistent.
    //It builds an array datastore for schematron errors.
    public ArrayList loadSchematronErrors(NodeList schematronReport) {

        ArrayList<HashMap> schematronErrors = new ArrayList<HashMap>();
       // HashMap<String, String> errorMsg = new HashMap<String, String>();
        for (int count = 0; count < schematronReport.getLength(); count++) {
            Node issue = schematronReport.item(count);
            //every issue will always have 3 children Message, Content, Test. I will always store
            //the content of these elements.
            NodeList issueChildren = issue.getChildNodes();
            HashMap<String, String> errorMsg = new HashMap<String, String>();
            String message = issueChildren.item(0).getTextContent();
            errorMsg.put("Message", message);

            String content = issueChildren.item(1).getTextContent();
            errorMsg.put("Context", content);

            String test = issueChildren.item(2).getTextContent();
            errorMsg.put("Test", test);

            schematronErrors.add(count, errorMsg);

        }
        return schematronErrors;
    }

    public void setSchematronErrors(ArrayList schematronRtErrors) {
        schematronRptErrors = schematronRtErrors;
    }

    public ArrayList getSchematronErrors() {
        return schematronRptErrors;
    }

    //Creates and array of schema errors.
    public ArrayList loadSchemaErrors(NodeList schemaReport) {

        ArrayList<String> schemaErrors = new ArrayList<String>();
        for (int num = 0; num < schemaReport.getLength(); num++) {
            schemaErrors.add(schemaReport.item(num).getTextContent());
        }

        return schemaErrors;
    }

    public void setSchemaErrors(ArrayList schemaRtErrors) {
        schemaRptErrors = schemaRtErrors;
    }

    public ArrayList getSchemaErrors() {
        return schemaRptErrors;
    }

    //this method will parse the report into 3 data stores. One for report header, another for
    //schema errors and the last for schematron errors. The report is very structured that's why I'm
    //explicitly referencing the nodes in the doucment.
    public void documentParser(String reportString) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            InputSource source = new InputSource(new StringReader(reportString));
            Document document = factory.newDocumentBuilder().parse(source);

            document.getDocumentElement().normalize();
            //Setting and Testing Header report/reportHeader
            //  ReportProcessor rp = new ReportProcessor();
            NodeList root = document.getFirstChild().getChildNodes();
            Node reportHeader = root.item(0);
            HashMap headerValues = loadHeaderValues(reportHeader.getChildNodes());
            setHeaderValues(headerValues);
            //Setting and Testing Report
            Node schemaReport = root.item(1);
            //Check is there's errors in schema report section
            if (schemaReport.hasChildNodes()) {
                ArrayList schemaErr = loadSchemaErrors(schemaReport.getChildNodes());
                //populates the array list with schema errors to be used in a report section for retrieval
                setSchemaErrors(schemaErr);

            } else {
                //setting the array to null meaning clean schema report
                ArrayList noSchemaErrors = null;
                setSchemaErrors(noSchemaErrors);
            }

            Node schematronReport = root.item(2);
            if (schematronReport.hasChildNodes()) {
                NodeList issues = schematronReport.getChildNodes();
                ArrayList schematronErr = loadSchematronErrors(issues);
                //populates the array list with schematron errors to be used in a report section for retrieval
                setSchematronErrors(schematronErr);
            } else {
            }
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {

       // File config = new File("/Users/gunn/schematronValidator/schematronValidation/files/schematronValidationConfig.xml");
    File config = new File("/Users/gunn/Documents/workplace_metrix/shared-lib/lib/schematronTool/files/schematronValidationConfig.xml");

//        String message = "<PRPA_IN201301UV02 xmlns=\"urn:hl7-org:v3\">" +
        //                       "</PRPA_IN201301UV02>";
       String warHome = "";
        try {
            String message = schematronValidation.readFile("/Users/gunn/Desktop/test305.xml");
            //String message = schematronValidation.readFile("/Users/gunn/Desktop/NHINToolDistribution/SampleTestFiles/MeaningfulUse_Examples_Jan2011/MU_Rev0_HITSP_BaseC32v2.5_RequiredTemplateIds_FourErrors.xml");
            //XmlObject report1 = schematronValidation.createReport(config, message, "InitGatewaySchema");
           // XmlObject report = schematronValidation.createReport(config, message, "MU_HITSP_C32");
           // String message = schematronValidation.readFile("/Users/gunn/schematronValidator/schematronValidation/files/TestMessage-1.xml");
            //XmlObject report1 = schematronValidation.createReport(config, message, "InitGatewaySchema");
            XmlObject report = schematronValidation.createReport(warHome, config, message, "PRPA_IN201305UV02");
            //We need a Document
            String reportString = report.xmlText();
            ReportProcessor rp = new ReportProcessor();
            rp.documentParser(reportString);

             ArrayList<HashMap> schematronErrors = rp.getSchematronErrors();
             int m = schematronErrors.size();
             System.out.println("The size " + m);
             for (int z=0; z<m; z++) {
                HashMap errorMsg = (HashMap) schematronErrors.get(z);
                String errorMsgg = errorMsg.get("Message").toString();
                System.out.println("Error: " + errorMsg.get("Message"));
        }
            System.out.println("result passed? " + schematronValidation.isReportSuccess(report));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
