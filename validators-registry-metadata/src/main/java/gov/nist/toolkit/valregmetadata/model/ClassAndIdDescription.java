package gov.nist.toolkit.valregmetadata.model;

import java.util.List;
import java.util.Map;

public class ClassAndIdDescription {
	List<String> definedSchemes;
	List<String> requiredSchemes;
	List<String> multipleSchemes;
	// id => name mapping
	Map<String, String> names;

	public List<String> getDefinedSchemes() {
		return definedSchemes;
	}

	public void setDefinedSchemes(List<String> definedSchemes) {
		this.definedSchemes = definedSchemes;
	}

	public List<String> getRequiredSchemes() {
		return requiredSchemes;
	}

	public void setRequiredSchemes(List<String> requiredSchemes) {
		this.requiredSchemes = requiredSchemes;
	}

	public List<String> getMultipleSchemes() {
		return multipleSchemes;
	}

	public void setMultipleSchemes(List<String> multipleSchemes) {
		this.multipleSchemes = multipleSchemes;
	}

	public Map<String, String> getNames() {
		return names;
	}

	public void setNames(Map<String, String> names) {
		this.names = names;
	}
}
