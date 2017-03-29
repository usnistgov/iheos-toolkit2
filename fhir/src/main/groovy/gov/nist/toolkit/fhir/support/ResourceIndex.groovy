package gov.nist.toolkit.fhir.support

/**
 *
 */
class ResourceIndex {
    List<ResourceIndexItem> items = []
    String path

    ResourceIndex() {}

    ResourceIndex(ResourceIndexItem item, String path) {
        items.add(item)
        this.path = path
    }

    ResourceIndex add(ResourceIndexItem item) {
        items.add(item)
        return this
    }
}
