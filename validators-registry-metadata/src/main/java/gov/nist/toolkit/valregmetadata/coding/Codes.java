package gov.nist.toolkit.valregmetadata.coding;

import java.util.ArrayList;
import java.util.List;

public class Codes {
	private Uuid classificationScheme;
	private List<Code> codes = new ArrayList<Code>();
	private int pickIndex = -1;
	
	public Codes(Uuid classificationScheme) {
		this.classificationScheme = classificationScheme;
	}
	
	public Codes add(Code code) {
		codes.add(code);
		return this;
	}
	
	public Uuid getClassificationScheme() { return classificationScheme; }
	
	private int pickIndex() {
		pickIndex++;
		if (pickIndex >= codes.size()) pickIndex = 0;
		return pickIndex;
	}
	
	public Code pick() {
		int index = pickIndex();
		return codes.get(index);
	}
	
	public boolean exists(Code code) {
		for (Code c : codes) if (c.equals(code)) return true;
		return false;
	}
}
