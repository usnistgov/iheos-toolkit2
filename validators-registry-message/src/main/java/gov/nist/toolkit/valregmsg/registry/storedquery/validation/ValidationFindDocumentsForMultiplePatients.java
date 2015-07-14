package gov.nist.toolkit.valregmsg.registry.storedquery.validation;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.FindDocumentsForMultiplePatients;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsException;

public class ValidationFindDocumentsForMultiplePatients extends
		FindDocumentsForMultiplePatients {

	public ValidationFindDocumentsForMultiplePatients(StoredQuerySupport sqs)
			throws MetadataValidationException {
		super(sqs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

}
