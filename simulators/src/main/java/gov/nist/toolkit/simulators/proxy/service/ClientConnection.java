package gov.nist.toolkit.simulators.proxy.service;

import gov.nist.toolkit.simulators.proxy.util.ProxyLogger;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.http.*;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * proxy acting as client to eventual server
 */
public class ClientConnection extends DefaultBHttpClientConnection {

    public ClientConnection(int buffersize) {
        super(buffersize);
    }

    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request)
            throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        ensureOpen();
        final HttpEntity entity = request.getEntity();
        if (entity == null) {
            return;
        }
        if (entity instanceof BasicHttpEntity) {
            System.out.println("Got Server Request entity");
            BasicHttpEntity entity2 = (BasicHttpEntity) entity;

            InputStream is = entity2.getContent();
            byte[] buffer = Io.getBytesFromInputStream(is);
            final BasicHttpEntity entity3 = new BasicHttpEntity();
            entity3.setContent(Io.bytesToInputStream(buffer));
            final OutputStream outstream = this.prepareOutput(request);
            entity3.writeTo(outstream);
            outstream.close();
            return;
        }
        final OutputStream outstream = prepareOutput(request);
        entity.writeTo(outstream);
        outstream.close();
    }


    @Override
    public void receiveResponseEntity(
            final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        ensureOpen();
        final HttpEntity entity = prepareInput(response);
        if (entity != null && entity instanceof BasicHttpEntity) {
            System.out.println("Got Server Response entity");
            BasicHttpEntity entity2 = (BasicHttpEntity) entity;
            InputStream is = entity2.getContent();
            byte[] buffer = Io.getBytesFromInputStream(is);
            //System.out.println(new String(buffer));
            final BasicHttpEntity entity3 = new BasicHttpEntity();
            entity3.setContent(Io.bytesToInputStream(buffer));
            response.setEntity(entity3);
            return;
        }
        response.setEntity(entity);
    }


}
