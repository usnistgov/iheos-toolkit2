package gov.nist.toolkit.soap.http;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

/* This was pulled together from Internet sources and doesn't yet do
 * anything useful.
 */

public class MutualAuth {
    private static final boolean debug = true;
    
    public static void main(String[] args) {
//        String url = "https://localhost:9443/tf6/services/xdsregistryb";
        String url = "https://localhost:9443/xdstools2/simulator/81541fb5_9131_44c6_9b73_b06c6ea23219/registry/sq";
        String keyStoreFileName = "/Users/bill/dev/xdstoolkit/xdstest/keystores/EURO2011/keystore";
        String keyStorePassword = "password";
        String trustStoreFileName = "/Users/bill/dev/xdstoolkit/xdstest/keystores/EURO2011/keystore";
        String trustStorePassword = "password";
        String alias = "tomcat";
        
        //System.setProperty("javax.net.debug", "all");
        
        MutualAuth me = new MutualAuth();
 
        try {
            //create key and trust managers
            KeyManager[] keyManagers = createKeyManagers(keyStoreFileName, keyStorePassword, alias);
            TrustManager[] trustManagers = createTrustManagers(trustStoreFileName, trustStorePassword);
            //init context with managers data   
            SSLSocketFactory factory = initItAll(keyManagers, trustManagers);
            //get the url and display content
            me.doitAll(url, factory);
 
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
 
    private  void doitAll(String urlString, SSLSocketFactory sslSocketFactory) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) connection).setHostnameVerifier(new NullHostnameVerifier());
        }
        
        HttpURLConnection hconn = (HttpURLConnection) connection;
        
        if (debug)
        	System.out.println("Secure connection established");
        
        String sqin = Io.stringFromFile(new File("/Users/bill/dev/common/src/gov/nist/registry/common2/http/soap/sq1.txt"));
        

        
        connection.setDoOutput(true);
		connection.setUseCaches(false);
		hconn.addRequestProperty("Content-Type", "application/soap+xml");
        OutputStream os = (OutputStream) connection.getOutputStream();
        os.write(sqin.getBytes());
        os.close();
        

        String reply = Io.getStringFromInputStream(connection.getInputStream());
        System.out.println(reply);
        
//        int x;
//        while ((x = ((InputStream) connection.getContent()).read()) != -1) {
//            System.out.print(new String(new byte[] {(byte) x }));
//        }
    }
 
    private static SSLSocketFactory initItAll(KeyManager[] keyManagers, TrustManager[] trustManagers)
        throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context;
        //sslSocket.setEnabledProtocols(new String[] {"SSLv3", "TLSv1"});
        try {
        	context = SSLContext.getInstance("TLS");
        	if (debug)
        		System.out.println("Context built with TLS");
        } catch (NoSuchAlgorithmException e) {
        	context = SSLContext.getInstance("SSLv3");
        	if (debug)
        		System.out.println("Context built with SSLV3");
        }
        //TODO investigate: could also be "SSLContext context = SSLContext.getInstance("TLS");" Why?
        context.init(keyManagers, trustManagers, null);
        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }
 
    private static KeyManager[] createKeyManagers(String keyStoreFileName, String keyStorePassword, String alias)
        throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        //create Inputstream to keystore file
        java.io.InputStream inputStream = new java.io.FileInputStream(keyStoreFileName);
        //create keystore object, load it with keystorefile data
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());
        //DEBUG information should be removed
        if (debug) {
        	System.out.println("For KeyStore ...........");
            printKeystoreInfo(keyStore);
        }
 
        KeyManager[] managers;
        if (alias != null) {
            managers =
                new KeyManager[] {
                     new MutualAuth().new AliasKeyManager(keyStore, alias, keyStorePassword)};
        } else {
            //create keymanager factory and load the keystore object in it 
            KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword == null ? null : keyStorePassword.toCharArray());
            managers = keyManagerFactory.getKeyManagers();
        }
        //return 
        return managers;
    }
 
    private static TrustManager[] createTrustManagers(String trustStoreFileName, String trustStorePassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        //create Inputstream to truststore file
        java.io.InputStream inputStream = new java.io.FileInputStream(trustStoreFileName);
        //create keystore object, load it with truststorefile data
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(inputStream, trustStorePassword == null ? null : trustStorePassword.toCharArray());
        //DEBUG information should be removed
        if (debug) {
        	System.out.println("For TrustStore ...............");
            printKeystoreInfo(trustStore);
        }
        //create trustmanager factory and load the keystore object in it 
        TrustManagerFactory trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        //return 
        return trustManagerFactory.getTrustManagers();
    }
 
    private static void printKeystoreInfo(KeyStore keystore) throws KeyStoreException {
        System.out.println("Provider : " + keystore.getProvider().getName());
        System.out.println("Type : " + keystore.getType());
        System.out.println("Size : " + keystore.size());
 
        Enumeration en = keystore.aliases();
        while (en.hasMoreElements()) {
            System.out.println("Alias: " + en.nextElement());
        }
    }
    private class AliasKeyManager implements X509KeyManager {
 
        private KeyStore _ks;
        private String _alias;
        private String _password;
 
        public AliasKeyManager(KeyStore ks, String alias, String password) {
            _ks = ks;
            _alias = alias;
            _password = password;
        }
 
        public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
            return _alias;
        }
 
        public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
            return _alias;
        }
 
        public X509Certificate[] getCertificateChain(String alias) {
            try {
                java.security.cert.Certificate[] certificates = this._ks.getCertificateChain(alias);
                if(certificates == null){throw new FileNotFoundException("no certificate found for alias:" + alias);}
                X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
                System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
                return x509Certificates;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
 
        public String[] getClientAliases(String str, Principal[] principal) {
            return new String[] { _alias };
        }
 
        public PrivateKey getPrivateKey(String alias) {
            try {
                return (PrivateKey) _ks.getKey(alias, _password == null ? null : _password.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
 
        public String[] getServerAliases(String str, Principal[] principal) {
            return new String[] { _alias };
        }
 
    }
    
    class NullHostnameVerifier implements HostnameVerifier {

		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
    	
    }

}
