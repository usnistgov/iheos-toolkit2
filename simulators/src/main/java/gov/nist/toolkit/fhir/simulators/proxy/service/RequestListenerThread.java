package gov.nist.toolkit.fhir.simulators.proxy.service;

import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase;
import org.apache.http.*;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 *
 */
class RequestListenerThread extends Thread {

    private final ServerSocket serversocket;
    private final HttpService httpService;

    public RequestListenerThread(final int port) throws IOException {
        this.serversocket = new ServerSocket(port);

        // Set up HTTP protocol processor for incoming connections
        final HttpProcessor inhttpproc = new ImmutableHttpProcessor(
                new ResponseDate(),
                new ResponseServer("Test/1.1"),
                new ResponseContent(),
                new ResponseConnControl());

        // Set up HTTP protocol processor for outgoing connections
        final HttpProcessor outhttpproc = new ImmutableHttpProcessor(
                new RequestContent(),
                new RequestTargetHost(),
                new RequestConnControl(),
                new RequestUserAgent("Test/1.1"),
                new RequestExpectContinue(true));

        // Set up outgoing request executor
        final HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        // Set up incoming request handler
        final UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
        reqistry.register("*", new ProxyHandler(
                outhttpproc,
                httpexecutor));

        // Set up the HTTP service (the listener)
        this.httpService = new HttpService(inhttpproc, reqistry);
    }

    @Override
    public void run() {
        System.out.println("Listening on port " + this.serversocket.getLocalPort());
        while (!Thread.interrupted()) {
            try {
                final int bufsize = 8 * 1024;
                // Set up incoming HTTP connection
                final Socket insocket = this.serversocket.accept();
                SimProxyBase proxyBase = new SimProxyBase();
                final DefaultBHttpServerConnection inconn = new ServerConnection(bufsize, proxyBase);    //new DefaultBHttpServerConnection(bufsize);
                System.out.println("Incoming connection from " + insocket.getInetAddress());
                inconn.bind(insocket);

                // Set up outgoing HTTP connection
                //final DefaultBHttpClientConnection outconn = new ClientConnection(bufsize, proxyBase);

                // Start worker thread
                final Thread t = new ProxyThread(this.httpService, inconn);
                t.setDaemon(true);
                t.start();
            } catch (final InterruptedIOException ex) {
                break;
            } catch (final IOException e) {
                System.err.println("I/O error initialising connection thread: "
                        + e.getMessage());
                break;
            }
        }
    }

    class MyClientConnection extends DefaultBHttpClientConnection {

        public MyClientConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
            super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        }
    }

}
