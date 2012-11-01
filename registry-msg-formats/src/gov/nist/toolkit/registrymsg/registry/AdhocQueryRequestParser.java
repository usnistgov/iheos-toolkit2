package gov.nist.toolkit.registrymsg.registry;

import gov.nist.toolkit.registrysupport.MetadataSupport;

import org.apache.axiom.om.OMElement;

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
			request.adhocQueryRequestElement = MetadataSupport.firstDecendentWithLocalName(ele, "AdhocQueryRequest");
		request.adhocQueryElement = MetadataSupport.firstDecendentWithLocalName(ele, "AdhocQuery");
		if (request.adhocQueryElement == null) {
			throw new Exception( "Cannot find AdhocQuery element in request");
		}
		
		request.homeAtt = request.adhocQueryElement.getAttribute(MetadataSupport.home_qname);
		
		if (request.homeAtt != null)
			request.home = request.homeAtt.getAttributeValue();

		request.queryId = request.adhocQueryElement.getAttributeValue(MetadataSupport.id_qname);
		
	}
}
