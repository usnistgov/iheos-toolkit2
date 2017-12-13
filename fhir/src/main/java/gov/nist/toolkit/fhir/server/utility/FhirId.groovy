package gov.nist.toolkit.fhir.server.utility

import org.hl7.fhir.dstu3.model.Resource

/**
 *
 */
class FhirId {
    String type = null  // Resource type
    String id = null   // id - aka Logical ID
    String vid = null  // version
    String base = null

    /**
     * This is Ugly!
     *
     * The standard says that the id is just the id, the thing following the Resourcetype
     * But, HAPI includes the resource type as well as in DocumentEntry/1  instead of just 1
     * So, for now, this will acomodate and we will use the FhirId always as type/id (plus history...)
     */

    FhirId(Resource theResource) {
        type = theResource.class.simpleName
        id = theResource.id
        if (id.contains('/'))
            id = id.substring(id.indexOf('/') + 1)
    }

    // format is Patient/ea0d8b08-8f1d-40ac-b9e2-bbcc781c586b/_history/1
    FhirId(String value) {
        if (!value) return
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
        if (type && id)
            return "${type}/${id}/_history/${vid}"
        return null
    }

    public String withoutHistory() {
        if (type && id)
            return "${type}/${id}"
        return null
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

