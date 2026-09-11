package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader;
import gov.nist.toolkit.testengine.engine.AbstractValidater;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.xdsexception.client.ValidaterNotFoundException;

public class Context {
    TestKit.PluginType pluginType;
    String environment;
    TestSession testSession;
    PluginClassLoader pluginClassLoader;

    public Context(TestKit.PluginType pluginType, String environment, TestSession testSession, PluginClassLoader pluginClassLoader) {
        this.pluginType = pluginType;
        this.environment = environment;
        this.testSession = testSession;
        this.pluginClassLoader = pluginClassLoader;
    }

    public AbstractValidater getValidater(String validaterClassName, java.util.Map<String, String> parameters) throws ValidaterNotFoundException {
        Class validaterClass;
        validaterClass = getPluginClassLoader().loadFile(validaterClassName + ".groovy");
        if (validaterClass == null)
            throw new ValidaterNotFoundException("Validator " + validaterClassName + " not available in " + environment + "/" + testSession);
        Object obj;
        try {
            obj = validaterClass.getDeclaredConstructor(java.util.Map.class).newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!(obj instanceof AbstractValidater))
            throw new ValidaterNotFoundException("Validator " + validaterClassName + " in " + environment + "/" + testSession + " not instance of AbstractValidater");
        return (AbstractValidater) obj;
    }

    public TestKit.PluginType getPluginType() {
        return pluginType;
    }

    public String getEnvironment() {
        return environment;
    }

    public TestSession getTestSession() {
        return testSession;
    }

    public PluginClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }
}
