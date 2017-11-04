package gov.nist.toolkit.fhir.simulators.proxy.service;

import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * extend server connection class and add capture of request message from client
 */
public class ServerConnection extends DefaultBHttpServerConnection {
    private SimProxyBase proxyBase;

    ServerConnection(int buffersize, SimProxyBase base) {
        super(buffersize);
        this.proxyBase = base;
    }

    SimProxyBase getSimProxyBase() { return proxyBase; }

    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        ensureOpen();
        proxyBase.init(request);
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
            proxyBase.getClientLogger().logRequestEntity(buffer);
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
            proxyBase.getClientLogger().logResponseEntity(buffer);
            return;
        }
        if(entity != null) {
            OutputStream outstream = this.prepareOutput(response);
            entity.writeTo(outstream);
            outstream.close();
        }
    }

    public OutputStream prepareOutputStream(final HttpMessage message) throws HttpException {
        return prepareOutput(message);
    }

}
