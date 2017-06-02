package gov.nist.toolkit.registrymetadata

/**
 *
 */
class MultiResponse {
    List<Response> responses = []

    MultiResponse() {}

    MultiResponse(Response response) {
        responses.add(response)
    }

    MultiResponse add(ErrorType _errorType, UUID _object) {
        responses.add(new Response(_errorType, _object))
        return this
    }
}
