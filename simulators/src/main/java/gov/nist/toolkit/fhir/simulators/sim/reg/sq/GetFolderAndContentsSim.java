package gov.nist.toolkit.fhir.simulators.sim.reg.sq;

import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.*;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetFolderAndContents;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsException;

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
		List<Ro> results = new ArrayList<>();
		
		List<Fol> fols = new ArrayList<>();
		if (fol_uuid != null) {
			fols.add(mc.folCollection.getById(fol_uuid));
		}
		else if (fol_uid != null) {
		    List<Fol> exclude = new ArrayList<>();
			fols = mc.folCollection.getByUid(fol_uid);
			if (!metadataLevel2) {
			    for (Fol fol : fols) {
			        if (fol.getAvailabilityStatus() != StatusValue.APPROVED)
			            exclude.add(fol);
                }
                fols.removeAll(exclude);
            }
		} else {
			getStoredQuerySupport().er.err(Code.XDSRegistryError, "Internal error: uid and uuid both null", this, null);
		}
		
		if (fols.isEmpty()) {
			return new Metadata();
		} else {
			results.addAll(fols);
		}



		Metadata allM = new Metadata();

		// this can be multiple because of MU
		for (Fol fol : fols) {
			String folid = fol.getId();

			List<Assoc> folAssocs = mc.assocCollection.getBySourceDestAndType(folid, null, RegIndex.AssocType.HasMember);
			folAssocs = mc.filterAssocsByStatus(folAssocs, status);

			List<DocEntry> docEntries = new ArrayList<>();

			for (Assoc a : folAssocs) {
				String toId = a.getTo();

				DocEntry de = mc.docEntryCollection.getById(toId);
				if (de != null) {
				    docEntries.add(de);
				}
			}

			// next remove docs that don't meet validate requirements based on formatCode and confidentialityCode
			try {
				if (format_code != null)
					docEntries = mc.docEntryCollection.filterByFormatCode(format_code, docEntries);
				if (conf_code != null)
					docEntries = mc.docEntryCollection.filterByConfCode(conf_code, docEntries);
			} catch (Exception e) {
				getStoredQuerySupport().er.err(Code.XDSRegistryError, "Error filtering DocumentEntries by formatCode or confidentialityCode: " + e.getMessage(), this, null);
				return new Metadata();
			}

			List<Ro> ros = new ArrayList<>();
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
			allM.addAllObjects(m);
		}
		
		return allM;

	}

}
