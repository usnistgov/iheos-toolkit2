package gov.nist.toolkit.fhir.simulators.sim.reg;

import gov.nist.toolkit.common.datatypes.UuidValidator;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.IdParser;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.*;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.fhir.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valregmsg.message.MetadataContainer;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegRSim extends TransactionSimulator   {
	protected Metadata m = null;
	public MetadataCollection mc;
	public MetadataCollection delta;
	protected MessageValidatorEngine mvc;
    protected DsSimCommon dsSimCommon;

	static Logger log = Logger.getLogger(RegRSim.class);


	public RegRSim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
		super(common, simulatorConfig);
        this.dsSimCommon = dsSimCommon;
	}

	public Map<String, String> UUIDToSymbolic = null;
	Map<String, String> symbolicToUUID = null;
	List<String> submittedUUIDs;

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		this.mvc = mvc;

		// These steps are common to Registry and Update.  They operate
		// on the entire metadata collection in both transactions.
		setup();

		// Verify patient id against patient identity feed
		if (vc.validateAgainstPatientIdentityFeed) {
			log.debug("validating patient id ");
			// this MetadataCollection is separate from delta and only used for PID validation
			MetadataCollection submission = new MetadataCollection();
			try {
				RegistryFactory.buildMetadataIndex(m, submission);
			} catch (MetadataException e) {
				er.err(Code.XDSRegistryMetadataError, e);
				return;
			}
			if (submission.subSetCollection.size() > 0) {
				SubSet ss = (SubSet) submission.subSetCollection.getAllRo().get(0);
				Pid pid = PidBuilder.createPid(ss.pid);
				try {
					if (pid == null || !common.db.patientIdExists(pid)) {
                        er.err(Code.XDSUnknownPatientId, "Patient ID " + ss.pid + " has not been received in a Patient Identity Feed", this, null);
                    }
				} catch (IOException e) {
					er.err(Code.XDSUnknownPatientId, "Patient ID " + ss.pid + " has not been received in a Patient Identity Feed", this, null);
				}
			}
		}

		// Check whether Extra Metadata is present, is allowed, and is legal
		//  - split into validation (as validator) and remover
		extraMetadataCheck(m);

		//  - some of the checks here can be done independent of the registry - move to validator
		processMetadata(m, new ProcessMetadataForRegister(er, mc, delta));

		// if errors then don't commit registry update
		if (hasErrors())
			return;

		// save metadata objects XML
		saveMetadataXml();

		// delta will be flushed to disk, assuming no errors, by caller

	}

	// These steps are common to Registry and Update.  They operate
	// on the entire metadata collection in both transactions.
	protected void setup() {

		// Pull metadata container off validation stack
		try {
			MetadataContainer metaCon = (MetadataContainer) dsSimCommon.getMessageValidatorIfAvailable(MetadataContainer.class);
			m = metaCon.getMetadata();
			if (m == null) throw new Exception("");
		} catch (Exception e) {
			er.err(Code.XDSRegistryError, "Internal Error: cannot access input metadata", this, null);
		}

		mc = dsSimCommon.regIndex.mc;

		// this will hold our updates - transaction style
		delta = mc.mkDelta();

		// allocate uuids for symbolic ids
		allocateUUIDs(m);

		m.re_index();

		logAssignedUUIDs();

		// Check for submission of id already present in registry
		checkSubmittedIdsNotInRegistry();

		// remove all instances of the home attribute
		rmHome();
	}

	// These steps are run on the entire metadata collection
	// for the Register transaction but only on an operation
	// for the Update transaction.
	public void processMetadata(Metadata m, ProcessMetadataInterface pmi) {

		// Are all UUIDs, submitted and generated, valid?
		validateUUIDs();

		// MU will change
		pmi.checkUidUniqueness(m);

		// set logicalId to id
		pmi.setLidToId(m);

		// install version attribute in SubmissionSet, DocumentEntry and Folder objects
		// install default version in Association, Classification, ExternalIdentifier
		pmi.setInitialVersion(m);

		// build update to metadata index with new objects
		// this will later be committed
		// This is done now because the operations below need this index
		buildMetadataIndex(m);

		// verify model/patient id linking rules are observed
		pmi.associationPatientIdRules();

		// set folder lastUpdateTime on folders in the submission
		// must be done after metadata index built
		pmi.setNewFolderTimes(m);

		// set folder lastUpdateTime on folders already in the registry
		// that this submission adds documents to
		// must be done after metadata index built
		pmi.updateExistingFolderTimes(m);

		// verify that no associations are being added that:
		//     reference a non-existant model in submission or registry
		//     reference a Deprecated model in registry
		pmi.verifyAssocReferences(m);

		// check for RPLC and RPLC_XFRM and do the deprecation
		pmi.doRPLCDeprecations(m);

		// if a replaced doc is in a Folder, then new doc is placed in folder
		// and folder lastUpateTime is updated
		pmi.updateExistingFoldersWithReplacedDocs(m);
	}

	void rmHome() {
		for (OMElement ele : m.getAllObjects()) {
			OMAttribute homeAtt = ele.getAttribute(MetadataSupport.home_qname);
			if (homeAtt != null)
				ele.removeAttribute(homeAtt);
		}
	}

	void saveMetadataXml() {
		try {
			delta.storeMetadata(m);
		} catch (Exception e1) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e1);
		}
	}

	public void buildMetadataIndex(Metadata m) {
		try {
			RegistryFactory.buildMetadataIndex(m, delta);
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}
	}

	void logAssignedUUIDs() {
		er.detail("Assigned UUIDs");
		if (symbolicToUUID != null) {
			for (String symId : symbolicToUUID.keySet()) {
				String uuidId = symbolicToUUID.get(symId);
				er.detail(symId + " ==> " + uuidId);
			}
		}
	}

	// Check for submission of id already present in registry
	void checkSubmittedIdsNotInRegistry() {
		for (String id : submittedUUIDs) {
			if (mc.hasObject(id))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Submission includes pre-assigned id " + id + " which is already present in the Registry", this, null);
		}
	}

	// allocate uuids for symbolic ids
	protected void allocateUUIDs(Metadata m) {
		IdParser ra = new IdParser(m);
		try {
			symbolicToUUID = ra.compileSymbolicNamesIntoUuids();
		} catch (XdsInternalException e1) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e1);
		}
		submittedUUIDs = ra.getSubmittedUUIDs();

		UUIDToSymbolic = reverse(symbolicToUUID);
	}

	void validateUUIDs() {
		UuidValidator validator;

		validator = new UuidValidator(er, "Validating submitted UUID ");
		for (String uuid : submittedUUIDs) {
			validator.validateUUID(uuid);
		}

		validator = new UuidValidator(er, "Validating generated UUID ");
		for (String uuid : UUIDToSymbolic.keySet()) {
			validator.validateUUID(uuid);
		}

	}

	// check for Extra Metadata
	void extraMetadataCheck(Metadata m) {
		SimulatorConfigElement extraMetadataASCE = simulatorConfig.get(SimulatorProperties.extraMetadataSupported);
		boolean isExtraMetadataSupported = extraMetadataASCE.asBoolean();

		for (OMElement ele : m.getMajorObjects()) {
			String id = m.getId(ele);
			try {
				for (OMElement slotEle : m.getSlots(id)) {
					String slotName = m.getSlotName(slotEle);
					if (!slotName.startsWith("urn:"))
						continue;
					if (slotName.equals("urn:ihe:iti:xds:2013:referenceIdList"))    // used by referenceIdList
						continue;
					if (slotName.startsWith("urn:ihe:")) {
						er.err(XdsErrorCode.Code.XDSRegistryError, "Illegal Slot name - " + slotName, "RegRSim.java", MetadataSupport.error_severity, "ITI-TF3:4.1.14");
						continue;
					}
					if (!isExtraMetadataSupported) {
						// register the warning to be returned
						er.err(XdsErrorCode.Code.XDSExtraMetadataNotSaved, "Extra Metadata Slot - " + slotName + " present. Extra metadata not supported by this registry", "RegRSim.java", MetadataSupport.warning_severity, "ITI-TF3:4.1.14");
						// remove the slot
						m.rmObject(slotEle);
					}
				}
			} catch (Exception e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e);
			}
		}
	}

	protected String getIdSubmittedValue(String id) {
		if (UUIDToSymbolic.get(id) == null)
			return id;
		return UUIDToSymbolic.get(id);
	}

	boolean isSubmittedIdValueUUID(String id) {
		String orig = getIdSubmittedValue(id);
		return orig.startsWith("urn:uuid:");
	}

	Map<String, String> reverse(Map<String, String> in)  {
		Map<String, String> out = new HashMap<String, String>();

		for (String key : in.keySet() ) {
			String val = in.get(key);
			out.put(val, key);
		}

		return out;
	}

	public boolean hasErrors() {
		return er.hasErrors() || mvc.hasErrors();
	}

	public void save(Metadata m, boolean buildIndex) {
		try {
			if (m.getSubmissionSet() != null)
				log.debug("Save SubmissionSet(" + m.getSubmissionSetId() + ")");
			for (OMElement ele : m.getExtrinsicObjects())
				log.debug("Save DocEntry(" + m.getId(ele) + ")");
			for (OMElement ele : m.getFolders())
				log.debug("Save Folder(" + m.getId(ele) + ")");
			for (OMElement ele : m.getAssociations())
				log.debug("Save Assoc(" + m.getId(ele) + ")("+ m.getAssocSource(ele) + ", " + m.getAssocTarget(ele) + ", " + m.getSimpleAssocType(ele) + ")");
		} catch (Exception e) {}

		if (buildIndex) {
			// update index
			try {
				RegistryFactory.buildMetadataIndex(m, delta);
			} catch (MetadataException e) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
			}
		}

		// save metadata objects XML
		try {
			delta.storeMetadata(m);
		} catch (Exception e1) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e1);
		}
	}
}
