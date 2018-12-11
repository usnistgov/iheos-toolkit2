package gov.nist.toolkit.testengine.engine.validations.registry

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath

class RegistryValidatorLoader extends PluginClassLoader {

    RegistryValidatorLoader(String... paths) throws IOException {
        super(paths)
    }

    RegistryValidatorLoader(TestKitSearchPath testKitSearchPath) {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.REGISTRY_VALIDATOR));
    }

}
