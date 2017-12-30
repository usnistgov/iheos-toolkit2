package gov.nist.toolkit.fhir.support

/**
 * Index items for a single transaction
 * Can contain multiple resources
 * (one ResourceIndex per resource)
 */
class ResourceIndexSet {
    List<ResourceIndex> items = []

    ResourceIndex add(ResourceIndex idx) {
        items.add(idx)
        idx
    }

    @Override
    String toString() {
        items.collect { it.toString() }.join('\n')
    }
}
