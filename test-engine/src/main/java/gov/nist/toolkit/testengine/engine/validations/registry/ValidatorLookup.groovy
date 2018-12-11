package gov.nist.toolkit.testengine.engine.validations.registry

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.testengine.assertionEngine.AssertionContext
import gov.nist.toolkit.testengine.assertionEngine.Context
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException

class ValidatorLookup {

    static AbstractServerValidater find(String className, String environment, TestSession testSession) {
        Context context = AssertionContext.get(TestKit.PluginType.REGISTRY_VALIDATOR, environment, testSession)
        AbstractValidater val = context.getValidater(className, new HashMap<String, String>())
        if (!( val instanceof AbstractServerValidater))
            throw new ToolkitRuntimeException("Server validator " + className + " is not a subclass of AbstractServerValidater")
        return (AbstractServerValidater) val
    }
}
