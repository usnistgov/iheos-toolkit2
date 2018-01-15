package gov.nist.toolkit.fhir.simulators.proxy.service;

import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * this is a singleton
 */
public class RequestListenerThread extends Thread {
    private static Logger logger = Logger.getLogger(RequestListenerThread.class);
    private final ServerSocket serversocket;
    private final HttpService httpService;
    private boolean running = false;

    public RequestListenerThread(final int port) throws IOException {
        logger.info("Starting proxy listener thread on " + port);
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
        long id = Thread.currentThread().getId();
        System.out.println("Proxy Operation: Listening on port " + this.serversocket.getLocalPort() + "(" + id + ")");
        while (!Thread.interrupted()) {
            try {
                running = true;
                final int bufsize = 8 * 1024;
                // This will generate a SocketTimeoutException every second essentially
                // polling for Thread.interrupted.  The wait on the socket will
                // not be canceled when we terminate the thread so polling
                // is the preferred approach
                this.serversocket.setSoTimeout(1000);
                // Set up incoming HTTP connection
                final Socket insocket = this.serversocket.accept();
                SimProxyBase proxyBase = new SimProxyBase();
                final DefaultBHttpServerConnection inconn = new ServerConnection(bufsize, proxyBase);    //new DefaultBHttpServerConnection(bufsize);
                System.out.println("Incoming connection from " + insocket.getInetAddress());
                proxyBase.setClientAddress(insocket.getInetAddress().toString());
                inconn.bind(insocket);

                // Set up outgoing HTTP connection
                //final DefaultBHttpClientConnection outconn = new ClientConnection(bufsize, proxyBase);

                // Start worker thread
                final Thread t = new ProxyThread(this.httpService, inconn);
                t.setDaemon(true);
                t.start();
            }
            catch (SocketTimeoutException e) {
                ;// continue waiting - this just allows the thread interupt to work
            }
//            catch (final InterruptedIOException ex) {
//                logger.info("Proxy main thread interrupted");
//                break;
//            }
            catch (final IOException e) {
                System.err.println("I/O error in Proxy thread: "
                        + e.getMessage());
                break;
            }
        }
        id = Thread.currentThread().getId();
        logger.info("Proxy Operation: main thread interrupted (" + id + ")");
        running = false;
        try {
            this.serversocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public ServerSocket getServersocket() {
        return serversocket;
    }

    class MyClientConnection extends DefaultBHttpClientConnection {

        public MyClientConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
            super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        }
    }

}
