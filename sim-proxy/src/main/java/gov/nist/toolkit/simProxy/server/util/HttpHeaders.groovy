package gov.nist.toolkit.simProxy.server.util

import gov.nist.toolkit.http.HttpHeader

/**
 *
 */
class HttpHeaders {
    List<HttpHeader> headers = [];

    HttpHeaders() {}

    HttpHeaders(Map<String, List<String>> hdrs) {
        hdrs.each {String name, values ->
            headers.add(new HttpHeader("${name}: ${values.join(';')}"))
        }
    }

    HttpHeaders(String headerBlock) {
        headerBlock.eachLine { headers << new HttpHeader(it) }
    }

    def add(String header) {
        headers.add(new HttpHeader(header))
    }

    String toString() {
        headers.collect {it.toHeaderString()}.join()
    }

}
