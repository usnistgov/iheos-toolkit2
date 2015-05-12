package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.valregmetadata.field.SubmissionStructure;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

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
			
			// get object from registry
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
						er.err(XdsErrorCode.Code.XDSRegistryError, "Internal Registry error - folder known to exist cannot be accesed", this, null);
					}
				}
			}
		}
	}

	// verify that no associations are being added that:
	//     reference a non-existant object in submission or registry
	//     reference a Deprecated object in registry
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
				} else if (ro.getAvailabilityStatus() == RegIndex.StatusValue.DEPRECATED) {
					er.err(Code.XDSRegistryError, "Association " + 
							type + "(" + m.getId(assocEle) + ")" +
							" references an object with its sourceObject attribute that has status Deprecated in the registry. " +
							"Object is " + source
							, this, null);
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
