package gov.nist.toolkit.registrymsg.registry;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;

public class AdhocQueryRequestParser {
    OMElement ele;
    AdhocQueryRequest request = new AdhocQueryRequest();


    public AdhocQueryRequestParser(OMElement ele) {
        this.ele = ele;
    }

    public AdhocQueryRequest getAdhocQueryRequest() throws Exception {
        parse();
        return request;
    }

    public void parse() throws Exception {
        if (ele.getLocalName().equals("AdhocQueryRequest"))
            request.adhocQueryRequestElement = ele;
        else
            request.adhocQueryRequestElement = XmlUtil.firstDecendentWithLocalName(ele, "AdhocQueryRequest");
        request.adhocQueryElement = XmlUtil.firstDecendentWithLocalName(ele, "AdhocQuery");
        if (request.adhocQueryElement == null) {
            throw new Exception( "Cannot find AdhocQuery element in request");
        }

        request.homeAtt = request.adhocQueryElement.getAttribute(MetadataSupport.home_qname);

        if (request.homeAtt != null)
            request.home = request.homeAtt.getAttributeValue();

        request.queryId = request.adhocQueryElement.getAttributeValue(MetadataSupport.id_qname);

        request.patientId = "";
        AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='Slot'][@name = '$XDSDocumentEntryPatientId']/*[local-name()='ValueList']/*[local-name()='Value']");
        Object o =  xpathExpression.selectSingleNode(ele);
        if (o == null) return;
        OMElement omEle = (OMElement) o;
        String text = omEle.getText();
        if (text == null || text.equals("")) return;
        if (text.startsWith("'")) text = text.substring(1);
        if (text.endsWith("'")) text = text.substring(0, text.length() - 1);
        request.patientId = text;
    }
}
