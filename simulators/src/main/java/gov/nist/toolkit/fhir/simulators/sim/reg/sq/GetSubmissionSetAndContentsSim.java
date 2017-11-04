package gov.nist.toolkit.fhir.simulators.sim.reg.sq;

import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.Fol;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.Ro;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.SubSet;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetSubmissionSetAndContents;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GetSubmissionSetAndContentsSim extends GetSubmissionSetAndContents {
	RegIndex ri;
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetSubmissionSetAndContentsSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();
//		List<Ro> results = new ArrayList<Ro>();
		
		SubSet ss = null;
		if (ss_uuid != null) {
			ss = mc.subSetCollection.getById(ss_uuid);
		}
		else if (ss_uid != null) {
			ss = mc.subSetCollection.getByUid(ss_uid);
		} else {
			getStoredQuerySupport().er.err(Code.XDSRegistryError, "Internal error: uid and uuid both null", this, null);
		}
		
		if (ss == null) 
			return new Metadata();
		
		
		String ssid = ss.getId();
		
		List<Assoc> ssAssocs = mc.assocCollection.getBySourceDestAndType(ssid, null, RegIndex.AssocType.HASMEMBER);
		
		// grab everything hanging off a SS HasMember assoc
		
		List<DocEntry> docEntries = new ArrayList<DocEntry>();
		List<Fol> fols = new ArrayList<Fol>();
		List<Assoc> assocs = new ArrayList<Assoc>();
		
		for (Assoc a : ssAssocs) {
			String toId = a.getTo();
			
			DocEntry de = mc.docEntryCollection.getById(toId);
			if (de != null) {
				docEntries.add(de);
				continue;
			}
			
			Fol f = mc.folCollection.getById(toId);
			if (f != null) {
				fols.add(f);
				continue;
			}
			
			Assoc as = mc.assocCollection.getById(toId);
			if (as != null) {
				assocs.add(as);
				continue;
			}
		}
		
		// next remove docs that don't meet validate requirements based on formatCode and confidentialityCode
		try {
			if (format_code != null)
				docEntries = mc.docEntryCollection.filterByFormatCode(format_code, docEntries);
			if (conf_code != null)
				docEntries = mc.docEntryCollection.filterByConfCode(conf_code, docEntries);
			if (entry_type != null) {
				docEntries = mc.docEntryCollection.filterByObjectType(entry_type, docEntries);
				// could be other things
//				if (docEntries==null || (docEntries!=null && docEntries.isEmpty())) {
//					return new Metadata();
//				}
			}

		} catch (Exception e) {
			getStoredQuerySupport().er.err(Code.XDSRegistryError, "Error filtering DocumentEntries by formatCode, confidentialityCode, or documentEntryType: " + e.getMessage(), this, null);
			return new Metadata();
		}

		
		// grab assocs between included folders and include docs
		List<Assoc> folderAssocs = new ArrayList<Assoc>();
		
		for (Fol f : fols) {
			for (DocEntry de : docEntries) {
				List<Assoc> aa = mc.assocCollection.getBySourceDestAndType(f.getId(), de.getId(), RegIndex.AssocType.HASMEMBER);
				folderAssocs.addAll(aa);
			}
		}
				
		List<Ro> ros = new ArrayList<Ro>();
		ros.add(ss);
		ros.addAll(ssAssocs);
		ros.addAll(docEntries);
		ros.addAll(fols);
		ros.addAll(assocs);
		ros.addAll(folderAssocs);
		
		// validate out assocs who reference objects not in the set
		// this is done twice because the first time could validate out a Fol-Doc assoc
		// the second time could validate out the SS-Assoc assoc that references the above assoc
		ros = mc.filterAssocs(ros);
		ros = mc.filterAssocs(ros);
		
		List<String> raw_uuids = mc.getIdsForObjects(ros);
		HashSet<String> uuids = new HashSet<String>();
		uuids.addAll(raw_uuids);
		
		Metadata m = new Metadata();
		m.setVersion3();
		if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
			m = mc.loadRo(uuids);
		} else {
			m.mkObjectRefs(uuids);
		}
		
		return m;
	}

}
