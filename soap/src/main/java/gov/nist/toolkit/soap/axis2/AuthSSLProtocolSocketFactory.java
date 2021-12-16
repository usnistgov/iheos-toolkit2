/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.server.PropertyManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import java.lang.Exception;
import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuthSSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    private static final Logger log = Logger.getLogger(AuthSSLProtocolSocketFactory.class.getName());

    private URL keystoreUrl = null;
    private String keystorePassword = null;
    private URL truststoreUrl = null;
    private String truststorePassword = null;
    private SSLContext sslcontext = null;

    /**
     * Constructor for AuthSSLProtocolSocketFactory. Either a keystore or truststore file
     * must be given. Otherwise SSL context initialization error will result.
     * 
     * @param keystoreUrl URL of the keystore file. May be <tt>null</tt> if HTTPS client
     *        authentication is not to be used.
     * @param keystorePassword Password to unlock the keystore. IMPORTANT: this implementation
     *        assumes that the same password is used to protect the key and the keystore itself.
     * @param truststoreUrl URL of the truststore file. May be <tt>null</tt> if HTTPS server
     *        authentication is not to be used.
     * @param truststorePassword Password to unlock the truststore.
     */
    public AuthSSLProtocolSocketFactory(
        final URL keystoreUrl, final String keystorePassword, 
        final URL truststoreUrl, final String truststorePassword)
    {
        super();
        this.keystoreUrl = keystoreUrl;
        this.keystorePassword = keystorePassword;
        this.truststoreUrl = truststoreUrl;
        this.truststorePassword = truststorePassword;

    }

    private static KeyStore createKeyStore(final URL url, final String password) 
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        if (url == null) {
            throw new IllegalArgumentException("Keystore url may not be null");
        }
        log.fine("Initializing key store");
        KeyStore keystore  = KeyStore.getInstance("jks");
        InputStream is = null;
        try {
        	is = url.openStream(); 
            keystore.load(is, password != null ? password.toCharArray(): null);
        } finally {
        	if (is != null) is.close();
        }
        return keystore;
    }
    
    private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password)
        throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException 
    {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        log.fine("Initializing key manager");
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password != null ? password.toCharArray(): null);
        return kmfactory.getKeyManagers(); 
    }

    private static TrustManager[] createTrustManagers(final KeyStore keystore)
        throws KeyStoreException, NoSuchAlgorithmException
    { 
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        log.fine("Initializing trust manager");
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(keystore);
        TrustManager[] trustmanagers = tmfactory.getTrustManagers();
        
        log.fine("Found " + trustmanagers.length + " trust managers");
        
        for (int i = 0; i < trustmanagers.length; i++) {
            if (trustmanagers[i] instanceof X509TrustManager) {
                trustmanagers[i] = new AuthSSLX509TrustManager(
                    (X509TrustManager)trustmanagers[i]); 
            } else {
            	System.out.println("non 509 trust manager: class is " + trustmanagers[i].getClass().getName());
            }
        }
        return trustmanagers; 
    }

    private SSLContext createSSLContext() throws IOException {
        try {
            KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            if (this.keystoreUrl != null) {
                KeyStore keystore = createKeyStore(this.keystoreUrl, this.keystorePassword);
                getDebugMessage(keystore);
                log.finer(() -> getDebugMessage(keystore));
                keymanagers = createKeyManagers(keystore, this.keystorePassword);
            }
            if (this.truststoreUrl != null) {
                KeyStore keystore = createKeyStore(this.truststoreUrl, this.truststorePassword);
                log.finer(() -> getTrustStoreDebugString(keystore));
                trustmanagers = createTrustManagers(keystore);
            }
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(keymanagers, trustmanagers, null);
            return sslcontext;
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException("Unsupported algorithm exception: " + e.getMessage());
        } catch (KeyStoreException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException("Keystore exception: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException("Key management exception: " + e.getMessage());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException("I/O error reading keystore/truststore file: " + e.getMessage());
        }
    }

    private String getTrustStoreDebugString(KeyStore keystore) {
        StringBuffer sb = new StringBuffer();
        try {
            Enumeration aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                sb.append("Trusted certificate '" + alias + "':");
                Certificate trustedcert = keystore.getCertificate(alias);
                if (trustedcert != null && trustedcert instanceof X509Certificate) {
                    X509Certificate cert = (X509Certificate) trustedcert;
                    sb.append("  Subject DN: " + cert.getSubjectDN());
                    sb.append("  Signature Algorithm: " + cert.getSigAlgName());
                    sb.append("  Valid from: " + cert.getNotBefore());
                    sb.append("  Valid until: " + cert.getNotAfter());
                    sb.append("  Issuer: " + cert.getIssuerDN());
                }
            }
        } catch (Exception ex) {
            sb.append(ex.toString());
        }
        return sb.toString();
    }

    private String getDebugMessage(KeyStore keystore) {
        StringBuffer sb = new StringBuffer();
        try {
            Enumeration aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Certificate[] certs = keystore.getCertificateChain(alias);
                if (certs != null) {
                    sb.append("Certificate chain '" + alias + "':");
                    for (int c = 0; c < certs.length; c++) {
                        if (certs[c] instanceof X509Certificate) {
                            X509Certificate cert = (X509Certificate) certs[c];
                            sb.append(" Certificate " + (c + 1) + ":");
                            sb.append("  Subject DN: " + cert.getSubjectDN());
                            sb.append("  Signature Algorithm: " + cert.getSigAlgName());
                            sb.append("  Valid from: " + cert.getNotBefore());
                            sb.append("  Valid until: " + cert.getNotAfter());
                            sb.append("  Issuer: " + cert.getIssuerDN());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            sb.append(ex.toString());
        }
            return sb.toString();
    }

    public SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }

    /**
     * Attempts to get a new socket connection to the given host within the given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect timeout a 
     * controller thread is executed. The controller thread attempts to create a new socket 
     * within the given limit of time. If socket constructor does not return until the 
     * timeout expires, the controller terminates and throws an {@link ConnectTimeoutException}
     * </p>
     *  
     * @param host the host name/IP
     * @param port the port on the host
     * @param params {@link HttpConnectionParams Http connection parameters}
     * 
     * @return Socket a new socket
     * 
     * @throws IOException if an I/O error occurs while creating the socket
     * @throws UnknownHostException if the IP address of the host cannot be
     * determined
     */
    public Socket createSocket(
        final String host,
        final int port,
        final InetAddress localAddress,
        final int localPort,
        final HttpConnectionParams params
    ) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        
        timeout = 0;   // wjm

        SSLSocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            SSLSocket mySocket = (SSLSocket)socketfactory.createSocket(host, port, localAddress, localPort);
            mySocket = setEnabledCipherSuites(setEnabledSSLProtocols(mySocket));
            this.logParameters(mySocket);
            return mySocket;
        } else {
            SSLSocket socket = (SSLSocket)socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket = setEnabledCipherSuites(setEnabledSSLProtocols(socket));
            this.logParameters(socket);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
     */
    public Socket createSocket(
        String host,
        int port,
        InetAddress clientHost,
        int clientPort)
        throws IOException, UnknownHostException
   {
       SSLSocket socket = (SSLSocket)getSSLContext().getSocketFactory().createSocket(
            host,
            port,
            clientHost,
            clientPort
        );
       socket = setEnabledCipherSuites(setEnabledSSLProtocols(socket));
       this.logParameters(socket);
       return socket;
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
     */
    public Socket createSocket(String host, int port)
        throws IOException, UnknownHostException
    {
        SSLSocket socket = (SSLSocket)getSSLContext().getSocketFactory().createSocket(
            host,
            port
        );
        socket = setEnabledCipherSuites(setEnabledSSLProtocols(socket));
        this.logParameters(socket);
        return socket;
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
     */
    public Socket createSocket(
        Socket socket,
        String host,
        int port,
        boolean autoClose)
        throws IOException, UnknownHostException
    {
        SSLSocket mySocket = (SSLSocket) getSSLContext().getSocketFactory().createSocket(
                socket,
                host,
                port,
                autoClose
        );
        mySocket = setEnabledCipherSuites(setEnabledSSLProtocols(mySocket));
        this.logParameters(mySocket);
        return mySocket;
    }

    private SSLSocket setEnabledSSLProtocols(SSLSocket socket)
    {
        log.info("setEnabledSSLProtocols");
        PropertyManager propertyManager = Installation.instance().propertyServiceManager().getPropertyManager();
        String[] sslProtocols = propertyManager.getClientSSLProtocols();
        if (sslProtocols == null)
        {
            log.fine("No Client SSL Protocols retrieved from runtime properties file; SSL Socket is unchanged");
            return socket;
        }
        try {
            socket.setEnabledProtocols(sslProtocols);
            log.fine("socket.setEnabledProtocols completed successfully");
        } catch ( java.lang.Exception e) {
            log.severe("Unable to complete operation: socket.setEnabledProtocols");
            log.severe(e.toString());
        }
        return socket;

    }

    private SSLSocket setEnabledCipherSuites(SSLSocket socket)
    {
      log.fine("setEnabledCipherSuites");
      PropertyManager propertyManager = Installation.instance().propertyServiceManager().getPropertyManager();
      String[] cipherSuites = propertyManager.getClientCipherSuites();
      log.fine("Got cipherSuites");
      if (cipherSuites == null)
      {
         log.fine("No Client Cipher Suites retrieved from runtime properties file; SSL Socket is unchanged");
         return socket;
      }
      String[] supportedCipherSuites = socket.getSupportedCipherSuites();
      HashSet<String> supportedSet = new HashSet<>();
      StringBuilder builder = new StringBuilder();
      for (String c: supportedCipherSuites) {
          supportedSet.add(c);
          builder.append(c).append(',');
      }
      log.fine("Supported CipherSuites" + builder.toString());
      log.fine("Count of supported CipherSuites: " + supportedSet.size());

      HashSet<String> enabledCipherSuites = new HashSet<>();
      builder = new StringBuilder();
      for (String c: cipherSuites) {
          if (supportedSet.contains(c)) {
              enabledCipherSuites.add(c);
              builder.append(c).append(',');
          } else {
              log.fine("User requested ciphersuite not supported by JVM: " + c);
          }
      }
      log.fine("Final set of requested / accepted CipherSuites: " + builder.toString());
      log.fine("Final count of requested / accepted CipherSuites: " + enabledCipherSuites.size());

      String[] finalCipherSuites = new String[enabledCipherSuites.size()];
      int i = 0;
      for (String c: enabledCipherSuites) {
          finalCipherSuites[i++] = c;
      }

      try {
          socket.setEnabledCipherSuites(finalCipherSuites);
          log.fine("socket.setEnabledCipherSuites completed successfully");
      } catch ( java.lang.Exception e) {
          log.severe("Unable to complete operation: socket.setEnabledCipherSuites");
          log.severe(e.toString());
      }
      return socket;
    }

    private void logParameters(SSLSocket s) {
        log.fine("Logging parameters for SSL Socket (Client Side)");
        String[] p = s.getSupportedCipherSuites();
        log.fine("\nSupported Cipher Suites (Client Side)");
        for (String px: p) {
            log.fine(" " + px);
        }

        p = s.getEnabledCipherSuites();
        log.fine("\nEnabled Cipher Suites (Client Side)");
        for (String px: p) {
            log.fine(" " + px);
        }

        log.fine("\nSupported SSL Protocols (Client Side)");
        p = s.getSupportedProtocols();
        for (String px: p) {
            log.fine(" " + px);
        }

        log.fine("\nEnabled SSL Protocols (Client Side)");
        p = s.getEnabledProtocols();
        for (String px: p) {
            log.fine(" " + px);
        }
    }
}
