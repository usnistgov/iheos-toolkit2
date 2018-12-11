package gov.nist.toolkit.testengine.engine.validations.fhir

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.testkitutilities.TestKitSearchPath

class FhirAssertionLoader extends PluginClassLoader {

    FhirAssertionLoader(String... paths) throws IOException {
        super(paths)
    }

    FhirAssertionLoader(TestKitSearchPath testKitSearchPath) {
        super(testKitSearchPath.getPluginDirs(TestKit.PluginType.FHIR_ASSERTION));
    }

}
