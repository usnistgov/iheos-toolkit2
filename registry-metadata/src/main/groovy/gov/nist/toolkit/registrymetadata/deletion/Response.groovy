package gov.nist.toolkit.registrymetadata.deletion

import groovy.transform.ToString

/**
 *
 */
@ToString(includePackage = false)
class Response {
    ErrorType errorType
    Uuid object
    String rule

    Response(ErrorType _errorType, Uuid _object) {
        errorType = _errorType
        object = _object
    }

    Response(ErrorType _errorType, String id, String _rule) {
        errorType = _errorType
        object = new Uuid(id)
        rule = _rule
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Response response = (Response) o

        if (errorType != response.errorType) return false
        if (object != response.object) return false
        if (rule != response.rule) return false

        return true
    }

    int hashCode() {
        int result
        result = (errorType != null ? errorType.hashCode() : 0)
        result = 31 * result + (object != null ? object.hashCode() : 0)
        result = 31 * result + (rule != null ? rule.hashCode() : 0)
        return result
    }
    static Response NoError = new Response(ErrorType.None, null)
}
