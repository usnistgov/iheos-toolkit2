package gov.nist.toolkit.valregmetadata.model;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valregmetadata.validators.RegistryObjectValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.util.*;

public class Folder extends AbstractRegistryObject implements TopLevelObject {

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

	public boolean equals(Folder f)  {
		if (!id.equals(id)) 
			return false;
		return	super.equals(f);
	}


}
