package gov.nist.toolkit.fhir.simulators.proxy.util;

import gov.nist.toolkit.simcommon.proxy.SimProxyBase;
import gov.nist.toolkit.simcommon.proxy.SimpleResponseTransform;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;

/**
 *
 */
public interface ContentResponseTransform extends SimpleResponseTransform {
    HttpResponse run(SimProxyBase base, BasicHttpResponse response);
}
