package gov.nist.toolkit.registrymetadata.deletion

import groovy.transform.ToString

/**
 *
 */
@ToString(includeNames=true,includePackage = false)
class MultiResponse {
    List<Response> responses = []

    MultiResponse() {}

    MultiResponse(Response response) {
        responses.add(response)
    }

    MultiResponse add(ErrorType _errorType, Uuid _object) {
        responses.add(new Response(_errorType, _object))
        return this
    }
}
