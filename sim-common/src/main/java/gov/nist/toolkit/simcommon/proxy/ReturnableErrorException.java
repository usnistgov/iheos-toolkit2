package gov.nist.toolkit.simcommon.proxy;

import org.apache.http.HttpResponse;

public class ReturnableErrorException extends Exception {
    HttpResponse response;

    public ReturnableErrorException(HttpResponse response) {
        super();
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
