package gov.nist.toolkit.results.client;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Author;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;

/**
 * Translate a Metadata object into client objects.
 * @author bill
 *
 */
public class MetadataToMetadataCollectionParser {
	MetadataCollection col;
	Metadata m;
	
	public static MetadataCollection buildMetadataCollection(Metadata m, String label) {
		MetadataToMetadataCollectionParser mmcp = new MetadataToMetadataCollectionParser(m, label);
		return mmcp.col;
	}

	public MetadataToMetadataCollectionParser(Metadata m, String label) {
		this.m = m;
		col = new MetadataCollection();
		
		col.label = label;

		col.docEntries = new ArrayList<DocumentEntry>();
		for (OMElement eo : m.getExtrinsicObjects()) {
			DocumentEntry de = new DocumentEntry();

			parse(de, eo);

			col.docEntries.add(de);
		}

		col.folders = new ArrayList<Folder>();
		for (OMElement ele : m.getFolders()) {
			Folder fol = new Folder();

			parse(fol, ele);

			col.folders.add(fol);
		}
		
		col.submissionSets = new ArrayList<SubmissionSet>();
		for (OMElement ele : m.getSubmissionSets()) {
			SubmissionSet ss = new SubmissionSet();
			
			parse(ss, ele);
			
			col.submissionSets.add(ss);
		}
		
		col.assocs = new ArrayList<Association>();
		for (OMElement ele : m.getAssociations()) {
			Association assoc = new Association();
			
			parse(assoc, ele);
			
			col.assocs.add(assoc);
		}
		
		col.objectRefs = new ArrayList<ObjectRef>();
		if (col.docEntries.size() == 0 &&
				col.folders.size() == 0 &&
				col.submissionSets.size() == 0 &&
				col.assocs.size() ==0) {
			for (OMElement ele : m.getObjectRefs()) {
				ObjectRef or = new ObjectRef();
				or.id = ele.getAttributeValue(MetadataSupport.id_qname);
				or.home = ele.getAttributeValue(MetadataSupport.home_qname);
				col.objectRefs.add(or);
			}
		}
	}
	
	String splitLast(String in, String separator) {
		String[] parts = in.split(separator);
		if (parts.length <= 1)
			return in;
		return parts[parts.length - 1];
	}
	
	public MetadataCollection get() { return col; }
	
	void parse(Association assoc, OMElement ele) {
		OMFormatter omf = new OMFormatter(ele);
		String aEleStr = omf.toHtml();  

		assoc.id = asString(m.getId(ele));
		assoc.idX = aEleStr;
		
		assoc.lid = asString(m.getLid(ele));
		assoc.lidX = aEleStr;
		
		assoc.version = asString(m.getVersion(ele));
		assoc.versionX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "VersionInfo")).toHtml();

		assoc.status = asString(m.getStatus(ele));
		assoc.statusX = aEleStr;
		
		assoc.type = splitLast(m.getAssocType(ele), ":");
		assoc.typeX = aEleStr;
		
		assoc.source = m.getAssocSource(ele);
		assoc.sourceX = aEleStr;
		
		assoc.target = m.getAssocTarget(ele);
		assoc.targetX = aEleStr;
		
		assoc.home = asString(m.getHome(ele));
		assoc.homeX = aEleStr;
		
		assoc.ssStatus = "";
		assoc.ssStatusX = aEleStr;
		
		parseExtra(assoc, ele);

		
		try {
			assoc.ssStatus = m.getSlotValue(ele, "SubmissionSetStatus", 0);
			assoc.ssStatusX = new OMFormatter(m.getSlot(ele, "SubmissionSetStatus")).toHtml();
		} catch (Exception e) {}
		
		assoc.previousVersion = "";
		assoc.previousVersionX = "";
		
		try {
			assoc.previousVersion = m.getSlotValue(ele, "PreviousVersion", 0);
			assoc.previousVersionX = new OMFormatter(m.getSlot(ele, "PreviousVersion")).toHtml();
			
		} catch (Exception e) {}
		
		
		try {
			List<String> schemes = new ArrayList<String>();
			schemes.add(MetadataSupport.XDSAssociationDocumentation_uuid);
			Map<String, List<String>> codes = m.getCodesWithDisplayName(ele, schemes);
			assoc.assocDoc = codes.get(MetadataSupport.XDSAssociationDocumentation_uuid);
			assoc.assocDocX = formatClassSrc(ele, MetadataSupport.XDSAssociationDocumentation_uuid);

		} catch(Exception e) {}

	
		try {
			assoc.previousVersion = m.getSlotValue(ele, "PreviousVersion", 0);
		} catch (Exception e) {}
}
	
	void parse(SubmissionSet ss, OMElement ele) {
		OMFormatter omf = new OMFormatter(ele);
		omf.noRecurse();
		String ssEleStr = omf.toHtml();  

		ss.id = asString(m.getId(ele));
		ss.idX = ssEleStr;
		
		ss.status = asString(m.getStatus(ele));
		ss.statusX = ssEleStr;
		
		ss.title = asString(m.getNameValue(ele));
		ss.titleX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "Name")).toHtml();
		
		ss.comments = asString(m.getDescriptionValue(ele));
		ss.commentsX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "Description")).toHtml();

		ss.home = asString(m.getHome(ele));
		ss.homeX = ssEleStr;
		
		ss.submissionTime = asString(m.getSlotValue(ele, "submissionTime", 0));
		ss.submissionTimeX = new OMFormatter(m.getSlot(ele, "submissionTime")).toHtml();
		
		parseExtra(ss, ele);


		try { 
			ss.patientId = asString(m.getPatientId(ele)); 
			ss.patientIdX = new OMFormatter(m.getExternalIdentifierElement(ss.id, MetadataSupport.XDSSubmissionSet_patientid_uuid)).toHtml();
			} catch (Exception e) {}
			
		try { 
			ss.uniqueId = asString(m.getUniqueIdValue(ele)); 
			ss.uniqueIdX = new OMFormatter(m.getExternalIdentifierElement(ss.id, MetadataSupport.XDSSubmissionSet_uniqueid_uuid)).toHtml();
			} catch (Exception e) {}
			
		try { 
			ss.sourceId = asString(m.getSubmissionSetSourceId(ele)); 
			List<OMElement> eids = m.getExternalIdentifiers(ele, MetadataSupport.XDSSubmissionSet_sourceid_uuid);
			OMElement eid = eids.get(0);
			ss.sourceIdX = new OMFormatter(eid).toHtml();
			} catch (Exception e) {}
			
		try {
			List<String> schemes = new ArrayList<String>();
			schemes.add(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid);
			Map<String, List<String>> codes = m.getCodesWithDisplayName(ele, schemes);
			ss.contentTypeCode = codes.get(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid);
			ss.contentTypeCodeX = formatClassSrc(ele, MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid);
		} catch(Exception e) {}
		
		try {
			List<OMElement> authorClassifications = m.getClassifications(ele, MetadataSupport.XDSSubmissionSet_author_uuid);
			ss.authors = parseAuthors(authorClassifications);
			ss.authorsX = new ArrayList<String>();
			for (OMElement auEle : authorClassifications) {
				ss.authorsX.add(new OMFormatter(auEle).toHtml());
			}
		} catch (Exception e) {}
		ss.intendedRecipients = m.getSlotValues(ele, "intendedRecipient");
		ss.intendedRecipientsX = new OMFormatter(m.getSlot(ele, "intendedRecipient")).toHtml();
	}

	void parse(DocumentEntry de, OMElement ele) {
		OMFormatter omf = new OMFormatter(ele);
		omf.noRecurse();
		String eoEleStr = omf.toHtml();  

		de.id = asString(m.getId(ele));
		de.idX = eoEleStr;
		
		de.lid = asString(m.getLid(ele));
		de.lidX = eoEleStr;
		
		de.version = asString(m.getVersion(ele));
		de.versionX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "VersionInfo")).toHtml();
		
		de.status = asString(m.getStatus(ele));
		de.statusX = eoEleStr;
		
		de.title = asString(m.getNameValue(ele));
		de.titleX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "Name")).toHtml();
		
		de.comments = asString(m.getDescriptionValue(ele));
		de.commentsX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "Description")).toHtml();

		de.home = asString(m.getHome(ele));
		de.homeX = eoEleStr;
		
		de.mimeType = asString(m.getMimeType(ele));
		de.mimeTypeX = eoEleStr;
		
		de.hash = asString(m.getSlotValue(ele, "hash", 0));
		de.hashX = new OMFormatter(m.getSlot(ele, "hash")).toHtml();
		
		de.lang = asString(m.getSlotValue(ele, "languageCode", 0));
		de.langX = new OMFormatter(m.getSlot(ele, "languageCode")).toHtml();
		
		de.legalAuth = asString(m.getSlotValue(ele, "legalAuthenticator", 0));
		de.legalAuthX = new OMFormatter(m.getSlot(ele, "legalAuthenticator")).toHtml();
		
		de.serviceStartTime = asString(m.getSlotValue(ele, "serviceStartTime", 0));
		de.serviceStartTimeX = new OMFormatter(m.getSlot(ele, "serviceStartTime")).toHtml();
		
		de.serviceStopTime = asString(m.getSlotValue(ele, "serviceStopTime", 0));
		de.serviceStopTimeX = new OMFormatter(m.getSlot(ele, "serviceStopTime")).toHtml();
		
		de.repositoryUniqueId = asString(m.getSlotValue(ele, "repositoryUniqueId", 0));
		de.repositoryUniqueIdX = new OMFormatter(m.getSlot(ele, "repositoryUniqueId")).toHtml();
		
		de.size = asString(m.getSlotValue(ele, "size", 0));
		de.sizeX = new OMFormatter(m.getSlot(ele, "size")).toHtml();
		
		parseExtra(de, ele);

		
		try { 
			de.patientId = asString(m.getPatientId(ele));
			de.patientIdX = new OMFormatter(m.getExternalIdentifierElement(de.id, MetadataSupport.XDSDocumentEntry_patientid_uuid)).toHtml();
			} catch (Exception e) {}

		try { 
			de.uniqueId = asString(m.getUniqueIdValue(ele)); 
			de.uniqueIdX = new OMFormatter(m.getExternalIdentifierElement(de.id, MetadataSupport.XDSDocumentEntry_uniqueid_uuid)).toHtml();
			} catch (Exception e) {}
			
		de.sourcePatientId = asString(m.getSlotValue(ele, "sourcePatientId", 0));
		de.sourcePatientIdX = new OMFormatter(m.getSlot(ele, "sourcePatientId")).toHtml();
		
		de.creationTime = asString(m.getSlotValue(ele, "creationTime", 0));
		de.creationTimeX = new OMFormatter(m.getSlot(ele, "creationTime")).toHtml();
		

		List<String> schemes = new ArrayList<String>();
		schemes.add(MetadataSupport.XDSDocumentEntry_classCode_uuid);
		schemes.add(MetadataSupport.XDSDocumentEntry_confCode_uuid);
		schemes.add(MetadataSupport.XDSDocumentEntry_eventCode_uuid);
		schemes.add(MetadataSupport.XDSDocumentEntry_formatCode_uuid);
		schemes.add(MetadataSupport.XDSDocumentEntry_hcftCode_uuid);
		schemes.add(MetadataSupport.XDSDocumentEntry_psCode_uuid);
		schemes.add(MetadataSupport.XDSDocumentEntry_typeCode_uuid);

		Map<String, List<String>> codes = null;
		try {
			codes = m.getCodesWithDisplayName(ele, schemes);
			
			de.classCode = codes.get(MetadataSupport.XDSDocumentEntry_classCode_uuid);
			de.classCodeX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_classCode_uuid);
			
			de.confCodes = codes.get(MetadataSupport.XDSDocumentEntry_confCode_uuid);
			de.confCodesX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_confCode_uuid);
			
			de.eventCodeList = codes.get(MetadataSupport.XDSDocumentEntry_eventCode_uuid);
			de.eventCodeListX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_eventCode_uuid);
			
			de.formatCode = codes.get(MetadataSupport.XDSDocumentEntry_formatCode_uuid);
			de.formatCodeX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_formatCode_uuid);
			
			de.hcftc = codes.get(MetadataSupport.XDSDocumentEntry_hcftCode_uuid);
			de.hcftcX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_hcftCode_uuid);
			
			de.pracSetCode = codes.get(MetadataSupport.XDSDocumentEntry_psCode_uuid);
			de.pracSetCodeX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_psCode_uuid);
			
			de.typeCode = codes.get(MetadataSupport.XDSDocumentEntry_typeCode_uuid);
			de.typeCodeX = formatClassSrc(ele, MetadataSupport.XDSDocumentEntry_typeCode_uuid);
			
		} catch(Exception e) {}

		try {
			List<OMElement> authorClassifications = m.getClassifications(ele, MetadataSupport.XDSDocumentEntry_author_uuid);
			de.authors = parseAuthors(authorClassifications);
			de.authorsX = new ArrayList<String>();
			for (OMElement auEle : authorClassifications) {
				de.authorsX.add(new OMFormatter(auEle).toHtml());
			}
		} catch (Exception e) {}

		de.sourcePatientInfo = m.getSlotValues(ele, "sourcePatientInfo");
		de.sourcePatientInfoX = new OMFormatter(m.getSlot(ele, "sourcePatientInfo")).toHtml();
	}
	
	void parseExtra(MetadataObject ro, OMElement ele) {
		ro.extra = new HashMap<String, List<String>>();
		ro.extraX = new HashMap<String, String>();
		
		try {
		for (OMElement slotEle :  m.getSlots(m.getId(ele))) {
			String slotName = m.getSlotName(slotEle);
			if (! slotName.startsWith("urn:"))
				continue;
			List<String> values = m.getSlotValues(ele, slotName);
			ro.extra.put(slotName, values);
			ro.extraX.put(slotName, new OMFormatter(slotEle).toHtml());
		}
		} catch (Exception e) {}

	}
	
	void parse(Folder fol, OMElement ele) {
		OMFormatter omf = new OMFormatter(ele);
		omf.noRecurse();
		String folEleStr = omf.toHtml();  

		fol.id = asString(m.getId(ele));
		fol.idX = folEleStr;
		
		fol.lid = asString(m.getLid(ele));
		fol.lidX = folEleStr;
		
		fol.version = asString(m.getVersion(ele));
		fol.versionX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "VersionInfo")).toHtml();
		
		fol.status = asString(m.getStatus(ele));
		fol.statusX = folEleStr;
		
		fol.title = asString(m.getNameValue(ele));
		fol.titleX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "Name")).toHtml();

		fol.comments = asString(m.getDescriptionValue(ele));
		fol.commentsX = new OMFormatter(MetadataSupport.firstChildWithLocalName(ele, "Description")).toHtml();

		fol.home = asString(m.getHome(ele));
		fol.homeX = folEleStr;
		
		fol.lastUpdateTime = asString(m.getSlotValue(ele, "lastUpdateTime", 0));
		fol.lastUpdateTimeX = new OMFormatter(m.getSlot(ele, "lastUpdateTime")).toHtml();
		
		parseExtra(fol, ele);


		try {
			List<String> schemes = new ArrayList<String>();
			schemes.add(MetadataSupport.XDSFolder_codeList_uuid);
			Map<String, List<String>> codes = m.getCodesWithDisplayName(ele, schemes);
			fol.codeList = codes.get(MetadataSupport.XDSFolder_codeList_uuid);
			fol.codeListX = formatClassSrc(ele, MetadataSupport.XDSFolder_codeList_uuid);

		} catch(Exception e) {}
		
		try { 
			fol.patientId = asString(m.getPatientId(ele));
			fol.patientIdX = new OMFormatter(m.getExternalIdentifierElement(fol.id, MetadataSupport.XDSFolder_patientid_uuid)).toHtml();

			} catch (Exception e) {}
			
		try { 
			fol.uniqueId = asString(m.getUniqueIdValue(ele)); 
			fol.uniqueIdX = new OMFormatter(m.getExternalIdentifierElement(fol.id, MetadataSupport.XDSFolder_uniqueid_uuid)).toHtml();
			} catch (Exception e) {}
	}


	List<String> formatClassSrc(OMElement ele, String classScheme) throws MetadataException {
		List<OMElement> clEles = m.getClassifications(ele, classScheme);
		List<String> strs = new ArrayList<String>();
		for (OMElement e : clEles) {
			strs.add(new OMFormatter(e).toHtml());
		}
		return strs;
	}

	List<Author> parseAuthors(List<OMElement> authorClassifications) {
		List<Author> authors = new ArrayList<Author>();

		for (OMElement authorClas : authorClassifications) {
			String name = m.getSlotValue(authorClas, "authorPerson", 0);
			List<String> institutions = m.getSlotValues(authorClas, "authorInstitution");
			List<String> roles = m.getSlotValues(authorClas, "authorRole");
			List<String> specialties = m.getSlotValues(authorClas, "authorSpecialty");
			Author a = new Author();
			a.person = name;
			a.institutions = institutions;
			a.roles = roles;
			a.specialties = specialties;
			authors.add(a);
		}

		return authors;
	}

	String asString(String in) {
		if (in == null) return "";
		return in;
	}

}
