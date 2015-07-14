package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;
import gov.nist.toolkit.valregmsg.registry.SQCodedTerm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DocEntryCollection extends RegObCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	List<DocEntry> entries;
	
	transient public DocEntryCollection parent = null;
	
	
	public String toString() {
		return entries.size() + " DocumentEntries";
	}
	
	public void init() {
		entries = new ArrayList<DocEntry>();
	}
	
//	// caller handles synchronization
//	public void applyDelta(Delta d) {
//		if (d.idsToDelete != null)
//			for (String id : d.idsToDelete)
//				syncDelete(id);
//		
//		if (d.entriesToAdd != null) 
//			for (Object o : d.entriesToAdd) 
//				entries.add((DocEntry) o);
//		
//	}

	// caller handles synchronization
	public void delete(String id) {
		DocEntry toDelete = null;
		for (DocEntry a : entries) {
			if (a.id.equals(id)) {
				toDelete = a;
				break;
			}
		}
		if (toDelete != null)
			entries.remove(toDelete);
	}
	

	public String statsToString() {
		int siz = 0;
		if (parent != null)
			siz = parent.entries.size();
		return (siz + entries.size()) + " DocumentEntries";
	}
	

	
	public Ro getRo(String id) {
		for (DocEntry de : entries) {
			if (de.id.equals(id))
				return de;
		}
		if (parent == null)
			return null;
		return parent.getRo(id);
	}
	
	public DocEntry getById(String id) {
		if (id == null)
			return null;
		for (DocEntry de : entries) {
			if (id.equals(de.id))
				return de;
		}
		if (parent == null)
			return null;
		return parent.getById(id);
	}
	
	public List<DocEntry> getByUid(String uid) {
		List<DocEntry> des = new ArrayList<DocEntry>();
		if (uid == null)
			return des;
		for (DocEntry de : entries) {
			if (uid.equals(de.uid))
				des.add(de);
		}
		if (parent != null)
			des.addAll(parent.getByUid(uid));
		return des;
	}
	
	public List<DocEntry> getByLid(String lid) {
		List<DocEntry> des = new ArrayList<DocEntry>();
		if (lid == null)
			return des;
		for (DocEntry de : entries) {
			if (lid.equals(de.lid))
				des.add(de);
		}
		if (parent != null)
			des.addAll(parent.getByLid(lid));
		return des;
	}
	
	public DocEntry getLatestVersion(String lid) {
		List<DocEntry> des = getByLid(lid);
		
		DocEntry latest = null;
		int version = -1;
		
		for (DocEntry de : des) {
			if (de.version > version) {
				version = de.version;
				latest = de;
			}
		}
		
		return latest;
	}
	
	public DocEntry getPreviousVersion(String id) {
		DocEntry de = getById(id);
		return getPreviousVersion(de);
	}
	
	public DocEntry getPreviousVersion(DocEntry de) {
		int ver = de.version;
		String lid = de.lid;
		List<DocEntry> des = getByLid(lid);
		for (DocEntry d : des) {
			if (d.version == ver-1) 
				return d;
		}
		return null;
	}
		
	public List<DocEntry> findByPid(String pid) {
		List<DocEntry> results = new ArrayList<DocEntry>();
		
		for (int i=0; i<entries.size(); i++) {
			DocEntry de = entries.get(i);
			if (pid.equals(de.pid))
				results.add(de);
		}
		if (parent != null)
			results.addAll(parent.findByPid(pid));
		return results;
	}
	
	public List<DocEntry> filterByStatus(List<StatusValue> statuses, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (statuses == null || statuses.isEmpty()) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			if (statuses.contains(de.getAvailabilityStatus()))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}

	public List<DocEntry> filterByAuthorPerson(List<String> authorPersons, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (authorPersons == null || authorPersons.isEmpty()) return docs;
		nextDoc:
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			for (String value : de.authorNames) {
				if (matchAnyAuthor(value, authorPersons))
					continue nextDoc;
			}
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	// _ match any character
	// % matches any string
	// this is too complicated to do with Java Regex ...
	static boolean matchAnyAuthor(String value, List<String> authors) {
		for (String author : authors) {
			if (matchAuthor(value, author))
				return true;
		}
		return false;
	}
	
	static boolean matchAuthor(String value, String pattern) {
		int vi = 0;
		
		for (int pi=0; pi<pattern.length(); pi++) {
			if (pattern.charAt(pi) == '_') {
				vi++;
			} else if (pattern.charAt(pi) == '%') {
				String after = getAfterText(pattern, pi);
				int afterI = value.indexOf(after, vi);
				if (afterI == -1)
					return false;
				vi = afterI;
			} else {
				if (pattern.charAt(pi) != value.charAt(vi))
					return false;
				vi++;
			}
			if (pi + 1 == pattern.length() && pattern.charAt(pi) == '%')
				return true;
			if (pattern.length() == pi+1 && value.length() == vi)
				return true;
			if (pi + 2 == pattern.length() && pattern.charAt(pi + 1) == '%')
				return true;
			if (value.length() == vi)
				return false;
		}
		
		return false;
	}
	
	// return text after % char at startAt and before next % (or end if no next %)
	// expect initial % to possibly be %% 
	static String getAfterText(String pattern, int startAt) {
		while (pattern.charAt(startAt) == '%') {
			startAt++;
			if (startAt == pattern.length())
				return "";
		}
		
		int endAt = startAt;
		
		while(pattern.charAt(endAt) != '%') {
			endAt++;
			if (endAt == pattern.length())
				return pattern.substring(startAt);
		}
		return pattern.substring(startAt, endAt);
	}
	
	public List<DocEntry> filterByCreationTime(String from, String to, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if ((from == null || from.equals("")) && (to == null || to.equals(""))) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			String creationTime = de.creationTime;
			if (timeCompare(creationTime, from, to))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	public List<DocEntry> filterByServiceStartTime(String from, String to, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if ((from == null || from.equals("")) && (to == null || to.equals(""))) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			String time = de.serviceStartTime;
			if (timeCompare(time, from, to))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	public List<DocEntry> filterByServiceStopTime(String from, String to, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if ((from == null || from.equals("")) && (to == null || to.equals(""))) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			String time = de.serviceStopTime;
			if (timeCompare(time, from, to))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	static public boolean timeCompare(String att, String from, String to) {
		if (att == null || att.equals(""))
			return false;
		if (from != null && !from.equals("")) {
			if ( !  (from.compareTo(att) <= 0))
				return false;
		}
		if (to != null && !to.equals("")) {
			if ( !  (att.compareTo(to) < 0)) 
				return false;
		}
		return true;
	}

	public List<DocEntry> filterByFormatCode(SQCodedTerm values, List<DocEntry> docs) throws Exception {
		if (values instanceof SQCodeOr) 
			return filterByFormatCode((SQCodeOr) values, docs);
		throw new Exception("DocEntryCollection#filterByFormatCode cannot decode " + values.getClass().getName());
	}
	
	public List<DocEntry> filterByFormatCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			if (values.isMatch(de.formatCode))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}

	public List<DocEntry> filterByClassCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			if (values.isMatch(de.classCode))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	public List<DocEntry> filterByTypeCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			if (values.isMatch(de.typeCode))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}

	public List<DocEntry> filterByPracticeSettingCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			if (values.isMatch(de.practiceSettingCode))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	public List<DocEntry> filterByHcftCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			if (values.isMatch(de.healthcareFacilityTypeCode))
				continue;
			docs.remove(i);
			i--;
		}
		return docs;
	}

	public List<DocEntry> filterByEventCode(SQCodeAnd values, List<DocEntry> docs) {
		for (SQCodeOr ors : values.codeOrs) {
			docs = filterByEventCode(ors, docs);
		}
		return docs;
	}
	
	public List<DocEntry> filterByEventCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		eachdoc:
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			for (String val : de.eventCode) {
				if (values.isMatch(val))
					continue eachdoc;
			}
			docs.remove(i);
			i--;
		}
		return docs;
	}
	
	public List<DocEntry> filterByConfCode(SQCodeAnd values, List<DocEntry> docs) {
		for (SQCodeOr ors : values.codeOrs) {
			docs = filterByConfCode(ors, docs);
		}
		return docs;
	}
	
	public List<DocEntry> filterByConfCode(SQCodedTerm values, List<DocEntry> docs) throws Exception {
		if (values instanceof SQCodeOr) 
			return filterByConfCode((SQCodeOr) values, docs);
		throw new Exception("DocEntryCollection#filterByConfCode cannot decode SQCodedTerm");
	}
	


	public List<DocEntry> filterByConfCode(SQCodeOr values, List<DocEntry> docs) {
		if (docs.isEmpty()) return docs;
		if (values == null || values.isEmpty()) return docs;
		eachdoc:
		for (int i=0; i<docs.size(); i++) {
			DocEntry de = docs.get(i);
			for (String val : de.confidentialityCode) {
				if (values.isMatch(val))
					continue eachdoc;
			}
			docs.remove(i);
			i--;
		}
		return docs;
	}

	public boolean hasObject(String id) {
		if (id == null)
			return false;
		for (DocEntry a : entries) {
			if (a.id == null)
				continue;
			if (a.id.equals(id))
				return true;
		}
		if (parent == null)
			return false;
		return parent.hasObject(id);
	}
	
	public boolean hasObject(String id, List<DocEntry> docEntries) {
		for (DocEntry de : docEntries) {
			if (id.equals(de.getId()))
				return true;
		}
		return false;
	}

	public Ro getRoByUid(String uid) {
		for (DocEntry de : entries) {
			if (de.uid.equals(uid))
				return de;
		}
		if (parent == null)
			return null;
		return parent.getRoByUid(uid);
	}



	public List<?> getAllRo() {
		if (parent == null)
			return entries;
		List<DocEntry> de = new ArrayList<DocEntry>();
		de.addAll(entries);
		de.addAll(parent.entries);
		return de;
	}

}
