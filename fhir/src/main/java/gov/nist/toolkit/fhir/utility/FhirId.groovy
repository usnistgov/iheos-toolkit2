package gov.nist.toolkit.fhir.utility

/**
 *
 */
class FhirId {
    String type = null  // Resource type
    String id = null   // id
    String vid = null  // version
    String base = null

    // format is Patient/ea0d8b08-8f1d-40ac-b9e2-bbcc781c586b/_history/1
    FhirId(String value) {
        def locationParts = value.split('/')
        int historyIndex = locationParts.findIndexOf { it == '_history'}
        if (historyIndex != -1) {
            id = locationParts[historyIndex - 1]
            type = locationParts[historyIndex - 2]
            vid = locationParts[historyIndex + 1]
            parseBase(locationParts, historyIndex - 2)
        } else {
            id = locationParts[locationParts.size()-1]
            type = locationParts[locationParts.size()-2]
            parseBase(locationParts, locationParts.size()-2)
        }
    }

    def parseBase(locationParts, typeI) {
        base = ''
        for (int i=0; i<typeI; i++)
            base = base + locationParts[i] + '/'
    }

    @Override
    public String toString() {
        "${type}/${id}/_history/${vid}"
    }

    public String withoutHistory() {
        "${type}/${id}"
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

