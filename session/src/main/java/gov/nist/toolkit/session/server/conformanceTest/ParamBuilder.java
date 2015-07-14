package gov.nist.toolkit.session.server.conformanceTest;

import java.util.HashMap;
import java.util.Map;

public class ParamBuilder {
	Map<String, String> sparms = new HashMap<String, String>();
	Map<String, Object> oparms = new HashMap<String, Object>();
	
	public ParamBuilder withParam(String name, Object value) {
		if (value instanceof String) 
			sparms.put(name, (String) value);
		else
			oparms.put(name, value);
		return this;
	}
	
	public Map<String, String> getSParms() { return sparms; }
	public Map<String, Object> getOParms() { return oparms; }
}
