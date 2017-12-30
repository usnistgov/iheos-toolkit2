package gov.nist.toolkit.fhir.support

/**
 * Collection of things about a resource to add to the index.
 * Always needs the path, which is the location in ResDb where the Resource
 * is stored.
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

    @Override
    String toString() {
        "${items} ==> ${path}"
    }
}
