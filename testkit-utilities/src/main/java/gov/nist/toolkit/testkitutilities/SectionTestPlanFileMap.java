package gov.nist.toolkit.testkitutilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionTestPlanFileMap {
	Map<String, File> planFileMap;
	List<String> sectionNames;
	
	public SectionTestPlanFileMap() {
		planFileMap = new HashMap<String, File>();
		sectionNames = new ArrayList<String>();
	}
	
	public List<String> getSectionNames() {
		return sectionNames;
	}
	
	public File getPlanFile(String sectionName) {
		return planFileMap.get(sectionName);
	}
	
	public void put(String name, File file) {
		planFileMap.put(name, file);
		sectionNames.add(name);
	}
	
	public Collection<File> values() {
		return planFileMap.values();
	}
	
	public List<String> keySet() {
		return sectionNames;
	}
	
	public File get(String name) {
		return planFileMap.get(name);
	}
	
	public int size() {
		return sectionNames.size();
	}
	
	public File get(int i) {
		if (i >= sectionNames.size())
			return null;
		return planFileMap.get(sectionNames.get(i));
	}
}
