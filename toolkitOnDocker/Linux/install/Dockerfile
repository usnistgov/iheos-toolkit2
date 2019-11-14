FROM tomcat:7-jre8

# Tomcat Server 7 configuration
# https://tomcat.apache.org/tomcat-7.0-doc/config/http.html 

ARG TOOLKIT_PROPERTY_FILE=toolkit.properties

ARG TK_VER=latest
# Toolkit War file to download
ARG REL_NUM=auto

# Ports
ARG TK_PORT=8080
ARG TK_TLS_PORT=8443
ARG TK_PROXYPORT=7279
ARG TK_PIFPORTRANGEBEGIN=5000
ARG TK_PIFPORTRANGEEND=5020


COPY cert/from-eu-gss-all-purpose-3129.jks /opt/toolkit/cert/keystore

# This copy instruction only serves to extract values for the purpose of configuring the Tomcat server.xml. 
#The host file is the one we hope to use at runtime.
COPY ${TOOLKIT_PROPERTY_FILE} /tmp/toolkit.properties

WORKDIR /usr/local/tomcat/webapps

# Assume DNS is configured properly!
# Assume the Toolkit GitHub release path for war is same for all future releases.
# 'Latest version' code snippet is from ahdis/xdstools-docker
#
RUN \
export TK_PORT=`grep Toolkit_Port /tmp/toolkit.properties | cut -d'=' -f2`; \
export TK_TLS_PORT=`grep Toolkit_TLS_Port /tmp/toolkit.properties | cut -d'=' -f2`; \
export TK_PROXYPORT=`grep Proxy_Port /tmp/toolkit.properties | cut -d= -f2`; \
export TK_PIFPORTRANGEBEGIN=`grep Listener_Port_Range /tmp/toolkit.properties | cut -d= -f2 | cut -d, -f1 | tr -d '[:space:]'`; \
export TK_PIFPORTRANGEEND=`grep Listener_Port_Range /tmp/toolkit.properties | cut -d= -f2 | cut -d, -f2 | tr -d '[:space:]'`; \
if [ $TK_VER = "latest" ]; then \
export REL_NUM=`wget --max-redirect=0 https://github.com/usnistgov/iheos-toolkit2/releases/latest 2>&1 | egrep 'Location:' | cut -b 69-73`; \
else \
export REL_NUM=$TK_VER; \
fi; \
mkdir xdstools${TK_VER} \
&& wget -q https://github.com/usnistgov/iheos-toolkit2/releases/download/v${REL_NUM}/xdstools${REL_NUM}.war \
&& unzip -q xdstools$REL_NUM.war -d xdstools${TK_VER} \
&& rm xdstools$REL_NUM.war \
&& sed -i \
-e s/port=\"8080\"/port=\"${TK_PORT}\"/ \
-e s/redirectPort=\"8443\"/redirectPort=\"${TK_TLS_PORT}\"/g \
-e s/\<\!--\ Define\ a\ SSL/\
\<\!--\ Define\ a\ SSL\ HTTP\ Connector\ --\>\
\\n\<Connector\\n\
\\tport=\"${TK_TLS_PORT}\"\\n\
\\tprotocol=\"org.apache.coyote.http11.Http11NioProtocol\"\\n\
\\tmaxThreads=\"150\"\\n\
\\tSSLEnabled=\"true\"\\n\
\\tclientAuth=\"true\"\\n\
\\tsslProtocol=\"TLS\"\\n\
\\tkeystoreFile=\"\\/opt\\/toolkit\\/cert\\/keystore\"\\n\
\\tkeystorePass=\"changeit\"\\n\
\\tkeyAlias=\"toolkit\"\\n\
\\ttruststoreFile=\"\\/opt\\/toolkit\\/cert\\/keystore\"\\n\
\\ttruststorePass=\"changeit\"\\n\
\\tSSLVerifyClient=\"true\"\\n\
\\tSSLProtocol=\"TLSv1.2\"\\n\
\\tSSLCipherSuite=\"TLS_RSA_WITH_AES_128_CBC_SHA\"\\n\
\\/\>\\n\
\<\!--\ Define\ a\ SSL\ HTTP/ \
/usr/local/tomcat/conf/server.xml



EXPOSE $TK_PORT \
$TK_TLS_PORT \
$TK_PROXYPORT \ 
$TK_PIFPORTRANGEBEGIN-$TK_PIFPORTRANGEEND


# RUN mkdir /opt/ecdir 


