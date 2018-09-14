package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FolCollection extends RegObCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	List<Fol> fols;
	
	transient public FolCollection parent = null;

	
	public String toString() {
		return fols.size() + " Folders";
	}
	
	
	public void init() {
		fols = new ArrayList<Fol>();
	}
	
	// caller handles synchronization
	public void delete(String id) {
		Fol toDelete = null;
		for (Fol a : fols) {
			if (a.id.equals(id)) {
				toDelete = a;
				break;
			}
		}
		if (toDelete != null)
			fols.remove(toDelete);
	}

	public int size() { return fols.size(); }
	
	public List<Fol> getByLid(String lid) {
		List<Fol> flist = new ArrayList<Fol>();
		if (lid == null)
			return fols;
		for (Fol fol : fols) {
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
		for (Fol de : fols) {
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
		for (Fol f : fols) {
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
		for (Fol f : fols) {
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
			siz = parent.fols.size();
		return (siz + fols.size()) + " Folders";
	}

	public boolean hasObject(String id) {
		if (id == null)
			return false;
		for (Fol a : fols) {
			if (a.id.equals(id))
				return true;
		}
		if (parent == null)
			return false;
		return parent.hasObject(id);
	}

	public Ro getRoByUid(String uid) {
		for (Fol f : fols) {
			if (f.uid.equals(uid))
				return f;
		}
		if (parent == null)
			return null;
		return parent.getRoByUid(uid);
	}
	
	public List<Fol> findByPid(String pid) {
		List<Fol> results = new ArrayList<Fol>();
		
		for (int i=0; i<fols.size(); i++) {
			Fol f = fols.get(i);
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
		if (parent == null)
			return fols;
		List<Fol> fs = new ArrayList<Fol>();
		fs.addAll(fols);
		fs.addAll(parent.fols);
		return fs;
	}

	@Override
	public List<String> getIds() {
		List<String> ids = new ArrayList<>();
		for (Fol a : fols) ids.add(a.getId());
		return ids;
	}

	@Override
	public List<?> getNonDeprecated() {
		List<Fol> nonDep = new ArrayList<>();
		for (Fol a : fols) {
			if (!a.isDeprecated())
				nonDep.add(a);
		}
		if (parent != null)
			return parent.getNonDeprecated();
		return nonDep;
	}


}
