
URL = http://localhost:8081/xdsServices2/registry/soap/portals/yr3a/storedquery
patientid = NIST-query-10^^^&1.3.6.1.4.1.21367.2005.3.7&ISO

************************************************


Header: REQUEST
	Content-Type : text/xml; charset="utf-8"
	Content-Length : 944
	SOAPAction : ""


Message: REQUEST
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"><SOAP-ENV:Header> </SOAP-ENV:Header><SOAP-ENV:Body><query:AdhocQueryRequest xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0" xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0" xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">
  <query:ResponseOption returnComposedObjects="true" returnType="ObjectRef"/>
  <rim:AdhocQuery id="urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d">
    <rim:Slot name="$XDSDocumentEntryStatus">
      <rim:ValueList>
        <rim:Value>('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')</rim:Value>
      </rim:ValueList>
    </rim:Slot>
    <rim:Slot name="$XDSDocumentEntryPatientId">
      <rim:ValueList>
        <rim:Value>'NIST-test-11^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO'</rim:Value>
      </rim:ValueList>
    </rim:Slot>
  </rim:AdhocQuery>
</query:AdhocQueryRequest></SOAP-ENV:Body></SOAP-ENV:Envelope>

************************************************


Header: 


 RESPONSE
	Set-Cookie : JSESSIONID=CE846A66F950177F0811EF11A9A40D82; Path=/xdsServices2
	Content-Type : text/xml; charset="utf-8"
	Transfer-Encoding : chunked
	Date : Sat
	Date : 23 Dec 2006 02:18:29 GMT
	Server : Apache-Coyote/1.1
	Content-Length : 17090
	SOAPAction : ""


Message: 


 RESPONSE
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header>
  <xdsheader SOAP-ENV:mustUnderstand="0">
    <logentry url="http://localhost/~bill/XdsLogReader/details.php?host=localhost&amp;id=1166840298766127.0.0.1"/>
  </xdsheader>
</SOAP-ENV:Header>
<SOAP-ENV:Body>
<AdhocQueryResponse xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0" status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure">
  <RegistryErrorList xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0">
    <RegistryError codeContext="" errorCode="Unknown" severity="Error">
StoredQueryPortal: exception thrown: StoredQuerySrv: executeQuery():  XML:parse: XML document failed to parse

java.lang.Exception: XML:parse: XML document failed to parse
	at gov.nist.registry.common.xml.XML.parse(XML.java:54)
	at gov.nist.registry.common.xml.XML.parse(XML.java:68)
	at gov.nist.registry.common.query.StoredQuerySrv.executeQueryInternal(StoredQuerySrv.java:358)
	at gov.nist.registry.common.query.StoredQuerySrv.executeQuery(StoredQuerySrv.java:103)
	at gov.nist.registry.xds.StoredQueryPortal.run(StoredQueryPortal.java:57)
	at org.apache.jsp.xds.portals.yr3a.storedquery_jsp._jspService(storedquery_jsp.java:88)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:133)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:311)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:301)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:248)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:750)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:510)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:445)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:359)
	at org.apache.jasper.runtime.PageContextImpl.doForward(PageContextImpl.java:712)
	at org.apache.jasper.runtime.PageContextImpl.forward(PageContextImpl.java:682)
	at org.apache.jsp.xds.portals.controller_jsp._jspService(controller_jsp.java:296)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:133)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:311)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:301)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:248)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:750)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:510)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:445)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:359)
	at gov.nist.registry.xds.XdsServlet.forward(XdsServlet.java:490)
	at gov.nist.registry.xds.XdsServlet.doPost(XdsServlet.java:239)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:763)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:257)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardContextValve.invokeInternal(StandardContextValve.java:245)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:199)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:509)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:184)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:164)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:578)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:156)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.ContainerBase.invoke(ContainerBase.java:972)
	at org.apache.coyote.tomcat5.CoyoteAdapter.service(CoyoteAdapter.java:206)
	at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:833)
	at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.processConnection(Http11Protocol.java:732)
	at org.apache.tomcat.util.net.TcpWorkerThread.runIt(PoolTcpEndpoint.java:619)
	at org.apache.tomcat.util.threads.ThreadPool$ControlRunnable.run(ThreadPool.java:688)
	at java.lang.Thread.run(Thread.java:613)


java.lang.Exception: StoredQuerySrv: executeQuery():  XML:parse: XML document failed to parse

java.lang.Exception: XML:parse: XML document failed to parse
	at gov.nist.registry.common.xml.XML.parse(XML.java:54)
	at gov.nist.registry.common.xml.XML.parse(XML.java:68)
	at gov.nist.registry.common.query.StoredQuerySrv.executeQueryInternal(StoredQuerySrv.java:358)
	at gov.nist.registry.common.query.StoredQuerySrv.executeQuery(StoredQuerySrv.java:103)
	at gov.nist.registry.xds.StoredQueryPortal.run(StoredQueryPortal.java:57)
	at org.apache.jsp.xds.portals.yr3a.storedquery_jsp._jspService(storedquery_jsp.java:88)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:133)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:311)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:301)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:248)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:750)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:510)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:445)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:359)
	at org.apache.jasper.runtime.PageContextImpl.doForward(PageContextImpl.java:712)
	at org.apache.jasper.runtime.PageContextImpl.forward(PageContextImpl.java:682)
	at org.apache.jsp.xds.portals.controller_jsp._jspService(controller_jsp.java:296)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:133)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:311)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:301)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:248)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:750)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:510)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:445)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:359)
	at gov.nist.registry.xds.XdsServlet.forward(XdsServlet.java:490)
	at gov.nist.registry.xds.XdsServlet.doPost(XdsServlet.java:239)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:763)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:257)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardContextValve.invokeInternal(StandardContextValve.java:245)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:199)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:509)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:184)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:164)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:578)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:156)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.ContainerBase.invoke(ContainerBase.java:972)
	at org.apache.coyote.tomcat5.CoyoteAdapter.service(CoyoteAdapter.java:206)
	at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:833)
	at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.processConnection(Http11Protocol.java:732)
	at org.apache.tomcat.util.net.TcpWorkerThread.runIt(PoolTcpEndpoint.java:619)
	at org.apache.tomcat.util.threads.ThreadPool$ControlRunnable.run(ThreadPool.java:688)
	at java.lang.Thread.run(Thread.java:613)

	at gov.nist.registry.common.query.StoredQuerySrv.executeQueryInternal(StoredQuerySrv.java:362)
	at gov.nist.registry.common.query.StoredQuerySrv.executeQuery(StoredQuerySrv.java:103)
	at gov.nist.registry.xds.StoredQueryPortal.run(StoredQueryPortal.java:57)
	at org.apache.jsp.xds.portals.yr3a.storedquery_jsp._jspService(storedquery_jsp.java:88)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:133)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:311)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:301)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:248)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:750)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:510)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:445)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:359)
	at org.apache.jasper.runtime.PageContextImpl.doForward(PageContextImpl.java:712)
	at org.apache.jasper.runtime.PageContextImpl.forward(PageContextImpl.java:682)
	at org.apache.jsp.xds.portals.controller_jsp._jspService(controller_jsp.java:296)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:133)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:311)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:301)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:248)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:750)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:510)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:445)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:359)
	at gov.nist.registry.xds.XdsServlet.forward(XdsServlet.java:490)
	at gov.nist.registry.xds.XdsServlet.doPost(XdsServlet.java:239)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:763)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:856)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:284)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:204)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:257)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardContextValve.invokeInternal(StandardContextValve.java:245)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:199)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:509)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:184)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:164)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:578)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:149)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:156)
	at org.apache.catalina.core.StandardValveContext.invokeNext(StandardValveContext.java:151)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:567)
	at org.apache.catalina.core.ContainerBase.invoke(ContainerBase.java:972)
	at org.apache.coyote.tomcat5.CoyoteAdapter.service(CoyoteAdapter.java:206)
	at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:833)
	at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.processConnection(Http11Protocol.java:732)
	at org.apache.tomcat.util.net.TcpWorkerThread.runIt(PoolTcpEndpoint.java:619)
	at org.apache.tomcat.util.threads.ThreadPool$ControlRunnable.run(ThreadPool.java:688)
	at java.lang.Thread.run(Thread.java:613)

    </RegistryError>
  </RegistryErrorList>
<RegistryObjectList xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"/>
</AdhocQueryResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
