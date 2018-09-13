package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.valregmsg.registry.SQCodeOr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubSetCollection extends RegObCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<SubSet> subSets;
	
	transient public SubSetCollection parent = null;

	public List<SubSet> getAll() {
		List<SubSet> all = new ArrayList<>();

		all.addAll(subSets);
		SubSetCollection theParent = parent;
		while (theParent != null) {
			all.addAll(theParent.getAll());
			theParent = theParent.parent;
		}

		return all;
	}

	public List<SubSet> getAllForUpdate() {
		return subSets;
	}

	public String toString() {
		return getAll().size() + " Submission Sets";
	}
	
	public void init() {
		subSets = new ArrayList<>();
	}
	
	// caller handles synchronization
	public void delete(String id) {
		SubSet toDelete = null;
		for (SubSet a : getAll()) {
			if (a.id.equals(id)) {
				toDelete = a;
				break;
			}
		}
		if (toDelete != null)
			getAllForUpdate().remove(toDelete);
	}

	public int size() { return getAll().size(); }

	
	public Ro getRo(String id) {
		for (SubSet de : getAll()) {
			if (de.id.equals(id))
				return de;
		}
		if (parent == null)
			return null;
		return parent.getRo(id);
	}
	
	public SubSet getById(String id) {
		if (id == null)
			return null;
		for (SubSet s : getAll()) {
			if (id.equals(s.id))
				return s;
		}
		if (parent == null)
			return null;
		return parent.getById(id);
	}
	

	
	public String statsToString() {
		int parentStats = 0;
		if (parent != null)
			parentStats = parent.getAll().size();
		return (parentStats + getAll().size()) + " SubmissionSets";
	}

	public boolean hasObject(String id) {
		if (id == null)
			return false;
		for (SubSet a : getAll()) {
			if (a.id.equals(id))
				return true;
		}
		if (parent == null)
			return false;
		return parent.hasObject(id);
	}

	public Ro getRoByUid(String uid) {
		for (SubSet s : getAll()) {
			if (s.uid.equals(uid))
				return s;
		}
		if (parent == null)
			return null;
		return parent.getRoByUid(uid);
	}

	public SubSet getByUid(String uid) {
		for (SubSet s : getAll()) {
			if (s.uid.equals(uid))
				return s;
		}
		if (parent == null)
			return null;
		return parent.getByUid(uid);
	}
	
	public List<SubSet> findByPid(String pid) {
		List<SubSet> results = new ArrayList<SubSet>();
		
		for (int i=0; i<getAll().size(); i++) {
			SubSet s = getAll().get(i);
			if (pid.equals(s.pid))
				results.add(s);
		}
		
		if (parent != null) 
			results.addAll(parent.findByPid(pid));
		
		return results;
	}
	
	public List<SubSet> filterBySourceId(List<String> sourceIds, List<SubSet> subSets) {
		if (subSets.isEmpty()) return subSets;
		if (sourceIds == null || sourceIds.isEmpty()) return subSets;
		for (int i=0; i<subSets.size(); i++) {
			SubSet ss = subSets.get(i);
			if (sourceIds.contains(ss.sourceId))
				continue;
			subSets.remove(i);
			i--;
		}
		return subSets;
	}

	public List<SubSet> filterBySubmissionTime(String from, String to, List<SubSet> ss) {
		if (ss.isEmpty()) return ss;
		if ((from == null || from.equals("")) && (to == null || to.equals(""))) return ss;
		for (int i=0; i<ss.size(); i++) {
			SubSet s = ss.get(i);
			String submissionTime = s.submissionTime;
			if (DocEntryCollection.timeCompare(submissionTime, from, to))
				continue;
			ss.remove(i);
			i--;
		}
		return ss;
	}
	
	public List<SubSet> filterByAuthorPerson(String authorPerson, List<SubSet> sss) {
		if (sss.isEmpty()) return sss;
		if (authorPerson == null) return sss;
		List<String> authorPersons = new ArrayList<String>();
		authorPersons.add(authorPerson);

		nextSS:
			for (int i=0; i<sss.size(); i++) {
				SubSet ss = sss.get(i);
				for (String value : ss.authorNames) {
					if (DocEntryCollection.matchAnyAuthor(value, authorPersons))
						continue nextSS;
				}
				sss.remove(i);
				i--;
			}
		return sss;
	}
	
	public List<SubSet> filterByContentTypeCode(SQCodeOr values, List<SubSet> ss) {
		if (ss.isEmpty()) return ss;
		if (values == null || values.isEmpty()) return ss;
		for (int i=0; i<ss.size(); i++) {
			SubSet s = ss.get(i);
			if (values.isMatch(s.contentType))
				continue;
			ss.remove(i);
			i--;
		}
		return ss;
	}


	public List<SubSet> filterByStatus(List<StatusValue> statuses, List<SubSet> ss) {
		if (ss.isEmpty()) return ss;
		if (statuses == null || statuses.isEmpty()) return ss;
		for (int i=0; i<ss.size(); i++) {
			SubSet s = ss.get(i);
			if (statuses.contains(s.getAvailabilityStatus()))
				continue;
			ss.remove(i);
			i--;
		}
		return ss;
	}

	public List<?> getAllRo() {
			return getAll();
	}

	@Override
	public List<String> getIds() {
		List<String> ids = new ArrayList<>();
		for (SubSet a : getAll()) ids.add(a.getId());
		return ids;
	}

	@Override
	public List<?> getNonDeprecated() {
		List<SubSet> nonDep = new ArrayList<>();
		for (SubSet a : getAll()) {
			if (!a.isDeprecated())
				nonDep.add(a);
		}
		if (parent != null)
			return parent.getNonDeprecated();
		return nonDep;
	}


}
