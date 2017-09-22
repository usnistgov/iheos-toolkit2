package gov.nist.toolkit.simulators.proxy.service;

import gov.nist.toolkit.simulators.proxy.util.SimProxyBase;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * proxy acting as client to eventual server
 */
public class ClientConnection extends DefaultBHttpClientConnection {
    private SimProxyBase proxyBase;

    public ClientConnection(int buffersize, SimProxyBase base) {
        super(buffersize);
        this.proxyBase = base;
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
            proxyBase.getTargetLogger().logRequestEntity(buffer);
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
            proxyBase.getTargetLogger().logResponseEntity(buffer);
            return;
        }
        response.setEntity(entity);
    }


}
