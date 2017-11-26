package gov.nist.toolkit.fhir.utility

/**
 * this is necessary because URI(String) will not do encoding.  You need
 * the multi-parameter version to get that support
 */
class UriBuilder {

    static URI build(String ref) {
        try {
            if (ref.startsWith('#'))
                return new URI(null, '', ref)
            def parts = ref.split(':', 2)
            if (parts.size() == 1)
                return new URI(null, parts[0], null)
            def partx = parts[1].split('#')
            def scheme = parts[0]
            def ssp = partx[0]
            def party = ssp.split('#', 2)
            ssp = party[0]
            def fragment = null
            if (party.size() == 2)
                fragment = party[1]
            new URI(parts[0], parts[1], fragment)
        } catch (ArrayIndexOutOfBoundsException e) {
            throw e
        }
    }

}
