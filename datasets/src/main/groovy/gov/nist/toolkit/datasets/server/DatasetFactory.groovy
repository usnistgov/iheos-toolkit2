package gov.nist.toolkit.datasets.server

import gov.nist.toolkit.datasets.shared.DatasetElement
import gov.nist.toolkit.datasets.shared.DatasetModel
import gov.nist.toolkit.installation.Installation

/**
 *
 */
class DatasetFactory {


    static List<String> getDatasetNames() {
        getDatasetNames(Installation.instance().datasets())
    }


    static List<String> getDatasetNames(File datasetsDir) {
        List<String> names = []
        if (!datasetsDir.exists() || !datasetsDir.isDirectory() || !datasetsDir.canRead()) return names

        datasetsDir.listFiles().each { File f ->
            if (f.isDirectory())
                names << f.name
        }
        names
    }

    static DatasetModel getDataset(File root, String name) {
        DatasetModel model = new DatasetModel(name)
        File datasetDir = new File(root, name)
        if (!datasetDir.exists() || !datasetDir.isDirectory()) return model

        list(model, datasetDir.absolutePath, datasetDir)
        model
    }

    static DatasetModel getDataset(String name) {
        File root = new File(Installation.instance().datasets(), name)
        getDataset(root, name)
    }

    static void list(DatasetModel model, String root, File dir) {
        dir.listFiles().each { File f ->
            if (f.isDirectory()) list(model, root, f)
            else if (f.path.endsWith('.xml') || f.path.endsWith('.json')) {
                model.add(new DatasetElement(f.absolutePath - root - '/'))
            }
        }
    }

    static Map<String, DatasetModel> getAllDatasets() {
        Map<String, DatasetModel> all = new HashMap<>();
        def names = datasetNames
        names.each { all[it] = getDataset(it) }
        all
    }
}
