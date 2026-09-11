package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader;
import gov.nist.toolkit.testengine.engine.validations.evs.EVSValidatorLoader;
import gov.nist.toolkit.testengine.engine.validations.registry.RegistryValidatorLoader;
import gov.nist.toolkit.testengine.engine.validations.soap.SoapAssertionLoader;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// keeping track of assertion plugins
public class AssertionContext {

    private static List<Context> contexts = new ArrayList<>();

    public static Context get(TestKit.PluginType pluginType, String environment, TestSession testSession) {
        Context context = null;
        for (Context c : contexts) {
            if (c.getPluginType() == pluginType && c.getEnvironment().equals(environment) && c.getTestSession().equals(testSession)) {
                context = c;
                break;
            }
        }

        if (context == null) {
            PluginClassLoader loader;
            /* if (pluginType == TestKit.PluginType.FHIR_ASSERTION)
                loader = new FhirAssertionLoader(new TestKitSearchPath(environment, testSession))
            else */
            try {
                if (pluginType == TestKit.PluginType.SOAP_ASSERTION)
                    loader = new SoapAssertionLoader(new TestKitSearchPath(environment, testSession));
                else if (pluginType == TestKit.PluginType.REGISTRY_VALIDATOR)
                    loader = new RegistryValidatorLoader(new TestKitSearchPath(environment, testSession));
                else if (pluginType == TestKit.PluginType.EVS_VALIDATOR)
                    loader = new EVSValidatorLoader(new TestKitSearchPath(environment, testSession));
                else
                    throw new ToolkitRuntimeException("No classloader for Plugin type " + pluginType);
            } catch (IOException e) {
                throw new ToolkitRuntimeException("Cannot load plugin classloader", e);
            }
            context = new Context(pluginType, environment, testSession, loader);
            contexts.add(context);
        }
        return context;
    }
}
