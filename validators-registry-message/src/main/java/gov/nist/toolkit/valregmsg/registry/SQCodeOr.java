package gov.nist.toolkit.valregmsg.registry;

import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

public class SQCodeOr extends SQCodedTerm {

	public class CodeLet {
		public String code;
		public String scheme;
		public String coded_term;
		
		public CodeLet(String value) throws XdsInternalException {
			String[] a = value.split("\\^");
			if (a.length != 3 || a[0] == null || a[0].equals("") || a[2] == null || a[2].equals("") )
				throw new XdsInternalException("CodeLet: code value " + value + "  is not in CE format (code^^scheme)");
			code = a[0];
			scheme = a[2];
			coded_term = value;
		}
		
		public String toString() {
			return coded_term;
		}
	}
	
	
	
	String varname;
	int index;   // used to make varname unique
	public List<CodeLet> values;
	public String classification;   // uuid
	public List<String> coded_terms;
	
	public SQCodeOr(String varname, String classification) {
		this.varname = varname;
		this.classification = classification;
		index = 0;  // means no index
		values = new ArrayList<CodeLet>();
		coded_terms = new ArrayList<String>();
	}
	
	public String toString() {
		return "SQCodeOr: [\n" +
		"varname=" + varname + "\n" +
		"index=" + index + "\n" +
		"values=" + values + "\n" +
		"classification=" + classification + "\n" +
		"]\n";
	}
	
	
	public void setIndex(int i) {  // so unique names can be generated
		index = i;
	}
	
	public void addValue(String value) throws XdsInternalException {
		values.add(new CodeLet(value));
		coded_terms.add(value);
	}
	
	public void addValues(List<String> values) throws XdsInternalException {
		for (String value : values) {
			addValue(value);
		}
	}
	
	public List<String> getCodes() {
		List<String> a = new ArrayList<String>();
		
		for (CodeLet cl : values) {
			a.add(cl.code);
		}
		return a;
	}

	public List<String> getSchemes() {
		List<String> a = new ArrayList<String>();
		
		for (CodeLet cl : values) {
			a.add(cl.scheme);
		}
		return a;
	}
	
	public String getCodeVarName() {
		if (index == 0)
			return codeVarName(varname) + "_code";
		return codeVarName(varname) + "_code_" + index;
	}
	
	public String getSchemeVarName() {
		if (index == 0)
			return codeVarName(varname) + "_scheme";
		return codeVarName(varname) + "_scheme_" + index;
	}

	public boolean isEmpty() {
		return values.size() == 0;
	}

	public boolean isMatch(String coded_value) {
		return coded_terms.contains(coded_value);
	}
	
	public boolean isMatch(List<String> coded_values) {
		for (String coded_value : coded_values) {
			if ( isMatch(coded_value))
				return true;
		}
		return false;
	}
	
}
