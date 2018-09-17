package gov.nist.toolkit.fhir.simulators.sim.reg.store;


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
	String pathToMetadata;    // this is a relative path based on RegIndex.filename
							// this is managed by RegIndex
							// unless it is tagged as non-relative
	//boolean pathIsRelative = false;
	private StatusValue availabilityStatus = StatusValue.APPROVED;
	
	public void setAvailabilityStatus(StatusValue status) {
		availabilityStatus = status;
	}
	
	public StatusValue getAvailabilityStatus() {
		return availabilityStatus;
	}

	public boolean isDeprecated() {
		return availabilityStatus == StatusValue.DEPRECATED;
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

	public String toString() { return this.getClass().getSimpleName() + ": " + id; }

    static public List<String> getIds(List<Ro> ros) {
        List<String> ids = new ArrayList<>();
        for (Ro r : ros) ids.add(r.getId());
        return ids;
    }

	public boolean isPathIsRelative() {
		return pathToMetadata != null && new File(pathToMetadata).toPath().getRoot()==null;
	}

	public void setPathIsRelative(boolean pathIsRelative) {
		//this.pathIsRelative = pathIsRelative;
	}

	public void setPathToMetadata(String pathToMetadata) {
		this.pathToMetadata = pathToMetadata;
	}

	public String getPathToMetadata() {
		return pathToMetadata;
	}
}
