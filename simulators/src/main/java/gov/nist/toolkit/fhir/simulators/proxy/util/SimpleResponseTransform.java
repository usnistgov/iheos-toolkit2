package gov.nist.toolkit.fhir.simulators.proxy.util;

import org.apache.http.HttpResponse;

/**
 *
 */
public interface SimpleResponseTransform {
    HttpResponse run(SimProxyBase base, HttpResponse response);
}
