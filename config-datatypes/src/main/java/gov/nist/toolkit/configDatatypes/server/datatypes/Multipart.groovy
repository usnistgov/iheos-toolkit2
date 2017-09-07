package gov.nist.toolkit.configDatatypes.server.datatypes

/**
 *
 */
class Multipart {
    List<Part> parts = []
    String startPartId

    Part getStartPart() {
        if (startPartId && parts.size() > 0) {
            return parts.find { Part part -> part.id == startPartId}
        } else if (parts.size() > 0) {
            return parts[0]
        }
        return null
    }
}
