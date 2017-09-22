package gov.nist.toolkit.simulators.proxy.util;

import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

/**
 *
 */
public interface ContentRequestTransform extends SimpleRequestTransform {
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request);
}
