# Toolkit Installation #

This tool is downloadable from
<a href="https://bitbucket.org/iheos/toolkit2/downloads">here</a>.
Always grab the latest release.

1. Choose a Servlet container to run the toolkit. It is known to run well in Tomcat versions 7*.  Others will
likely work as well but we develop on Tomcat so we know it.
2. Install xdstools2.war in your servlet container.  This frequently can be done by copying it to the
webapps directory.
3. Launch xdstools2 in your browser as   http://yourmachine:yourport/xdstools2
4. Choose the [Toolkit Configuration] from the top of the window.  Launching this window
requires the admin password.  As delivered, it is "easy" (no quotes).
5. Enter a new password if desired and the location of your External Cache. If you already have an
External Cache set up then just point to it. If not then create an empty directory on the machine hosting
toolkit.  It must be writable. Upon Save the External Cache directory will be initialized.
6. You're done.  The parameters you have just edited are stored in the file WEB-INF/toolkit.properties.

## Installing Certificates for TLS ##

Certificates used are those types native to Java. Two sets of certificates are installed,
one to govern outgoing request and one to govern incoming requests. Frequently the
same set of certificates are installed in both places.

### Certificates for incoming requests ###

Incoming requests go through Tomcat and Tomcat manages these certificates. In the Tomcat
file

    conf/server.xml

is a Connector definition that links configuration information, including certificates,
to a port:

    <Connector port="9443"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" disableUploadTimeout="true"
               acceptCount="100" debug="0" scheme="https" secure="true"
               clientAuth="true" sslProtocol="TLS"
               log4jConfFile="/usr/local/tomcat1/conf/log4j-trustmanager.properties"
               ciphers="TLS_RSA_WITH_AES_128_CBC_SHA, SSL_RSA_WITH_3DES_EDE_CBC_SHA"
	       keystoreFile="/usr/local/tomcat1/conf/keystore/EURO2010/keystore"
               keystorePass="changeit" 
	       truststoreFile="/usr/local/tomcat1/conf/keystore/EURO2010/keystore"
               truststorePass="changeit" />

Note:

* The ciphers attribute is optional, include only if you want to restrict the ciphers that
can be used. This toolkit resticts to two ciphers, on acceptable to Windows based systems
and one acceptable to *ix based systems.  This has worked for 5 years worth of Connectathons.
* The keystoreFile attribute points to a keystore file, a file containing certificates with
keys that identify this port
* The truststoreFile attribute points to a truststore file, a file containing certificates
for all partners in a TLS relationship
* Note that the keystoreFile and truststoreFile can be the same
*See the general Java literature on how to build keystores and truststores

### Certificates for outgoing requests ###

The certificates that are used for outgoing request are in the same format as those
for incoming requests.  In toolkit they are stored in environment configurations in the External Cache.
For IHE Testing events we distribute the entire environmnet configuration ready to install in toolkit.
