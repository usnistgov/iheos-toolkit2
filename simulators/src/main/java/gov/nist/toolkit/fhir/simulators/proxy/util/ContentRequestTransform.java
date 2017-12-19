package gov.nist.toolkit.fhir.simulators.proxy.util;

import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase;
import gov.nist.toolkit.simcoresupport.proxy.util.SimpleRequestTransform;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

/**
 *
 */
public interface ContentRequestTransform extends SimpleRequestTransform {
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request);
}
