package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class RegistryFactory {

	static public DocEntry buildDocEntryIndex(Metadata m, OMElement ele, MetadataCollection delta) throws MetadataException {
		DocEntry de = new DocEntry();

		de.id = m.getId(ele);
		de.lid = m.getLid(ele);
		de.uid = m.getUniqueIdValue(ele);
		de.pid = m.getPatientId(ele);
		de.hash = m.getSlotValue(ele, "hash", 0);
		de.size = m.getSlotValue(ele, "size", 0);
		de.objecttype = m.getObjectTypeById(de.id);
		
		String version = m.getVersion(ele);
		
		try {
			int verI = Integer.parseInt(version);
			de.version = verI;
		} catch (NumberFormatException e) {
			throw new MetadataException("Version attribute does not parse as an integer, value is " + version, null);
		}
		
		de.creationTime = m.getSlotValue(ele, "creationTime", 0);
		de.serviceStartTime = m.getSlotValue(ele, "serviceStartTime", 0);
		de.serviceStopTime = m.getSlotValue(ele, "serviceStopTime", 0);
		
		de.classCode = getSingleCode(m, ele, MetadataSupport.XDSDocumentEntry_classCode_uuid);
		de.typeCode = getSingleCode(m, ele, MetadataSupport.XDSDocumentEntry_typeCode_uuid);
		de.practiceSettingCode = getSingleCode(m, ele, MetadataSupport.XDSDocumentEntry_psCode_uuid);
		de.healthcareFacilityTypeCode = getSingleCode(m, ele, MetadataSupport.XDSDocumentEntry_hcftCode_uuid);
		de.formatCode = getSingleCode(m, ele, MetadataSupport.XDSDocumentEntry_formatCode_uuid);

		de.eventCode = getMultipleCode(m, ele, MetadataSupport.XDSDocumentEntry_eventCode_uuid);
		de.confidentialityCode = getMultipleCode(m, ele, MetadataSupport.XDSDocumentEntry_confCode_uuid);
	
		List<String> authorNames = new ArrayList<String>();
		List<OMElement> authorEles = m.getClassifications(ele, MetadataSupport.XDSDocumentEntry_author_uuid);
		for (OMElement authorEle : authorEles) {
			String name = m.getSlotValue(authorEle, "authorPerson", 0);
			authorNames.add(name);
		}
		de.authorNames = authorNames.toArray(new String[0]);
		
		nullIdCheck(de);
		
		delta.add(de);
		
		return de;
	}
	
	static String[] getMultipleCode(Metadata m, OMElement ele, String classUuid) throws MetadataException {
		
		return m.getClassificationsCodes(ele, classUuid).toArray(new String[0]);
	}
	
	static String getSingleCode(Metadata m, OMElement ele, String classUuid) throws MetadataException {
		List<String> codes = m.getClassificationsCodes(ele, classUuid);
		if (codes.size() > 0)
			return codes.get(0);
		return null;
	}
	
	static public SubSet buildSubSetIndex(Metadata m, OMElement ele, MetadataCollection delta) throws MetadataException {
		SubSet s = new SubSet();
		
		s.id = m.getId(ele);
		s.pid = m.getPatientId(ele);
		s.uid = m.getUniqueIdValue(ele);
		s.sourceId = m.getSourceIdValue(ele);
		s.submissionTime = m.getSlotValue(ele, "submissionTime", 0);
		
		List<String> authorNames = new ArrayList<String>();
		List<OMElement> authorEles = m.getClassifications(ele, MetadataSupport.XDSSubmissionSet_author_uuid);
		for (OMElement authorEle : authorEles) {
			String name = m.getSlotValue(authorEle, "authorPerson", 0);
			authorNames.add(name);
		}
		s.authorNames = authorNames.toArray(new String[0]);
		
		s.contentType = getSingleCode(m, ele, MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid);

			
		nullIdCheck(s);
		
		delta.add(s);
		
		return s;
	}
	
	static public Assoc buildAssocIndex(Metadata m, OMElement ele, MetadataCollection delta) throws MetadataException {
		Assoc a = new Assoc();
		
		a.id = m.getId(ele);
		a.from = m.getAssocSource(ele);
		a.to = m.getAssocTarget(ele);
		a.type = RegIndex.getAssocType(m, ele);
		nullIdCheck(a);
		
		delta.add(a);
		
		return a;
	}
	
	static public Fol buildFolIndex(Metadata m, OMElement ele, MetadataCollection delta) throws MetadataException {
		Fol f = new Fol();
		
		f.id = m.getId(ele);
		f.pid = m.getPatientId(ele);
		f.lid = m.getLid(ele);
		f.uid = m.getUniqueIdValue(ele);
		f.lastUpdateTime = m.getSlotValue(ele, "lastUpdateTime", 0);
		f.codeList = getMultipleCode(m, ele, MetadataSupport.XDSFolder_codeList_uuid);

		String version = m.getVersion(ele);
		
		try {
			int verI = Integer.parseInt(version);
			f.version = verI;
		} catch (NumberFormatException e) {
			throw new MetadataException("Version attribute does not parse as an integer, value is " + version, null);
		}

		
		nullIdCheck(f);
		
		delta.add(f);
		
		return f;
	}
	
	static public void buildMetadataIndex(Metadata m, MetadataCollection delta) throws MetadataException {
		for (OMElement ele : m.getExtrinsicObjects()) 
			buildDocEntryIndex(m, ele, delta);
		
		
		for (OMElement ele : m.getSubmissionSets()) 
			buildSubSetIndex(m, ele, delta);
		
		for (OMElement ele : m.getFolders()) 
			buildFolIndex(m, ele, delta);
		
		for (OMElement ele : m.getAssociations())
			buildAssocIndex(m, ele, delta);
	}
	
	static void nullIdCheck(Ro ro) throws MetadataException {
		if (ro.id == null)
			throw new MetadataException("RegistryObject has null value for id", null);
	}
}
