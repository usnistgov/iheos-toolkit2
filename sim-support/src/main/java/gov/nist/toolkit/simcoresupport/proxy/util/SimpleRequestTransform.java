package gov.nist.toolkit.simcoresupport.proxy.util;

import org.apache.http.HttpRequest;

/**
 *
 */
public interface SimpleRequestTransform {
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException;
}
