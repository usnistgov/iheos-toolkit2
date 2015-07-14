package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetRelatedDocuments;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GetRelatedDocumentsSim extends GetRelatedDocuments {
	RegIndex ri;

	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetRelatedDocumentsSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
	XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();

		//		HashSet<String> uuids = new HashSet<String>();
		
		List<RegIndex.AssocType> assocTypes = new ArrayList<RegIndex.AssocType>();
		
		for (String atype : assoc_types) {
			assocTypes.add(RegIndex.getAssocType(atype));
		}

		HashSet<String> uuids = new HashSet<String>();

		HashSet<String> docEntryIds = new HashSet<String>(); 

		if (uuid != null) {
			DocEntry de = mc.docEntryCollection.getById(uuid);
			if (de != null)
				docEntryIds.add(de.getId());
		}

		if (uid != null) {
			List<DocEntry> des = mc.docEntryCollection.getByUid(uid);
			for (DocEntry de : des) {
				docEntryIds.add(de.getId());
			}
		}
		
		uuids.addAll(docEntryIds);
		
		for (String id : docEntryIds) {
			List<Assoc> as = mc.assocCollection.getBySourceOrDest(id, id);
			for (Assoc a : as) {
				if (!assocTypes.contains(a.getAssocType()))
					continue;
				
				String sourceId = a.getFrom();
				String targetId = a.getTo();
				DocEntry sourceDoc = mc.docEntryCollection.getById(sourceId);
				DocEntry targetDoc = mc.docEntryCollection.getById(targetId);
				
				// One side of Association points to our focus DocEntry
				// if the other side points to a DocEntry then include
				// the Assoc and DocEntry in the output
				if (!sourceId.equals(id) && sourceDoc != null) {
					uuids.add(sourceId);
					uuids.add(a.getId());
				}

				if (!targetId.equals(id) && targetDoc != null) {
					uuids.add(targetId);
					uuids.add(a.getId());
				}
					
					
			}
		}
		
		if (uuids.size() == 1)
			uuids.clear();


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
