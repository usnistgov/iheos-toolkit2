package gov.nist.toolkit.simcoresupport.proxy.util

import org.apache.http.HttpResponse

class ReturnableErrorException extends Exception {
    HttpResponse response

    ReturnableErrorException(HttpResponse response) {
        super();
        this.response = response
    }
}
