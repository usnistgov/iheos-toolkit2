package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Base model for carrying the content of a metadata model. Each attribute
 * is represented by three fields: name, nameX, and nameDoc.  Name is the value of the field.
 * NameX is the XML representation. The XML representation is only fit for display.
 * NameDoc is some arbitrary information about the attribute. One use is to hold comments
 * when this structure is used to hold the output of a metadata-diff. 
 * @author bmajur
 *
 */
public abstract class MetadataObject implements IsSerializable, Serializable {
	public String id;
	public String idX;
	public String idDoc;
	
	public String home;
	public String homeX;
	public String homeDoc;
	
	public Map<String, List<String>> extra;
	public Map<String, String> extraX;

	public boolean isFhir = false;
	public String fullUrl = null;


	abstract public String displayName();

	public boolean hasExtra() {
		return extra != null && extra.size() > 0;
	}

}
