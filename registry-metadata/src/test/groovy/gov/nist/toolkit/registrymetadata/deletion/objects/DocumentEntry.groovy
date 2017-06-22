package gov.nist.toolkit.registrymetadata.deletion.objects

import groovy.transform.ToString

/**
 *
 */
@ToString(includeSuper=true, includeNames=true,includePackage = false)
class DocumentEntry extends RO {
    DocumentEntry(String id) { super(id); }
}
