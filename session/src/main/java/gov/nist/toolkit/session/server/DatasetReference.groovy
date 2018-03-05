package gov.nist.toolkit.session.server

import gov.nist.toolkit.datasets.shared.DatasetElement
import gov.nist.toolkit.installation.server.Installation

/**
 *
 */
class DatasetReference {
    private final DatasetElement datasetElement;

    DatasetReference(DatasetElement datasetElement) {
        this.datasetElement = datasetElement
    }

    DatasetElement getDatasetElement() {
        return datasetElement
    }

    File getFile() {
        if (!datasetElement.valid) throw new Exception("DatasetElement is not valid (${datasetElement})")
        String[] parts = datasetElement.getParts()
        new File(new File(new File(Installation.instance().datasets(), parts[0]), parts[1]), parts[2])
    }
}
