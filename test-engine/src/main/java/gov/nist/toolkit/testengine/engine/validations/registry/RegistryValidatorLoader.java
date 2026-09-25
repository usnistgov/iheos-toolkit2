package gov.nist.toolkit.testengine.engine.validations.registry;

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import java.io.IOException;

public class RegistryValidatorLoader extends PluginClassLoader {

    public RegistryValidatorLoader(String... paths) throws IOException {
        super(paths);
    }

    public RegistryValidatorLoader(TestKitSearchPath testKitSearchPath) throws IOException {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.REGISTRY_VALIDATOR));
    }
}
