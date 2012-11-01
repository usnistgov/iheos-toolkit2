package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import java.util.ArrayList;
import java.util.List;

public class CSVTable {
	List<CSVEntry> entries = new ArrayList<CSVEntry>();

	
	public List<CSVEntry> entries() {
		return entries;
	}
	
	public int size() {
		return entries.size();
	}
	
	public CSVEntry get(int entry) {
		return entries.get(entry);
	}
	
	public String get(int entry, int field) {
		try {
			return entries.get(entry).get(field).trim();
		} catch (Exception e) {
			
		}
		return "";
	}
}
