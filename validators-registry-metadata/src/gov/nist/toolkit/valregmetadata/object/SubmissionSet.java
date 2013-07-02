package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valregmetadata.datatype.XonXcnFormat;
import gov.nist.toolkit.valregmetadata.datatype.XonXcnXtnFormat;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.axiom.om.OMElement;

public class SubmissionSet extends AbstractRegistryObject implements TopLevelObject {

	static List<String> statusValues = 
			Arrays.asList(
					MetadataSupport.status_type_namespace + "Approved"
					);

	static List<String> definedSlots = 
			Arrays.asList(
					"intendedRecipient",
					"submissionTime"
					);

	static List<String> requiredSlots = 
			Arrays.asList(
					"submissionTime"
					);

	static List<String> requiredSlotsMinimal = 
			Arrays.asList(
					"intendedRecipient"
					);

	static public ClassAndIdDescription classificationDescription = new ClassAndIdDescription();
	static {
		classificationDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid ,
						MetadataSupport.XDSSubmissionSet_author_uuid
						);
		classificationDescription.requiredSchemes = 
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid 
						);
		classificationDescription.multipleSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_author_uuid
						);
		classificationDescription.names = new HashMap<String, String>();
		classificationDescription.names.put(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid, "Content Type Code");
		classificationDescription.names.put(MetadataSupport.XDSSubmissionSet_author_uuid, "Author");
	} 

	static public ClassAndIdDescription XDMclassificationDescription = new ClassAndIdDescription();
	static {
		XDMclassificationDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid ,
						MetadataSupport.XDSSubmissionSet_author_uuid
						);
		XDMclassificationDescription.requiredSchemes = 
				Arrays.asList(
						);
		XDMclassificationDescription.multipleSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_author_uuid
						);
		XDMclassificationDescription.names = new HashMap<String, String>();
		XDMclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid, "Content Type Code");
		XDMclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_author_uuid, "Author");
	} 

	static public ClassAndIdDescription MinimalclassificationDescription = new ClassAndIdDescription();
	static {
		MinimalclassificationDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid ,
						MetadataSupport.XDSSubmissionSet_author_uuid
						);
		MinimalclassificationDescription.requiredSchemes = 
				Arrays.asList(
						);
		MinimalclassificationDescription.multipleSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_author_uuid
						);
		MinimalclassificationDescription.names = new HashMap<String, String>();
		//MinimalclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid, "Content Type Code");
		MinimalclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_author_uuid, "Author");
	} 

	static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
	static {
		externalIdentifierDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_patientid_uuid,
						MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
						MetadataSupport.XDSSubmissionSet_sourceid_uuid
						);

		externalIdentifierDescription.requiredSchemes = 
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_patientid_uuid,
						MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
						MetadataSupport.XDSSubmissionSet_sourceid_uuid
						);
		externalIdentifierDescription.multipleSchemes = new ArrayList<String>(); 

		externalIdentifierDescription.names = new HashMap<String, String>();
		externalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_patientid_uuid, "Patient ID");
		externalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_uniqueid_uuid, "Unique ID");
		externalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_sourceid_uuid, "Source ID");
	}

	static public ClassAndIdDescription XDMexternalIdentifierDescription = new ClassAndIdDescription();
	static {
		XDMexternalIdentifierDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_patientid_uuid,
						MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
						MetadataSupport.XDSSubmissionSet_sourceid_uuid
						);

		XDMexternalIdentifierDescription.requiredSchemes = 
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
						MetadataSupport.XDSSubmissionSet_sourceid_uuid
						);
		XDMexternalIdentifierDescription.multipleSchemes = new ArrayList<String>(); 

		XDMexternalIdentifierDescription.names = new HashMap<String, String>();
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_patientid_uuid, "Patient ID");
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_uniqueid_uuid, "Unique ID");
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_sourceid_uuid, "Source ID");
	}

	static public ClassAndIdDescription MinimalexternalIdentifierDescription = new ClassAndIdDescription();
	static {
		MinimalexternalIdentifierDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_patientid_uuid,
						MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
						MetadataSupport.XDSSubmissionSet_sourceid_uuid
						);

		MinimalexternalIdentifierDescription.requiredSchemes = 
				Arrays.asList(
						MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
						MetadataSupport.XDSSubmissionSet_sourceid_uuid
						);
		MinimalexternalIdentifierDescription.multipleSchemes = new ArrayList<String>(); 

		MinimalexternalIdentifierDescription.names = new HashMap<String, String>();
		//MinimalexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_patientid_uuid, "Patient ID");
		MinimalexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_uniqueid_uuid, "Unique ID");
		MinimalexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_sourceid_uuid, "Source ID");
	}



	static public String table416 = "ITI TF-3: Table 4.1-6";

	public SubmissionSet(Metadata m, OMElement ro) throws XdsInternalException  {
		super(m, ro);
	}

	public SubmissionSet(String id) {
		super(id);
		internalClassifications.add(new InternalClassification("cl" + id, id, MetadataSupport.XDSSubmissionSet_classification_uuid));
	}

	public boolean equals(SubmissionSet s)  {
		if (!id.equals(id)) 
			return false;
		return	super.equals(s);
	}

	public String identifyingString() {
		return "SubmissionSet(" + getId() + ")";
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

	public boolean isMetadataLimited() {
		return isClassifiedAs(MetadataSupport.XDSSubmissionSet_limitedMetadata_uuid);
	}


	public void validate(ErrorRecorder er, ValidationContext vc,
			Set<String> knownIds) {

		if (vc.skipInternalStructure)
			return;

		if (vc.isXDR)
			vc.isXDRLimited = isMetadataLimited();

		if (vc.isXDRLimited)
			er.sectionHeading("is labeled as Limited Metadata");

		if (vc.isXDRMinimal)
			er.sectionHeading("is labeled as Minimal Metadata (Direct)");

		validateTopAtts(er, vc);

		validateSlots(er, vc);

		if (vc.isXDM || vc.isXDRLimited)
			validateClassifications(er, vc, XDMclassificationDescription, table416);
		else if (vc.isXDRMinimal)
			validateClassifications(er, vc, MinimalclassificationDescription, table416);
		else
			validateClassifications(er, vc, classificationDescription, table416);

		if (vc.isXDM || vc.isXDRLimited)
			validateExternalIdentifiers(er, vc, XDMexternalIdentifierDescription, table416);
		else if (vc.isXDRMinimal)
			validateExternalIdentifiers(er, vc, MinimalexternalIdentifierDescription, table416);
		else
			validateExternalIdentifiers(er, vc, externalIdentifierDescription, table416);

		verifyIdsUnique(er, knownIds);
	}

	public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {
		if (vc.isXDRMinimal) { 
			validateDirectSlotsCodedCorrectly(er, vc);
		} else {
			//                    name				   multi	format                                                  resource
			validateSlot(er, 	"submissionTime", 	   false, 	new DtmFormat(er, "Slot submissionTime",            table416),  table416);
			validateSlot(er, 	"intendedRecipient",   true, 	new XonXcnXtnFormat(er, "Slot intendedRecipient",      table416),  table416);
		}
	}

	public void validateDirectSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {

		//                    name				   multi	format                                                  resource
		validateSlot(er, 	"submissionTime", 	   false, 	new DtmFormat(er, "Slot submissionTime",            table416),  table416);
		validateSlot(er, 	"intendedRecipient",   true, 	new XonXcnXtnFormat(er, "Slot intendedRecipient",     table416),  table416);
	}

	public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc) {
		// Slots always required
		if (vc.isXDRMinimal) {
			for (String slotName : requiredSlotsMinimal) {
				if (getSlot(slotName) == null)
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": Slot " + slotName + " missing", this, table416);
			}
		} else {
			for (String slotName : requiredSlots) {
				if (getSlot(slotName) == null)
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": Slot " + slotName + " missing", this, table416);
			}
		}
	}

	public void validateSlotsLegal(ErrorRecorder er)  {
		verifySlotsUnique(er);
		for (Slot slot : getSlots()) {
			if ( ! legal_slot_name(slot.getName()))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": " + slot.getName() + " is not a legal slot name for a SubmissionSet",  this,  table416);

		}
	}

	boolean legal_slot_name(String name) {
		if (name == null) return false;
		if (name.startsWith("urn:")) return true;
		return definedSlots.contains(name);
	}

	public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
		validateTopAtts(er, vc, table416, statusValues);
	}


}
