package gov.nist.toolkit.simulators.proxy.service;

import gov.nist.toolkit.simulators.proxy.util.ProxyLogger;
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase;
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
    ProxyLogger proxyLogger;

    public ServerConnection(int buffersize) {
        super(buffersize);
    }

    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        ensureOpen();
        proxyLogger = SimProxyBase.getProxyLoggerForRequest(request);
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
            proxyLogger.logRequestEntity(buffer);
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
            proxyLogger.logResponseEntity(buffer);
            return;
        }
        if(entity != null) {
            OutputStream outstream = this.prepareOutput(response);
            entity.writeTo(outstream);
            outstream.close();
        }
    }


}
