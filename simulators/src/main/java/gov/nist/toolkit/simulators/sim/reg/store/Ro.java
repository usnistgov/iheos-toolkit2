package gov.nist.toolkit.simulators.sim.reg.store;


import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.io.File;
import java.io.IOException;
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

    public String getFullMetadataAsString() throws IOException {
        return Io.stringFromFile(getFile());
    }

    public OMElement getFullMetadata() throws XdsInternalException {
        return Util.parse_xml(getFile());
    }
	
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
