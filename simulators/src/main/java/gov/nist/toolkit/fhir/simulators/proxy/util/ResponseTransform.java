package gov.nist.toolkit.fhir.simulators.proxy.util;

import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase;
import org.apache.http.HttpResponse;

/**
 *
 */
public interface ResponseTransform {
    HttpResponse run(SimProxyBase base, HttpResponse response);
}
