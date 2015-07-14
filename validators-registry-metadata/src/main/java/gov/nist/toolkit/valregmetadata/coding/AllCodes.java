package gov.nist.toolkit.valregmetadata.coding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AllCodes {
	private Map<Uuid, Codes> allCodes = new HashMap<Uuid, Codes>();
	
	public AllCodes add(Codes codes) {
		allCodes.put(codes.getClassificationScheme(), codes);
		return this;
	}
	
	public boolean exists(Uuid classification, Code code) {
		Codes codes = allCodes.get(classification);
		if (codes == null) return false;
		return codes.exists(code);
	}
	
	public boolean isKnownClassification(Uuid classification) {
		return allCodes.containsKey(classification);
	}
	
	public Set<Uuid> definedClassifications() {
		return allCodes.keySet();
	}
	
	public Code pick(Uuid classification) {
		Codes codes = allCodes.get(classification);
		if (codes == null) return null;
		return codes.pick();
	}
}
