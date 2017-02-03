package gov.nist.toolkit.valregmsg.registry.storedquery.validation;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetAll;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;

/**
 * Created by bill on 8/31/15.
 */
public class ValidationGetAll extends GetAll {
    public ValidationGetAll(StoredQuerySupport sqs)
            throws MetadataValidationException {
        super(sqs);
    }

    @Override
    protected Metadata runImplementation() throws MetadataException,
            XdsException, LoggerException {
        return null;
    }
}
