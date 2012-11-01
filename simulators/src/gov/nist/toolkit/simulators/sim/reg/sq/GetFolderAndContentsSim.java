package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.Fol;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.reg.store.Ro;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetFolderAndContents;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.List;

public class GetFolderAndContentsSim extends GetFolderAndContents {
	RegIndex ri;
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetFolderAndContentsSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {
		
		MetadataCollection mc = ri.getMetadataCollection();
		List<Ro> results = new ArrayList<Ro>();
		
		Fol fol = null;
		if (fol_uuid != null) {
			fol = mc.folCollection.getById(fol_uuid);
		}
		else if (fol_uid != null) {
			fol = mc.folCollection.getByUid(fol_uid);
		} else {
			getStoredQuerySupport().er.err(Code.XDSRegistryError, "Internal error: uid and uuid both null", this, null);
		}
		
		if (fol == null) {
			return new Metadata();
		} else {
			results.add(fol);
		}

		String folid = fol.getId();
		
		List<Assoc> folAssocs = mc.assocCollection.getBySourceDestAndType(folid, null, RegIndex.AssocType.HASMEMBER);
		List<DocEntry> docEntries = new ArrayList<DocEntry>();

//		results.addAll(folAssocs);
		
		for (Assoc a : folAssocs) {
			String toId = a.getTo();
			
			DocEntry de = mc.docEntryCollection.getById(toId);
			if (de != null) {
				docEntries.add(de);
				continue;
			}
			
		}
		
		// next remove docs that don't meet filter requirements based on formatCode and confidentialityCode
		try {
			if (format_code != null)
				docEntries = mc.docEntryCollection.filterByFormatCode(format_code, docEntries);
			if (conf_code != null)
				docEntries = mc.docEntryCollection.filterByConfCode(conf_code, docEntries);
		} catch (Exception e) {
			getStoredQuerySupport().er.err(Code.XDSRegistryError, "Error filtering DocumentEntries by formatCode or confidentialityCode: " + e.getMessage(), this, null);
			return new Metadata();
		}

		List<Ro> ros = new ArrayList<Ro>();
		ros.add(fol);
		ros.addAll(docEntries);

		// add in assocs where source and target are in response
		// the source attributes all ref the folder
		for (Assoc a : folAssocs) {
			String target = a.getTo();
			
			if (mc.docEntryCollection.hasObject(target, docEntries))
				ros.add(a);
		}
		
		List<String> uuids = mc.getIdsForObjects(ros);

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
