package gov.nist.toolkit.simulators.sim.reg.store;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ro  implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	public String id;
	public String uid;
	String pathToMetadata;
	private RegIndex.StatusValue availabilityStatus = RegIndex.StatusValue.APPROVED;
	
	public void setAvailabilityStatus(RegIndex.StatusValue status) {
		availabilityStatus = status;
	}
	
	public RegIndex.StatusValue getAvailabilityStatus() {
		return availabilityStatus;
	}
		
	public String getType() {
		return "RegistryObject";
	}
	
	public File getFile() {
		return new File(pathToMetadata);
	}

	public void setFile(String file) { pathToMetadata = file; }
	
	public boolean metadataExists() {
		File f = getFile();
		return f.exists();
	}
	
	public String getUid() {
		return uid;
	}
	
	public String getId() {
		return id;
	}
	
	public String getObjectDescription() {
		return getType() + "(" + id + ")";
	}
	
	public boolean equals(Ro ro) {
		return ro.getId() == id;
	}

	public String toString() { return id; }

    static public List<String> getIds(List<Ro> ros) {
        List<String> ids = new ArrayList<>();
        for (Ro r : ros) ids.add(r.getId());
        return ids;
    }
}
