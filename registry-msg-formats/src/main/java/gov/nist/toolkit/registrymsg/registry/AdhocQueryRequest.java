package gov.nist.toolkit.registrymsg.registry;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

import java.util.List;

public class AdhocQueryRequest {
	String home;
	String queryId;
	OMElement adhocQueryElement;
	OMElement adhocQueryRequestElement;
	OMAttribute homeAtt;
    String patientId = null;
	List<String> documentEntryObjectTypeList = null;

    public String getHome() {
		return home;
	}
	public String getQueryId() {
		return queryId;
	}
	public OMElement getAdhocQueryElement() {
		return adhocQueryElement;
	}
	public OMElement getAdhocQueryRequestElement() {
		return adhocQueryRequestElement;
	}
	public OMAttribute getHomeAtt() {
		return homeAtt;
	}
    public String getPatientId() { return patientId; }

	public List<String> getDocumentEntryObjectTypeList() {
		return documentEntryObjectTypeList;
	}

	public String toString() {
		return "AdhocQueryRequest: queryId=" + queryId + " home=" + home;
	}
}
