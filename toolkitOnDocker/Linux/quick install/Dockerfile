FROM tomcat:7-jre8

# Toolkit War file to download
ARG TK_VER=latest

# Ports
ARG TK_PORT=8080
ARG TK_TLS_PORT=8443
ARG TK_PROXYPORT=7279
ARG TK_PIFPORTRANGEBEGIN=5000
ARG TK_PIFPORTRANGEEND=5020

# External cache 
ARG TK_ECVOL=ecdir

WORKDIR /usr/local/tomcat/webapps

# Assume DNS is configured properly!
# Assume the Toolkit GitHub release path for war is same for all future releases.
# 'Latest version' code snippet is from ahdis/xdstools-docker
#
RUN if [ $TK_VER = "latest" ]; then \
export TK_VER=`wget --max-redirect=0 https://github.com/usnistgov/iheos-toolkit2/releases/latest 2>&1 | egrep 'Location:' | cut -b 69-73`; \
fi; \
mkdir xdstools${TK_VER} \
&& wget https://github.com/usnistgov/iheos-toolkit2/releases/download/v${TK_VER}/xdstools${TK_VER}.war \
&& unzip xdstools$TK_VER.war -d xdstools${TK_VER} \
&& rm xdstools$TK_VER.war \
&& sed -i \
-e "s/^Toolkit_Port=.*$/Toolkit_Port=${TK_PORT}/" \
-e "s/^Toolkit_TLS_Port=.*$/Toolkit_TLS_Port=${TK_TLS_PORT}/" \
-e "s/^Proxy_Port=.*$/Proxy_Port=${TK_PROXYPORT}/" \
-e "s/^Listener_Port_Range=.*$/Listener_Port_Range=${TK_PIFPORTRANGEBEGIN},${TK_PIFPORTRANGEEND}/" \
-e "s/^External_Cache=.*$/External_Cache=\/opt\/ecdir/" \
xdstools${TK_VER}/WEB-INF/classes/toolkit.properties  \
&& sed -i \
-e s/port=\"8080\"/port=\"${TK_PORT}\"/ \
/usr/local/tomcat/conf/server.xml

EXPOSE $TK_PORT
EXPOSE $TK_PROXYPORT
EXPOSE $TK_PIFPORTRANGEBEGIN-$TK_PIFPORTRANGEEND


RUN mkdir /opt/ecdir 


