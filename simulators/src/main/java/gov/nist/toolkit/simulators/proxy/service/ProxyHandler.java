package gov.nist.toolkit.simulators.proxy.service;

import gov.nist.toolkit.simcommon.client.BadSimIdException;
import gov.nist.toolkit.simulators.proxy.util.ProxyLogger;
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.http.*;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 *
 */
class ProxyHandler implements HttpRequestHandler {
    static private final Logger logger = Logger.getLogger(ProxyHandler.class);
    private final HttpProcessor httpproc;
    private final HttpRequestExecutor httpexecutor;
    private final ConnectionReuseStrategy connStrategy;
    final int bufsize = 8 * 1024;

    public ProxyHandler(
            final HttpProcessor httpproc,
            final HttpRequestExecutor httpexecutor) {
        super();
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
            Object pb = context.getAttribute(ElementalReverseProxy.HTTP_PROXY_BASE);
            if (pb != null && pb instanceof SimProxyBase) {
                proxyBase = (SimProxyBase) pb;
            } else
                throw new Exception("SimProxyBase not available in ProxyHandler");

            if (proxyBase.getSimDb() == null)
                throw new Exception("ProxyHandler - SimProxyBase has not been initialized");
            clientLogger = proxyBase.getClientLogger();
        }
        catch (BadSimIdException e) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setReasonPhrase(e.getMessage());
            return;
        }
        catch (final Throwable e) {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            response.setReasonPhrase(e.getMessage());
            logger.error(ExceptionUtil.exception_details(e));
            return;
        }

        clientLogger.logRequest(request);

        proxyBase.init(request);

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

        HttpRequest targetRequest;
        try {
            // throws exception if target endpoint is not produced by one of
            // the transforms
            targetRequest = proxyBase.preProcessRequest(request);
        } catch (Throwable e) {
            returnInternalError(response, clientLogger, e);
            return;
        }

        Socket outsocket = null;
        try {
            HttpHost target = proxyBase.getTargetHost();

            // TODO when is this connection shut down?
            DefaultBHttpClientConnection conn = new ClientConnection(bufsize, proxyBase);

            if (!conn.isOpen() || conn.isStale()) {
                int port = target.getPort() >= 0 ? target.getPort() : 80;
                outsocket = new Socket(target.getHostName(), port);
                conn.bind(outsocket);
                System.out.println("Outgoing connection to " + outsocket.getInetAddress() + ":" + port );
            }

            context.setAttribute(HttpCoreContext.HTTP_CONNECTION, conn);
            context.setAttribute(HttpCoreContext.HTTP_TARGET_HOST, target);


            targetLogger = proxyBase.getTargetLogger();
            targetLogger.logRequest(targetRequest);

            this.httpexecutor.preProcess(targetRequest, this.httpproc, context);

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
        } catch (Throwable e) {
            returnInternalError(response, clientLogger, e);
            return;
        } finally {
            if (outsocket != null)
                outsocket.close();
        }
    }

    private void returnInternalError(HttpResponse response, ProxyLogger clientLogger, Throwable e) {
        response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        response.setReasonPhrase("TargetSystem - " + e.getMessage());
        logger.error(ExceptionUtil.exception_details(e));
        clientLogger.logResponse(response);
    }

}
