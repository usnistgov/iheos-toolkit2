package gov.nist.toolkit.utilities.html

import org.apache.http.HeaderElement
import org.apache.http.RequestLine
import org.apache.http.StatusLine

/**
 *
 */
class HeaderBlock {
    RequestLine requestLine
    StatusLine statusLine
    List<Header> headers = []

    HeaderElement get(String name) {
        return headers.find { it.name == name}.headerElements[0]
    }

    List<HeaderElement> getAll(String name) {
        return headers.find { it.name == name}.headerElements as List
    }

    def remove(String name) {
        headers.removeAll(headers.findAll { it.name == name})
    }

    static class Header {
        String name
        HeaderElement[] headerElements

        Header(String name, HeaderElement[] headerElements) {
            this.name = name
            this.headerElements = headerElements
        }
    }
}
