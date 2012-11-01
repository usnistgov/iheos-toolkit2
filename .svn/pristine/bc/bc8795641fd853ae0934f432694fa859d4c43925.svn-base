package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

/**
Generic implementation of GetSubmissionSets Stored Query. This class knows how to parse a 
 * GetSubmissionSets Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class GetSubmissionSets extends StoredQuery {
	
	/**
	 * Method required in subclasses (implementation specific class) to define specific
	 * linkage to local database
	 * @return matching metadata
	 * @throws MetadataException
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract protected Metadata runImplementation() throws MetadataException, XdsException, LoggerException;
	
	private final static Logger logger = Logger.getLogger(GetSubmissionSets.class);

	/**
	 * Basic constructor
	 * @param sqs
	 * @throws MetadataValidationException
	 */
	public GetSubmissionSets(StoredQuerySupport sqs) {
		super(sqs);
	}
	
	public void validateParameters() throws MetadataValidationException {
		//                    param name,                 required?, multiple?, is string?,   is code?,  AND/OR ok?,   alternative
		sqs.validate_parm("$uuid",                            true,      true,     true,      false,      false,      (String[])null												);

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);
	}

	
	boolean isRpBroken(OMElement ele) throws MetadataException, MetadataValidationException {
		Metadata m = MetadataParser.parseNonSubmission(ele);
		logger.fatal("rpBroken: " + m.structure());
		return m.isRegistryPackageClassificationBroken();
	}

	boolean isRpBroken2(OMElement ele) throws MetadataException, MetadataValidationException {
		Metadata m = new Metadata();
		m.addMetadata(ele, false);
		logger.fatal("rpBroken2: " + m.structure());
		return m.isRegistryPackageClassificationBroken();
	}
	
	public List<String> uuids;

	void parseParameters() throws XdsInternalException, XdsException, LoggerException {
		uuids = sqs.params.getListParm("$uuid");
	}

	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsInternalException
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata runSpecific() throws XdsException, LoggerException {

		validateParameters();
		parseParameters();

		if ((uuids == null || uuids.size() == 0) 
				) 
			throw new XdsInternalException("GetSubmissionSets Stored Query: $uuid" 
					+ " must be specified");
		return runImplementation();
	}


}
