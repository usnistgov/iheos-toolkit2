/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.toolkit.valregmsg.validation.schematron;

import gov.nist.toolkit.valregmsg.validation.schematron.exception.BadConfigurationFileException;
import gov.nist.toolkit.valregmsg.validation.schematron.exception.UnknownMessageTypeException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author mccaffrey
 */
public class schematronValidation {

    // PRPA_IN201301UV02

    public static XmlObject createReport(String warHome, File configuration, String message, String messageType) throws XmlException, IOException, UnknownMessageTypeException, SAXException, ParserConfigurationException, BadConfigurationFileException {

        XmlObject config = XmlObject.Factory.parse(configuration);
        XmlObject[] skeletonLocations = config.selectPath("/Configuration/files/skeletonLocation");
        if(skeletonLocations == null || skeletonLocations.length < 1) 
            throw new BadConfigurationFileException("Skeleton Path not set!");
        String skeletonLocation = skeletonLocations[0].newCursor().getTextValue();
        XmlObject[] messageTypes = config.selectPath("/Configuration/messageTypes/messageType[@id='" + messageType + "']");
        if (messageTypes == null || messageTypes.length < 1)
            throw new UnknownMessageTypeException("Message type '" + messageType + "' not found in configuration file.");
        MessageType messageInformation = new MessageType(messageTypes[0]);


        SchemaValidationErrorHandler errorHandler = new SchemaValidationErrorHandler();
        System.out.print("warHome[SchematronValidator]: " + warHome + "\n");
        System.out.print("skeletonLocation: " + warHome + File.separator + skeletonLocation + "\n");
        System.out.print("warHome[SchematronValidator]: " + warHome + "\n");
        Document doc = schematronValidation.validateWithSchema(message, errorHandler, warHome + File.separator + messageInformation.getSchemaLocation());
        String schematronResult = schematronValidation.validateWithSchematron(doc, warHome + File.separator + messageInformation.getSchematronLocation(), warHome + File.separator + skeletonLocation, "errors");

        Node schematronResultNode = null;
        schematronResultNode = schematronValidation.stringToDom(schematronResult);

        Node[] messageList = { schematronResultNode };
        Document result = schematronValidation.generateReport(doc, errorHandler, messageList);

        return XmlObject.Factory.parse(result);

    }

    public static boolean isReportSuccess(XmlObject report) {

        XmlObject[] resultsOfTest = report.selectPath("/Report/ReportHeader/ResultOfTest");

        if(!(resultsOfTest.length > 0))
            return false;
        XmlObject resultOfTest = resultsOfTest[0];
        if("Passed".equalsIgnoreCase(resultOfTest.newCursor().getTextValue()))
                return true;
        return false;

    }

    public static void main(String[] args) {

        File config = new File("/Users/gunn/schematronValidator/schematronValidation/files/schematronValidationConfig.xml");

        //File config = new File("/Users/gunn/Documents/workplace_metrix/shared-lib/lib/schematronTool/files/schematronValidationConfig.xml");

//        String message = "<PRPA_IN201301UV02 xmlns=\"urn:hl7-org:v3\">" +
  //                       "</PRPA_IN201301UV02>";

        
        try {
           String message = readFile("/Users/gunn/Desktop/306NameTest.xml");
           String warHome = "";
           //String message = readFile("/Users/gunn/Desktop/NHINToolDistribution/SampleTestFiles/MeaningfulUse_Examples_Jan2011/MU_Rev0_HITSP_BaseC32v2.5_RequiredTemplateIds_FourErrors.xml");
            //XmlObject report1 = schematronValidation.createReport(config, message, "InitGatewaySchema");

            XmlObject report = schematronValidation.createReport(warHome, config, message, "NHIN_test_SD0001");
            //XmlObject report = schematronValidation.createReport(config, message, "MU_HITSP_C32");
            //System.out.println(report1.toString());
            System.out.println(report.toString());

            System.out.println("result passed? " + schematronValidation.isReportSuccess(report));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new FileReader(new File(path)));
        String line = null;
        StringBuilder xml = new StringBuilder();
        while((line = input.readLine()) != null)
            xml.append(line);
        return xml.toString();
    }

    private static Document generateReport(Document doc, SchemaValidationErrorHandler errorHandler,
            Node[] messages) {

        Document result = null;
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            result = builder.newDocument();
        } catch(ParserConfigurationException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        Element report = result.createElement("Report");
        result.appendChild(report);

        int errorCount = errorHandler.getNumberErrors() + schematronValidation.getMessageCount(messages);
        // int warningCount = warnings.getFirstChild().getChildNodes().getLength();

        Element header = schematronValidation.createHeader(result, errorCount);
        report.appendChild(header);

        //if(errorHandler.hasErrors()) {
        Element schemaErrorReport = schematronValidation.createSchemaErrorReport(result, errorHandler);
        report.appendChild(schemaErrorReport);
        //}

        if(messages != null) {
            for(int i = 0; i < messages.length; i++) {
                Node message = messages[i];
                report.appendChild(result.importNode(message.getFirstChild(), true));
            }
        }
/*
        Element testObject = result.createElement("TestObject");

            if(doc != null) {
                testObject.appendChild(result.importNode(doc.getDocumentElement(), true));
            } else {
                testObject.setTextContent("Error: Could not read file to generate test object.  Verify it is valid XML.");
            }
        report.appendChild(testObject);
 */
        return result;
    }


    private static Element createHeader(Document result, int errorCountInt) {

        Element reportHeader = result.createElement("ReportHeader");

        Element validationStatus = result.createElement("ValidationStatus");
        validationStatus.setTextContent("Complete");
        reportHeader.appendChild(validationStatus);

        Element serviceName = result.createElement("ServiceName");
        serviceName.setTextContent("NHIN Validation Tools");
        reportHeader.appendChild(serviceName);

        Element dateOfTest = result.createElement("DateOfTest");
        dateOfTest.setTextContent(schematronValidation.createDateOfTest());
        reportHeader.appendChild(dateOfTest);

        Element timeOfTest = result.createElement("TimeOfTest");
        timeOfTest.setTextContent(schematronValidation.createTimeOfTest());
        reportHeader.appendChild(timeOfTest);

        Element resultOfTest = result.createElement("ResultOfTest");
        if(errorCountInt == 0)
            resultOfTest.setTextContent("Passed");
        else
            resultOfTest.setTextContent("Failed");
        reportHeader.appendChild(resultOfTest);

        Element errorCount = result.createElement("ErrorCount");
        errorCount.setTextContent(String.valueOf(errorCountInt));
        reportHeader.appendChild(errorCount);

        return reportHeader;
    }


    private static int getMessageCount(Node[] messages) {

        if(messages == null || messages.length == 0) return 0;

        int count = 0;
        for(int i = 0; i < messages.length; i++) {
            Node message = messages[i];
            count += message.getFirstChild().getChildNodes().getLength();
        }
        return count;
    }

    private static Element createSchemaErrorReport(Document doc, SchemaValidationErrorHandler errorHandler) {

        Element result = doc.createElement("Results");
        result.setAttribute("severity", "schemaViolation");        
        if(errorHandler.hasErrors()) {
            Iterator<String> it = errorHandler.getErrors().iterator();
            while(it.hasNext()) {
                Element issue = doc.createElement("issue");
                result.appendChild(issue);

                Element message = doc.createElement("message");
                message.setTextContent(it.next());
                issue.appendChild(message);
            }
        }

        return result;
    }

    private static String createDateOfTest() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private static String createTimeOfTest() {
        DateFormat dateFormat = new SimpleDateFormat("HHmmss.SSSS ZZZZ");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private static Document validateWithSchema(String xml, SchemaValidationErrorHandler handler, String schemaLocation) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");

        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
                schemaLocation);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
            return null;
        }

        builder.setErrorHandler(handler);
        Document doc = null;
        StringReader stringReader = new StringReader(xml);
        InputSource inputSource = new InputSource(stringReader);
        try {
            doc = builder.parse(inputSource);
        } catch(SAXException e) {
            System.out.println("Message is not valid XML.");
            handler.addError("Message is not valid XML.", null);
            e.printStackTrace();
        } catch(IOException e) {
            System.out.println("Message is not valid XML.  Possible empty message.");
            handler.addError("Message is not valid XML.  Possible empty message.", null);
            e.printStackTrace();
        }
        stringReader.close();
        return doc;
    }

    // validateWithSchematron( ... ) does schematron validation, but not in the
    // most efficient way.  For stable schematron, it would be more efficient
    // to run the schematron through the skeleton transform once, save that
    // transformation to a file and then simply reuse that transform rather than
    // generating it on every run.  That is left as an exercise for the
    // implementor.

    private static String validateWithSchematron(Document xml, String schematronLocation, String skeletonLocation, String phase) {

        StringBuilder result = new StringBuilder();
        System.out.print("schematronLocation: " + schematronLocation + "\n");
        System.out.print("skeletonLocation: " + skeletonLocation + "\n");
        File schematron = new File(schematronLocation);
        File skeleton = new File(skeletonLocation);
        Node schematronTransform = schematronValidation.doTransform(schematron, skeleton, phase);
        result.append(schematronValidation.doTransform(xml, schematronTransform));
        return result.toString();
    }

    private static Node doTransform(File originalXml, File transform, String phase) {

        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        DOMResult result = new DOMResult();
        try {
            Source xmlSource = new StreamSource(originalXml);
            Source xsltSource = new StreamSource(transform);

            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
            transformer.setParameter("phase", phase);
            transformer.transform(xmlSource, result);
        } catch(TransformerConfigurationException tce) {
            tce.printStackTrace();
            return null;
        } catch(TransformerException te) {
            te.printStackTrace();
            return null;
        } finally {
            System.clearProperty("javax.xml.transform.TransformerFactory");
        }
        return result.getNode();
    }


    private static String doTransform(Document originalXml, Node transform) {

        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(os);
        try {
            Source xmlSource = new DOMSource(originalXml);
            Source xsltSource = new DOMSource(transform);

            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
            transformer.transform(xmlSource, result);
        } catch(TransformerConfigurationException tce) {
            tce.printStackTrace();
            return null;
        } catch(TransformerException te) {
            te.printStackTrace();
            return null;
        } finally {
            System.clearProperty("javax.xml.transform.TransformerFactory");
        }
        return os.toString();
    }

    private static Document stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

}
