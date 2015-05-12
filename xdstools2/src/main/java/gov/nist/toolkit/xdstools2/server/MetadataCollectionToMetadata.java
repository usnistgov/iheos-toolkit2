package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Author;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.client.MetadataToMetadataCollectionParser;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class MetadataCollectionToMetadata {
	Metadata m = new Metadata();
	
	static public Metadata buildMetadata(MetadataCollection mc) {
		MetadataCollectionToMetadata mcm = new MetadataCollectionToMetadata(mc);
		return mcm.m;
	}
	
	MetadataCollectionToMetadata(MetadataCollection mc) {
		for (DocumentEntry de : mc.docEntries) 
			buildDocumentEntry(de);
		for (SubmissionSet ss : mc.submissionSets) 
			buildSubmissionSet(ss);
		for (Folder fol : mc.folders) 
			buildFolder(fol);
		for (Association a : mc.assocs) 
			buildAssociation(a);
	}
	
	void buildDocumentEntry(DocumentEntry de) {
		OMElement eo = m.mkExtrinsicObject(de.id, de.mimeType);
		if (de.status != null && !de.status.equals(""))
			m.setStatus(eo, de.status);
		if (de.title != null && !de.title.equals(""))
			m.setTitleValue(eo, de.title);
		if (de.comments != null && !de.comments.equals(""))
			m.setDescriptionValue(eo, de.comments);
		if (de.patientId != null && !de.patientId.equals(""))
			m.addDocumentEntryPatientId(eo, de.patientId);
		if (de.uniqueId != null && !de.uniqueId.equals(""))
			m.addDocumentEntryUniqueId(eo, de.uniqueId);
		
		if(de.lid != null && !de.lid.equals("") && de.lid.startsWith("urn:uuid:"))
			m.addLid(eo, de.lid);
		if (de.version != null && !de.version.equals("") && de.id.startsWith("urn:uuid:"))
			m.setVersion(eo, de.version);
		if (de.hash != null && !de.hash.equals(""))
			m.addSlot(eo, "hash", de.hash);
		if (de.lang != null && !de.lang.equals(""))
			m.addSlot(eo, "languageCode", de.lang);
		else
			m.addSlot(eo, "languageCode", "en-us");
		if (de.legalAuth != null && !de.legalAuth.equals(""))
			m.addSlot(eo, "legalAuthenticator", de.legalAuth);
		if (de.serviceStartTime != null && !de.serviceStartTime.equals(""))
			m.addSlot(eo, "serviceStartTime", de.serviceStartTime);
		if (de.serviceStopTime != null && !de.serviceStopTime.equals(""))
			m.addSlot(eo, "serviceStopTime", de.serviceStopTime);
		if (de.repositoryUniqueId != null && !de.repositoryUniqueId.equals(""))
			m.addSlot(eo, "repositoryUniqueId", de.repositoryUniqueId);
		if (de.size != null && !de.size.equals(""))
			m.addSlot(eo, "size", de.size);
		if (de.sourcePatientId != null && !de.sourcePatientId.equals(""))
			m.addSlot(eo, "sourcePatientId", de.sourcePatientId);
		if (de.creationTime != null && !de.creationTime.equals(""))
			m.addSlot(eo, "creationTime", de.creationTime);
		
		addClassification(eo, de.classCode, "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a");
		addClassification(eo, de.confCodes, "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f");
		addClassification(eo, de.eventCodeList, "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4");
		addClassification(eo, de.formatCode, "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d");
		addClassification(eo, de.hcftc, "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1");
		addClassification(eo, de.pracSetCode, "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead");
		addClassification(eo, de.typeCode, "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983");
		
		for (Author a : de.authors) {
			OMElement aele = m.addExtClassification(eo, "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d", null, null, "");
			m.addSlot(aele, "authorPerson", a.person);
			if (a.institutions.size() > 0) {
				OMElement iSlot = m.mkSlot("authorInstitution");
				aele.addChild(iSlot);
				for (String inst : a.institutions) {
					m.addSlotValue(iSlot, inst);
				}
			}
			if (a.roles.size() > 0) {
				OMElement iSlot = m.mkSlot("authorRole");
				aele.addChild(iSlot);
				for (String inst : a.roles) {
					m.addSlotValue(iSlot, inst);
				}
			}
			if (a.specialties.size() > 0) {
				OMElement iSlot = m.mkSlot("authorSpecialty");
				aele.addChild(iSlot);
				for (String inst : a.specialties) {
					m.addSlotValue(iSlot, inst);
				}
			}
		}
		
		OMElement spiEle = m.mkSlot("sourcePatientInfo");
		eo.addChild(spiEle);
		for (String spi : de.sourcePatientInfo) {
			m.addSlotValue(spiEle, spi);
		}
		
	}
	
	void buildSubmissionSet(SubmissionSet ss) {
		OMElement ssEle = m.mkSubmissionSet(ss.id);
		m.addIntClassification(ssEle, "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");
		if (ss.status != null && !ss.status.equals(""))
			m.setStatus(ssEle, ss.status);
		if (ss.title != null && !ss.title.equals(""))
			m.setTitleValue(ssEle, ss.title);
		if (ss.comments != null && !ss.comments.equals(""))
			m.setDescriptionValue(ssEle, ss.comments);
		if (ss.patientId != null && !ss.patientId.equals(""))
			m.addSubmissionSetPatientId(ssEle, ss.patientId);
		if (ss.uniqueId != null && !ss.uniqueId.equals(""))
			m.addSubmissionSetUniqueId(ssEle, ss.uniqueId);
		
		if (ss.id.startsWith("urn:uuid:"))
			m.setVersion(ssEle, "1.1");
		
		if (ss.submissionTime != null && !ss.submissionTime.equals(""))
			m.addSlot(ssEle, "submissionTime", ss.submissionTime);
		
		if (ss.sourceId != null && !ss.sourceId.equals(""))
			m.addExternalId(ssEle, "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832", ss.sourceId);
		
		addClassification(ssEle, ss.contentTypeCode, "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500");
		
		if (ss.intendedRecipients.size() > 0) {
			OMElement iSlot = m.mkSlot("intendedRecipients");
			ssEle.addChild(iSlot);
			for (String inst : ss.intendedRecipients) {
				m.addSlotValue(iSlot, inst);
			}
		}
		
		for (Author a : ss.authors) {
			OMElement aele = m.addExtClassification(ssEle, "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d", null, null, "");
			m.addSlot(aele, "authorPerson", a.person);
			if (a.institutions.size() > 0) {
				OMElement iSlot = m.mkSlot("authorInstitution");
				aele.addChild(iSlot);
				for (String inst : a.institutions) {
					m.addSlotValue(iSlot, inst);
				}
			}
			if (a.roles.size() > 0) {
				OMElement iSlot = m.mkSlot("authorRole");
				aele.addChild(iSlot);
				for (String inst : a.roles) {
					m.addSlotValue(iSlot, inst);
				}
			}
			if (a.specialties.size() > 0) {
				OMElement iSlot = m.mkSlot("authorSpecialty");
				aele.addChild(iSlot);
				for (String inst : a.specialties) {
					m.addSlotValue(iSlot, inst);
				}
			}
		}
		
		
	}
	
	void buildFolder(Folder fol) {
		OMElement folEle = m.mkFolder(fol.id);
		if (fol.status != null && !fol.status.equals(""))
			m.setStatus(folEle, fol.status);
		if (fol.title != null && !fol.title.equals(""))
			m.setTitleValue(folEle, fol.title);
		if (fol.comments != null && !fol.comments.equals(""))
			m.setDescriptionValue(folEle, fol.comments);
		if (fol.patientId != null && !fol.patientId.equals(""))
			m.addFolderPatientId(folEle, fol.patientId);
		if (fol.uniqueId != null && !fol.uniqueId.equals(""))
			m.addFolderUniqueId(folEle, fol.uniqueId);
		
		if(fol.lid != null && !fol.lid.equals("") && fol.lid.startsWith("urn:uuid:"))
			m.addLid(folEle, fol.lid);

		
		m.setVersion(folEle, "1.1");
		
		if (fol.lastUpdateTime != null && !fol.lastUpdateTime.equals(""))
			m.addSlot(folEle, "lastUpdateTime", fol.lastUpdateTime);
				
		addClassification(folEle, fol.codeList, "urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5");
				
	}
	
	void buildAssociation(Association a) {
		OMElement aEle = m.mkAssociation(a.type, a.source, a.target);
		aEle.addAttribute("id", a.id, null);
		if(a.lid != null && !a.lid.equals("") && a.lid.startsWith("urn:uuid:"))
			aEle.addAttribute("lid", a.lid, null);
		if (a.status != null && !a.status.equals(""))
			m.setStatus(aEle, a.status);

		if (a.previousVersion != null && !a.previousVersion.equals(""))
			m.addSlot(aEle, "previousVersion", a.previousVersion);
		
		if (a.ssStatus != null && !a.ssStatus.equals("")) 
			m.addSlot(aEle, "SubmissionSetStatus", a.ssStatus);

		
		m.setVersion(aEle, "1.1");
		
				
	}
	
	void addClassification(OMElement ele, List<String> values, String classificationScheme) {
		for (String value : values) {
			String[] parts = value.split("\\^");
			String codeValue   = (parts.length < 1) ? "" : parts[0];
			String codeName    = (parts.length < 2) ? "" : parts[1];
			String codeScheme  = (parts.length < 3) ? "" : parts[2];
			
			m.addExtClassification(ele, classificationScheme, codeScheme, codeName, codeValue);
		}
	}
	
	static public void main(String[] args) {
		File infile = new File("/Users/bill/dev/testkit/tests/11966/submit/single_doc.xml");
		Metadata m = null;
		
		try {
			m = MetadataParser.parseNonSubmission(infile);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} 
		
		MetadataCollection mc = MetadataToMetadataCollectionParser.buildMetadataCollection(m, "test");
		
		Metadata m2 = MetadataCollectionToMetadata.buildMetadata(mc);
		
		List<OMElement> eles = null;
		
		try {
			eles = m2.getV3();
		} catch (XdsInternalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
		
		OMElement x = MetadataSupport.om_factory.createOMElement("Metadata", null);
		
		for (OMElement e : eles)
			x.addChild(e);
		
		try {
			Io.stringToFile(new File("/Users/bill/tmp/submission.xml"), new OMFormatter(x).toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
