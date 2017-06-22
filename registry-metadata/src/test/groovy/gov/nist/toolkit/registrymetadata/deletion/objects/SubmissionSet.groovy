package gov.nist.toolkit.registrymetadata.deletion.objects

import groovy.transform.ToString

/**
 *
 */
@ToString(includeSuper=true, includeNames=true, includePackage = false)
class SubmissionSet extends RO {
    SubmissionSet(String id) { super(id)}
}
