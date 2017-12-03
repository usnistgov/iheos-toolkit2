package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssocCollection extends RegObCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public List<Assoc> assocs = new ArrayList<Assoc>();
	
	transient public AssocCollection parent = null;

	
	public String toString() {
		return assocs.size() + " Associations";
	}
	
	public void init() {
		assocs = new ArrayList<Assoc>();
	}
	
	// caller handles synchronization
	public void delete(String id) {
		Assoc toDelete = null;
		for (Assoc a : assocs) {
			if (a.id.equals(id)) {
				toDelete = a;
				break;
			}
		}
		if (toDelete != null)
			assocs.remove(toDelete);
	}

	public int size() { return assocs.size(); }
	
	public Ro getRo(String id) {
		for (Assoc de : assocs) {
			if (de.id.equals(id))
				return de;
		}
		if (parent == null)
			return null;
		else
			return parent.getRo(id);
	}
	

	
	public String statsToString() {
		int parentCount = 0;
		if (parent != null)
			parentCount = parent.assocs.size();
		return (parentCount + assocs.size()) + " Associations";
	}
	

	public Assoc getById(String id) {
		if (id == null)
			return null;
		for (Assoc a : assocs) {
			if (id.equals(a.id))
				return a;
		}
		if (parent == null)
			return null;
		else
			return parent.getById(id);
	}

	public boolean hasObject(String id) {
		if (id == null)
			return false;
		for (Assoc a : assocs) {
			if (a.id.equals(id))
				return true;
		}
		if (parent == null)
			return false;
		else
			return parent.hasObject(id);
	}

	public Ro getRoByUid(String uid) {
		return null;
	}

	@Override
	public List<String> getIds() {
		List<String> ids = new ArrayList<>();
		for (Assoc a : assocs) ids.add(a.getId());
		return ids;
	}

	/**
	 * Any of the parameters may be null implying ANY.
	 * @param sourceId
	 * @param destId
	 * @param type
	 * @return
	 */
	public List<Assoc> getBySourceDestAndType(String sourceId, String targetId, RegIndex.AssocType type) {
		List<Assoc> myassocs = new ArrayList<Assoc>();
		
		for (Assoc a : assocs) {
			if ( sourceId != null && ! a.from.equals(sourceId))
				continue;
			if ( targetId != null && ! a.to.equals(targetId))
				continue;
			if ( type != null && a.type != type)
				continue;
			myassocs.add(a);
		}
		
		if (parent != null)
			myassocs.addAll(parent.getBySourceDestAndType(sourceId, targetId, type));
		
		return myassocs;
	}

	public List<Assoc> getBySourceOrDest(String sourceId, String targetId) {
		List<Assoc> myassocs = new ArrayList<Assoc>();
		
		for (Assoc a : assocs) {
			if ( a.from.equals(sourceId)) {
				myassocs.add(a);
				continue;
			}
			if ( a.to.equals(targetId)) {
				myassocs.add(a);
				continue;
			}
		}
		
		if (parent != null)
			myassocs.addAll(parent.getBySourceOrDest(sourceId, targetId));
		
		return myassocs;
	}

	public List<?> getAllRo() {
		if (parent == null)
			return assocs;
		List<Assoc> as = new ArrayList<Assoc>();
		as.addAll(assocs);
		as.addAll(parent.assocs);
		return as;
	}


}
