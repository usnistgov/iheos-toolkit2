package gov.nist.toolkit.proxy;

import org.apache.http.*;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.protocol.*;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 *
 */
class RequestListenerThread extends Thread {

    private final HttpHost target;
    private final ServerSocket serversocket;
    private final HttpService httpService;

    public RequestListenerThread(final int port, final HttpHost target) throws IOException {
        this.target = target;
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
                this.target,
                outhttpproc,
                httpexecutor));

        // Set up the HTTP service
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
                final DefaultBHttpServerConnection inconn = new MyServerConnection(bufsize);    //new DefaultBHttpServerConnection(bufsize);
                System.out.println("Incoming connection from " + insocket.getInetAddress());
                inconn.bind(insocket);

                // Set up outgoing HTTP connection
                final DefaultBHttpClientConnection outconn = new DefaultBHttpClientConnection(bufsize);

                // Start worker thread
                final Thread t = new ProxyThread(this.httpService, inconn, outconn);
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

    class MyServerConnection extends DefaultBHttpServerConnection {

        public MyServerConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory<HttpRequest> requestParserFactory, HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
            super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestParserFactory, responseWriterFactory);
        }

        public MyServerConnection(int buffersize) {
            super(buffersize);
        }

        @Override
        public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws IOException, HttpException {
            Args.notNull(request, "HTTP request");
            ensureOpen();
            final HttpEntity entity = prepareInput(request);
            if (entity instanceof BasicHttpEntity) {
                System.out.println("Got It");
                BasicHttpEntity entity2 = (BasicHttpEntity) entity;
                InputStream is = entity2.getContent();
                byte[] buffer = Io.getBytesFromInputStream(is);
                System.out.println(new String(buffer));
                final BasicHttpEntity entity3 = new BasicHttpEntity();
                entity3.setContent(Io.bytesToInputStream(buffer));
                request.setEntity(entity3);
                return;
            }
            request.setEntity(entity);
        }

//        protected void onRequestReceived(HttpRequest request) {
//            System.out.println("Got a Request");
//            try {
//                if (false && request instanceof HttpEntityEnclosingRequest) {
//                    final HttpEntityEnclosingRequest req = (HttpEntityEnclosingRequest) request;
//                    ContentLengthStrategy contentLengthStrategy = StrictContentLengthStrategy.INSTANCE;
//                    InputStream contentStream = null;
//                    long len = contentLengthStrategy.determineLength(req);
//                    if (len == ContentLengthStrategy.CHUNKED) {
//                        contentStream = new ChunkedInputStream(getSessionInputBuffer());
//                    } else if (len == ContentLengthStrategy.IDENTITY) {
//                        contentStream = new IdentityInputStream(getSessionInputBuffer());
//                    } else {
//                        contentStream = new ContentLengthInputStream(getSessionInputBuffer(), len);
//                    }
//                    final byte[] bytes = Io.getBytesFromInputStream(contentStream);
//                    System.out.println("Got bytes - " + bytes.length);
//                    System.out.println(new String(bytes));
//
//                    req.setEntity(new HttpEntity() {
//                        public boolean isRepeatable() {
//                            return false;
//                        }
//
//                        public boolean isChunked() {
//                            return false;
//                        }
//
//                        public long getContentLength() {
//                            return bytes.length;
//                        }
//
//                        public Header getContentType() {
//                            return req.getFirstHeader("Content-Type");
//                        }
//
//                        public Header getContentEncoding() {
//                            return null;
//                        }
//
//                        public InputStream getContent() throws IOException, UnsupportedOperationException {
//                            return null;
//                        }
//
//                        public void writeTo(OutputStream outstream) throws IOException {
//                            outstream.write(bytes);
//                        }
//
//                        public boolean isStreaming() {
//                            return false;
//                        }
//
//                        public void consumeContent() throws IOException {
//
//                        }
//                    });
//                    super.onRequestReceived(req);
//                    return;
//                }
//            } catch (Exception e1) {
//                System.out.println("********\noops " + e1.getMessage() + "\n***************");
//            }
//            super.onRequestReceived(request);
//        }
    }

}
