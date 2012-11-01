package gov.nist.toolkit.registrymetadata.client;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Folder extends RegistryObject  implements IsSerializable, Serializable  {

	private static final long serialVersionUID = 1L;
	public String version;
	public String versionX;
	public String versionDoc;

	public String lid;
	public String lidX;
	public String lidDoc;

	public String lastUpdateTime;
	public String lastUpdateTimeX;
	public String lastUpdateTimeDoc;
	
	public List<String> codeList;
	public List<String> codeListX;
	public List<String> codeListDoc;
	
	public String displayName() {
		String name = title;
		if (name == null || name.equals(""))
			name = id;
		return "Folder( " + name + ")";
	}
	
}
