package gov.nist.toolkit.registrymetadata.client;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Transfer bean to/from GUI.  ...X is the XML string representation.
 * @author bill
 *
 */
public class DocumentEntry extends RegistryObject implements IsSerializable, Serializable {
	
	private static final long serialVersionUID = 1L;
	public String lid;
	public String lidX;
	public String lidDoc;

	public String version;
	public String versionX;
	public String versionDoc;

	public String mimeType;
	public String mimeTypeX;
	public String mimeTypeDoc;

	public String hash;
	public String hashX;
	public String hashDoc;
	
	public String lang;
	public String langX;
	public String langDoc;
	
	public String legalAuth;
	public String legalAuthX;
	public String legalAuthDoc;
	
	public String serviceStartTime;
	public String serviceStartTimeX;
	public String serviceStartTimeDoc;
	
	public String serviceStopTime;
	public String serviceStopTimeX;
	public String serviceStopTimeDoc;
	
	public String repositoryUniqueId;
	public String repositoryUniqueIdX;
	public String repositoryUniqueIdDoc;
	
	public String size; 	
	public String sizeX;
	public String sizeDoc; 	
	
	public String sourcePatientId;
	public String sourcePatientIdX;
	public String sourcePatientIdDoc;
	
	public String creationTime;
	public String creationTimeX;
	public String creationTimeDoc;
	
	public List<String> classCode;
	public List<String> classCodeX;
	public List<String> classCodeDoc;
	
	public List<String> confCodes;
	public List<String> confCodesX;
	public List<String> confCodesDoc;
	
	public List<String> eventCodeList;
	public List<String> eventCodeListX;
	public List<String> eventCodeListDoc;
	
	public List<String> formatCode;
	public List<String> formatCodeX;
	public List<String> formatCodeDoc;
	
	public List<String> hcftc;
	public List<String> hcftcX;
	public List<String> hcftcDoc;
	
	public List<String> pracSetCode;
	public List<String> pracSetCodeX;
	public List<String> pracSetCodeDoc;
	
	public List<String> typeCode;
	public List<String> typeCodeX;
	public List<String> typeCodeDoc;
	
	public List<Author> authors;
	public List<String> authorsX;
	public List<Author> authorsDoc;
	
	public List<String> sourcePatientInfo;
	public String sourcePatientInfoX;
	public List<String> sourcePatientInfoDoc;


	public String displayName() {
		String name = title;
		if (name == null || name.equals(""))
			name = id;
		return "DocumentEntry(" + name + ")";
	}

}
