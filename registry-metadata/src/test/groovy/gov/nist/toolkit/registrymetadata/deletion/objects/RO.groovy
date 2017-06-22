package gov.nist.toolkit.registrymetadata.deletion.objects

import gov.nist.toolkit.registrymetadata.deletion.Uuid
import groovy.transform.ToString

/**
 *
 */
@ToString(includeNames=true, includePackage = false)
class RO {
    Uuid id

    RO() {}

    RO(String id) { this.id = new Uuid(id)}
}
