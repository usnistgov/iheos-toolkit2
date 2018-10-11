package gov.nist.toolkit.testengine.engine.validations.registry

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.testengine.engine.AbstractValidater

abstract class AbstractServerValidater extends AbstractValidater<Object> {
    String filterDescription
    ErrorRecorder er

    abstract void run(Metadata m, ErrorRecorder er)

    AbstractServerValidater(ErrorRecorder er) {
        this.er = er
    }

    void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription
    }

}
