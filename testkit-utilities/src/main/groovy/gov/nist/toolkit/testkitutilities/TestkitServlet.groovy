package gov.nist.toolkit.testkitutilities

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.log4j.Logger

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 *
 */
class TestkitServlet extends HttpServlet {
    static final Logger logger = Logger.getLogger(TestkitServlet.class);
    ServletConfig config

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config
        this.init()

        try {
            logger.info("TestkitServlet initialized")

            File ec = new File(config.getInitParameter('toolkit-external-cache'))
            if (!ec || !ec.isDirectory() || !new File(ec, 'environment').isDirectory()) {
                throw new ServletException("Initialization failed: external cache (${ec}) is invalid.")
            }
            Installation.instance().externalCache(ec)

            File warHome = new File(config.getInitParameter('toolkit-warhome'))
            if (warHome == null || !warHome.isDirectory() || !new File(warHome, 'toolkitx').isDirectory()) {
                throw new ServletException("Initialization failed: warHome (${warHome}) is invalid.")
            }
            Installation.instance().warHome(warHome)
        } catch (Throwable e) {
            logger.fatal(ExceptionUtil.exception_details(e, 'Initialization failed'))
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI()

        logger.info("URI is ${uri}")

        def parts = uri.split('\\/')
        logger.info("URI parts are ${parts}")

        if (parts.size() == 3) {
            // index
            response.setContentType('text/html')
            response.getOutputStream().write('<html><body><h2>Test Index</h2></body<</html>'.bytes)
            response.setStatus(HttpServletResponse.SC_OK)
            return
        }

        try {
            def testId = parts[3]

            TestDocumentationGenerator gen = new TestDocumentationGenerator()
            def doc = []
            def environment = 'default'
            def testSession = 'default'
            TestDefinition testDef = new TestKitSearchPath(environment, testSession).getTestDefinition(testId)
            gen.eachTest(doc, testDef.getTestDir())
            String html = gen.toHtml(doc).join('\n')
            response.setContentType('text/html')
            response.getOutputStream().write(html.bytes)
            response.setStatus(HttpServletResponse.SC_OK)

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND)
        }
    }

}
