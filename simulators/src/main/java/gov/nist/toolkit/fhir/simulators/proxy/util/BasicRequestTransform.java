package gov.nist.toolkit.fhir.simulators.proxy.util;

import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;

/**
 *
 */
public interface BasicRequestTransform {
    HttpRequest run(SimProxyBase base, BasicHttpRequest request);
}
