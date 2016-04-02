package gov.nist.toolkit.valregmetadata.model;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.datatype.AnyFormat;
import gov.nist.toolkit.valregmetadata.datatype.CxFormat;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valregmetadata.datatype.HashFormat;
import gov.nist.toolkit.valregmetadata.datatype.IntFormat;
import gov.nist.toolkit.valregmetadata.datatype.OidFormat;
import gov.nist.toolkit.valregmetadata.datatype.Rfc3066Format;
import gov.nist.toolkit.valregmetadata.datatype.SourcePatientInfoFormat;
import gov.nist.toolkit.valregmetadata.datatype.XcnFormat;
import gov.nist.toolkit.valregmetadata.validators.RegistryObjectValidator;
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
	static public String table415 = "ITI TF-3: Table 4.2.3.2-1"; // Rev 12.1 Final Text

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

}
