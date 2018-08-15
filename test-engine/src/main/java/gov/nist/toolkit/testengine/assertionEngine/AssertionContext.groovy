package gov.nist.toolkit.testengine.assertionEngine

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.validations.fhir.FhirAssertionLoader
import gov.nist.toolkit.testengine.engine.validations.fhir.XdsAssertionLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import gov.nist.toolkit.xdsexception.client.ValidaterNotFoundException

// keeping track of assertion plugins
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
            Class validaterClass
            try {
                validaterClass = getPluginClassLoader().loadFile(validaterClassName + ".groovy");
            } catch (ClassNotFoundException e) {
                throw new ValidaterNotFoundException("Unknown validater ${validaterClassName} in " + environment + "/" + testSession + "\n" + getPluginClassLoader().paths)
            }
            if (validaterClass == null)
                throw new ValidaterNotFoundException("Validator " + validaterClassName + " not available in " + environment + "/" + testSession)
            Object obj = validaterClass.newInstance(parameters)
            if (!(obj instanceof AbstractValidater))
                throw new ValidaterNotFoundException("Validator " + validaterClassName + " in " + environment + "/" + testSession + " not instance of AbstractValidater")
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
            else if (pluginType == TestKit.PluginType.XDS_ASSERTION)
                loader = new XdsAssertionLoader(new TestKitSearchPath(environment, testSession))
            else
                throw new ToolkitRuntimeException("No classloader for Plugin type $pluginType")
            context = new Context(pluginType, environment, testSession, loader)
        }
        return context
    }

}
