package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

	public String objectType;
	public String objectTypeX;
	public String objectTypeDoc;

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
		if (isFhir) {
			return "DocumentReference(" + name + ")";
		}
		return "DocumentEntry(" + name + ")";
	}

	public static DocumentEntry clone(DocumentEntry src) {
		DocumentEntry dest = new DocumentEntry();
		dest.lid = src.lid;
		dest.lidX = src.lidX;
		dest.lidDoc = src.lidDoc;

		dest.objectType = src.objectType;
		dest.objectTypeX = src.objectTypeX;
		dest.objectTypeDoc = src.objectTypeDoc;

		dest.version = src.version;
		dest.versionX = src.versionX;
		dest.versionDoc = src.versionDoc;

		dest.mimeType = src.mimeType;
		dest.mimeTypeX = src.mimeTypeX;
		dest.mimeTypeDoc = src.mimeTypeDoc;

		dest.hash = src.hash;
		dest.hashX = src.hashX;
		dest.hashDoc = src.hashDoc;

		dest.lang = src.lang;
		dest.langX = src.langX;
		dest.langDoc = src.langDoc;

		dest.legalAuth = src.legalAuth;
		dest.legalAuthX = src.legalAuthX;
		dest.legalAuthDoc = src.legalAuthDoc;

		dest.serviceStartTime = src.serviceStartTime;
		dest.serviceStartTimeX = src.serviceStartTimeX;
		dest.serviceStartTimeDoc = src.serviceStartTimeDoc;

		dest.serviceStopTime = src.serviceStopTime;
		dest.serviceStopTimeX = src.serviceStopTimeX;
		dest.serviceStopTimeDoc = src.serviceStopTimeDoc;

		dest.repositoryUniqueId = src.repositoryUniqueId;
		dest.repositoryUniqueIdX = src.repositoryUniqueIdX;
		dest.repositoryUniqueIdDoc = src.repositoryUniqueIdDoc;

		dest.size = src.size;
		dest.sizeX = src.sizeX;
		dest.sizeDoc = src.sizeDoc;

		dest.sourcePatientId = src.sourcePatientId;
		dest.sourcePatientIdX = src.sourcePatientIdX;
		dest.sourcePatientIdDoc = src.sourcePatientIdDoc;

		dest.creationTime = src.creationTime;
		dest.creationTimeX = src.creationTimeX;
		dest.creationTimeDoc = src.creationTimeDoc;

		if (src.classCode != null) {
			dest.classCode = new ArrayList<>(src.classCode);
			dest.classCodeX = new ArrayList<>(src.classCodeX);
			dest.classCodeDoc = new ArrayList<>(src.classCodeDoc);
		}

		if (src.confCodes != null) {
            dest.confCodes = new ArrayList<>(src.confCodes);
			dest.confCodesX = new ArrayList<>(src.confCodesX);
			dest.confCodesDoc = new ArrayList<>(src.confCodesDoc);
        }

        if (src.eventCodeList != null) {
			dest.eventCodeList = new ArrayList<>(src.eventCodeList);
			dest.eventCodeListX = new ArrayList<>(src.eventCodeListX);
			dest.eventCodeListDoc = new ArrayList<>(src.eventCodeListDoc);
		}

		if (src.formatCode != null) {
			dest.formatCode = new ArrayList<>(src.formatCode);
			dest.formatCodeX = new ArrayList<>(src.formatCodeX);
			dest.formatCodeDoc = new ArrayList<>(src.formatCodeDoc);
		}

		if (src.hcftc != null) {
			dest.hcftc = new ArrayList<>(src.hcftc);
			dest.hcftcX = new ArrayList<>(src.hcftcX);
			dest.hcftcDoc = new ArrayList<>(src.hcftcDoc);
		}

		if (src.pracSetCode != null) {
			dest.pracSetCode = new ArrayList<>(src.pracSetCode);
			dest.pracSetCodeX = new ArrayList<>(src.pracSetCodeX);
			dest.pracSetCodeDoc = new ArrayList<>(src.pracSetCodeDoc);
		}

		if (src.typeCode != null) {
			dest.typeCode = new ArrayList<>(src.typeCode);
			dest.typeCodeX = new ArrayList<>(src.typeCodeX);
			dest.typeCodeDoc = new ArrayList<>(src.typeCodeDoc);
		}

		if (src.authors!=null) {
			dest.authors = new ArrayList<>();
			for (Author a : src.authors) {
				dest.authors.add(Author.clone(a));
			}
			dest.authorsX = new ArrayList<>(dest.authorsX);
			if (src.authorsDoc!=null) {
				dest.authorsDoc = new ArrayList<>();
				for (Author a : src.authorsDoc) {
					dest.authorsDoc.add(Author.clone(a));
				}
			}
		}

		if (src.sourcePatientInfo != null) {
            dest.sourcePatientInfo = new ArrayList<>(src.sourcePatientInfo);
			dest.sourcePatientInfoX = src.sourcePatientInfoX;
			dest.sourcePatientInfoDoc = new ArrayList<>(src.sourcePatientInfoDoc);
        }

		return dest;
	}
}
