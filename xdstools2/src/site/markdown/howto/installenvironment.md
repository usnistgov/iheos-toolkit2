# Installing and environment in toolkit

In toolkit an environment is the combination of a code set for the Affinity Domain and a certificate to be used
in TLS connections when toolkit initiates the connections. When toolkit accepts the connections the certificate
must be installed in the servlet container (Tomcat) directly.

For North American and European Connectathons the environment definitions are downloadable from
<a href="http://ihexds.nist.gov/downloads.html" target="_blank" >http://ihexds.nist.gov/downloads.html</a>.

A typical name for the download is NA2016.zip which is the environment definition for the North American 2016
Pre-Connectathon and Connectathon testing.

The download is a zip file containing the following structure:

    PN107101:environment bill$ ls -lR NA2016
    total 160
    -rw-r--r--  1 bill  bill  80374 Nov  9 08:30 codes.xml
    drwxr-xr-x  9 bill  bill    306 Nov 25 09:16 keystore

    NA2016/keystore:
    total 48
    -rw-r-----@ 1 bill  bill  2976 Nov 25 08:23 keystore
    -rw-r--r--@ 1 bill  bill    25 Nov 25 08:27 keystore.properties

The contents of this zip must be expanded and installed in your External Cache. Looking at the External Cache

    PN107101:toolkit2b bill$ ls -l
    total 0
    drwxr-xr-x  3 bill  bill  102 Nov 18 09:00 TestLogCache
    drwxr-xr-x  6 bill  bill  204 Nov 30 18:06 actors
    drwxr-xr-x  6 bill  bill  204 Nov 30 06:45 environment
    drwxr-xr-x  4 bill  bill  136 Dec  2 08:07 simdb


The top level directory inside the zip, NA2016, would be installed in the environment directory.

Once this is installed and you refresh the toolkit in your browser the environment name, NA2016, should appear in the
Environment selector on tools like FindDocuments where it is used. If on your system the browser refresh does not make
this entry appear then it may be necessary to restart Tomcat.


