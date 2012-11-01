package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.reg.store.SubSet;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetSubmissionSets;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.HashSet;
import java.util.List;

public class GetSubmissionSetsSim extends GetSubmissionSets {
	RegIndex ri;
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}

	public GetSubmissionSetsSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();
		
		HashSet<String> returnIds = new HashSet<String>();
		
		for (String id : uuids) {
			List<Assoc> assocs = mc.assocCollection.getBySourceDestAndType(null, id, RegIndex.AssocType.HASMEMBER);
			for (Assoc assoc : assocs) {
				String sourceId = assoc.getFrom();
				SubSet s = mc.subSetCollection.getById(sourceId);
				if (s == null)
					continue;
				returnIds.add(sourceId);
				returnIds.add(assoc.getId());
			}
		}
		
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
