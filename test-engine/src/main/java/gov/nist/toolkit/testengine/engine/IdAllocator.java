package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.HashMap;
import java.util.List;


public abstract class IdAllocator {
	TestConfig testConfig;

	String mgmt_dir;
	File patientIdFile;
	File altPatientIdFile;
	protected File uniqueIdBaseFile;
	protected File uniqueIdIncrFile;
	File sourceIdFile;

	abstract String allocate() throws XdsInternalException;

	// object_type is ExtrinsicObject or RegistryPackage
	//
	// Although the variable names below imply uniqueId, they are use for patient ID also based on the sub-class
	//
	//

	public IdAllocator(TestConfig config) {
		if (config != null) {
			testConfig = config;
			mgmt_dir = testConfig.testmgmt_dir;
			patientIdFile = new File(mgmt_dir + File.separator + "patientid.txt");
			altPatientIdFile = new File(mgmt_dir + File.separator + "patientid_alt.txt");
			uniqueIdBaseFile = new File(mgmt_dir + File.separator + "uniqueid_base.txt");
			uniqueIdIncrFile = new File(mgmt_dir + File.separator + "uniqueid_incr.txt");
			sourceIdFile = new File(mgmt_dir + File.separator + "sourceid.txt");
		}
	}

	IdAllocator() {}

	public String assign(Metadata metadata, String object_type, String external_identifier_uuid, HashMap<String, String> assignments, String no_assign_uid_to)
	throws XdsInternalException {

		List<OMElement> eos;

		if (object_type.equals("ExtrinsicObject"))
			eos = metadata.getExtrinsicObjects();
		else if (object_type.equals("RegistryPackage"))
			eos = metadata.getRegistryPackages();
		else
			throw new XdsInternalException("Only ExtrinsicObject and RegistryPackage supported - request was for " + object_type);

		String new_value = null;
		for (int i=0; i<eos.size(); i++) {
			OMElement eo = (OMElement) eos.get(i);
			String id = eo.getAttributeValue(MetadataSupport.id_qname);
			if (no_assign_uid_to != null) {
				if (id.equals(no_assign_uid_to))
					continue;
			}
			// for all xxx.uniqueId
			List eis = metadata.getExternalIdentifiers(eo, external_identifier_uuid);
			for (int j=0; j<eis.size(); j++) {
				OMElement ei = (OMElement) eis.get(j);
				OMAttribute uniqueid_value_att = ei.getAttribute(new QName("value"));
				String old_value = uniqueid_value_att.getAttributeValue();
				new_value = allocate();
				uniqueid_value_att.setAttributeValue(new_value);
				assignments.put(id, new_value);
			}
		}
		return new_value;
	}

}
