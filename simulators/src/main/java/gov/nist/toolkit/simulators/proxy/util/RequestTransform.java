package gov.nist.toolkit.simulators.proxy.util;

import org.apache.http.HttpRequest;

/**
 *
 */
public interface RequestTransform {
    HttpRequest run(SimProxyBase base, HttpRequest request);
}
