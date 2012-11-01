package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.Fol;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetFolders;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.List;

public class GetFoldersSim extends GetFolders {
	RegIndex ri;

	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetFoldersSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
	XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();

		Metadata m = new Metadata();
		m.setVersion3();

		if (fol_uuid != null) {
			if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
				m = mc.loadRo(fol_uuid);
			} else {
				m.mkObjectRefs(fol_uuid);
			}
		} else if (fol_uid != null) {
			List<Fol> des = new ArrayList<Fol>();
			for (String uid : fol_uid) {
				Fol f = mc.folCollection.getByUid(uid);
				if (f != null)
					des.add(f);
			}
			
			List<String> uuidList = new ArrayList<String>();
			for (Fol f : des) {
				uuidList.add(f.getId());
			}
			if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
				m = mc.loadRo(uuidList);
			} else {
				m.mkObjectRefs(uuidList);
			}
		}

		return m;
	}

}
