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
            if (f.isDirectory()) {
                names << f.name
            }
        }
        names
    }

    static DatasetModel getDataset(File root, String name) {
        DatasetModel model = new DatasetModel(name)
        File datasetDir = new File(root, name)
        if (!datasetDir.exists() || !datasetDir.isDirectory()) return model

        scanTypes(model, name, datasetDir.absolutePath, datasetDir)
        model
    }

    static DatasetModel getDataset(String name) {
        File root = Installation.instance().datasets()
        getDataset(root, name)
    }

    static void scanTypes(DatasetModel model, String name, String root, File dir) {
        assert dir.exists()
        assert dir.isDirectory()
        dir.listFiles().each { File f ->
            if (f.isDirectory()) scanResources(model, name, root, f)
        }
    }

    static void scanResources(DatasetModel model, String name, String root, File resourceTypeDir) {
        String type = resourceTypeDir.name
        resourceTypeDir.listFiles().each { File file ->
            if (file.path.endsWith('.xml') || file.path.endsWith('.json')) {
                model.add(new DatasetElement(name, type, file.name))
            }
        }
    }

    static List<DatasetModel> getAllDatasets() {
        List<DatasetModel> all = new ArrayList<>();
        def names = datasetNames
        names.each { all << getDataset(it) }
        all
    }
}
