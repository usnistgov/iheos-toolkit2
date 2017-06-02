package gov.nist.toolkit.registrymetadata

/**
 *
 */
class Response {
    ErrorType errorType
    UUID object

    Response(ErrorType _errorType, UUID _object) {
        errorType = _errorType
        object = _object
    }

    static Response NoError = new Response(ErrorType.None, null)
}
