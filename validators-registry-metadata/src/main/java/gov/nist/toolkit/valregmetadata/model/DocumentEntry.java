package gov.nist.toolkit.valregmetadata.model;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.util.Arrays;
import java.util.List;

public class DocumentEntry extends AbstractRegistryObject implements TopLevelObject {
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

	public static List<String> requiredSlots =
		Arrays.asList(
				"creationTime",
				"languageCode",
				"sourcePatientId"
		);

	public static List<String> roddeRequiredSlots =
			Arrays.asList(
					"languageCode",
					"sourcePatientId"
			);

	public static List<String> directRequiredSlots =
			Arrays.asList(
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

	public String getObjectType() {return objectType;}
	public String getMimeType() { return mimeType; }

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

	public boolean isODDE() { return "urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248".equals(getObjectType()); }

}
