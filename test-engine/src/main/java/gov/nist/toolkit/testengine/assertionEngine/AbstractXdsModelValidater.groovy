package gov.nist.toolkit.testengine.assertionEngine

import gov.nist.toolkit.testengine.engine.AbstractValidater

abstract class AbstractXdsModelValidater extends AbstractValidater {

    abstract void validate(XdsModel model, XdsModelValidationResult result)
    abstract String getDescription()
    abstract int getNumberObjectsProcessed()
}
