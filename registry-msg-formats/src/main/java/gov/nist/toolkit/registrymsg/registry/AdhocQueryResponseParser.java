package gov.nist.toolkit.registrymsg.registry;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class AdhocQueryResponseParser {
	OMElement ele;
	AdhocQueryResponse response = new AdhocQueryResponse();

    public AdhocQueryResponseParser(OMElement ele) throws XdsInternalException {
		this.ele = ele;

		response.ele = ele;

		response.status = ele.getAttributeValue(MetadataSupport.status_qname);

		response.registryErrorList = new RegistryErrorListParser(ele).getRegistryErrorList();

		response.registryObjectListEle = XmlUtil.firstDecendentWithLocalName(ele, "RegistryObjectList");

		response.registryErrorListEle = XmlUtil.firstDecendentWithLocalName(ele, "RegistryErrorList");
}

	public AdhocQueryResponse getResponse() { return response; }
}
