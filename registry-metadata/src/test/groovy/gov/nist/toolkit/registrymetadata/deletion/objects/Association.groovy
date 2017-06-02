package gov.nist.toolkit.registrymetadata.deletion.objects

import gov.nist.toolkit.registrymetadata.deletion.AssnType
import gov.nist.toolkit.registrymetadata.deletion.Uuid
import groovy.transform.ToString

/**
 *
 */
@ToString(includeSuper=true, includeNames=true,includePackage = false)
class Association extends RO {
    Uuid source
    Uuid target
    AssnType type

    Association(String id, String source, String target, AssnType type) {
        super(id)
        this.source = new Uuid(source)
        this.target = new Uuid(target)
        this.type = type
    }
}
