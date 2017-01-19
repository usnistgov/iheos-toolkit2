package gov.nist.toolkit.valregmetadata.field

import spock.lang.Specification

/**
 *
 */
class CXTest extends Specification {

    def 'null'() {
        expect:
        'No Patient ID found' == ValidatorCommon.validate_CX_datatype_list(null)
    }

    def 'empty'() {
        expect:
        'Not Patient ID format: ^^^ not found:' == ValidatorCommon.validate_CX_datatype_list('')
    }

    def 'single'() {
        expect:
        null == ValidatorCommon.validate_CX_datatype_list('DTP 1^^^&1.3.6&ISO')

    }

    def 'double'() {
        expect:
        null == ValidatorCommon.validate_CX_datatype_list('DTP 1^^^&1.3.6&ISO~XTP1^^^&1.3.11&ISO')

    }
}
