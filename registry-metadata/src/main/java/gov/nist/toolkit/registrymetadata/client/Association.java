package gov.nist.toolkit.registrymetadata.client;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Association extends MetadataObject  implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;
	public String lid;
	public String lidX;
	public String lidDoc;

	public String version;
	public String versionX;
	public String versionDoc;

	public String type;
	public String typeX;
	public String typeDoc;
	
	public String status;
	public String statusX;
	public String statusDoc;
	
	public String previousVersion;
	public String previousVersionX;
	public String previousVersionDoc;
	
	public String source;
	public String sourceX;
	public String sourceDoc;
	
	public String target;
	public String targetX;
	public String targetDoc;
	
	public String ssStatus;
	public String ssStatusX;
	public String ssStatusDoc;
	
	public List<String> assocDoc;
	public List<String> assocDocX;
	public List<String> assocDocDoc;

	public String displayName() {
		return type;
	}

}
