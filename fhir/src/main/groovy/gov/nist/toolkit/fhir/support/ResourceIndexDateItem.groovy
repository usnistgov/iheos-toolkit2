package gov.nist.toolkit.fhir.support

import org.apache.lucene.document.DateTools
import org.apache.lucene.document.DateTools.Resolution

/**
 * Encapsulate the mapping of Dates to a consistent String representation that lucene
 * can reliably do ordered searches on.
 */
class ResourceIndexDateItem extends ResourceIndexItem {

    ResourceIndexDateItem(String _field, Date _value) {
        super( _field, DateTools.dateToString( _value, Resolution.MILLISECOND))
    }

}
