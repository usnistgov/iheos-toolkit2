package gov.nist.toolkit.fhir.simulators.proxy.util;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;

/**
 *
 */
public interface ContentResponseTransform extends SimpleResponseTransform {
    HttpResponse run(SimProxyBase base, BasicHttpResponse response);
}
