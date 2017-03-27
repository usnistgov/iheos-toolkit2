package gov.nist.toolkit.fhir.support

/**
 *
 */
class ResourceIndexItem {
    String field
    String value
    String path

    ResourceIndexItem(String _field, String _value, String _path) {
        field = _field
        value = _value
        path = _path
    }

}
