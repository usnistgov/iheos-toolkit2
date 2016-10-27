package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.valregmetadata.datatype.AnyFormat;
import gov.nist.toolkit.valregmetadata.datatype.CxFormat;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valregmetadata.datatype.HashFormat;
import gov.nist.toolkit.valregmetadata.datatype.IntFormat;
import gov.nist.toolkit.valregmetadata.datatype.OidFormat;
import gov.nist.toolkit.valregmetadata.datatype.Rfc3066Format;
import gov.nist.toolkit.valregmetadata.datatype.SourcePatientInfoFormat;
import gov.nist.toolkit.valregmetadata.datatype.XcnFormat;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DocumentEntry extends AbstractRegistryObject implements TopLevelObject {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	static List<String> definedSlots =
			Arrays.asList(
					"creationTime",
					"languageCode",
					"sourcePatientId",
					"sourcePatientInfo",
					"legalAuthenticator",
					"serviceStartTime",
					"serviceStopTime",
					"hash",
					"size",
					"URI",
					"repositoryUniqueId",
					"documentAvailability",
					"urn:ihe:iti:xds:2013:referenceIdList"
			);

	static List<String> requiredSlots =
			Arrays.asList(
					"creationTime",
					"languageCode",
					"sourcePatientId"
			);

	static List<String> roddeRequiredSlots =
			Arrays.asList(
					"languageCode",
					"sourcePatientId"
			);

	static List<String> directRequiredSlots =
			Arrays.asList(
			);


	static public ClassAndIdDescription classificationDescription = new ClassAndIdDescription();
	static {
		classificationDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_classCode_uuid ,
						MetadataSupport.XDSDocumentEntry_confCode_uuid ,
						MetadataSupport.XDSDocumentEntry_eventCode_uuid ,
						MetadataSupport.XDSDocumentEntry_formatCode_uuid ,
						MetadataSupport.XDSDocumentEntry_hcftCode_uuid ,
						MetadataSupport.XDSDocumentEntry_psCode_uuid ,
						MetadataSupport.XDSDocumentEntry_typeCode_uuid,
						MetadataSupport.XDSDocumentEntry_author_uuid
				);
		classificationDescription.requiredSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_classCode_uuid,
						MetadataSupport.XDSDocumentEntry_confCode_uuid,
						MetadataSupport.XDSDocumentEntry_formatCode_uuid,
						MetadataSupport.XDSDocumentEntry_hcftCode_uuid,
						MetadataSupport.XDSDocumentEntry_psCode_uuid,
						MetadataSupport.XDSDocumentEntry_typeCode_uuid
				);
		classificationDescription.multipleSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_author_uuid,
						MetadataSupport.XDSDocumentEntry_confCode_uuid,
						MetadataSupport.XDSDocumentEntry_eventCode_uuid
				);
		classificationDescription.names = new HashMap<String, String>();
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_classCode_uuid, "Class Code");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_confCode_uuid, "Confidentiality Code");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_eventCode_uuid, "Event Codelist");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_formatCode_uuid, "Format Code");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_hcftCode_uuid, "Healthcare Facility Type Code");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_psCode_uuid, "Practice Setting Code");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_typeCode_uuid, "Type Code");
		classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_author_uuid, "Author");
	}

	static public ClassAndIdDescription directClassificationDescription = new ClassAndIdDescription();
	static {
		directClassificationDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_classCode_uuid ,
						MetadataSupport.XDSDocumentEntry_confCode_uuid ,
						MetadataSupport.XDSDocumentEntry_eventCode_uuid ,
						MetadataSupport.XDSDocumentEntry_formatCode_uuid ,
						MetadataSupport.XDSDocumentEntry_hcftCode_uuid ,
						MetadataSupport.XDSDocumentEntry_psCode_uuid ,
						MetadataSupport.XDSDocumentEntry_typeCode_uuid,
						MetadataSupport.XDSDocumentEntry_author_uuid
				);
		directClassificationDescription.requiredSchemes =
				Arrays.asList(
				);
		directClassificationDescription.multipleSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_author_uuid,
						MetadataSupport.XDSDocumentEntry_confCode_uuid,
						MetadataSupport.XDSDocumentEntry_eventCode_uuid
				);
		directClassificationDescription.names = new HashMap<String, String>();
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_classCode_uuid, "Class Code");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_confCode_uuid, "Confidentiality Code");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_eventCode_uuid, "Event Codelist");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_formatCode_uuid, "Format Code");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_hcftCode_uuid, "Healthcare Facility Type Code");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_psCode_uuid, "Practice Setting Code");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_typeCode_uuid, "Type Code");
		directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_author_uuid, "Author");
	}

	static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
	static {
		externalIdentifierDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_patientid_uuid,
						MetadataSupport.XDSDocumentEntry_uniqueid_uuid
				);

		externalIdentifierDescription.requiredSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_patientid_uuid,
						MetadataSupport.XDSDocumentEntry_uniqueid_uuid
				);
		externalIdentifierDescription.multipleSchemes = new ArrayList<String>();

		externalIdentifierDescription.names = new HashMap<String, String>();
		externalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_patientid_uuid, "Patient ID");
		externalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_uniqueid_uuid, "Unique ID");
	}

	static public ClassAndIdDescription XDMexternalIdentifierDescription = new ClassAndIdDescription();
	static {
		XDMexternalIdentifierDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_patientid_uuid,
						MetadataSupport.XDSDocumentEntry_uniqueid_uuid
				);

		XDMexternalIdentifierDescription.requiredSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_uniqueid_uuid
				);
		XDMexternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

		XDMexternalIdentifierDescription.names = new HashMap<String, String>();
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_patientid_uuid, "Patient ID");
		XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_uniqueid_uuid, "Unique ID");
	}

	static public ClassAndIdDescription directExternalIdentifierDescription = new ClassAndIdDescription();
	static {
		directExternalIdentifierDescription.definedSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_patientid_uuid,
						MetadataSupport.XDSDocumentEntry_uniqueid_uuid
				);

		directExternalIdentifierDescription.requiredSchemes =
				Arrays.asList(
						MetadataSupport.XDSDocumentEntry_uniqueid_uuid
				);
		directExternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

		directExternalIdentifierDescription.names = new HashMap<String, String>();
		directExternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_patientid_uuid, "Patient ID");
		directExternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_uniqueid_uuid, "Unique ID");
	}

	static List<String> statusValues =
			Arrays.asList(
					MetadataSupport.status_type_namespace + "Approved",
					MetadataSupport.status_type_namespace + "Deprecated"
			);

	String mimeType = "";
	String objectType = "";

	public DocumentEntry(String id, String mimeType) {
		super(id);
		this.mimeType = mimeType;
		this.objectType = MetadataSupport.XDSDocumentEntry_objectType_uuid;
	}

	public DocumentEntry(Metadata m, OMElement de) throws XdsInternalException  {
		super(m, de);
		mimeType = de.getAttributeValue(MetadataSupport.mime_type_qname);
		objectType = de.getAttributeValue(MetadataSupport.object_type_qname);
	}

	public boolean equals(DocumentEntry d) {
		if (!d.mimeType.equals(mimeType))
			return false;
		if (!id.equals(d.id))
			return false;
		return super.equals(d);
	}

	public OMElement toXml() throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.extrinsicobject_qnamens);
		ro.addAttribute("id", id, null);
		ro.addAttribute("mimeType", mimeType, null);
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

	public String identifyingString() {
		return "DocumentEntry(" + getId() + ")";
	}

	public boolean isMetadataLimited() {
		return isClassifiedAs(MetadataSupport.XDSDocumentEntry_limitedMetadata_uuid);
	}

	public void validate(ErrorRecorder er, ValidationContext vc, Set<String> knownIds) {

		if (vc.skipInternalStructure)
			return;

		if (vc.isXDR || vc.isPartOfRecipient)
			vc.isXDRLimited = isMetadataLimited();

		if (vc.isXDRLimited)
			er.sectionHeading("is labeled as Limited Metadata");

		// A registry response can contain both stable and on-demand object types. Use the object type to prepare the validation context at runtime.
		if (vc.isResponse && vc.isStableOrODDE) {
			vc.isRODDE = MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid.equals(objectType);
		}

		validateTopAtts(er, vc);

		validateSlots(er, vc);

		if (vc.isXDRMinimal)
			validateClassifications(er, vc, directClassificationDescription, table415);
		else
			validateClassifications(er, vc, classificationDescription, table415);

		if (vc.isXDRMinimal)
			validateExternalIdentifiers(er, vc, directExternalIdentifierDescription, table415);
		else if (vc.isXDM || vc.isXDRLimited)
			validateExternalIdentifiers(er, vc, XDMexternalIdentifierDescription, table415);
		else
			validateExternalIdentifiers(er, vc, externalIdentifierDescription, table415);

		verifyIdsUnique(er, knownIds);

		// Restore the dynamic validation flag
		if (vc.isResponse && vc.isStableOrODDE) {
			vc.isRODDE = false;
		}
	}

	static public String table415 = "ITI TF-3: Table 4.2.3.2-1";

	// this takes in two circumstances:
	//	Slots always required
	//  Optional Slots required by this transaction
	public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc) {
		// Slots always required

		if (vc.isXDRMinimal) {
			for (String slotName : directRequiredSlots) {
				if (getSlot(slotName) == null) {
					String assertionID = "TA004";
					Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);
					String location = identifyingString();
					String detail = "Slot " + slotName + " missing";
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
				}
			}
		} else if (vc.isStableOrODDE) {

		} else if (vc.isRODDE) {
			for (String slotName : roddeRequiredSlots) {
				if (getSlot(slotName) == null) {
					String assertionID = "TA005";
					Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);
					String location = identifyingString();
					String detail = "Slot " + slotName + " missing";
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
				}
			}
		} else if (!(vc.isXDM || vc.isXDRLimited || vc.isXDRMinimal)) {
			for (String slotName : requiredSlots) {
				if (getSlot(slotName) == null) {
					String assertionID = "TA006";
					Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);
					String location = identifyingString();
					String detail = "Slot " + slotName + " missing";
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
				}
			}
		}

		//  Optional Slots required by this transaction
		if (vc.hashRequired() && getSlot("hash") == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA007");
			String location = identifyingString();
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, "");
		}

		if (vc.sizeRequired() && getSlot("size") == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA008");
			String location = identifyingString();
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, "");
		}

		if (vc.repositoryUniqueIdRequired() && getSlot("repositoryUniqueId") == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA009");
			String location = identifyingString();
			String detail = vc.toString();
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
		}

		if (vc.uriRequired() && getSlot("URI") == null){
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA010");
			String location = identifyingString();
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, "");
		}
	}

	/**
	 * Validate all slots present are legal for DocumentEntry
	 * @param er
	 */
	public void validateSlotsLegal(ErrorRecorder er)  {
		verifySlotsUnique(er);
		for (Slot slot : getSlots()) {
			if ( ! legal_slot_name(slot.getName())) {
				String assertionID = "TA011";
				Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);
				String location = identifyingString();
				String detail = "Found: '" + slot.getName() + "'";
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
			}
		}
	}

	boolean legal_slot_name(String name) {
		if (name == null) return false;
		if (name.startsWith("urn:")) return true;
		return definedSlots.contains(name);
	}

	public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {

		//                    name				   multi	format                                                  resource
		validateSlot(er, 	"creationTime", 	   false, 	new DtmFormat(er, "Slot creationTime",      table415),  table415);
		validateSlot(er, 	"languageCode",		   false, 	new Rfc3066Format(er, "Slot languageCode",      table415),  table415);
		validateSlot(er, 	"legalAuthenticator",  false, 	new XcnFormat(er, "Slot legalAuthenticator",table415),  table415);
		validateSlot(er, 	"serviceStartTime",	   false, 	new DtmFormat(er, "Slot serviceStartTime",  table415),  table415);
		validateSlot(er, 	"serviceStopTime",	   false, 	new DtmFormat(er, "Slot serviceStopTime",   table415),  table415);
		validateSlot(er, 	"sourcePatientInfo",   true, 	new SourcePatientInfoFormat(er, "Slot sourcePatientInfo", table415),  table415);
		validateSlot(er, 	"sourcePatientId",     false, 	new CxFormat(er, "Slot sourcePatientId",   table415),  table415);
		validateSlot(er, 	"hash",			 	   false, 	new HashFormat(er, "Slot hash",   null), 		        table415);
		validateSlot(er, 	"size",				   false, 	new IntFormat(er, "Slot size",   table415),             table415);
		validateSlot(er, 	"URI",				   true, 	new AnyFormat(er, "Slot URI",   table415),   table415);
		validateSlot(er, 	"repositoryUniqueId",	false, 	new OidFormat(er, "Slot repositoryUniqueId",   table415),   table415);


		if ( getSlot("URI") != null ) {
			try {
				m.getURIAttribute(ro, !vc.isXDM);
			} catch (MetadataException e) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Slot URI: " + e.getMessage(), this, table415);
			}
		}

		Slot docAvail = getSlot("documentAvailability");
		if (docAvail != null) {
			if (docAvail.getValues().size() > 1)
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Slot documentAvailability shall have a single value", this, table415);
			String val;
			try {
				val = docAvail.getValue(0);
				if (MetadataSupport.documentAvailability_offline.equals(val)   ||
						MetadataSupport.documentAvailability_online.equals(val)) {

				} else {
					er.err(Code.XDSRegistryMetadataError, "Slot documentAvailability must have one of two values: " + MetadataSupport.documentAvailability_offline + " or " +
							MetadataSupport.documentAvailability_online + ". Found instead " + val, this, table415
					);
				}
			} catch (Exception e) {
				er.err(Code.XDSRegistryMetadataError, e);
			}
		}
	}

	public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
		if(vc.isRODDE) {
			if (!MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid.equals(objectType))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": On-Demand objectType must be " + MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid + " (found " + objectType + ")", this, table415);

		} else if (!MetadataSupport.XDSDocumentEntry_objectType_uuid.equals(objectType))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": objectType must be " + MetadataSupport.XDSDocumentEntry_objectType_uuid + " (found " + objectType + ")", this, table415);

		if (mimeType == null || mimeType.equals(""))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": mimeType attribute missing or empty", this, table415);

		validateTopAtts(er, vc, table415, statusValues);

	}

}
