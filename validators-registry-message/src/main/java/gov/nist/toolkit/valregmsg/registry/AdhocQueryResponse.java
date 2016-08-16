package gov.nist.toolkit.valregmsg.registry;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;

import java.util.List;

public class AdhocQueryResponse extends Response {
	OMElement queryResult = null;

	public AdhocQueryResponse(short version, RegistryErrorListGenerator rel)  throws XdsInternalException {
		super(version, rel);

		init(version);
	}

	public AdhocQueryResponse(short version) throws XdsInternalException {
		super(version);

		init(version);
	}

	public AdhocQueryResponse() throws XdsInternalException {
		super(version_3);

		init(version_3);
	}

	public OMElement getRoot() {
		response.addChild(getQueryResult());
		return response;
//		return getQueryResult();
	}


	private void init(short version) {
		if (version == version_2) {
			response = MetadataSupport.om_factory.createOMElement("RegistryResponse", ebRSns);
			OMElement ahqr = MetadataSupport.om_factory.createOMElement("AdhocQueryResponse", ebQns);
			response.addChild(ahqr);
			OMElement sqr = null;
			sqr = MetadataSupport.om_factory.createOMElement("SQLQueryResult", ebQns);
			queryResult = sqr;
			ahqr.addChild(sqr);
		} else {
			response = MetadataSupport.om_factory.createOMElement("AdhocQueryResponse", ebQns);
		}
	}

	// called to getRetrievedDocumentsModel parent element of query results

	public OMElement getQueryResult() {
		if (queryResult != null)
			return queryResult;


		if (version == version_2) {
			OMElement adhocQueryResponse = MetadataSupport.om_factory.createOMElement("AdhocQueryResponse", ebQns);
			response.addChild(adhocQueryResponse);
			queryResult = MetadataSupport.om_factory.createOMElement("SQLQueryResult", ebQns);
			adhocQueryResponse.addChild(queryResult);
		} else {  // add RegistryObjectList
			queryResult = MetadataSupport.om_factory.createOMElement("RegistryObjectList", ebRIMns);
			//response.addChild(queryResult);
		}
		return queryResult;
	}

	public void addQueryResults(OMElement metadata)  throws XdsInternalException {
		OMElement res = getQueryResult();  // used for side effect if v3 and error - must
		// still have empty RegistryObjectList after RegistryErrorList
		if (metadata != null)
			res.addChild(Util.deep_copy(metadata));
	}

	public void addQueryResults(List<OMElement> metadatas)  throws XdsInternalException {
		OMElement res = getQueryResult();  // used for side effect if v3 and error - must
		// still have empty RegistryObjectList after RegistryErrorList
		if (metadatas != null)
			for (int i=0; i<metadatas.size(); i++) {
				res.addChild(Util.deep_copy((OMElement) metadatas.get(i)));
			}
	}

	public void addQueryResults(List<OMElement> metadatas, boolean deepCopy)  throws XdsInternalException {
		if (deepCopy) {
			addQueryResults(metadatas);
			return;
		}
		OMElement res = getQueryResult();  // used for side effect if v3 and error - must
		// still have empty RegistryObjectList after RegistryErrorList

		if (metadatas != null)
			for (int i=0; i<metadatas.size(); i++) {
				OMElement ele = metadatas.get(i);
				OMContainer parent = ele.getParent();
				if (parent != null)
					ele.detach();
				res.addChild(ele);
			}

	}

}
