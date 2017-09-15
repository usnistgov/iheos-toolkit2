package gov.nist.toolkit.proxy;

import org.apache.http.*;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by bill on 9/13/17.
 */
class ProxyHandler implements HttpRequestHandler {

    private final HttpHost target;
    private final HttpProcessor httpproc;
    private final HttpRequestExecutor httpexecutor;
    private final ConnectionReuseStrategy connStrategy;

    public ProxyHandler(
            final HttpHost target,
            final HttpProcessor httpproc,
            final HttpRequestExecutor httpexecutor) {
        super();
        this.target = target;
        this.httpproc = httpproc;
        this.httpexecutor = httpexecutor;
        this.connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
    }

    public void handle(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        final DefaultBHttpClientConnection conn = (DefaultBHttpClientConnection) context.getAttribute(
                ElementalReverseProxy.HTTP_OUT_CONN);

        if (!conn.isOpen() || conn.isStale()) {
            final Socket outsocket = new Socket(this.target.getHostName(), this.target.getPort() >= 0 ? this.target.getPort() : 80);
            conn.bind(outsocket);
            System.out.println("Outgoing connection to " + outsocket.getInetAddress());
        }

        context.setAttribute(HttpCoreContext.HTTP_CONNECTION, conn);
        context.setAttribute(HttpCoreContext.HTTP_TARGET_HOST, this.target);

        System.out.println(">> Request URI: " + request.getRequestLine().getUri());

        // Remove hop-by-hop headers
        request.removeHeaders(HTTP.TARGET_HOST);
        request.removeHeaders(HTTP.CONTENT_LEN);
        request.removeHeaders(HTTP.TRANSFER_ENCODING);
        request.removeHeaders(HTTP.CONN_DIRECTIVE);
        request.removeHeaders("Keep-Alive");
        request.removeHeaders("Proxy-Authenticate");
        request.removeHeaders("TE");
        request.removeHeaders("Trailers");
        request.removeHeaders("Upgrade");

        this.httpexecutor.preProcess(request, this.httpproc, context);
        final HttpResponse targetResponse = this.httpexecutor.execute(request, conn, context);
        this.httpexecutor.postProcess(response, this.httpproc, context);

        // Remove hop-by-hop headers
        targetResponse.removeHeaders(HTTP.CONTENT_LEN);
        targetResponse.removeHeaders(HTTP.TRANSFER_ENCODING);
        targetResponse.removeHeaders(HTTP.CONN_DIRECTIVE);
        targetResponse.removeHeaders("Keep-Alive");
        targetResponse.removeHeaders("TE");
        targetResponse.removeHeaders("Trailers");
        targetResponse.removeHeaders("Upgrade");

        response.setStatusLine(targetResponse.getStatusLine());
        response.setHeaders(targetResponse.getAllHeaders());
        response.setEntity(targetResponse.getEntity());

        System.out.println("<< Response: " + response.getStatusLine());

        final boolean keepalive = this.connStrategy.keepAlive(response, context);
        context.setAttribute(ElementalReverseProxy.HTTP_CONN_KEEPALIVE, new Boolean(keepalive));
    }

}
