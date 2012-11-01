package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.axiom.om.OMElement;

public class Folder extends AbstractRegistryObject implements TopLevelObject {

	static List<String> statusValues = 
		Arrays.asList(
				MetadataSupport.status_type_namespace + "Approved"
		);

	static List<String> definedSlots = 
		Arrays.asList(
				"lastUpdateTime"
		);

	static List<String> requiredSlots = new ArrayList<String>();

	static public ClassAndIdDescription classificationDescription = new ClassAndIdDescription();
	static {
		classificationDescription.definedSchemes =
			Arrays.asList(
					MetadataSupport.XDSFolder_codeList_uuid
			);
		classificationDescription.requiredSchemes = 
			Arrays.asList(
					MetadataSupport.XDSFolder_codeList_uuid
			);
		classificationDescription.multipleSchemes =
			Arrays.asList(
					MetadataSupport.XDSFolder_codeList_uuid
			);
		classificationDescription.names = new HashMap<String, String>();
		classificationDescription.names.put(MetadataSupport.XDSFolder_codeList_uuid, "Code List");
	} 
	
	static public ClassAndIdDescription XDMclassificationDescription = new ClassAndIdDescription();
	static {
		XDMclassificationDescription.definedSchemes =
			Arrays.asList(
					MetadataSupport.XDSFolder_codeList_uuid
			);
		XDMclassificationDescription.requiredSchemes = 
			Arrays.asList(
			);
		XDMclassificationDescription.multipleSchemes =
			Arrays.asList(
					MetadataSupport.XDSFolder_codeList_uuid
			);
		XDMclassificationDescription.names = new HashMap<String, String>();
		XDMclassificationDescription.names.put(MetadataSupport.XDSFolder_codeList_uuid, "Code List");
	} 
	
	static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
	static {
		externalIdentifierDescription.definedSchemes =
			Arrays.asList(
					MetadataSupport.XDSFolder_patientid_uuid,
					MetadataSupport.XDSFolder_uniqueid_uuid
			);
		
		externalIdentifierDescription.requiredSchemes = 
			Arrays.asList(
					MetadataSupport.XDSFolder_patientid_uuid,
					MetadataSupport.XDSFolder_uniqueid_uuid
					);
		externalIdentifierDescription.multipleSchemes = new ArrayList<String>(); 
		
		externalIdentifierDescription.names = new HashMap<String, String>();
		externalIdentifierDescription.names.put(MetadataSupport.XDSFolder_patientid_uuid, "Patient ID");
		externalIdentifierDescription.names.put(MetadataSupport.XDSFolder_uniqueid_uuid, "Unique ID");
	}

	static public ClassAndIdDescription XDMexternalIdentifierDescription = new ClassAndIdDescription();
	static {
		XDMexternalIdentifierDescription.definedSchemes =
			Arrays.asList(
					MetadataSupport.XDSFolder_patientid_uuid,
					MetadataSupport.XDSFolder_uniqueid_uuid
			);
		
		XDMexternalIdentifierDescription.requiredSchemes = 
			Arrays.asList(
					MetadataSupport.XDSFolder_uniqueid_uuid
					);
		XDMexternalIdentifierDescription.multipleSchemes = new ArrayList<String>(); 
		
		XDMexternalIdentifierDescription.names = new HashMap<String, String>();
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSFolder_patientid_uuid, "Patient ID");
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSFolder_uniqueid_uuid, "Unique ID");
	}


	public Folder(Metadata m, OMElement ro) throws XdsInternalException  {
		super(m, ro);
	}
	
	public Folder(String id) {
		super(id);
		internalClassifications.add(new InternalClassification("cl" + id, id, MetadataSupport.XDSFolder_classification_uuid));

	}
	public boolean isMetadataLimited() {
		return isClassifiedAs(MetadataSupport.XDSFolder_limitedMetadata_uuid);
	}

	static public String table417 = "ITI TF-3: Table 4.1-7";

	public String identifyingString() {
		return "Folder(" + getId() + ")";	
	}

	public OMElement toXml() throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.registrypackage_qnamens);
		ro.addAttribute("id", id, null);
		if (status != null)
			ro.addAttribute("status", status, null);
		if (home != null)
			ro.addAttribute("home", home, null);

		addSlotsXml(ro);
		addNameToXml(ro);
		addDescriptionXml(ro);
		addClassificationsXml(ro);
		addAuthorsXml(ro);
		addExternalIdentifiersXml(ro);

		return ro;
	}

	public void validate(ErrorRecorder er, ValidationContext vc,
			Set<String> knownIds) {
		
		if (vc.skipInternalStructure)
			return;
		
		if (vc.isXDR)
			vc.isXDRLimited = isMetadataLimited();
		
		if (vc.isXDRLimited)
			er.sectionHeading("Limited Metadata");

		validateTopAtts(er, vc);

		validateSlots(er, vc);

		if (vc.isXDM || vc.isXDRLimited)
			validateClassifications(er, vc, XDMclassificationDescription, table417);
		else
			validateClassifications(er, vc, classificationDescription, table417);

		if (vc.isXDM || vc.isXDRLimited)
			validateExternalIdentifiers(er, vc, XDMexternalIdentifierDescription, table417);
		else
			validateExternalIdentifiers(er, vc, externalIdentifierDescription, table417);

		verifyIdsUnique(er, knownIds);
	}

	public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {

		//                    name				   multi	format                                                  resource
		validateSlot(er, 	"lastUpdateTime", 	   false, 	new DtmFormat(er, "Slot lastUpdateTime",            table417),  table417);
	}

	public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc) {
		// Slots always required
		for (String slotName : requiredSlots) {
			if (getSlot(slotName) == null)
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": Slot " + slotName + " missing", this, table417);
		}
	}

	public void validateSlotsLegal(ErrorRecorder er)  {
		verifySlotsUnique(er);
		for (Slot slot : getSlots()) {
			if ( ! legal_slot_name(slot.getName()))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": " + slot.getName() + " is not a legal slot name for a SubmissionSet",  this,  table417);

		}
	}

	boolean legal_slot_name(String name) {
		if (name == null) return false;
		if (name.startsWith("urn:")) return true;
		return definedSlots.contains(name);
	}

	public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
		validateTopAtts(er, vc, table417, statusValues);
	}

	public boolean equals(Folder f)  {
		if (!id.equals(id)) 
			return false;
		return	super.equals(f);
	}


}
