package gov.nist.toolkit.fhir.simulators.sim.reg.models

import gov.nist.toolkit.metadataModel.MetadataCollection
import gov.nist.toolkit.metadataModel.RegistryFactory
import gov.nist.toolkit.registrymetadata.Metadata
import groovy.transform.TypeChecked

@TypeChecked
class Store {

    MetadataCollection mc = new MetadataCollection()

    Store(Metadata metadata) {
        RegistryFactory.buildMetadataIndex(metadata, mc)
    }

    Store() {

    }

}
