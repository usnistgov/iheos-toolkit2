package gov.nist.toolkit.fhir.simulators.sim.reg.store;


import java.util.ArrayList;
import java.util.List;

abstract public class RegObCollection {

	abstract public String statsToString();
	abstract public boolean hasObject(String id);
	abstract public Ro getRo(String id);
	abstract public List<?> getAllRo();
	/**
	 * Returns only the first matching RO
	 * @param uid
	 * @return
	 */
	abstract public Ro getRoByUid(String uid);
	abstract public List<Ro> getRosByUid(String uid);
	abstract public List<String> getIds();
	abstract public boolean delete(String id);  	// caller handles synchronization
	abstract public List<?> getNonDeprecated();

	private List<String> deleting = new ArrayList<>();

	public void setDeleting(List<String> ids) {
		deleting = ids;
	}

	public List<String> idsBeingDeleted() { return deleting; }

}
