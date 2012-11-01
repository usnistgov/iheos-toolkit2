package gov.nist.toolkit.simulators.sim.ig;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class XcQueryMockSoap {
	
	class Request {
		String endpoint;
		OMElement request;
		
		Request(String endpoint, OMElement request) {
			this.endpoint = endpoint;
			this.request = request;
		}
		
		public String getEndpoint() { return endpoint; }
		public OMElement getRequest() { return request; }
	}

	List<Request> requests = new ArrayList<Request>();
	List<OMElement> responses = new ArrayList<OMElement>();
	int responseIndex = 0;
	
	public void addResponse(OMElement ele) {
		responses.add(ele);
	}
	
	public List<Request> getRequests() { return requests; }
	
	public OMElement call(String endpoint, OMElement request) throws Exception {
		requests.add(new Request(endpoint, request));
		if (responses.size() > responseIndex) {
			OMElement response = responses.get(responseIndex);
			responseIndex++;
			return response;
		}
		throw new Exception("XcQueryMockSoap: response underflow: responseIndex = " + responseIndex + " responses = " + responses.size());
	}
	
}
