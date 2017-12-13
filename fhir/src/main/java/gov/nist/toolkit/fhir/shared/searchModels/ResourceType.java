package gov.nist.toolkit.fhir.shared.searchModels;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public enum ResourceType implements Serializable, IsSerializable {
    DocumentReference, DocumentManifest, Patient;
}
