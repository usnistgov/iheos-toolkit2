package gov.nist.toolkit.testengine.assertionEngine

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.xdsexception.client.ValidaterNotFoundException

class Context {
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
