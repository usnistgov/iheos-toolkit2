package gov.nist.toolkit.testengine.engine.validations.fhir

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath

class XdsAssertionLoader extends PluginClassLoader {

    XdsAssertionLoader(String... paths) throws IOException {
        super(paths)
    }

    XdsAssertionLoader(TestKitSearchPath testKitSearchPath) {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.XDS_ASSERTION));
    }

}
