package gov.nist.toolkit.fhir.simulators.sim.reg
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.fhir.simulators.sim.reg.store.MetadataCollection
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegistryFactory
import org.apache.axiom.om.OMElement
/**
 * Supports tests
 */
class RegIndexFactory {

    static public RegIndex buildRegIndex(File submissionDir) {
        MetadataCollection metadataCollection = new MetadataCollection()
        submissionDir.listFiles().each { File f ->
            Metadata m = MetadataParser.parseNonSubmission(f)
            OMElement ele = m.getAllLeafClasses().get(0)
            RegistryFactory.buildMetadataIndex(ele, f.toString(), metadataCollection)
        }
        return new RegIndex(metadataCollection)
    }

    static public RegIndex buildRegIndex(List<File> submissionDirs) {
        MetadataCollection metadataCollection = new MetadataCollection()
        submissionDirs.each { File submissionDir ->
            submissionDir.listFiles().each { File f ->
                Metadata m = MetadataParser.parseNonSubmission(f)
                OMElement ele = m.getAllLeafClasses().get(0)
                RegistryFactory.buildMetadataIndex(ele, f.toString(), metadataCollection)
            }
        }
        return new RegIndex(metadataCollection)
    }
}
