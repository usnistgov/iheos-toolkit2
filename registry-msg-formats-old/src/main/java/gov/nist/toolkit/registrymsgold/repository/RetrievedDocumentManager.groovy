package gov.nist.toolkit.registrymsgold.repository

import gov.nist.toolkit.registrymetadata.client.DocumentEntry
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class RetrievedDocumentManager {
    static RetrievedDocumentModel getRetrievedDocumentModel(DocumentEntry docEntry) {
        RetrievedDocumentModel model = new RetrievedDocumentModel()
        model.setDocUid(docEntry.uniqueId)
        model.setRepUid(docEntry.repositoryUniqueId)
        model.setHome(docEntry.home)
        model
    }

    static RetrievedDocumentsModel getRetrievedDocumentsModel(MetadataCollection mc) {
        RetrievedDocumentsModel models = new RetrievedDocumentsModel()
        for (DocumentEntry de : mc.docEntries) {
            def model = getRetrievedDocumentModel(de)
            models.add(model)
        }
        models
    }
}
