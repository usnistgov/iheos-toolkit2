package gov.nist.toolkit.itTests.support

/**
 *
 */
class FhirId {
    String type = null  // Resource type
    String id = null   // id
    String vid = null  // version


    // format is Patient/ea0d8b08-8f1d-40ac-b9e2-bbcc781c586b/_history/1
    FhirId(String value) {
        def locationParts = value.split('/')
        int historyIndex = locationParts.findIndexOf { it == '_history'}
        if (historyIndex != -1) {
            id = locationParts[historyIndex - 1]
            type = locationParts[historyIndex - 2]
            vid = locationParts[historyIndex + 1]
        } else {
            id = locationParts[locationParts.size()-1]
        }

    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        FhirId fhirId = (FhirId) o

        if (id != fhirId.id) return false
        if (type != fhirId.type) return false
        if (vid != fhirId.vid) return false

        return true
    }

    int hashCode() {
        int result
        result = (type != null ? type.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (vid != null ? vid.hashCode() : 0)
        return result
    }
}

