package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetDocuments;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.List;

public class GetDocumentsSim extends GetDocuments {
	RegIndex ri;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}


	public GetDocumentsSim(StoredQuerySupport sqs) {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();

		Metadata m = new Metadata();
		m.setVersion3();

		if (mc.vc.updateEnabled && !(metadataLevel == null || metadataLevel.equals("1") || metadataLevel.equals("2"))) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA121");
			String detail = "MetadataLevel found: '" + metadataLevel + "'";
			sqs.er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", detail);
			return new Metadata();
		}

		if (uuids != null) {
			if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
				m = mc.loadRo(uuids);
			} else {
				m.mkObjectRefs(uuids);
			}
		} else if (uids != null) {
			List<DocEntry> des = new ArrayList<DocEntry>();
			for (String uid : uids) {
				des.addAll(mc.docEntryCollection.getByUid(uid));
			}

			List<String> uuidList = new ArrayList<String>();
			for (DocEntry de : des) {
				uuidList.add(de.getId());
			}
			if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
				m = mc.loadRo(uuidList);
			} else {
				m.mkObjectRefs(uuidList);
			}
		} else if (lids != null) {
			if (!mc.vc.updateEnabled) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA122");
				sqs.er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
				return new Metadata();
			}
			if (metadataLevel == null || metadataLevel.equals("1")) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA123");
				sqs.er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
				return new Metadata();
			}

			List<DocEntry> des = new ArrayList<DocEntry> ();
			for (String lid : lids) {
				des.addAll(mc.docEntryCollection.getByLid(lid));
			}
			List<String> uuidList = new ArrayList<String>();
			for (DocEntry de : des) {
				uuidList.add(de.getId());
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
