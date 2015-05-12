package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.Fol;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetFoldersForDocument;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GetFoldersForDocumentSim extends GetFoldersForDocument {
	RegIndex ri;
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetFoldersForDocumentSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {
		
		MetadataCollection mc = ri.getMetadataCollection();
		
		List<String> docIds = new ArrayList<String>();
		
		if (uuid != null) {
			DocEntry de = mc.docEntryCollection.getById(uuid);
			if (de != null)
				docIds.add(de.getId());
		}
		
		if (uid != null) {
			List<DocEntry> des = mc.docEntryCollection.getByUid(uid);
			for (DocEntry de : des) {
				docIds.add(de.getId());
			}
		}
		
		HashSet<String> folIds = new HashSet<String>();
		
		for (String docid : docIds) {
			List<Assoc> as = mc.assocCollection.getBySourceDestAndType(null, docid, RegIndex.AssocType.HASMEMBER);
			for (Assoc a : as) {
				String sourceId = a.getFrom();
				Fol f = mc.folCollection.getById(sourceId);
				if (f != null)
					folIds.add(f.getId());
			}
		}

		
		Metadata m = new Metadata();
		m.setVersion3();
		if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
			m = mc.loadRo(folIds);
		} else {
			m.mkObjectRefs(folIds);
		}
		
		return m;
	}

}
