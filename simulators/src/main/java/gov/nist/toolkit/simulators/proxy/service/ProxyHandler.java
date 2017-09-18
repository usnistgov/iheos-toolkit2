package gov.nist.toolkit.simulators.proxy.service;

import gov.nist.toolkit.simcommon.client.BadSimIdException;
import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simulators.proxy.util.ProxyLogger;
import gov.nist.toolkit.simulators.proxy.util.SimDoesNotExistException;
import gov.nist.toolkit.simulators.servlet.SimServlet;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.http.*;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.*;
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 *
 */
class ProxyHandler implements HttpRequestHandler {
    static Logger logger = Logger.getLogger(ProxyHandler.class);
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

        SimProxyBase proxyBase;
        ProxyLogger clientLogger;
        ProxyLogger targetLogger;
        try {
            String uri = request.getRequestLine().getUri();
            proxyBase = new SimProxyBase(uri);
            clientLogger = proxyBase.getClientLogger();
        }
        catch (BadSimIdException e) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setReasonPhrase(e.getMessage());
            return;
        }
        catch (final Exception e) {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            response.setReasonPhrase(e.getMessage());
            logger.error(ExceptionUtil.exception_details(e));
            return;
        }

        clientLogger.logRequest(request);

        final DefaultBHttpClientConnection conn = (DefaultBHttpClientConnection) context.getAttribute(
                ElementalReverseProxy.HTTP_OUT_CONN);

        if (!conn.isOpen() || conn.isStale()) {
            int port = this.target.getPort() >= 0 ? this.target.getPort() : 80;
            final Socket outsocket = new Socket(this.target.getHostName(), port);
            conn.bind(outsocket);
            System.out.println("Outgoing connection to " + outsocket.getInetAddress() + ":" + port );
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

        HttpRequest targetRequest = proxyBase.preProcessRequest(request);
        targetLogger = proxyBase.getTargetLogger();

        this.httpexecutor.preProcess(targetRequest, this.httpproc, context);
        targetLogger.logRequest(targetRequest);

        final HttpResponse targetResponse1 = this.httpexecutor.execute(targetRequest, conn, context);
        targetLogger.logResponse(targetResponse1);

        HttpResponse targetResponse = proxyBase.preProcessResponse(targetResponse1);

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
        clientLogger.logResponse(response);

        System.out.println("<< Response: " + response.getStatusLine());

        final boolean keepalive = this.connStrategy.keepAlive(response, context);
        context.setAttribute(ElementalReverseProxy.HTTP_CONN_KEEPALIVE, new Boolean(keepalive));
    }

}
