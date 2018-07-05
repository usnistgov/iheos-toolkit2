package gov.nist.toolkit.fhir.simulators.sim.reg

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.metadataModel.MetadataCollection
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.RegistryFactory
import spock.lang.Specification
/**
 * Created by bill on 8/24/15.
 */
class CreateIndexTest extends Specification {
    File resourcesDir

    def setup() {
        resourcesDir = new File(getClass().getResource('/root.txt').file).parentFile
    }

    def 'Build index of submission1'() {
        when:
        File sub1Dir = new File(resourcesDir, 'registry-data/submission1/Registry')

        then:
        sub1Dir.isDirectory()

        when: 'Build MetadataCollection'
        Metadata metadata = new Metadata()
        MetadataCollection metadataCollection = new MetadataCollection()
        sub1Dir.listFiles().each { File f ->
            metadata.addMetadata(MetadataParser.parseNonSubmission(f))
        }
        RegistryFactory.buildMetadataIndex(metadata, metadataCollection)

        then:
        metadataCollection.docEntryCollection.allRo.size() == 1
        metadataCollection.subSetCollection.allRo.size() == 1
        metadataCollection.assocCollection.allRo.size() == 1
        metadataCollection.folCollection.allRo.size() == 0

        when: 'Build RegIndex'
        RegIndex ri = new RegIndex(metadataCollection)

        then: 'No testable condition'
        true
    }


    def 'Build another index of submission1'() {
        when:
        File sub1Dir = new File(resourcesDir, 'registry-data/submission1/Registry')
        RegIndex ri = RegIndexFactory.buildRegIndex(sub1Dir)

        then:
        ri.metadataCollection.docEntryCollection.allRo.size() == 1
        ri.metadataCollection.subSetCollection.allRo.size() == 1
        ri.metadataCollection.assocCollection.allRo.size() == 1
        ri.metadataCollection.folCollection.allRo.size() == 0
    }

    def 'Build index of submission1 and submission2'() {
        when:
        File sub1Dir = new File(resourcesDir, 'registry-data/submission1/Registry')
        File sub2Dir = new File(resourcesDir, 'registry-data/submission2/Registry')
        RegIndex ri = RegIndexFactory.buildRegIndex([sub1Dir, sub2Dir])

        then:
        ri.metadataCollection.docEntryCollection.allRo.size() == 2
        ri.metadataCollection.subSetCollection.allRo.size() == 2
        ri.metadataCollection.assocCollection.allRo.size() == 2
        ri.metadataCollection.folCollection.allRo.size() == 0
    }
}
