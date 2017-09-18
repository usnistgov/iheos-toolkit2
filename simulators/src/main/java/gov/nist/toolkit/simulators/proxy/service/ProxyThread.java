package gov.nist.toolkit.simulators.proxy.service;

import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

/**
 *
 */
class ProxyThread extends Thread {

    private final HttpService httpservice;
    private final DefaultBHttpServerConnection inconn;
    private final DefaultBHttpClientConnection outconn;

    public ProxyThread(
            final HttpService httpservice,
            final DefaultBHttpServerConnection inconn,
            final DefaultBHttpClientConnection outconn) {
        super();
        this.httpservice = httpservice;
        this.inconn = inconn;
        this.outconn = outconn;
    }

    @Override
    public void run() {
        System.out.println("New connection thread");
        final HttpContext context = new BasicHttpContext(null);

        // Bind connection objects to the execution context
        context.setAttribute(ElementalReverseProxy.HTTP_IN_CONN, this.inconn);
        context.setAttribute(ElementalReverseProxy.HTTP_OUT_CONN, this.outconn);

        try {
            while (!Thread.interrupted()) {
                if (!this.inconn.isOpen()) {
                    this.outconn.close();
                    break;
                }

                this.httpservice.handleRequest(this.inconn, context);

                final Boolean keepalive = (Boolean) context.getAttribute(ElementalReverseProxy.HTTP_CONN_KEEPALIVE);
                if (!Boolean.TRUE.equals(keepalive)) {
                    this.outconn.close();
                    this.inconn.close();
                    break;
                }
            }
        } catch (final ConnectionClosedException ex) {
            System.err.println("Client closed connection");
        } catch (final IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
        } catch (final HttpException ex) {
            System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
        } catch (final Exception e) {
            System.err.println("Unknown Exception: " + ExceptionUtil.exception_details(e));
        } finally {
            try {
                this.inconn.shutdown();
            } catch (final IOException ignore) {
            }
            try {
                this.outconn.shutdown();
            } catch (final IOException ignore) {
            }
        }
    }

}
