package gov.nist.toolkit.registrymetadata.client;


import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataDiff extends MetadataDiffBase implements IsSerializable {
	
	static public MetadataObject nullObject(MetadataObject a) {
		if (a.getClass().getName().endsWith("SubmissionSet"))
			return new SubmissionSet();
		if (a.getClass().getName().endsWith("DocumentEntry"))
			return new DocumentEntry();
		if (a.getClass().getName().endsWith("Folder"))
			return new Folder();
		return null;
	}
	
	static public MetadataObject diff(MetadataObject a, MetadataObject b) {
		if (!a.getClass().equals(b.getClass()))
			return null;
		
		if (a.getClass().getName().endsWith("SubmissionSet"))
			return diff((SubmissionSet)a, (SubmissionSet) b);
		if (a.getClass().getName().endsWith("DocumentEntry"))
			return diff((DocumentEntry)a, (DocumentEntry) b);
		if (a.getClass().getName().endsWith("Folder"))
			return diff((Folder)a, (Folder) b);
		return nullObject(a);
	}
	
	static public Folder diff(Folder a, Folder b) {
		Folder d = new Folder();
		boolean diph = false;

		if (dif(a.id, b.id)) {
			d.id = b.id;
			d.idX = b.idX;
			diph = true;
		}
		
		if (dif(a.home, b.home)) {
			d.home = b.home;
			d.homeX = b.homeX;
			diph = true;
		}

		if (dif(a.title, b.title)) {
			d.title = b.title;
			d.titleX = b.titleX;
			diph = true;
		}
		
		if (dif(a.status, b.status)) {
			d.status = b.status;
			d.statusX = b.statusX;
			diph = true;
		}
		
		if (dif(a.comments, b.comments)) {
			d.comments = b.comments;
			d.commentsX = b.commentsX;
			diph = true;
		}
		
		if (dif(a.patientId, b.patientId)) {
			d.patientId = b.patientId;
			d.patientIdX = b.patientIdX;
			diph = true;
		}
		
		if (dif(a.uniqueId, b.uniqueId)) {
			d.uniqueId = b.uniqueId;
			d.uniqueIdX = b.uniqueIdX;
			diph = true;
		}

		if (dif(a.lastUpdateTime, b.lastUpdateTime)) {
			d.lastUpdateTime = b.lastUpdateTime;
			d.lastUpdateTimeX = b.lastUpdateTimeX;
			diph = true;
		}
		
		if (dif(a.codeList, b.codeList)) {
			d.codeList = dup(b.codeList);
			d.codeListX = dup(b.codeListX);
			diph = true;
		}

		if (diph)
			return d;
		return null;
	}

	static public SubmissionSet diff(SubmissionSet a, SubmissionSet b) {
		SubmissionSet d = new SubmissionSet();
		boolean diph = false;

		if (dif(a.id, b.id)) {
			d.id = b.id;
			d.idX = b.idX;
			diph = true;
		}
		
		if (dif(a.home, b.home)) {
			d.home = b.home;
			d.homeX = b.homeX;
			diph = true;
		}

		if (dif(a.title, b.title)) {
			d.title = b.title;
			d.titleX = b.titleX;
			diph = true;
		}
		
		if (dif(a.status, b.status)) {
			d.status = b.status;
			d.statusX = b.statusX;
			diph = true;
		}
		
		if (dif(a.comments, b.comments)) {
			d.comments = b.comments;
			d.commentsX = b.commentsX;
			diph = true;
		}
		
		if (dif(a.patientId, b.patientId)) {
			d.patientId = b.patientId;
			d.patientIdX = b.patientIdX;
			diph = true;
		}
		
		if (dif(a.uniqueId, b.uniqueId)) {
			d.uniqueId = b.uniqueId;
			d.uniqueIdX = b.uniqueIdX;
			diph = true;
		}

		
		if (dif(a.submissionTime, b.submissionTime)) {
			d.submissionTime = b.submissionTime;
			d.submissionTimeX = b.submissionTimeX;
			diph = true;
		}

		if (dif(a.sourceId, b.sourceId)) {
			d.sourceId = b.sourceId;
			d.sourceIdX = b.sourceIdX;
			diph = true;
		}

		if (dif(a.contentTypeCode, b.contentTypeCode)) {
			d.contentTypeCode = dup(b.contentTypeCode);
			d.contentTypeCodeX = dup(b.contentTypeCodeX);
			diph = true;
		}

		if (difa(a.authors, b.authors)) {
			d.authors = dupa(b.authors);
			d.authorsX = dup(b.authorsX);
			diph = true;
		}

		if (dif(a.intendedRecipients, b.intendedRecipients)) {
			d.intendedRecipients = dup(b.intendedRecipients);
			d.intendedRecipientsX = b.intendedRecipientsX;
			diph = true;
		}

		if (diph)
			return d;
		return null;
}
	
	static public DocumentEntry diff(DocumentEntry a, DocumentEntry b) {
		DocumentEntry d = new DocumentEntry();
		boolean diph = false;
		
		if (dif(a.id, b.id)) {
			d.id = b.id;
			d.idX = b.idX;
			diph = true;
		}
		
		if (dif(a.home, b.home)) {
			d.home = b.home;
			d.homeX = b.homeX;
			diph = true;
		}
		
		if (dif(a.title, b.title)) {
			d.title = b.title;
			d.titleX = b.titleX;
			diph = true;
		}
		
		if (dif(a.status, b.status)) {
			d.status = b.status;
			d.statusX = b.statusX;
			diph = true;
		}
		
		if (dif(a.comments, b.comments)) {
			d.comments = b.comments;
			d.commentsX = b.commentsX;
			diph = true;
		}
		
		if (dif(a.patientId, b.patientId)) {
			d.patientId = b.patientId;
			d.patientIdX = b.patientIdX;
			diph = true;
		}
		
		if (dif(a.uniqueId, b.uniqueId)) {
			d.uniqueId = b.uniqueId;
			d.uniqueIdX = b.uniqueIdX;
			diph = true;
		}
		
		if (dif(a.lid, b.lid)) {
			d.lid = b.lid;
			d.lidX = b.lidX;
			diph = true;
		}
		
		if (dif(a.version, b.version)) {
			d.version = b.version;
			d.versionX = b.versionX;
			diph = true;
		}
		
		if (dif(a.mimeType, b.mimeType)) {
			d.mimeType = b.mimeType;
			d.mimeTypeX = b.mimeTypeX;
			diph = true;
		}
		
		if (dif(a.hash, b.hash)) {
			d.hash = b.hash;
			d.hashX = b.hashX;
			diph = true;
		}
		
		if (dif(a.lang, b.lang)) {
			d.lang = b.lang;
			d.langX = b.langX;
			diph = true;
		}
		
		if (dif(a.legalAuth, b.legalAuth)) {
			d.legalAuth = b.legalAuth;
			d.legalAuthX = b.legalAuthX;
			diph = true;
		}
		
		if (dif(a.serviceStartTime, b.serviceStartTime)) {
			d.serviceStartTime = b.serviceStartTime;
			d.serviceStartTimeX = b.serviceStartTimeX;
			diph = true;
		}
		
		if (dif(a.serviceStopTime, b.serviceStopTime)) {
			d.serviceStopTime = b.serviceStopTime;
			d.serviceStopTimeX = b.serviceStopTimeX;
			diph = true;
		}
		
		if (dif(a.repositoryUniqueId, b.repositoryUniqueId)) {
			d.repositoryUniqueId = b.repositoryUniqueId;
			d.repositoryUniqueIdX = b.repositoryUniqueIdX;
			diph = true;
		}
		
		if (dif(a.size, b.size)) {
			d.size = b.size;
			d.sizeX = b.sizeX;
			diph = true;
		}
		
		if (dif(a.sourcePatientId, b.sourcePatientId)) {
			d.sourcePatientId = b.sourcePatientId;
			d.sourcePatientIdX = b.sourcePatientIdX;
			diph = true;
		}
		
		if (dif(a.creationTime, b.creationTime)) {
			d.creationTime = b.creationTime;
			d.creationTimeX = b.creationTimeX;
			diph = true;
		}
		
		if (dif(a.classCode, b.classCode)) {
			d.classCode = dup(b.classCode);
			d.classCodeX = dup(b.classCodeX);
			diph = true;
		}
		
		if (dif(a.confCodes, b.confCodes)) {
			d.confCodes = dup(b.confCodes);
			d.confCodesX = dup(b.confCodesX);
			diph = true;
		}
		
		if (dif(a.eventCodeList, b.eventCodeList)) {
			d.eventCodeList = dup(b.eventCodeList);
			d.eventCodeListX = dup(b.eventCodeListX);
			diph = true;
		}
		
		if (dif(a.formatCode, b.formatCode)) {
			d.formatCode = dup(b.formatCode);
			d.formatCodeX = dup(b.formatCodeX);
			diph = true;
		}
		
		if (dif(a.hcftc, b.hcftc)) {
			d.hcftc = dup(b.hcftc);
			d.hcftcX = dup(b.hcftcX);
			diph = true;
		}
		
		if (dif(a.pracSetCode, b.pracSetCode)) {
			d.pracSetCode = dup(b.pracSetCode);
			d.pracSetCodeX = dup(b.pracSetCodeX);
			diph = true;
		}
		
		if (dif(a.typeCode, b.typeCode)) {
			d.typeCode = dup(b.typeCode);
			d.typeCodeX = dup(b.typeCodeX);
			diph = true;
		}
		
		if (difa(a.authors, b.authors)) {
			d.authors = dupa(b.authors);
			d.authorsX = dup(b.authorsX);
			diph = true;
		}
		
		if (dif(a.sourcePatientInfo, b.sourcePatientInfo)) {
			d.sourcePatientInfo = dup(b.sourcePatientInfo);
			d.sourcePatientInfoX = new String(b.sourcePatientInfoX);
			diph = true;
		}
		
		if (diph)
			return d;
		return null;
	}
	
}
