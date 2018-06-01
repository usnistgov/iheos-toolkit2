package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.testengine.engine.TransactionSettings;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import net.ihe.gazelle.sequoia.security.SOAPHeaderModifier;
import net.ihe.gazelle.simulator.sts.client.WSSEConstants;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by aberge on 14/06/17.
 */
public class SequoiaHeaderBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SequoiaHeaderBuilder.class);

    public static final String UTF_8 = "UTF-8";

    public static OMElement buildSequoiaSecurityHeader(String endpoint, String assertionStr, TransactionSettings settings) throws Exception {
        LOG.info("Creating security header for Sequoia");
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage emptySoapMessage = factory.createMessage();
        insertAssertionIntoSoapHeader(emptySoapMessage.getSOAPHeader(), assertionStr);
        String keystorePath = getKeystorePath(settings.environmentName);
        String keystorePassword = settings.securityParams.getKeystorePassword();
        String privateKeyAlias = getPrivateKeyAlias(settings.environmentName);
        LOG.info("call to SOAPHeader modifier");
        SOAPHeaderModifier modifier = new SOAPHeaderModifier("valid", "connectathon", "https://validation.sequoiaproject.org/gazelle-sts?wsdl", endpoint);
        Document withHeaders;
        try {
            withHeaders = modifier.appendSequoiaSecurityHeaders(emptySoapMessage.getSOAPPart(), keystorePath, privateKeyAlias, keystorePassword, keystorePassword);
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("Error calling SOAPHeaderModifier: " + e.getMessage());
            throw e;
        }
        if (withHeaders != null) {
            NodeList securityNodes = withHeaders.getElementsByTagNameNS(WSSEConstants.WSSE_NS, WSSEConstants.WSSE_LOCAL);
            if (securityNodes != null && securityNodes.getLength() > 0) {
                LOG.info("security node is present");
                Node security = securityNodes.item(0);
                ByteArrayOutputStream securityNodeAsBaos = nodeToOutputStream(security);
                String securityNodeAsString = securityNodeAsBaos.toString(UTF_8);
                return Util.parse_xml(securityNodeAsString);
            } else {
                LOG.error("An error occured when building Sequoia security header");
                throw new Exception("An error occured when building Sequoia security header");
            }
        } else {
            LOG.error("returned header is null");
            throw new Exception("Returned header is null");
        }
    }

    private static String getPrivateKeyAlias(String environmentName) {
        return getProperty(environmentName, "privateKeyAlias");
    }

    private static void insertAssertionIntoSoapHeader(SOAPHeader soapHeader, String assertionStr) throws Exception {
        Element securityElement = soapHeader.getOwnerDocument().createElementNS(WSSEConstants.WSSE_NS, WSSEConstants.WSSE_LOCAL);
        if (assertionStr != null) {
            LOG.info("Assertion to be inserted: " + assertionStr);
            Node assertion = createNodeFromString(assertionStr);
            Node importedAssertion;
            try {
                importedAssertion = soapHeader.getOwnerDocument().importNode(assertion.getFirstChild(), true);
            }catch (DOMException e){
                throw new Exception("Cannot import assertion to soap document: " + e.getMessage());
            }
            try {
                securityElement.appendChild(importedAssertion);
            }catch (DOMException e){
                throw  new Exception("Cannot append Assertion to Security element: " + e.getMessage());
            }
        } else {
            LOG.error("No assertion can be asserted, assertionStr set to null");
            throw new Exception("No assertion can be asserted, assertionStr set to null");
        }
        try {
            soapHeader.appendChild(securityElement);
        }catch (DOMException e){
            throw new Exception("Cannot append Security element to SOAP Header: " + e.getMessage());
        }
    }

    private static ByteArrayOutputStream nodeToOutputStream(Node node) {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(baos));
            return baos;
        } catch (TransformerException e) {
            return null;
        }
    }

    private static Node createNodeFromString(String content) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8"))));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getProperty(String environmentName, String propertyName ) {
        File propertiesFile = Installation.instance().getKeystorePropertiesFile(environmentName);
        if (!propertiesFile.exists() || propertiesFile.isDirectory())
            return null;
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = Io.getInputStreamFromFile(propertiesFile);
            props.load(is);
        }catch (IOException e){
            return null;
        } finally {
            if (is!=null) {
                IOUtils.closeQuietly(is);
            }
        }
        return props.getProperty(propertyName);
    }

    private static String getKeystorePath(String environmentName){
        return getProperty(environmentName, "keyStorePathForSignature");
    }





}
