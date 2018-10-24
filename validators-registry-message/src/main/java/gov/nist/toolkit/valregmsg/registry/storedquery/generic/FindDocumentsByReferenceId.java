package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.List;

abstract public class FindDocumentsByReferenceId extends StoredQuery {
    FindDocuments fd;
    protected List<String> referenceIdList;

    abstract protected Metadata runImplementation() throws XdsException;

    class FindDocumentsSupport extends FindDocuments {
        public FindDocumentsSupport(StoredQuerySupport sqs) {
            super(sqs);
        }

        @Override
        protected Metadata runImplementation() throws XdsException {
            return null;
        }
    }

    public FindDocumentsByReferenceId(StoredQuerySupport storedQuerySupport) {
        super(storedQuerySupport);
    }

    public void setFd(FindDocuments fd) {
        this.fd = fd;
    }

    @Override
    public Metadata runSpecific() throws XdsException, LoggerException, XDSRegistryOutOfResourcesException {
        parseParameters();
        Metadata m = runImplementation();

        if (sqs.log_message != null)
            sqs.log_message.addOtherParam("Results structure", m.structure());

        return m;
    }

    protected void parseParameters() throws XdsException {
        fd.parseParameters();

        referenceIdList                   = sqs.params.getListParm("$XDSDocumentEntryReferenceIdList");

    }

    @Override
    public void validateParameters() throws MetadataValidationException {
        fd.validateParameters();
        sqs.validate_parm("$XDSDocumentEntryReferenceIdList", true, true, true, false, false, (String[])null);

        if (sqs.has_validation_errors)
            throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);

    }
}
