package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;

import java.util.List;

public class DocumentEntryFieldFilterSelector {
    DocumentEntryIndexField field;
    DocumentEntryFieldComponent component;
    List<DocumentEntry> result;

    public DocumentEntryFieldFilterSelector(DocumentEntryIndexField field, DocumentEntryFieldComponent component) {
        this.field = field;
        this.component = component;
    }

    public DocumentEntryIndexField getField() {
        return field;
    }

    public DocumentEntryFieldComponent getComponent() {
        return component;
    }

    List<DocumentEntry> getResult() {
        return result;
    }

    void setResult(List<DocumentEntry> result) {
        this.result = result;
    }


}
