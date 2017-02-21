package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.valregmetadata.field.SubmissionStructure;
import gov.nist.toolkit.xdsexception.MetadataException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProcessMetadataForRegister implements ProcessMetadataInterface {
	static Logger log = Logger.getLogger(ProcessMetadataForRegister.class);
	ErrorRecorder er;
	MetadataCollection mc;
	MetadataCollection delta;

	public ProcessMetadataForRegister(ErrorRecorder er, MetadataCollection mc, MetadataCollection delta) {
		this.er = er;
		this.mc = mc;
		this.delta = delta;
	}

	public void checkUidUniqueness(Metadata m) {
		List<String> submittedUIDs = new ArrayList<String>();
		for (OMElement ele : m.getMajorObjects()) {
			String id = m.getId(ele);
			if ( ! (m.isDocument(id) || m.isFolder(id) || m.isSubmissionSet(id))  ) 
				continue;

			log.debug("Processing metadata object " + m.getId(ele));
			String uid = null;
			try {
				uid = m.getUniqueIdValue(ele);
			} catch (MetadataException e) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
				continue;
			}
			if (uid == null) {
				log.error("Processing metadata object " + m.getId(ele) + " - Unable to extract uniqueId from object");
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Unable to extract uniqueId from object " + m.getId(ele), this, null);
				continue;
			}
			if (submittedUIDs.contains(uid)) 
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "UniqueID " + uid + "  is assigned to more than one object within the submission", this, null);
			submittedUIDs.add(uid);
			
			// getRetrievedDocumentsModel object from registry
			Ro ro = mc.getObjectByUid(uid);
			if (ro != null) {
				// RegistryObject with this UID already in Registry
				if (ro instanceof DocEntry) {
					DocEntry de = (DocEntry) ro;
					String hash = m.getSlotValue(ele, "hash", 0);
					if (de.hash == null || !de.hash.equals(hash))
						er.err(XdsErrorCode.Code.XDSNonIdenticalHash, "Registry contains DocumentEntry with UID " + uid + " and hash " + de.hash + 
								". This submission contains DocumentEntry with same UID and a different hash (" + hash + ")", this, null);
				} else
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Submission includes unique ID " + uid + " on object " +  m.getObjectDescription(ele)  + 
							". This UID is already present in the Registry as " + ro.getObjectDescription(), this, null);
			}
		}
	}

	public void setLidToId(Metadata m) {
		for (OMElement ele : m.getAllObjects()) 
			m.setLid(ele, m.getId(ele));
	}

	// install version in SubmissionSet, DocumentEntry and Folder objects
	// as well as the smaller RegistryObjects that require this attribute
	public void setInitialVersion(Metadata m) {
		for (OMElement ele : m.getExtrinsicObjects()) {
			m.setVersion(ele, "1");
		}
		for (OMElement ele : m.getFolders()) {
			m.setVersion(ele, "1");
		}
		for (OMElement ele : m.getSubmissionSets()) {
			m.setVersion(ele, "1");
		}
		try {
			m.setDefaultVersionOfUnversionedElements();
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}
	}

	// set folder lastUpdateTime on folders in the submission
	// must be done after metadata index built
	public void setNewFolderTimes(Metadata m) {
		try {
			for (OMElement ele : m.getFolders()) {
				Fol fol = delta.folCollection.getById(m.getId(ele));
				fol.lastUpdateTime = new Hl7Date().now();
			}
		} catch (Exception e) {
			er.err(Code.XDSRegistryError, e);
		}
	}

	// set folder lastUpdateTime on folders already in the registry
	// that this submission adds documents to
	// must be done after metadata index built
	public void updateExistingFolderTimes(Metadata m) {
		for (OMElement aele : m.getAssociations()) {
			String sourceId = m.getAssocSource(aele);
			if (!m.contains(sourceId)) {
				// folder not in submission

				if (new SubmissionStructure(m, mc).is_fol_to_de_hasmember(aele)) {
					// folder is in registry

					try {
						Fol f = delta.folCollection.getById(sourceId);  // will look in delta and main table
						delta.labelFolderUpdated(f, new Hl7Date().now());
					} catch (Exception e) {
						er.err(XdsErrorCode.Code.XDSRegistryError, "Internal Registry error - folder known to exist cannot be accessed", this, null);
					}
				}
			}
		}
	}

	// verify that no associations are being added that:
	//     reference a non-existant object in submission or registry
	//     reference a Deprecated object in registry
	//     link objects with different patient ids (except for special cases)
	public void verifyAssocReferences(Metadata m) {
		for (OMElement assocEle : m.getAssociations()) {
			String source = m.getAssocSource(assocEle);
			String target = m.getAssocTarget(assocEle);
			String type;
			try {
				type = m.getSimpleAssocType(assocEle);
			} catch (MetadataException e) {
				er.err(Code.XDSRegistryError, "Error extracting associationType attribute from Association " + m.getId(assocEle)
						, this, null);
				continue;
			}
			if (m.contains(source)) {
				// ok
			} else {
				Ro ro = delta.getObjectById(source);
				if (ro == null) {
					er.err(Code.XDSRegistryError, "Association " + 
							type + "(" + m.getId(assocEle) + ")" +
							" references an object with its sourceObject attribute that does not exist in submission or registry. " +
							"Object is " + source
							, this, null);
				} else {
					if (ro.getAvailabilityStatus() == RegIndex.StatusValue.DEPRECATED) {
						er.err(Code.XDSRegistryError, "Association " +
								type + "(" + m.getId(assocEle) + ")" +
								" references an object with its sourceObject attribute that has status Deprecated in the registry. " +
								"Object is " + source
								, this, null);
					}
				}
			}

			if (m.contains(target)) {
				// ok
			} else {
				Ro ro = delta.getObjectById(target);
				if (ro == null) {
					er.err(Code.XDSRegistryError, "Association " + 
							type + "(" + m.getId(assocEle) + ")" +
							" references an object with its targetObject attribute that does not exist in submission or registry. " +
							"Object is " + target
							, this, null);
				} else if (ro.getAvailabilityStatus() == RegIndex.StatusValue.DEPRECATED) {
					er.err(Code.XDSRegistryError, "Association " + 
							type + "(" + m.getId(assocEle) + ")" +
							" references an object with its targetObject attribute that has status Deprecated in the registry. " +
							"Object is " + target
							, this, null);
				}
			}

			if (type.equals("signs")) {
				evalSigns(er, m, source, target);
			} else if (type.equals("IsSnapshotOf")) {
				evalIsSnapshotOf(er, m, source, target);
			}

		}
	}

	/**
	 * See ITI TF-3: 4.2.2.2.6
	 * @param er
	 * @param m
	 * @param source
	 * @param target
	 */
	void evalIsSnapshotOf(ErrorRecorder er, Metadata m, String source, String target) {


		// Check sourceObject's objectType to see if it is of Stable type
		// ITI TF-3: 4.2.2.2.6
		// Bullet #1 The sourceObject references a DocumentEntry in the submission
		try {
			String sourceObjectType = m.getObjectTypeById(source);
			// #4 Verify that the objectType attribute of the sourceObject DocumentEntry is (Stable)
			if (!MetadataSupport.XDSDocumentEntry_objectType_uuid.equals(sourceObjectType)) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "sourceObject's DocumentEntry objectType ["+ sourceObjectType +"] is not of Stable type: " , this, "ITI TF-3: 4.2.2.2.6"); // Rev 12.1
			}
		} catch (MetadataException me) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Cannot retrieve the sourceObject's DocumentEntry objectType attribute" , this, "ITI TF-3: 4.2.2.2.6"); // Rev 12.1
		}

		// Check targetObject's objectType to see if it is of OD type
		try {

			// #2 The targetObject references a DocumentEntry in the Registry
			if (m.getObjectById(target)==null) {

				DocEntry de = ((DocEntry)delta.getObjectById(target));
				// #3 The targetObject DocumentEntry has availabilityStatus of Approved
				if (!RegIndex.StatusValue.APPROVED.equals(de.getAvailabilityStatus())) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "targetObject's DocumentEntry availabilityStatus is not Approved." , this, "ITI TF-3: 4.2.2.2.6"); // Rev 12.1
				}
				// #5 Verify that the objectType attribute of the targetObject DocumentEntry is (On-Demand)
				String targetObjectType = de.objecttype;
				if (!MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid.equals(targetObjectType)) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "targetObject's DocumentEntry objectType  ["+ targetObjectType +"] is not of On-Demand type." , this, "ITI TF-3: 4.2.2.2.6"); // Rev 12.1
				}

			} else {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "targetObject's DocumentEntry seems be present in the submission. It's only expected to be in the Registry." , this, "ITI TF-3: 4.2.2.2.6"); // Rev 12.1
			}

		} catch (MetadataException me) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Cannot retrieve the targetObject's DocumentEntry objectType attribute" , this, "ITI TF-3: 4.2.2.2.6"); // Rev 12.1
		}

	}

	/**
	 * See ITI TF-3: 4.2.2.5 Rev 12.1
	 * According to Bill, the content of the sourceObject is not checked at this point.
	 */
	void evalSigns(ErrorRecorder er, Metadata m, String source, String target) {
		//
	}


	// verify that no associations are being added that:
	//     link objects with different patient ids (except for special cases)
	public void associationPatientIdRules() {
		log.debug("Checking Association PID rules for " + delta.assocCollection.assocs);
		for (Assoc a : delta.assocCollection.assocs) {
			String fromId = a.getFrom();
			String toId = a.getTo();

			Ro sourceObject = delta.getObjectById(fromId);
			if (sourceObject == null) sourceObject = mc.getObjectById(fromId);

			Ro targetObject = delta.getObjectById(toId);
			if (targetObject == null) targetObject = mc.getObjectById(toId);

			if (sourceObject == null || targetObject == null) continue;  // checked elsewhere

			PatientObject src = null;
			if (sourceObject instanceof PatientObject) src = (PatientObject) sourceObject;
			PatientObject tgt = null;
			if (targetObject instanceof PatientObject) tgt = (PatientObject) targetObject;

			if (src == null || tgt == null) continue;

			if (src.pid != null && src.pid.equals(tgt.pid)) continue;  // all is good

			// if is Reference type HasMember then ok
			if (a.isReference) continue;
			er.err(Code.XDSPatientIdDoesNotMatch, "Association " +
					a.getType() + "(" + a.getId() + ")" +
					" links two objects with different Patient IDs: " +
					src.getType() + "(" + src.getId() + ") and " +
					tgt.getType() + "(" + tgt.getId() + ") "
					, this, null);
		}
	}


	// check for RPLC and RPLC_XFRM and do the deprecation
	public  void doRPLCDeprecations(Metadata m) {
		for (OMElement assocEle : m.getAssociations()) {
			AssocType atype = RegIndex.getAssocType(m, assocEle);
			if (atype == AssocType.RPLC || atype == AssocType.RPLC_XFRM) {
				String targetId = m.getAssocTarget(assocEle);
				deprecateDoc(targetId);
			}
		}
	}
	
	public void deprecateDoc(String docId) {
		Ro ro = mc.docEntryCollection.getRo(docId);
		if (ro == null) {
			er.err(Code.XDSRegistryError, "RPLC failed, replaced DocumentEntry [ " + docId + "] does not exist in registry", this, null);
		} else {
			// Deprecate
			delta.changeAvailabilityStatus(docId, ro.getAvailabilityStatus(), RegIndex.StatusValue.DEPRECATED);
			delta.mkDirty();
			// Find all XFRMs and APNDs and deprecate - recurse
			
			List<Assoc> assocs = mc.assocCollection.getBySourceDestAndType(null, docId, AssocType.XFRM);
			assocs.addAll(mc.assocCollection.getBySourceDestAndType(null, docId, AssocType.APND));
			for (Assoc assoc : assocs) {
				String id = assoc.from;
				deprecateDoc(id);
			}
		}
	}

	// if a replaced doc is in a Folder, then new doc is placed in folder
	// and folder lastUpateTime is updated
	// all these folders are in registry
	public void updateExistingFoldersWithReplacedDocs(Metadata m) {
		for (OMElement assocEle : m.getAssociations()) {
			AssocType atype = RegIndex.getAssocType(m, assocEle);
			if (atype == AssocType.RPLC || atype == AssocType.RPLC_XFRM) {
				String targetId = m.getAssocTarget(assocEle);
				String sourceId = m.getAssocSource(assocEle);
				
				List<Fol> foldersHoldingTarget = mc.getFoldersContaining(targetId);
				
				// add source doc to these folders
				for (Fol f : foldersHoldingTarget) {
					try {
						delta.addAssoc(f.getId(), sourceId, AssocType.HASMEMBER);
						delta.labelFolderUpdated(f, new Hl7Date().now());
					} catch (Exception e) {
						er.err(Code.XDSRegistryError, e.getMessage(), this, null);
					}
				}
			}
		}
	}




}
