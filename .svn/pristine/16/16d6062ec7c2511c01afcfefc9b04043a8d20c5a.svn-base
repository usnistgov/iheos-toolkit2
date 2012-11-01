package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetDocumentsAndAssociations;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GetDocumentsAndAssociationsSim extends GetDocumentsAndAssociations {
	RegIndex ri;
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetDocumentsAndAssociationsSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();
		
		HashSet<String> returnIds = new HashSet<String>();
		
		if (uuids != null) {
			for (String id : uuids) {
				DocEntry de = mc.docEntryCollection.getById(id);
				if (de != null)
					returnIds.add(de.getId());
			}
		}
		
		if (uids != null) {
			for (String uid : uids) {
				List<DocEntry> des = mc.docEntryCollection.getByUid(uid);
				for (DocEntry de : des) {
					returnIds.add(de.getId());
				}
			}
		}
		
		List<Assoc> assocs = new ArrayList<Assoc>();
		
		for (String id : returnIds) {
			List<Assoc> as = mc.assocCollection.getBySourceOrDest(id, id);
			assocs.addAll(as);
		}
		
		for (Assoc a : assocs)
			returnIds.add(a.getId());
		
		Metadata m = new Metadata();
		m.setVersion3();
		if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
			m = mc.loadRo(returnIds);
		} else {
			m.mkObjectRefs(returnIds);
		}
		
		return m;
	}

}
