package gov.nist.toolkit.simulators.proxy.util;

import org.apache.http.HttpResponse;

/**
 *
 */
public interface ResponseTransform {
    HttpResponse run(SimProxyBase base, HttpResponse response);
}
