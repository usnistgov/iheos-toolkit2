package gov.nist.toolkit.testengine.assertionEngine

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.fhirValidations.FhirAssertionLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException

class AssertionContext {
    static class Context {
        TestKit.PluginType pluginType
        String environment
        TestSession testSession
        PluginClassLoader pluginClassLoader

        Context(TestKit.PluginType pluginType, String environment, TestSession testSession, PluginClassLoader pluginClassLoader) {
            this.pluginType = pluginType
            this.environment = environment
            this.testSession = testSession
            this.pluginClassLoader = pluginClassLoader
        }

        AbstractValidater getValidater(String validaterClassName, Map<String, String> parameters) {
            Class validaterClass = 	getPluginClassLoader().loadFile(validaterClassName + ".groovy");
            if (validaterClass == null)
                throw new ToolkitRuntimeException("Validator " + validaterClassName + " not available in " + environment + "/" + testSession)
            Object obj = validaterClass.newInstance(parameters)
            if (!(obj instanceof AbstractValidater))
                throw new ToolkitRuntimeException("Validator " + validaterClassName + " in " + environment + "/" + testSession + " not instance of AbstractValidater")
            return obj
        }
    }

    private static List<Context> contexts = new ArrayList<>()

    static Context get(TestKit.PluginType pluginType, String environment, TestSession testSession) {
        Context context = contexts.find { it.pluginType == pluginType && it.environment == environment && it.testSession == testSession }

        if (!context) {
            PluginClassLoader loader
            if (pluginType == TestKit.PluginType.FHIR_ASSERTION)
                loader = new FhirAssertionLoader(new TestKitSearchPath(environment, testSession))
            else
                throw new ToolkitRuntimeException("No classloader for Plugin type $pluginType")
            context = new Context(pluginType, environment, testSession, loader)
        }
        return context
    }

}
