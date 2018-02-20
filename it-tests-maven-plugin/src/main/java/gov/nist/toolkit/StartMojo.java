package gov.nist.toolkit;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import gov.nist.toolkit.grizzlySupport.GrizzlyController;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager;
import gov.nist.toolkit.session.server.Session;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "start", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class StartMojo
    extends AbstractMojo
{

    GrizzlyController server = null;
    String remoteToolkitPort = "8889";
    Session session;
    ToolkitApi api;

    @Parameter(property = "warHome", required = true)
    private String warHome;
    @Parameter(property = "ecDir", required = true)
    private String ecDir;
    @Parameter(property = "axis2", required = true)
    private String axis2;

    public void execute()
        throws MojoExecutionException
    {

       initToolkit();

        clearUserTestDirectories();

        startGrizzlywFhir();
    }

    private void initToolkit() {
        Installation.setTestRunning(true);
        Installation.instance().setServletContextName("");
//        session = UnitTestEnvironmentManager.setupLocalToolkit();
        getLog().info("Got war: " + warHome);
        getLog().info("Got ecDir: " + ecDir);
        UnitTestEnvironmentManager uteMgr = new UnitTestEnvironmentManager(new File(warHome), new File(ecDir));
        session = uteMgr.getSession();
        api = uteMgr.localToolkitApi();

        // Not sure why this is needed in the 'client' side. I think SimServlet should call this from the Grizzly server.
//        ListenerFactory listenerFactory = new ListenerFactory();
//        getPluginContext().put("listenerFactory", listenerFactory);
//        listenerFactory.init(Installation.instance().getListenerPortRange());
    }


    private void clearUserTestDirectories() {
        try {

            /*
        Installation.instance().simDbFile(TestSession.DEFAULT_TEST_SESSION).mkdirs()
        Installation.instance().actorsDir(TestSession.DEFAULT_TEST_SESSION).mkdirs()
        Installation.instance().testLogCache(TestSession.DEFAULT_TEST_SESSION).mkdirs()
             */

            File testDataDir = Installation.instance().propertyServiceManager().getTestLogCache();
            if (testDataDir.exists()) {
                getLog().info("Clearing TEST (testLogCache): " + testDataDir.toString());
                FileUtils.cleanDirectory(testDataDir);
            }

            File simDbFile = Installation.instance().simDbFile(new TestSession(""));
            if (simDbFile.exists()) {
                System.out.println("Clearing TEST (simdb): " + simDbFile.toString());
                FileUtils.cleanDirectory(simDbFile);
            }
        } catch (IOException ioex) {
            getLog().error(ioex);
        }
    }

    private void startGrizzlywFhir() {
        getLog().info("Starting grizzly server...");
        server = new GrizzlyController();
        server.setAxis2(new File(axis2));
        server.start(remoteToolkitPort);
        server.withToolkit();
        server.withFhirServlet();
        Installation.instance().overrideToolkitPort(remoteToolkitPort);  // ignore toolkit.properties
        Installation.instance().setServletContextName("");

        getPluginContext().put("grizzlyService", server);
    }
}
