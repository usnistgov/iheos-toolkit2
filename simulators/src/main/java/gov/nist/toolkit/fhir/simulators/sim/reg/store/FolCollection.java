package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FolCollection extends RegObCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<Fol> fols;
	
	transient public FolCollection parent = null;

	public List<Fol> getAll() {
		List<Fol> all = new ArrayList<>();

		all.addAll(fols);
		FolCollection theParent = parent;
		while (theParent != null) {
			all.addAll(theParent.getAll2(idsBeingDeleted()));
			theParent = theParent.parent;
		}
		List<String> deletedIds = idsBeingDeleted();

		List<Fol> deleted = new ArrayList<>();
		for (Fol f : fols) {
			if (deletedIds.contains(f.id))
				deleted.add(f);
		}

		all.removeAll(deleted);

		return all;
	}

	public boolean isMostRecentVersion(Fol fol) {
		int ver = fol.version;
		String lid = fol.lid;
		List<Fol> fes = getByLid(lid);
		for (Fol f : fes) {
			if (f.version > ver)
				return false;
		}
		return true;
	}

	private List<Fol> getAll2(List<String> deletedIds) {
		List<Fol> all = new ArrayList<>();

		all.addAll(fols);
		FolCollection theParent = parent;
		while (theParent != null) {
			all.addAll(theParent.getAll());
			theParent = theParent.parent;
		}

		List<Fol> deleted = new ArrayList<>();
		for (Fol f : fols) {
			if (deletedIds.contains(f.id))
				deleted.add(f);
		}

		all.removeAll(deleted);

		return all;
	}

	List<Fol> getAllForUpdate() {
		return fols;
	}

	private List<Fol> getAllForDelete() {
		if (parent == null)
			return new ArrayList<>();
		return parent.fols;
	}
	
	public String toString() {
		return getAll().size() + " Folders";
	}
	
	
	public void init() {
		fols = new ArrayList<Fol>();
	}
	
	// caller handles synchronization
	public boolean delete(String id) {
		boolean deleted = false;
		Fol toDelete = null;
		for (Fol a : getAllForDelete()) {
			if (a.id.equals(id)) {
				toDelete = a;
				break;
			}
		}
		if (toDelete != null) {
			getAllForDelete().remove(toDelete);
			deleted = true;
		}
		return deleted;
	}

	public int size() { return getAll().size(); }
	
	public List<Fol> getByLid(String lid) {
		List<Fol> flist = new ArrayList<Fol>();
		if (lid == null)
			return getAll();
		for (Fol fol : getAll()) {
			if (lid.equals(fol.lid))
				flist.add(fol);
		}
		if (parent != null)
			flist.addAll(parent.getByLid(lid));
		return flist;
	}
	

	public Fol getLatestVersion(String lid) {
		List<Fol> flist = getByLid(lid);
		
		Fol latest = null;
		int version = -1;
		
		for (Fol f : flist) {
			if (f.version > version) {
				version = f.version;
				latest = f;
			}
		}
		
		return latest;
	}

	public Fol getPreviousVersion(Fol fol) {
		int thisVersion = fol.version;
		if (thisVersion == 1)
			return null;
		int targetVersion = thisVersion - 1;
		List<Fol> allVersions = getByLid(fol.lid);
		for (Fol f : allVersions) {
			if (f.version == targetVersion)
				return f;
		}
		return null;
	}

	
	public Ro getRo(String id) {
		for (Fol de : getAll()) {
			if (de.id.equals(id))
				return de;
		}
		if (parent == null)
			return null;
		return parent.getRo(id);
	}
	
	public Fol getById(String id) {
		if (id == null)
			return null;
		for (Fol f : getAll()) {
			if (id.equals(f.id))
				return f;
		}
		if (parent == null)
			return null;
		return parent.getById(id);
	}
	
	public List<Fol> getByUid(String uid) {
		List<Fol> des = new ArrayList<>();
		if (uid == null)
			return des;
		for (Fol f : getAll()) {
			if (uid.equals(f.uid))
				des.add(f);
		}
		if (parent != null)
			des.addAll(parent.getByUid(uid));
		return des;
	}
	


	public String statsToString() {
		int siz = 0;
		if (parent != null)
			siz = parent.getAll().size();
		return (siz + getAll().size()) + " Folders";
	}

	public boolean hasObject(String id) {
		if (id == null)
			return false;
		for (Fol a : getAll()) {
			if (a.id.equals(id))
				return true;
		}
		if (parent == null)
			return false;
		return parent.hasObject(id);
	}

	public Ro getRoByUid(String uid) {
		for (Fol f : getAll()) {
			if (f.uid.equals(uid))
				return f;
		}
		if (parent == null)
			return null;
		return parent.getRoByUid(uid);
	}

	public List<Ro> getRosByUid(String uid) {
		List<Ro> list = new ArrayList<>();
		for (Fol f : getAll()) {
			if (f.uid.equals(uid))
			    list.add(f);
		}
		if (! list.isEmpty())
			return list;
		if (parent == null)
			return null;
		return parent.getRosByUid(uid);
	}

	public List<Fol> findByPid(String pid) {
		List<Fol> results = new ArrayList<Fol>();
		
		for (int i=0; i<getAll().size(); i++) {
			Fol f = getAll().get(i);
			if (pid.equals(f.pid))
				results.add(f);
		}
		
		if (parent != null)
			results.addAll(parent.findByPid(pid));
		return results;
	}
	
	public List<Fol> filterByStatus(List<StatusValue> statuses, List<Fol> fs) {
		if (fs.isEmpty()) return fs;
		if (statuses == null || statuses.isEmpty()) return fs;
		for (int i=0; i<fs.size(); i++) {
			Fol f = fs.get(i);
			if (statuses.contains(f.getAvailabilityStatus()))
				continue;
			fs.remove(i);
			i--;
		}
		return fs;
	}

	public List<Fol> filterByLatestVersion(List<Fol> fs) {
		if (fs.isEmpty()) return fs;
		for (int i=0; i<fs.size(); i++) {
			Fol f = fs.get(i);
			if (isMostRecentVersion(f))
				continue;
			fs.remove(i);
			i--;
		}
		return fs;
	}

	public List<Fol> filterBylastUpdateTime(String from, String to, List<Fol> fols) {
		if (fols.isEmpty()) return fols;
		if ((from == null || from.equals("")) && (to == null || to.equals(""))) return fols;
		for (int i=0; i<fols.size(); i++) {
			Fol f = fols.get(i);
			String time = f.lastUpdateTime;
			if (DocEntryCollection.timeCompare(time, from, to))
				continue;
			fols.remove(i);
			i--;
		}
		return fols;
	}
	
	public List<Fol> filterByFolderCodeList(SQCodeAnd values, List<Fol> fols) {
		for (SQCodeOr ors : values.codeOrs) {
			fols = filterByFolderCodeList(ors, fols);
		}
		return fols;
	}
	

	
	public List<Fol> filterByFolderCodeList(SQCodeOr values, List<Fol> fols) {
		if (fols.isEmpty()) return fols;
		if (values == null || values.isEmpty()) return fols;
		eachFol:
		for (int i=0; i<fols.size(); i++) {
			Fol f = fols.get(i);
			for (String val : f.codeList) {
				if (values.isMatch(val))
					continue eachFol;
			}
			fols.remove(i);
			i--;
		}
		return fols;
	}

	public List<?> getAllRo() {
			return getAll();
	}

	@Override
	public List<String> getIds() {
		List<String> ids = new ArrayList<>();
		for (Fol a : getAll()) ids.add(a.getId());
		return ids;
	}

	@Override
	public List<?> getNonDeprecated() {
		List<Fol> nonDep = new ArrayList<>();
		for (Fol a : getAll()) {
			if (!a.isDeprecated())
				nonDep.add(a);
		}
		if (parent != null)
			return parent.getNonDeprecated();
		return nonDep;
	}


}
