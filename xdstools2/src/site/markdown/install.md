# Toolkit Installation #

This tool is downloadable from
<a href="https://github.com/usnistgov/iheos-toolkit2/releases/latest.html">https://github.com/usnistgov/iheos-toolkit2/releases/latest.html</a>.
Always grab the latest release. The release notes are 
<a href="http://ihexds.nist.gov/XdsDocs/site/releasenotes/index.html">here</a>.

These are the basic installation steps. For a more detailed description of the configuration see 
[here](howto/config.html).

1. Choose a Servlet container to run the toolkit. It is known to run well in Tomcat versions 7*.  Others will
likely work as well but we develop on Tomcat so we know it best.
2. Install xdstools2.war in your servlet container.  This frequently can be done by copying it to the
webapps directory.
3. Choose a directory to hold your External Cache.  The External Cache is a where toolkit local customizations
and data are kept. This directory must be: dedicated to toolkit (no other content present), writable, 
have a pathname with out spaces, tabs or special characters. On my development machine I use
/home/bill/tmp/ec.  As an example of what will not work, /home/bill/temp dir/ec.  This will not work because of the
*temp dir* directory name with the embeded space character.
3. Create the external cache directory and make it writable by the user account that manages Tomcat (this could be 
the account tomcat or your account depending on the Tomcat configuration). Toolkit will initialize it.
3. Launch xdstools2 in your browser as   http://yourmachine:yourport/xdstools2.
4. Choose the [Toolkit Configuration] from the top of the window.  Launching this window
requires the admin password.  As delivered, it is "easy" (no quotes).
5. Enter a new admin password if desired and the location of your External Cache. If you already have an
External Cache set up then just point to it. If not then create an empty directory on the machine hosting
toolkit.  It must be writable. Upon Save the External Cache directory will be initialized.
6. If you are using simulators then Toolkit Host, Toolkit Port, and possibly Toolkit TLS Port must be 
initialized and correct. If you change any of these three parameters after creating a simulator then see 
[here](howto/movesim.html) for instructions on how to update the configuration of the simulators.
6. You're done.  The parameters you have just edited are stored in the file WEB-INF/classes/toolkit.properties.

## Things you might want to do

*Install multiple copies of toolkit* - The toolkit WAR file, xdstools2.war can be renamed.  To install a second
copy take the second war file and rename it.  I will use xdstools2a.war as an example.  Given this renamed WAR file,
copy it to the webapps directory of your Tomcat.  It will launch and run under the name xdstools2a.  The URL
will be http://hostname:port/xdstools2a.  Two copiies of toolkit can not share an external cache so create a second
external cache as described above and update the toolkit configuration to use it. Make sure the Listener Port Range
for each installation is different.  

![Toolkit Configuration could not be displayed](../images/config.png)

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
