package gov.nist.toolkit.registrymetadata.deletion

import groovy.transform.ToString

/**
 *
 */
@ToString(includePackage = false)
class Uuid {
    String id

    Uuid(String id) { this.id = id }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Uuid uuid = (Uuid) o

        if (id != uuid.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }
}
