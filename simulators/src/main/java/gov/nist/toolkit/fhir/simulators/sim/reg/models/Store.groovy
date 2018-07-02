package gov.nist.toolkit.fhir.simulators.sim.reg.models

import gov.nist.toolkit.fhir.simulators.sim.reg.store.MetadataCollection
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegistryFactory
import gov.nist.toolkit.registrymetadata.Metadata
import groovy.transform.TypeChecked

@TypeChecked
class Store {

    MetadataCollection mc = new MetadataCollection()

    Store(Metadata metadata) {
        RegistryFactory.buildMetadataIndex(metadata, mc)
    }

}
