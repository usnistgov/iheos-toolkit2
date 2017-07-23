package gov.nist.toolkit.datasets.server

import gov.nist.toolkit.datasets.shared.DatasetElement
import gov.nist.toolkit.datasets.shared.DatasetModel
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

/**
 *
 */
class DatasetFactoryTest extends Specification {

    def buildData() {
        Path datasetsPath = Files.createTempDirectory('DatasetFactoryTest')
        File datasets = datasetsPath.toFile()
        datasets.deleteOnExit()
        File ds = new File(datasets, 'mydataset')
        ds.mkdir()
        File patients = new File(ds, 'Patients')
        patients.deleteOnExit()
        patients.mkdir()
        patients.deleteOnExit()
        new File(patients, "patient1.json").with {
            write('patient1')
            deleteOnExit()
        }
        new File(patients, "patient2.json").with {
            write('patient2')
            deleteOnExit()
        }
        new File(patients, "readme.txt").with {
            write('Hello')
            deleteOnExit()
        }
        new File(patients, 'patient3.xml').with {
            write('patient3')
            deleteOnExit()
        }
        return datasets
    }

    def 'list'() {
        setup:
        File datasets = buildData()
        def expected = [
                'Patients/patient2.json',
                'Patients/patient3.xml',
                'Patients/patient1.json'
        ] as Set

        when:
        def list = DatasetFactory.getDatasetNames(datasets)

        then:
        list.size() == 1
        list[0] == 'mydataset'

        when:
        DatasetModel model = DatasetFactory.getDataset(datasets, list[0])
        def results = model.items.collect { DatasetElement ele ->
            ele.type + '/' + ele.file
        } as Set

        then:
        expected == results
    }
}
