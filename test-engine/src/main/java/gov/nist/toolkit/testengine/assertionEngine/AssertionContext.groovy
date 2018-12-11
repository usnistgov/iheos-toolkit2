package gov.nist.toolkit.testengine.assertionEngine

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.validations.fhir.FhirAssertionLoader
import gov.nist.toolkit.testengine.engine.validations.registry.RegistryValidatorLoader
import gov.nist.toolkit.testengine.engine.validations.soap.SoapAssertionLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import gov.nist.toolkit.xdsexception.client.ValidaterNotFoundException

// keeping track of assertion plugins
class AssertionContext {

    private static List<Context> contexts = new ArrayList<>()

    static Context get(TestKit.PluginType pluginType, String environment, TestSession testSession) {
        Context context = contexts.find { it.pluginType == pluginType && it.environment == environment && it.testSession == testSession }

        if (!context) {
            PluginClassLoader loader
            if (pluginType == TestKit.PluginType.FHIR_ASSERTION)
                loader = new FhirAssertionLoader(new TestKitSearchPath(environment, testSession))
            else if (pluginType == TestKit.PluginType.SOAP_ASSERTION)
                loader = new SoapAssertionLoader(new TestKitSearchPath(environment, testSession))
            else if (pluginType == TestKit.PluginType.REGISTRY_VALIDATOR)
                loader = new RegistryValidatorLoader(new TestKitSearchPath(environment, testSession))
            else
                throw new ToolkitRuntimeException("No classloader for Plugin type $pluginType")
            context = new Context(pluginType, environment, testSession, loader)
        }
        return context
    }

}
