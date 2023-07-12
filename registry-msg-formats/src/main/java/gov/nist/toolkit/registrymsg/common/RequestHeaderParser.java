package gov.nist.toolkit.registrymsg.common;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;

import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RequestHeaderParser {
    OMElement ele;
    RequestHeader header = new RequestHeader();


    public RequestHeaderParser(OMElement ele) {
        this.ele = ele;
    }

    public RequestHeader getRequestHeader() throws Exception {
        parse();
        return header;
    }

    public void parse() throws Exception {
        String zz = ele.getLocalName();
        if (!ele.getLocalName().equals("Header")) {
            // Something is wrong
            return;
        }

        AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='AttributeStatement']");
        Object o =  xpathExpression.selectSingleNode(ele);
        if (o == null) return;
        OMElement omEle = (OMElement) o;
        Iterator<OMElement> iterator = omEle.getChildElements();
        HashMap<String, OMElement> attributeListMap = new HashMap<>();
        while (iterator.hasNext()) {
            OMElement x = iterator.next();
            String name = x.getAttributeValue(new QName("Name"));
            attributeListMap.put(name, x.cloneOMElement());
        }

        header.omElement = omEle;
        header.attributeStatement = attributeListMap;

        header.samlAssertionID =                      evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']", "ID");
        header.samlAssertionIssueInstant =            evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']", "IssueInstant");
        header.samlAssertionVersion =                 evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']", "Version");
        header.samlAssertionIssuer =                  evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Issuer']");
        header.samlCanonicalizationMethodAlgorithm =  evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='SignedInfo']/*[local-name()='CanonicalizationMethod']", "Algorithm");
        header.samlSignatureMethodAlgorithm =         evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='SignedInfo']/*[local-name()='SignatureMethod']", "Algorithm");
        header.samlDigestMethodAlgorithm =            evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='SignedInfo']/*[local-name()='Reference']/*[local-name()='DigestMethod']", "Algorithm");
        header.samlDigestValue =                      evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='SignedInfo']/*[local-name()='Reference']/*[local-name()='DigestValue']");
        header.samlSignatureValue =                   evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='SignatureValue']");
        header.samlX509Certificate =                  evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='KeyInfo']/*[local-name()='X509Data']/*[local-name()='X509Certificate']");
        header.samlRSAKyValueModulus =                evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='KeyInfo']/*[local-name()='KeyValue']/*[local-name()='RSAKeyValue']/*[local-name()='Modulus']");
        header.samlRSAKeyValueExponent =              evaluateXPath(ele, "//*[local-name()='Security']/*[local-name()='Assertion']/*[local-name()='Signature']/*[local-name()='KeyInfo']/*[local-name()='KeyValue']/*[local-name()='RSAKeyValue']/*[local-name()='Exponent']");

        header.samlNHINHomeCommunityID = header.getAttributeValue("urn:nhin:names:saml:homeCommunityId");
        header.samlIHEHomeCommunityID  = header.getAttributeValue("urn:ihe:iti:xca:2010:homeCommunityId");
        header.samlPurposeOfUseCSP     = header.getAttributeValue("csp");
        header.samlPurposeOfUseValidatedAttributes = header.getAttributeValue("validated_attributes");




                System.out.println("SAML Assertion ID: " + header.samlAssertionID);
    }

    String evaluateXPath(OMElement ele, String expression) throws Exception  {
        AXIOMXPath xpathExpression = new AXIOMXPath (expression);
        Object o =  xpathExpression.selectSingleNode(ele);
        if (o == null) return null;

        OMElement omEle = (OMElement) o;
        String rtn = omEle.getText();
        return rtn;
    }

    String evaluateXPath(OMElement ele, String expression, String attribute) throws Exception  {
        try {
            AXIOMXPath xpathExpression = new AXIOMXPath(expression);
            Object o = xpathExpression.selectSingleNode(ele);
            if (o == null) return null;

            OMElement omEle = (OMElement) o;
            String rtn = null;
            rtn = omEle.getAttributeValue(new QName(attribute));

            return rtn;
        } catch (Exception e) {
            String z = e.toString();
            throw e;
        }
    }

    List<String> parseValueList(OMElement e, String xpath) throws Exception {
        ArrayList<String> rtn = new ArrayList<>();

        AXIOMXPath xpathExpression = new AXIOMXPath (xpath);
        List<Object> objectList =  xpathExpression.selectNodes(ele);
        if (objectList != null) {
            for (Object o: objectList) {
                OMElement omEle = (OMElement) o;
                String text = omEle.getText();
                rtn.add(text);
            }
        }
        return rtn;
    }
}
