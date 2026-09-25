package gov.nist.toolkit.testengine.engine.validations.evs;

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import java.io.IOException;

public class EVSValidatorLoader extends PluginClassLoader {

    public EVSValidatorLoader(String... paths) throws IOException {
        super(paths);
    }

    public EVSValidatorLoader(TestKitSearchPath testKitSearchPath) throws IOException {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.EVS_VALIDATOR));
    }
}
