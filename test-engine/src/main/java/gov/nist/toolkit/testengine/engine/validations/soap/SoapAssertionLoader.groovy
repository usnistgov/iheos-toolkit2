package gov.nist.toolkit.testengine.engine.validations.soap

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath

class SoapAssertionLoader extends PluginClassLoader {

    SoapAssertionLoader(String... paths) throws IOException {
        super(paths)
    }

    SoapAssertionLoader(TestKitSearchPath testKitSearchPath) {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.SOAP_ASSERTION));
    }

}
