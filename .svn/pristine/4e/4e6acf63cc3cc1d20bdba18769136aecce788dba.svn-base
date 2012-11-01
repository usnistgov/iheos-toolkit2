package gov.nist.toolkit.valregmsg.registry;

import java.util.ArrayList;
import java.util.List;

public class SQCodeAnd extends SQCodedTerm {

	public List<SQCodeOr> codeOrs;
	
	public SQCodeAnd() {
		codeOrs = new ArrayList<SQCodeOr>();
	}
	
	public void add(SQCodeOr or) {
		codeOrs.add(or);
		or.setIndex(codeOrs.size());  // so unique names can be generated
	}
	
	public List<SQCodeOr> getCodeOrs() {
		return codeOrs;
	}
	
	public String toString() {
		return "SQCodeAnd: [\n" +
		codeOrs + 
		"]\n";
	}

	public boolean isEmpty() {
		return codeOrs.size() == 0;
	}
	
	public List<String> getCodeVarNames() {
		List<String> names = new ArrayList<String>();
		
		for (SQCodeOr or : codeOrs) {
			names.add(or.getCodeVarName());
		}
		return names;
	}
	
	public List<String> getSchemeVarNames() {
		List<String> names = new ArrayList<String>();
		
		for (SQCodeOr or : codeOrs) {
			names.add(or.getSchemeVarName());
		}
		return names;
	}

	public boolean isMatch(List<String> coded_values) {
		for (SQCodeOr or : codeOrs) {
			if ( ! or.isMatch(coded_values))
				return false;
		}
		return true;
	}


}
