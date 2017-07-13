package gov.nist.toolkit.fhir.support

/**
 * The Lucene index is made up of name/value pairs. This class defines a pair.
 */
class ResourceIndexItem {
    String field
    String value

    ResourceIndexItem(String _field, String _value) {
        field = _field
        value = _value
    }

}
