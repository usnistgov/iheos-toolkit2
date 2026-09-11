package gov.nist.toolkit.soap.http;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import org.apache.axiom.om.OMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SoapFault {
    String faultCode = null;
    String faultReason = null;
    List<String> details = new ArrayList<String>();

    public enum FaultCodes { VersionMismatch, MustUnderstand, DataEncodingUnknown, Sender, Receiver };

    public SoapFault(FaultCodes code, String reason) {
        setFaultCode(code);
        faultReason = reason;
    }

    public SoapFault(String code, String reason) {
        if (code.contains(":"))
            code = code.split(":")[1];
        FaultCodes cod = null;
        for (FaultCodes fc : FaultCodes.values()) {
            if (fc.name().equals(code)) {
                cod = fc;
                break;
            }
        }
        setFaultCode(cod);
        faultReason = reason;
    }

    public void addDetail(String adetail) {
        details.add(adetail);
    }

    static SoapFault parse(String xmlBlock) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new org.xml.sax.InputSource(new StringReader(xmlBlock)));
            NodeList faults = doc.getElementsByTagNameNS("*", "Fault");
            if (faults.getLength() == 0)
                return null;
            Element fault = (Element) faults.item(0);
            String asText = elementToString(fault);
            return new SoapFault(asText);
        } catch (Exception e) {
            return null;
        }
    }

    private static String elementToString(Element element) {
        try {
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(element), new javax.xml.transform.stream.StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            return element.toString();
        }
    }

    public SoapFault(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            NodeList codeNodes = doc.getElementsByTagNameNS("*", "Code");
            if (codeNodes.getLength() > 0) {
                Element codeElement = (Element) codeNodes.item(0);
                NodeList valueNodes = codeElement.getElementsByTagNameNS("*", "Value");
                if (valueNodes.getLength() > 0) {
                    faultCode = valueNodes.item(0).getTextContent();
                }
            }
            if (faultCode != null && faultCode.contains(":"))
                faultCode = faultCode.substring(faultCode.indexOf(":") + 1);
            if (faultCode != null)
                faultCode = faultCode.trim();
            
            NodeList reasonNodes = doc.getElementsByTagNameNS("*", "Reason");
            if (reasonNodes.getLength() > 0) {
                Element reasonElement = (Element) reasonNodes.item(0);
                NodeList textNodes = reasonElement.getElementsByTagNameNS("*", "Text");
                if (textNodes.getLength() > 0) {
                    faultReason = textNodes.item(0).getTextContent();
                }
            }
            if (faultReason != null)
                faultReason = faultReason.trim();
        } catch (Exception e) {
            // If parsing fails, leave fields null
        }
    }

    String getCodeString(FaultCodes code) {
        switch (code) {
        case VersionMismatch:
            return "VersionMismatch";
        case MustUnderstand:
            return "MustUnderstand";
        case DataEncodingUnknown:
            return "DataEncodingUnknown";
        case Sender:
            return "Sender";
        case Receiver:
            return "Receiver";
        }
        return "Unknown";
    }

    String formattedDetails() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (String d : details) {
            if (!first) buf.append('\n');
            first = false;
            buf.append(d);
        }
        return buf.toString();
    }

    public OMElement getXML() {
        OMElement root = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_qnamens);

        OMElement code = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_code_qnamens);

        OMElement code_value = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_value_qnamens);
        code_value.setText(MetadataSupport.fault_pre + ":" + faultCode);
        code.addChild(code_value);
        root.addChild(code);

        OMElement reason = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_reason_qnamens);
        OMElement text = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_text_qnamens);
        text.addAttribute("lang", "en", MetadataSupport.xml_namespace);
        text.setText(faultReason + "\n" + formattedDetails());
        reason.addChild(text);
        root.addChild(reason);

        return root;
    }

    public String asString() {
        return new OMFormatter(getXML()).toString();
    }

    public void setFaultCode(FaultCodes code) {
        faultCode = getCodeString(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SoapFault soapFault = (SoapFault) o;

        if (faultCode != null ? !faultCode.equals(soapFault.faultCode) : soapFault.faultCode != null) return false;
        if (faultReason != null ? !faultReason.equals(soapFault.faultReason) : soapFault.faultReason != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = faultCode != null ? faultCode.hashCode() : 0;
        result = 31 * result + (faultReason != null ? faultReason.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Fault: code=" + (faultCode != null ? faultCode.trim() : null) + " reason=" + (faultReason != null ? faultReason.trim() : null);
    }
}
