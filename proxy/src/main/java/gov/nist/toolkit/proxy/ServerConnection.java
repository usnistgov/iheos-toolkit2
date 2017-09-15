package gov.nist.toolkit.proxy;

import gov.nist.toolkit.utilities.io.Io;
import org.apache.http.*;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * extend server connection class and add capture of request message from client
 */
class ServerConnection extends DefaultBHttpServerConnection {

    public ServerConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory<HttpRequest> requestParserFactory, HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestParserFactory, responseWriterFactory);
    }

    public ServerConnection(int buffersize) {
        super(buffersize);
    }

    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        ensureOpen();
        final HttpEntity entity = prepareInput(request);
        if (entity != null && entity instanceof BasicHttpEntity) {
            System.out.println("Got Client Request entity");
            BasicHttpEntity entity2 = (BasicHttpEntity) entity;
            InputStream is = entity2.getContent();
            byte[] buffer = Io.getBytesFromInputStream(is);
            //System.out.println(new String(buffer));
            final BasicHttpEntity entity3 = new BasicHttpEntity();
            entity3.setContent(Io.bytesToInputStream(buffer));
            request.setEntity(entity3);
            return;
        }
        request.setEntity(entity);
    }

    @Override
    public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        HttpEntity entity = response.getEntity();
        if (entity != null && entity instanceof BasicHttpEntity) {
            System.out.println("Got Client Response entity");
            BasicHttpEntity entity2 = (BasicHttpEntity) entity;

            InputStream is = entity2.getContent();
            byte[] buffer = Io.getBytesFromInputStream(is);
            final BasicHttpEntity entity3 = new BasicHttpEntity();
            entity3.setContent(Io.bytesToInputStream(buffer));
            OutputStream outstream = this.prepareOutput(response);

            entity3.writeTo(outstream);
            outstream.close();
            return;
        }
        if(entity != null) {
            OutputStream outstream = this.prepareOutput(response);
            entity.writeTo(outstream);
            outstream.close();
        }
    }


}
