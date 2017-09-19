package gov.nist.toolkit.simulators.proxy.util;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;

/**
 *
 */
public interface BasicRequestTransform {
    HttpRequest run(SimProxyBase base, BasicHttpRequest request);
}
