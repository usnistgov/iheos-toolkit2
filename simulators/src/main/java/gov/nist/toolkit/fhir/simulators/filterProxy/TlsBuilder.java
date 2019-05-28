package gov.nist.toolkit.fhir.simulators.filterProxy;

import gov.nist.toolkit.http.httpclient.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class TlsBuilder {
    private static Registry<ConnectionSocketFactory> getRegistry(File keyStoreFile, String keyStorePassword) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {

        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(keyStoreFile);
        try {
            keyStore.load(instream, keyStorePassword.toCharArray());
        } finally {
            instream.close();
        }


        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
                .build();
        String[] versions = new String[1];
        versions[0] = "TLSv1.2";
        String[] cipherSuites = new String[1];
        cipherSuites[0] = "TLS_RSA_WITH_AES_128_CBC_SHA";
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                versions, cipherSuites, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();
    }

    public static org.apache.http.client.HttpClient getTlsClient(File keyStoreFile, String keyStorePassword) {
        try {
            //Set the https use TLSv1.2
            PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(getRegistry(keyStoreFile, keyStorePassword));
            clientConnectionManager.setMaxTotal(100);
            clientConnectionManager.setDefaultMaxPerRoute(20);
            org.apache.http.client.HttpClient client = (org.apache.http.client.HttpClient) HttpClients.custom().setConnectionManager(clientConnectionManager).build();
            return client;
            //Then you can do : client.execute(HttpGet or HttpPost);
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

}
