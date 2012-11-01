package gov.nist.toolkit.registrymetadata;

import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

public class MetadataParser {

	public MetadataParser() {
	}

	static public Metadata parseNonSubmission(OMElement e) throws MetadataException, MetadataValidationException {
		return parseNonSubmission(e, false);
	}

	static public Metadata parseNonSubmission(String s) throws MetadataException, MetadataValidationException, XdsInternalException, FactoryConfigurationError {
		return parseNonSubmission(Util.parse_xml(s), false);
	}

	static public Metadata parseNonSubmission(OMElement e, boolean rm_duplicates) throws MetadataException, MetadataValidationException {
		Metadata m = new Metadata();

		m.setGrokMetadata(false);

		if (e != null) {
			m.setMetadata(e);

			m.runParser(rm_duplicates);
		}

		return m;
	}
	
	static public Metadata parseObject(OMElement e) throws MetadataException {
		Metadata m = new Metadata();
		
		m.setGrokMetadata(false);
				
		m.parseObject(e);
		
		return m;
	}


	static public Metadata parseNonSubmission(File metadata_file) throws MetadataException, MetadataValidationException, XdsInternalException {

		return parseNonSubmission(Util.parse_xml(metadata_file));

	}
	
	static public Metadata noParse(OMElement e) {
		Metadata m = new Metadata();

		m.setGrokMetadata(false);

		if (e != null) {
			m.setMetadata(e);

		}
		return m;
	}

	static public Metadata noParse(File metadata_file) throws MetadataException,XdsInternalException  {
		return noParse(Util.parse_xml(metadata_file));
	}
	
	static public Metadata parse(OMElement e)  throws MetadataException,XdsInternalException, MetadataValidationException {
		return new Metadata(e);
	}
}
