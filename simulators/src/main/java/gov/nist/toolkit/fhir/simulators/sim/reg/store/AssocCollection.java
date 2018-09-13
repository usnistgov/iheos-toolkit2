package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssocCollection extends RegObCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<Assoc> assocs = new ArrayList<Assoc>();
	
	transient public AssocCollection parent = null;

	public List<Assoc> getAll() {
		List<Assoc> all = new ArrayList<>();

		all.addAll(assocs);
		AssocCollection theParent = parent;
		while (theParent != null) {
			all.addAll(theParent.getAll());
			theParent = theParent.parent;
		}

		return all;
	}

	public List<Assoc> getAllForUpdate() {
		return assocs;
	}


	public String toString() {
		return getAll().size() + " Associations";
	}
	
	public void init() {
		assocs = new ArrayList<Assoc>();
	}


	// caller handles synchronization
	public void delete(String id) {
		Assoc toDelete = null;
		for (Assoc a : getAllForUpdate()) {
			if (a.id.equals(id)) {
				toDelete = a;
				break;
			}
		}
		if (toDelete != null)
			getAllForUpdate().remove(toDelete);
	}

	@Override
	public List<?> getNonDeprecated() {
		List<Assoc> nonDep = new ArrayList<>();
		for (Assoc a : getAll()) {
			if (!a.isDeprecated())
				nonDep.add(a);
		}
		if (parent != null)
			return parent.getNonDeprecated();
		return nonDep;
	}

	public int size() { return getAll().size(); }
	
	public Ro getRo(String id) {
		for (Assoc a : getAll()) {
			String aid = a.id;
			if (aid == null)
				throw new ToolkitRuntimeException("bad association");
			if (aid.equals(id))
				return a;
		}
		if (parent == null)
			return null;
		else
			return parent.getRo(id);
	}
	

	
	public String statsToString() {
		int parentCount = 0;
		if (parent != null)
			parentCount = parent.getAll().size();
		return (parentCount + getAll().size()) + " Associations";
	}
	

	public Assoc getById(String id) {
		if (id == null)
			return null;
		for (Assoc a : getAll()) {
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
		for (Assoc a : getAll()) {
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
		for (Assoc a : getAll()) ids.add(a.getId());
		return ids;
	}

	/**
	 * Any of the parameters may be null implying ANY.
	 * @param sourceId
	 * @param targetId
	 * @param type
	 * @return
	 */
	public List<Assoc> getBySourceDestAndType(String sourceId, String targetId, RegIndex.AssocType type) {
		List<Assoc> myassocs = new ArrayList<Assoc>();
		
		for (Assoc a : getAll()) {
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

	public List<Assoc> getBySourceOrDestAndType(String sourceId, String targetId, RegIndex.AssocType type) {
		List<Assoc> myassocs = new ArrayList<Assoc>();
		
		for (Assoc a : getAll()) {
			if ( type != null && a.type != type)
				continue;
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
			myassocs.addAll(parent.getBySourceOrDestAndType(sourceId, targetId, type));
		
		return myassocs;
	}

	public List<Assoc> allThatReference(String id) {
		List<Assoc> all = new ArrayList<>();

		for (Assoc a : getAll()) {
			if (a.from.equals(id) || a.to.equals(id))
				all.add(a);
		}

		return all;
	}

	public List<?> getAllRo() {
			return getAll();
	}


}
