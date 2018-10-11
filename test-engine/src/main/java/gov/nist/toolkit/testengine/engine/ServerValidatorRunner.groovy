package gov.nist.toolkit.testengine.engine

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.testengine.engine.validations.registry.AbstractServerValidater

class ServerValidatorRunner  {

    void run(AbstractServerValidater inst, Metadata m, ErrorRecorder er) {
        inst.invokeMethod('run', m, er)
    }

}
