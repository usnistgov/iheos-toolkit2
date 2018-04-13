package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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

		dest.isFhir = src.isFhir;
		dest.fullUrl = src.fullUrl;

		dest.id = src.id;
		dest.idX = src.idX;
		dest.idDoc = src.idDoc;

		dest.uniqueId = src.uniqueId;
		dest.uniqueIdX = src.uniqueIdX;
		dest.uniqueIdDoc = src.uniqueIdDoc;

		dest.home = src.home;
		dest.homeX = src.homeX;
		dest.homeDoc = src.homeDoc;

		if (src.extra!=null) {
			dest.extra = new HashMap<>(src.extra);
			if (src.extraX!=null) {
				dest.extraX = new HashMap<>(src.extraX);
			}
		}

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

		dest.patientId = src.patientId;
		dest.patientIdX = src.patientIdX;
		dest.patientIdDoc = src.patientIdDoc;

		dest.status = src.status;
		dest.statusX = src.statusX;
		dest.statusDoc = src.statusDoc;

		dest.title = src.title;
		dest.titleDoc = src.titleDoc;
		dest.titleX = src.titleX;

		dest.comments = src.comments;
		dest.commentsX = src.commentsX;
		dest.commentsDoc = src.commentsDoc;

		dest.creationTime = src.creationTime;
		dest.creationTimeX = src.creationTimeX;
		dest.creationTimeDoc = src.creationTimeDoc;

		if (src.classCode != null) {
			dest.classCode = new ArrayList<>(src.classCode);
			if (src.classCodeX!=null) {dest.classCodeX = new ArrayList<>(src.classCodeX);}
			if (src.classCodeDoc!=null) {dest.classCodeDoc = new ArrayList<>(src.classCodeDoc);}
		}

		if (src.confCodes != null) {
            dest.confCodes = new ArrayList<>(src.confCodes);
			if (src.confCodesX!=null) {dest.confCodesX = new ArrayList<>(src.confCodesX);}
			if (src.confCodesDoc!=null) {dest.confCodesDoc = new ArrayList<>(src.confCodesDoc);}
        }

        if (src.eventCodeList != null) {
			dest.eventCodeList = new ArrayList<>(src.eventCodeList);
			if (src.eventCodeListX!=null) {dest.eventCodeListX = new ArrayList<>(src.eventCodeListX);}
			if (src.eventCodeListDoc!=null) {dest.eventCodeListDoc = new ArrayList<>(src.eventCodeListDoc);}
		}

		if (src.formatCode != null) {
			dest.formatCode = new ArrayList<>(src.formatCode);
			if (src.formatCodeX!=null) {dest.formatCodeX = new ArrayList<>(src.formatCodeX);}
			if (src.formatCodeDoc!=null) {dest.formatCodeDoc = new ArrayList<>(src.formatCodeDoc);}
		}

		if (src.hcftc != null) {
			dest.hcftc = new ArrayList<>(src.hcftc);
			if (src.hcftcX!=null) {dest.hcftcX = new ArrayList<>(src.hcftcX);}
			if (src.hcftcDoc!=null) {dest.hcftcDoc = new ArrayList<>(src.hcftcDoc);}
		}

		if (src.pracSetCode != null) {
			dest.pracSetCode = new ArrayList<>(src.pracSetCode);
			if (src.pracSetCodeX!=null) {dest.pracSetCodeX = new ArrayList<>(src.pracSetCodeX);}
			if (src.pracSetCodeDoc!=null) {dest.pracSetCodeDoc = new ArrayList<>(src.pracSetCodeDoc);}
		}

		if (src.typeCode != null) {
			dest.typeCode = new ArrayList<>(src.typeCode);
			if (src.typeCodeX!=null) {dest.typeCodeX = new ArrayList<>(src.typeCodeX);}
			if (src.typeCodeDoc!=null) {dest.typeCodeDoc = new ArrayList<>(src.typeCodeDoc);}
		}

		if (src.authors!=null) {
			dest.authors = new ArrayList<>();
			for (Author a : src.authors) {
				dest.authors.add(Author.clone(a));
			}
			if (src.authorsX!=null) {dest.authorsX = new ArrayList<>(src.authorsX);}
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
			if (src.sourcePatientInfoDoc!=null) {dest.sourcePatientInfoDoc = new ArrayList<>(src.sourcePatientInfoDoc);}
        }
		return dest;
	}
}
