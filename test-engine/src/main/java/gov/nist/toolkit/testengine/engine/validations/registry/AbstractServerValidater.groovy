package gov.nist.toolkit.testengine.engine.validations.registry

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.testengine.engine.AbstractValidater
import org.apache.log4j.Logger

abstract class AbstractServerValidater extends AbstractValidater<Object> {
    static Logger logger = Logger.getLogger(AbstractServerValidater.class);
    String filterDescription
    ErrorRecorder er

    abstract void run(Metadata m, ErrorRecorder er)

    AbstractServerValidater(ErrorRecorder er) {
        this.er = er
    }

    void doRun(Metadata m, ErrorRecorder er) {
        logger.info("Running validator ${this.getClass().simpleName} with Metadata ${m.toString()}")
        er.challenge("Running validator ${this.getClass().simpleName} with Metadata ${m.toString()}")
        run(m, er)
    }

    void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription
    }

}
