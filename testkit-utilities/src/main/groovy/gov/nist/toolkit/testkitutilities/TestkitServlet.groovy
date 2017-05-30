package gov.nist.toolkit.testkitutilities

import gov.nist.toolkit.installation.Installation
import org.apache.log4j.BasicConfigurator
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
    def environment = 'default'
    def testSession = 'default'
    TestKitSearchPath searchPath
    def testDocBase ='/testdoc/testdoc'

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config

        BasicConfigurator.configure();

        testDocBase = new File(config.servletContext.contextPath + '/testdoc')


//        try {
            logger.info("TestkitServlet initializing - testDocBase is ${testDocBase}")

//            File ec = new File(config.getInitParameter('toolkit-external-cache'))
//            if (!ec || !ec.isDirectory() || !new File(ec, 'environment').isDirectory()) {
//                throw new ServletException("Initialization failed: external cache (${ec}) is invalid.")
//            }
//            Installation.instance().externalCache(ec)

//            File warHome = new File(config.getInitParameter('toolkit-warhome'))
//            if (warHome == null || !warHome.isDirectory() || !new File(warHome, 'toolkitx').isDirectory()) {
//                throw new ServletException("Initialization failed: warHome (${warHome}) is invalid.")
//            }
//            Installation.instance().warHome(warHome)
//        } catch (Throwable e) {
//            logger.fatal(ExceptionUtil.exception_details(e, 'Initialization failed'))
//        }

        searchPath = new TestKitSearchPath(environment, testSession)
    }

    // URI is  appname/testdoc/environment/testsession/testid
    // URI is  appname/testdoc/environment/testsession
    // or appname/testdoc/testid
    // or appname/testdoc

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI()

        logger.info("URI is ${uri}")

        def parts = uri.split('\\/')
        def size = parts.size()
        logger.info("URI parts are ${parts}")

        int testdocIndex = -1
        for (int i=0; i<parts.size(); i++) if (parts[i] == 'testdoc') testdocIndex = i

        if (testdocIndex == -1) {
            displayIndex(response)
            return
        }

        if (testdocIndex + 1 == size) {
            displayIndex(response)
            return
        }

        if (testdocIndex + 2 == size) {
            def testId = parts[testdocIndex + 1]
            displayTestDoc(testId, response)
            return
        }

        if (testdocIndex + 3 == size) {
            def env = parts[testdocIndex + 1]
            def ts = parts[testdocIndex + 2]
            if (isEnvironment(env) && isTestSession(ts)) {
                environment = env
                testSession = ts
                displayIndex(response)
                return
            }
        }

        if (testdocIndex + 4 == size) {
            def env = parts[testdocIndex + 1]
            def ts = parts[testdocIndex + 2]
            if (isEnvironment(env) && isTestSession(ts)) {
                def testId = parts[testdocIndex + 3]
                displayTestDoc(testId, response)
                return
            }
        }

        displayIndex(response)

    }

    boolean isEnvironment(String name) {
        Installation.instance().getEnvironmentNames().contains(name)
    }

    boolean isTestSession(String name) {
        Installation.instance().getTestSessionNames().contains(name)
    }

    def displayTestDoc(def testId, HttpServletResponse response) {
        try {
            TestDocumentationGenerator gen = new TestDocumentationGenerator()
            def doc = []
            TestDefinition testDef = new TestKitSearchPath(environment, testSession).getTestDefinition(testId)
            gen.eachTest(doc, testDef.getTestDir())
            String header = "<h1>Test Documentation</h1>\nEnvironment: ${environment} <br />Test Session: ${testSession} <br /><br />\n"
            String html = header + gen.toHtml(doc).join('\n')
            response.setContentType('text/html')
            response.getOutputStream().write(html.bytes)
            response.setStatus(HttpServletResponse.SC_OK)
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND)
        }
    }

    def displayIndex(HttpServletResponse response) {
        response.setContentType('text/html')
        def html = new StringBuilder()
        html << '<html><body><h1>Test Index</h1>'
        html <<  "Environment: ${environment}  <br />Test Session: ${testSession} <br /><br />\n"
        html << produceIndex()
        html << '</body></html>'
        response.getOutputStream().write(html.toString().bytes)
        response.setStatus(HttpServletResponse.SC_OK)
    }

    def produceIndex() {
        // shortname ==> description
        Map collections = searchPath.getActorCollectionsNamesAndDescriptions()
        Set types = [] // actor types
        collections.each { String name, String desc -> types.add(name) }
        return produceActorIndex(collections, types)
    }

    /**
     *
     * @param collections - map actorcode (includes options) => long name
     * @param types - set of actorcodes (does not include options)
     * Options are coded as name_option
     */
    def produceActorIndex(Map collections, Set types) {
        def buf = new StringBuilder()
        buf << '<h2>Actor Types</h2>'
        def orderedTypes = types.sort()

        // main index
        orderedTypes.findAll { !isOption(it) }.each { buf << "<a href=\"#${actorTypeAsBookmark(it)}\">${collections[it]}</a><br />\n" }
        buf << '<br />'

        // per actor
        orderedTypes.findAll{ !isOption(it) }.each { String theType ->
            buf << '<hr />'
            buf << "<h2 id=\"${actorTypeAsBookmark(theType)}\">${collections[theType]}</h2>\n"

            // the index
            optionsInOrder(types, theType).each { String subType ->
                if (subType == theType) {
                    def subTypeName = (subType == theType) ? 'Required' : collections[subType]
                    def subTypeDetail = subType+'Detail'
                    buf << "<a href=\"#${subTypeDetail}\">${subTypeName}&nbsp;</a>\n"
                } else {
                    def subTypeName = (subType == theType) ? 'Required' : collections[subType]
                    buf << "<a href=\"#${subType}\">${subTypeName}&nbsp;</a>\n"
                }
            }
            buf << '<br />\n'

            // the details
            optionsInOrder(types, theType).each { String subType ->
                if (subType == theType) {
                    def subTypeName = (subType == theType) ? 'Required' : collections[subType]
                    def subTypeDetail = subType+'Detail'
                    buf << "<h3 id=\"${subTypeDetail}\">${subTypeName}&nbsp;</h3>\n"

                    // members of the collection
                    membersOfTheCollection(buf, subType)
                } else {
                    def subTypeName = (subType == theType) ? 'Required' : collections[subType]
                    buf << "<h3 id=\"${subType}\">${subTypeName}&nbsp;</h3>\n"
                    membersOfTheCollection(buf, subType)
                }
            }

        }
        return buf
    }

    def membersOfTheCollection(def buf, def subType) {
        def testIds = searchPath.getCollectionMembers('actorcollections', subType)
//        println "Test collection ${subType} contains ${testIds}"
        testIds.each { def testId ->
            TestKit testkit = searchPath.getTestKitForTest(testId)
            if (!testkit) return
            TestDefinition testDef = testkit.getTestDef(testId)
            def testDescription = testDef.readmeFirstLine
            buf << "<a href=\"${testDocBase}/${testId}\">"
            buf << "${testId} - ${testDescription}"
            buf << '</a><br />\n'
        }

    }


    def actorTypeAsBookmark(String type) {
        return "${type.replaceAll(' ', '_')}"
    }

    def isOption(String type) { return type.contains('_')}

    def isSubtypeOf(String it, String mainType) { return it.startsWith("${mainType}_") }

    /**
     * required first then options alphabetically
     * @param types
     * @param theType
     * @return
     */
    def optionsInOrder(def types, def theType) {
        def lst = types.findAll { it.startsWith(theType) && it != theType }.collect {
            it
        }.sort()
        lst.add(0, theType)
        return lst
    }
}
