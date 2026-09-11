package gov.nist.toolkit.testengine.engine.validations.soap;

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import java.io.IOException;

public class SoapAssertionLoader extends PluginClassLoader {

    public SoapAssertionLoader(String... paths) throws IOException {
        super(paths);
    }

    public SoapAssertionLoader(TestKitSearchPath testKitSearchPath) throws IOException {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.SOAP_ASSERTION));
    }
}
