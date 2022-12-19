package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.docref.MetadataTables;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequest;
import gov.nist.toolkit.registrymsg.repository.RetrieveItemRequestModel;
import gov.nist.toolkit.registrymsg.repository.RetrieveRequestModel;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimDbEvent;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.SqParams;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.logging.Logger;

import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.valregmsg.message.RegistryResponseValidator;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.factories.TextErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valregmetadata.coding.Code;
import org.apache.axiom.om.OMException;
import org.w3c.dom.Document;


import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

public class Validator {
	Metadata m;
	AdhocQueryRequest request;
	SqParams storedQueryParams;
	RetrieveRequestModel retrieveRequestModel;
	StringBuffer errs = new StringBuffer();
	boolean error = false;
	OMElement test_assertions;
	ArrayList<OMElement> use_id = new ArrayList<OMElement>();
	private final static Logger logger = Logger.getLogger(Validator.class.getName());

	TestConfig testConfig = null;

	private enum DocumentEntryFilter {
		MUST_ONLY_INCLUDE,
		INCLUDE,
		EXCLUDE
	}

	public Validator() {
	}

	public Validator(File test_assertion_file, String subset_name) throws XdsInternalException {
		test_assertions = Util.parse_xml(test_assertion_file);
		if (subset_name != null) {
			test_assertions = XmlUtil.firstChildWithLocalName(test_assertions, subset_name);
			if ( test_assertions == null)
				throw new XdsInternalException("Validator: assertion subset " + subset_name + " not found in file " + test_assertion_file);
		}
	}

	public Validator setTestConfig(TestConfig testConfig) {
		this.testConfig = testConfig;
		return this;
	}

	public Validator setM(Metadata m) {
		this.m = m;
		return this;
	}

	public Validator setRequest(AdhocQueryRequest request) {
		this.request = request;
		return this;
	}

	public Validator setStoredQueryParams(SqParams storedQueryParams) {
		this.storedQueryParams = storedQueryParams;
		return this;
	}

	public Validator setRetrieveRequestModel(RetrieveRequestModel retrieveRequestModel) {
		this.retrieveRequestModel = retrieveRequestModel;
		return this;
	}

	public Validator setTest_assertions(OMElement test_assertions) {
		this.test_assertions = test_assertions;
		return this;
	}

	// return is ArrayList of ArrayLists.  Each internal ArrayList has two elements, The first is testname, the second is status, third
	// is errors
	public List<List<String>> getPatterns() throws MetadataException {
		List<List<String>> patterns = new ArrayList<List<String>>();

		clearError();
		List<String> stat = buildStatus("hasSubmissionSet",hasSubmissionSet());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("ssApproved",ssApproved());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("docRplcDoc",docRplcDoc());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("docsApproved",docsApproved());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("ss1Doc",ss1Doc());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("ss2Doc",ss2Doc());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("sswithOneFol",sswithOneFol());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("sswithOneDocOneFol",sswithOneDocOneFol());
		if (stat != null) patterns.add(stat);

		clearError();
		stat = buildStatus("sswithTwoDocOneFolOneDocInFol", sswithTwoDocOneFolOneDocInFol());
		if (stat != null) patterns.add(stat);

		return patterns;
	}

	List<String> mkList(String s1, String s2, String s3) {
		List<String> al = new ArrayList<String> ();
		al.add(s1);
		al.add(s2);
		al.add(s3);
		return al;
	}

	List<String> buildStatus(String testname, boolean status) {
		if (status) return mkList(testname, "true", null);
		return null;
//		String errs = getErrors();
//		if (errs == null || errs.equals("")) return mkArrayList(testname, "false", null);
//		return mkArrayList(testname, "false", errs);
	}

	void err(String msg) {
		errs.append("\nValidator: ");
		errs.append(msg);
		errs.append('\n');
		error = true;
	}

	void clearError() {
		error = false;
		errs = new StringBuffer();
	}

	public boolean hasError() {
		return error;
	}

	public String getErrors() {
		return errs.toString();
	}

	public boolean hasObjectRefs(int count) throws XdsInternalException {
		if (m.getObjectRefs().size() != count) {
			err("Found " + m.getObjectRefs().size() + " ObjectRefs instead of " + count);
			return false;
		}
		return true;
	}

	public boolean hasSubmissionSet() {
		if (m.getSubmissionSet() == null) {
			err("No Submission Set found");
			return false;
		}
		return true;
	}

	public boolean hasNoSubmissionSet() {
		if (m.getSubmissionSet() != null)  {
			err("Submission Set found");
			return false;
		}
		return true;
	}

	public boolean hasDocuments(int count) {
		if (m.getExtrinsicObjects().size() != count) {
			err("Found " + m.getExtrinsicObjects().size() + " Documents instead of " + count);
			return false;
		}
		return true;
	}

	public boolean hasFolders(int count) {
		if (m.getFolders().size() != count)  {
			err("Found " + m.getFolders().size() + " Folders instead of " + count);
			return false;
		}
		return true;
	}

	public boolean hasSubmissionSets(int count) {
		if (m.getSubmissionSets().size() != count)  {
			err("Found " + m.getSubmissionSets().size() + " SubmissionSets instead of " + count);
			return false;
		}
		return true;
	}

	public boolean hasAssociations(int count) {
		if (m.getAssociations().size() != count) {
			err("Found " + m.getAssociations().size() + " Associations instead of " + count);
			return false;
		}
		return true;
	}

	String att_val(OMElement ele, QName att_name) {
		String val = ele.getAttributeValue(att_name);
		if (val == null)
			val = "";
		return val;
	}

	String properAssocFormatting(String type) {
		return (m.isVersion2()) ?
				MetadataSupport.associationTypeWithoutNamespace(type) :
					MetadataSupport.associationTypeWithNamespace(type);
	}

	public OMElement hasAssociation(OMElement source, OMElement target, String type) throws MetadataException {
		String source_id = att_val(source, MetadataSupport.id_qname);
		String target_id = att_val(target, MetadataSupport.id_qname);
		String type1 = properAssocFormatting(type);

		List<OMElement> asss = m.getAssociations();
		for (int i=0; i<asss.size(); i++) {
			OMElement a = asss.get(i);
			if (  !att_val(a, MetadataSupport.source_object_qname).equals(source_id) )
				continue;
			if (  !att_val(a, MetadataSupport.target_object_qname).equals(target_id) )
				continue;
			if (  !att_val(a, MetadataSupport.association_type_qname).equals(type1) )
				continue;
			return a;
		}
		err(type + " assocation missing between sourceObject " +
				m.getIdentifyingString(source_id) +
				" and targetObject " +
				m.getIdentifyingString(target_id) +
				" expected " + type1);
		return null;
	}

	public OMElement hasAssociationWithOneTarget(OMElement source, List<OMElement> targets, String type) throws MetadataException {
		String source_id = att_val(source, MetadataSupport.id_qname);
		List<String> target_ids = new ArrayList<String>();
		for (OMElement tgt : targets) {
			String id = att_val(tgt, MetadataSupport.id_qname);
			target_ids.add(id);
		}
		String type1 = properAssocFormatting(type);

		List<OMElement> asss = m.getAssociations();
		for (int i=0; i<asss.size(); i++) {
			OMElement a = asss.get(i);
			if (  !att_val(a, MetadataSupport.source_object_qname).equals(source_id) )
				continue; //error

			boolean found_target = false;
			for (String id : target_ids) {
				if (  att_val(a, MetadataSupport.target_object_qname).equals(id) )
					found_target = true;
			}
			if ( !found_target )
				continue; //error

			if (  !att_val(a, MetadataSupport.association_type_qname).equals(type1) )
				continue; // error
			return a;
		}
		err(type + " assocation missing between sourceObject " +
				m.getIdentifyingString(source_id) +
				" and targetObject (one of) " +
				getIdentifyingStrings(target_ids) +
				" expected " + type1);
		return null;
	}

	List<String> getIdentifyingStrings(List<String> target_ids) throws MetadataException {
		List<String> idents = new ArrayList<String>();
		for (String id : target_ids) {
			idents.add(m.getIdentifyingString(id));
		}
		return idents;
	}

	public boolean registryResponseIsValid() throws MetadataException {
	    try {
			ValidationContext vc = new ValidationContext(Installation.instance().getDefaultCodesFile().toString());
		/*
		It is better to use RegistryReponseValidator over RegistryResponseParser because the parser does not check for errors.
		gov.nist.toolkit.registrymsg.registry.RegistryResponseParser rrp = new gov.nist.toolkit.registrymsg.registry.RegistryResponseParser(regresp)
		Constructor already calls rrp.parse()
		rrp.get_registry_response_status()
		 */
			vc.xds_b = true;
			vc.isResponse = true;
			RegistryResponseValidator rrv = new RegistryResponseValidator(vc, m.getMetadata());

			ErrorRecorderBuilder erBuilder = new TextErrorRecorderBuilder();
			TextErrorRecorder er = (TextErrorRecorder) erBuilder.buildNewErrorRecorder();
			MessageValidatorEngine mvc = new MessageValidatorEngine();
			rrv.run(er, mvc);
			if (!"".equals(er.toString().trim())) {
				// Error condition
				err(er.toString());
				return false;
			} else {
				// No structural errors
                // Check for Failure Status
				RegistryResponseParser rrp = new RegistryResponseParser(m.getMetadata());
				if (MetadataSupport.status_failure.equals(rrp.get_registry_response_status())) {
					err("Response status is Failure.");
					return false;
				}
				return true;
			}
		} catch (Exception ex) {
	        // Exception
			err(ex.toString());
			return false;
		}
	}


	public boolean ss1Doc() throws MetadataException {
		this.hasSubmissionSet();
		this.hasDocuments(1);
		this.hasAssociations(1);
		if (this.hasError())
			return false;

		if (this.hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(0), "HasMember") != null)
			return true;
		return false;
	}

	public boolean ss2Doc() throws MetadataException {
		this.hasSubmissionSet();
		this.hasDocuments(2);
		this.hasAssociations(2);
		if (this.hasError())
			return false;

		if ( this.hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(0), "HasMember") != null) return false;
		if ( this.hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(1), "HasMember") != null) return false;
		return true;
	}

	public boolean hasUniqueDocumentRelationshipOfType(String typeParam) throws MetadataException {
		String docUuid = null;
		boolean found = false;

		for (OMElement a : m.getAssociations()) {
			String type = m.getSimpleAssocType(a);
			if (type.equals(typeParam)) {
				found = true;
				if (docUuid == null)
					docUuid = m.getAssocTarget(a);
				else {
					if (docUuid.equals(m.getAssocTarget(a)))
						throw new MetadataException("Multiple "+ type +" associations found for same Document", MetadataTables.Doc_relationships);
				}
			}
		}
		if (!found) {
			err( typeParam + " association not found.");
		}
		return found;
	}

	public boolean hasXfrmRplc() throws MetadataException {
	    return hasUniqueDocumentRelationshipOfType("XFRM_RPLC");
	}

	public boolean hasRplc() throws MetadataException {
	    return hasUniqueDocumentRelationshipOfType("RPLC");
	}

	public boolean hasApnd() throws MetadataException {
		return hasUniqueDocumentRelationshipOfType("APND");
	}

	private boolean hasSnapshotPattern() throws MetadataException {
		List<OMElement> snapshotAssocs = findAssociations("IsSnapshotOf");
		if (snapshotAssocs.size() == 0) {
			err("No IsSnapshotOf Associations found");
			return false;
		}
		if (snapshotAssocs.size() > 1) {
			err("Multiple IsSnapshotOf Associations found");
			return false;
		}
		OMElement snapshotAssoc = snapshotAssocs.get(0);
		String sourceUuid = m.getAssocSource(snapshotAssoc);
		String targetUuid = m.getAssocTarget(snapshotAssoc);
		OMElement sourceObject = m.getExtrinsicObject(sourceUuid);
		if (sourceObject == null) {
			err("SnapShot DocumentEntry (" + sourceUuid + ") is not in query results");
			return false;
		}
		OMElement targetObject = m.getExtrinsicObject(targetUuid);
		if (targetObject == null) {
			err("On Demand DocumentEntry (" + targetUuid + ") is not in query results");
			return false;
		}

		if (!isDocApproved(sourceObject)) {
			err("SnapShot DocumentEntry is Deprecated");
			return false;
		}

		if (!isDocApproved(targetObject)) {
			err("On Demand DocumentEntry is Deprecated");
			return false;
		}

		String targetObjectType = m.getObjectType(targetObject);
		if (!MetadataSupport.on_demand_documententry_objecttype.equals(targetObjectType)) {
			err("On Demand DocumentEntry should have objectType of " + MetadataSupport.on_demand_documententry_objecttype + ", found instead " + MetadataSupport.stable_documententry_objecttype);
			return false;
		}

		String sourceObjectType = m.getObjectType(sourceObject);
		if (!MetadataSupport.stable_documententry_objecttype.equals(sourceObjectType)) {
			err("Stable DocumentEntry should have objectType of " + MetadataSupport.stable_documententry_objecttype + ", found instead " + MetadataSupport.on_demand_documententry_objecttype);
			return false;
		}
		return true;
	}

	private List<OMElement> findAssociations(String type) throws MetadataException {
		List<OMElement> assocs = new ArrayList<>();
		for (OMElement assoc : m.getAssociations()) {
			String theType = m.getSimpleAssocType(assoc);
			if (type.equals(theType))
				assocs.add(assoc);
		}
		return assocs;
	}

	public boolean isDocApproved(OMElement eo) {
		String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
		if ( status == null || !status.equals("Approved")) {
			return false;
		}
		return true;
	}

	public boolean isDocDeprecated(OMElement eo) {
		String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
		if ( status == null || !status.equals("Deprecated")) {
			return false;
		}
		return true;
	}

	public boolean docApproved(OMElement eo) {
		String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
		if ( status == null || !status.equals("Approved")) {
			err("ExtrinsicObject " + eo.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Approved'");
			return false;
		}
		return true;
	}

	public boolean docDeprecated(OMElement eo) {
		String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
		if ( status == null || !status.equals("Deprecated")) {
			err("ExtrinsicObject " + eo.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Deprecated'");
			return false;
		}
		return true;
	}

	public boolean docsApproved() throws MetadataException {
		boolean stat = false;
		for (OMElement eo : m.getExtrinsicObjects()) {
			String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
			if ( status == null || !status.equals("Approved")) {
				err("ExtrinsicObject " + eo.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Approved'");
				return false;
			}
			stat = true;
		}
		return stat;
	}

	public boolean folsApproved() throws MetadataException {
		for (OMElement fol : m.getFolders()) {
			String status = m.stripNamespace(fol.getAttributeValue(MetadataSupport.status_qname));
			if ( status == null || !status.equals("Approved")) {
				err("Folder " + fol.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Approved'");
				return false;
			}
		}
		return true;
	}

	public boolean folsDeprecated() throws MetadataException {
		for (OMElement fol : m.getFolders()) {
			String status = m.stripNamespace(fol.getAttributeValue(MetadataSupport.status_qname));
			if ( status == null || !status.equals("Deprecated")) {
				err("Folder " + fol.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Deprecated'");
				return false;
			}
		}
		return true;
	}

	public boolean ssApproved() throws MetadataException {
		OMElement ss = m.getSubmissionSet();
		if (ss == null) {
			err("No Submission Set");
			return false;
		}
		String status = m.stripNamespace(ss.getAttributeValue(MetadataSupport.status_qname));
		if ( status == null || !status.equals("Approved")) {
			err("SubmissionSet " + ss.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Approved'");
			return false;
		}
		return true;
	}

	public boolean docsDeprecated() throws MetadataException {
		for (OMElement eo : m.getExtrinsicObjects()) {
			String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
			if ( !status.equals("Deprecated")) {
				err("ExtrinsicObject " + eo.getAttributeValue(MetadataSupport.id_qname) + " has status " + status + " instead of 'Deprecated'");
				return false;
			}
		}
		return true;
	}

	public boolean oneDocDeprecated() throws MetadataException {
		int count = 0;
		for (OMElement eo : m.getExtrinsicObjects()) {
			String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
			if ( status.equals("Deprecated"))
				count++;
		}
		if (count == 1)
			return true;
		err("Found " + count + " Deprecated ExtrinsicObjects instead of one");
		return false;
	}

	public boolean oneDocApproved() throws MetadataException {
		int count = 0;
		for (OMElement eo : m.getExtrinsicObjects()) {
			String status = m.stripNamespace(eo.getAttributeValue(MetadataSupport.status_qname));
			if ( status.equals("Approved"))
				count++;
		}
		if (count == 1)
			return true;
		err("Found " + count + " Approved ExtrinsicObjects instead of one");
		return false;
	}

	/*
	    This is the public method that is called to compare the value in a metadata field
	    to an expected value provided by the caller.
	    The argument metadataField is a string of the form:
	       SubmimssionSet.xxx
	       DocumentEntry.xxx
	    where the xxx labels correspond to the metadata field names as documented in ITI TF 3:4.1.3.
	    For example: DocumentEntry.classCode
	 */
	public boolean namedMetadataCompare(String metadataField, String expectedValue) throws MetadataException {
		String submittedValue = extractNamedMetadata(metadataField);
		boolean rtn = true;
		if (!expectedValue.equals(submittedValue)) {
			err("Metadata Content Failure, key: " + metadataField + ", expectedValue: " + expectedValue + ", submittedValue: " + submittedValue);
			rtn = false;
		}
		return rtn;
	}

	public boolean attachmentFileFound(SoapSimulatorTransaction sst) throws MetadataException {
		String uniqueId = extractNamedMetadata("DocumentEntry.uniqueId");

		Path py = pathToAttachment(sst);
		File fx = py.toFile();

		boolean rtn = true;
		if (! fx.exists()) {
			err("Document Content Failure: Document Unique ID: " + uniqueId + ", Did not find expected file in simulator log: " + fx.getAbsolutePath());
			err(" This means that the simulator did not process the DocumentEntry successfully. The simulator software did not find/understand the Document Unique ID, was not able to extract the attachment, or was not able to write the attachment in the path listed.");
			rtn = false;
		}
		return rtn;
	}

	public boolean attachmentIsParsableXML(SoapSimulatorTransaction sst) throws MetadataException {
		String uniqueId = extractNamedMetadata("DocumentEntry.uniqueId");
		File fx = pathToAttachment(sst).toFile();

		boolean rtn = true;
		if (! fx.exists()) {
			err("Document Content Failure: Document Unique ID: " + uniqueId + ", Did not find expected file in simulator log: " + fx.getAbsolutePath());
			err(" This means that the simulator did not process the DocumentEntry successfully. The simulator software did not find/understand the Document Unique ID, was not able to extract the attachment, or was not able to write the attachment in the path listed.");
			rtn = false;
		} else {
			DocumentBuilderFactory dbFactory;
			DocumentBuilder dBuilder;
			Document doc;
			try {
				// Try to parse the document. If the parser throws an exception, we know the document is not well formed.
				dbFactory = DocumentBuilderFactory.newInstance();
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(fx);

				// You would uncomment for debugging. No reason to do this in production.
//				String x = doc.toString();

			} catch (Exception e) {
				err("Document Content Failure: Document Unique ID: " + uniqueId + ", XML parser was not able to parse file in simulator log: " + fx.getAbsolutePath());
				err("This exception was thrown when parsing the file: " + e.getMessage());
				rtn = false;
			} finally {
				dbFactory = null;
				dBuilder = null;
				doc = null;
			}
		}
		return rtn;
	}

	public boolean namedXMLDocumentCompare(SoapSimulatorTransaction sst, String documentField, String expectedValue) throws MetadataException{
		String submittedValue = extractedNamedDocumentValue_xml(sst, documentField);
		boolean rtn = true;
		if (!expectedValue.equals(submittedValue)) {
			err("Document Content Failure, key: " + documentField + ", expectedValue: " + expectedValue + ", submittedValue: " + submittedValue);
			rtn = false;
		}
		return rtn;
	}

	private String extractedNamedDocumentValue_xml(SoapSimulatorTransaction sst, String documentField) throws MetadataException{
		String rtn = null;

		OMElement documentRoot = safeXMLParse(pathToAttachment(sst));
		if (documentRoot == null) {
			err("Was not able to parse this document. That should have been tested previously with a different assertion.");
			return rtn;
		}
		String elementName = null;
		switch (documentField) {
			case "Header.typeId":
				elementName="typeId";
				break;
			default:
				err("Did not recognize this field to extract from an XML document: " + documentField);
				break;
		}
		if (elementName != null) {
			Iterator<OMElement> iterator = documentRoot.getChildrenWithLocalName(elementName);
			try {
				// Because of the parser we are using, there can be instances where it finds <templateId> elements
				// even in a poorly formatted XML document. In the while loop below, do not bail out if we find
				// a templateId that matches. This forces the iterator over the entire document. If something did not
				// parse properly, this will pick it up.
				while (iterator.hasNext()) {
					OMElement e = iterator.next();
					rtn = e.getAttributeValue(new QName("root"));
				}
			} catch (Exception e) {
				// A prior comparison might have found a match. Override here to make sure we return false.
				// TODO: There is another edge condition to cover, but that should really not happen.
				// A prior step in the test plan really should have determined if the document was legal XML.
				err("Was not able to iterate through elements with LocalName " + elementName + " and grab attribute 'root'. This could be a coding issue or some wrong with the document under review.");
				err(e.getMessage());
				rtn = null;
			}
		}


		return rtn;
	}


	public boolean containsTemplateId(SoapSimulatorTransaction sst, String templateId) throws MetadataException {
		OMElement documentRoot = safeXMLParse(pathToAttachment(sst));

		boolean rtn = false;	// Assume failure because of the logic below
		if (documentRoot == null) {
			err("Was not able to parse this document. That should have been tested previously with a different assertion.");
		} else {
			Iterator<OMElement> iterator = documentRoot.getChildrenWithLocalName("templateId");
			try {
				// Because of the parser we are using, there can be instances where it finds <templateId> elements
				// even in a poorly formatted XML document. In the while loop below, do not bail out if we find
				// a templateId that matches. This forces the iterator over the entire document. If something did not
				// parse properly, this will pick it up.
				while (iterator.hasNext()) {
					OMElement e = iterator.next();
					String templateIdInDocument = e.getAttributeValue(new QName("root"));
					if (templateId.equals(templateIdInDocument)) {
						rtn = true;
					}
				}
			} catch (Exception e) {
				// A prior comparison might have found a match. Override here to make sure we return false.
				// TODO: There is another edge condition to cover, but that should really not happen.
				// A prior step in the test plan really should have determined if the document was legal XML.
				err("Was not able to iterate through elements with LocalName 'templateId' and grab attribute 'root'. This could be a coding issue or some wrong with the document under review.");
				err(e.getMessage());
				rtn = false;
			}
			if (!rtn) {
				err("Did not find any (templateId root) attributes that match expected value: " + templateId);
			}
		}

		return rtn;
	}

	private OMElement safeXMLParse(Path path) {
		OMElement documentRoot = null;

		try {
			documentRoot = Util.parse_xml(path.toFile());
		} catch (Exception e) {
			documentRoot = null;
		} finally {
		}
		return documentRoot;
	}

	private Path pathToAttachment(SoapSimulatorTransaction sst) throws MetadataException {
		Path path = sst.getSimDbEvent().getRequestBodyFile().getParentFile().toPath();
		String uniqueId = extractNamedMetadata("DocumentEntry.uniqueId");
		String expectedAttachmentFileName = oidToFilename(uniqueId) + ".bin";

		return path.resolve("Repository").resolve(expectedAttachmentFileName);
	}

	private String oidToFilename(String oid) {
		return oid.replaceAll("\\.", "_");
	}

	/*
	    This is the public method that is called to compare the CODED value in a metadata field
	    to an expected value provided by the caller.
	    The argument metadataField is a string of the form:
	       SubmimssionSet.xxx
	       DocumentEntry.xxx
	    where the xxx labels correspond to the metadata field names as documented in ITI TF 3:4.1.3.
	    For example: DocumentEntry.classCode
	 */
	public boolean namedMetadataCompareCode(String metadataField, String expectedCodeValue,
										String expectedCodingScheme,
										String expectedCodeDisplayName) throws MetadataException {
		Code expectedCode = new Code(expectedCodeValue, expectedCodingScheme, expectedCodeDisplayName);
		Code submittedCode = extractNamedMetadataCoded(metadataField);

		boolean rtn = expectedCode.equals(submittedCode);
		if (!rtn) {
			err("Metadata Content Failure / Single Coded Value, key: " + metadataField +
					", expected/submitted code: " + expectedCodeValue + "/" + submittedCode.getCode() +
					", expected/submitted scheme: " + expectedCodingScheme + "/" + submittedCode.getScheme());
		}
		return rtn;
	}

	/*
	    Similar to namedMetadataCompareCode.
	    This method accepts one coded value from the caller and returns true if any of the items
	    in the list of codes in the metadata field match the code supplied by the caller.
	    For example, DocumentEntry.eventCodeList might contain 3 coded entries. Call this method
	    to determine if coded item A is found in any of those 3 coded entries.
	 */
	public boolean namedMetadataContainsCode(String metadataField, String expectedCodeValue,
											String expectedCodingScheme,
											String expectedCodeDisplayName) throws MetadataException {
		Code expectedCode = new Code(expectedCodeValue, expectedCodingScheme, expectedCodeDisplayName);
		List<Code> submittedCodeList = extractNamedMetadataCodedList(metadataField);

		boolean rtn = false;
		for (Code submittedCode: submittedCodeList) {
			if (expectedCode.equals(submittedCode)) {
				rtn = true;
				break;
			}
		}

		if (!rtn) {
			err("Metadata Content Failure / Contains Coded Value, key: " + metadataField +
					", expected/submitted code: " + expectedCodeValue + "/" +
					", expected/submitted scheme: " + expectedCodingScheme + "/");
		}
		return rtn;
	}

	public boolean namedMetadataCodeFromValueSet(String metadataField, String environment, String valueSetOID) throws MetadataException {
		boolean rtn = false;
		Code submittedCode = extractNamedMetadataCoded(metadataField);

		Installation i = Installation.instance();
		File f = i.environmentFile(environment);
		Path p = Paths.get(f.getAbsolutePath(), "value_sets", valueSetOID + ".txt");
		f = p.toFile();
		if (! f.exists()) {
			err("The value set file does not exist in environment: " +
					environment +
					". and Value Set OID: " +
					valueSetOID);
		} else {
			List<String> valueSetList = readTextFile(p);
			Iterator<String> it = valueSetList.iterator();
			while (it.hasNext() && !rtn) {
				String[] tokens = it.next().split("\t");
				if (tokens.length >= 3) {
					Code code = new Code(tokens[0], tokens[1], tokens[2]);
					if (submittedCode.equals(code)) {
						rtn = true;
					}
				}
			}
			if (!rtn) {
				err("Did not find the submitted coded value (" +
						submittedCode.toString() +
						") in value set: " + valueSetOID +
						". Value set contains " +
						valueSetList.size() +
						" item(s).");
			}

		}


		return rtn;
	}

	private List<String> readTextFile(Path path) {
		List<String> rtn = new ArrayList<>();

		try (Stream<String> stream = Files.lines(path)) {
			rtn = stream
					.filter(line -> !line.startsWith("#"))
					.collect(Collectors.toList());
		} catch (IOException e) {
		}
		return rtn;
	}

	private String extractNamedMetadata(String metadataField) throws MetadataException {
		String rtn = "";
		OMElement oe;
		HashMap<String, OMElement> map;
		switch(metadataField) {
			case "SubmissionSet.patientId":
				rtn = m.getSubmissionSetPatientId();
				break;
			case "SubmissionSet.sourceId":
				OMElement e = m.getSubmissionSet();
				rtn = m.getSourceIdValue(e);
				break;
			case "DocumentEntry.mimeType":
				oe = getSingleDocumentEntry();
				rtn = m.getMimeType(oe);
				break;
			case "DocumentEntry.homeCommunityId":
				oe = getSingleDocumentEntry();
				rtn = m.getHome(oe);
				break;
			case "DocumentEntry.objectType":
				oe = getSingleDocumentEntry();
				rtn = m.getObjectType(oe);
				break;
			case "DocumentEntry.patientId":
				oe = getSingleDocumentEntry();
				rtn = m.getPatientId(oe);
				break;
			case "DocumentEntry.uniqueId":
				oe = getSingleDocumentEntry();
				rtn = m.getUniqueIdValue(oe);
				break;
			// RequestSlotList.homeCommunityId is not in the traditional metadata area
			// We have to pull from a different spot
			case "RequestSlotList.homeCommunityId":
				oe = m.getSubmitObjectsRequestRequestSlotList();
				rtn = extractSingleSlotValue(oe, "homeCommunityId");
				break;
			// Cases below are taken from Stored Queries
			case "AdhocQuery.DocumentEntry.patientId":
				rtn = request.getPatientId();
				break;

			default:
				rtn = "Validator::extractNamedMetadata does not understand: " + metadataField;
				break;
		}
		return rtn;
	}



	private OMElement getSingleDocumentEntry() throws MetadataException {
		HashMap<String, OMElement> map = m.getDocumentUidMap();
		String uid = (String) map.keySet().toArray()[0];
		OMElement oe = map.get(uid);
		return oe;
	}

	private Code extractNamedMetadataCoded(String metadataField) throws MetadataException {
		Code code = new Code("","","");
		OMElement e;
		switch(metadataField) {
			case "SubmissionSet.contentTypeCode":
				e = m.getSubmissionSet();
				code = extractNamedMetadataCoded(e, "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500");
				break;
			case "DocumentEntry.classCode":
				e = getSingleDocumentEntry();
				code = extractNamedMetadataCoded(e, "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a");
				break;
			case "DocumentEntry.formatCode":
				e = getSingleDocumentEntry();
				code = extractNamedMetadataCoded(e, "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d");
				break;
			case "DocumentEntry.typeCode":
				e = getSingleDocumentEntry();
				code = extractNamedMetadataCoded(e, "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983");
				break;
			default:
				break;
		}
		return code;
	}


	private List<Code> extractNamedMetadataCodedList(String metadataField) throws MetadataException {
		List<Code> codeList = new ArrayList<>();
		OMElement e;
		switch(metadataField) {
			case "DocumentEntry.eventCodeList":
				e = getSingleDocumentEntry();
				codeList = extractNamedMetadataCodedList(e, "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4");
				break;
			default:
				break;
		}
		return codeList;
	}

	private Code extractNamedMetadataCoded(OMElement e, String classificationScheme) throws MetadataException {
        Code code = new Code("", "", "");
		List<OMElement> omElements = m.findClassifications(e, classificationScheme);
		int x = omElements.size();
		OMElement element = omElements.get(0);
		String cValue        = m.getClassificationValue(element);
		String cCodingScheme = m.getClassificationScheme(element);
		String cDisplayName  = m.getNameValue(element);
		code = new Code (cValue, cCodingScheme, cDisplayName);
		return code;
	}

	private List<Code> extractNamedMetadataCodedList(OMElement e, String classificationScheme) throws MetadataException {
		List<Code> codeList = new ArrayList<>();
		List<OMElement> omElements = m.findClassifications(e, classificationScheme);
		int x = omElements.size();
		for (int i = 0; i < omElements.size(); i++) {
			OMElement element = omElements.get(i);
			Code code = new Code(
				m.getClassificationValue(element),
				m.getClassificationScheme(element),
				m.getNameValue(element));
			codeList.add(code);
		}
		return codeList;
	}

	/*
        extractSingleSlotValue
        Extracts and returns all Slot values in a SlotList as a single string
        This is used when the caller is expecting a single Slot in the list.
        If there are multiple slots, the method will concatenate the values.
        We could throw an exception or return just the first value, but this
        will get the attention of someone reviewing validation logs.
	 */
	private String extractSingleSlotValue(OMElement e, String slotName) throws MetadataException{
		String rtn = "";
		String delimiter = "";

		List<String> stringList = extractSlotValues(e, slotName);
		for (String x: stringList) {
			rtn += x + delimiter;
			delimiter = ":";
		}
		return rtn;
	}

	/*
        extractSlotValues
        Extracts and returns a list of all Slot values found in a SlotList.
	 */
	private List<String> extractSlotValues(OMElement e, String slotName) throws MetadataException {
		List<String> rtn = new ArrayList<>();

		if (e != null) {
			Iterator<OMElement> iterator = e.getChildElements();
			while (iterator.hasNext()) {
				OMElement slot = iterator.next();
				String name = slot.getAttributeValue(new QName("name"));
				if ((!(name == null)) && name.equals(slotName)) {
					OMElement valueList = slot.getFirstElement();
					Iterator<OMElement> itValues = valueList.getChildElements();
					while (itValues.hasNext()) {
						OMElement value = itValues.next();
						String x = value.getText();
						rtn.add(x);
					}
				}
			}
		}
		return rtn;
	}

	/*
    This is the public method that is called to compare the value in a field in an adhoc query.
    to an expected value provided by the caller.
    The argument field is a string of the form:
       AdhocQuery.SubmimssionSet.xxx
       AdhocQuery.DocumentEntry.xxx
    where the xxx labels correspond to ..
    TODO fix above
    For example: AdhocQuery.DocumentEntry.xx
 */
	public boolean namedFieldCompare(String field, String expectedValue) throws MetadataException {
		String submittedValue = extractNamedField(field);
		boolean rtn = true;
		if (!expectedValue.equals(submittedValue)) {
			err("Metadata Content Failure, key: " + field + ", expectedValue: " + expectedValue + ", submittedValue: " + submittedValue);
			rtn = false;
		}
		return rtn;
	}

	public boolean namedFieldContains(String field, String expectedValue) throws XdsInternalException, MetadataException {
		boolean rtn = true;
		List<String> stringList = extractNamedFieldAsList(field);
		if (stringList != null) {
			Set<String> set = new HashSet<>(stringList);
			if (!set.contains(expectedValue)) {
				err("Named Field Failure for field: " + field + ". Did not find " + expectedValue + " in list of values.");
				rtn = false;
			}
		} else {
			err("Named Field Failure for field: " + field + ". There was no value for this field/key.");
			rtn = false;
		}

		return rtn;
	}

	private List<String> extractNamedFieldAsList(String field) throws XdsInternalException, MetadataException {
		List<String> rtn = null;
		switch(field) {
			case "AdhocQuery.DocumentEntry.objectType":
				rtn = storedQueryParams.getListParm("$XDSDocumentEntryType");
				break;
			case "AdhocQuery.DocumentEntry.availabilityStatus":
				rtn = storedQueryParams.getListParm("$XDSDocumentEntryStatus");
				break;
		}
		return rtn;
	}

	private String extractNamedField(String field) throws MetadataException {
		String rtn = "";
		List<RetrieveItemRequestModel> models;
		switch(field) {
			case "AdhocQuery.DocumentEntry.patientId":
				rtn = request.getPatientId();
				break;
			case "AdhocQuery.returnType":
				//TODO fix how we get the ResponseOption element
				OMElement x = request.getAdhocQueryRequestElement();
				OMElement y = x.getFirstElement();
//				OMElement z = x.getFirstChildWithName(new QName("{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}ResponseOption"));
//				OMElement e = request.getAdhocQueryRequestElement().getFirstChildWithName(new QName("ResponseOption"));
				rtn = y.getAttributeValue(new QName("returnType"));
				break;
				//TODO this was moved to the list section
			case "AdhocQuery.DocumentEntry.objectType":
				rtn = firstValue(request.getDocumentEntryObjectTypeList());
				break;
			case "XCR.homeCommunityId":
				models = retrieveRequestModel.getModels();
				rtn = models.get(0).getHomeId();
				break;
			case "XCR.repositoryUniqueId":
				models = retrieveRequestModel.getModels();
				rtn = models.get(0).getRepositoryId();
				break;
			case "XCR.documentId":
				models = retrieveRequestModel.getModels();
				rtn = models.get(0).getDocumentId();
				break;
			default:
				break;
		}
		return rtn;
	}

	private String firstValue(List<String> valueList) {
		String rtn = "";
		if (valueList != null && valueList.size() > 0) {
			rtn = valueList.get(0);
		}
		return rtn;
	}



	public void run_test_assertions(OMElement xml, OMElement instruction_output)  throws MetadataException, XdsInternalException, MetadataValidationException {
		Metadata m = new Metadata(xml);

		setM(m);
		run_test_assertions(instruction_output);
	}


	public void run_test_assertions(OMElement instruction_output) throws MetadataException, XdsInternalException {
		for (Iterator it=test_assertions.getChildElements(); it.hasNext(); ) {
			OMElement ec = (OMElement) it.next();
			String ec_name = ec.getLocalName();
			int count = -1; // Count=-1 means Not Used
			String countAttrValStr = ec.getAttributeValue(new QName("count"));
			if (countAttrValStr!=null) {
				count = Integer.parseInt(countAttrValStr);
			}
			if (ec_name.equals("DocumentEntries")) {
				if (testConfig == null) {
					throw new IllegalArgumentException("testConfig is null!");
				} else if (instruction_output == null) {
					throw new IllegalArgumentException("instruction_output is null!");
				}
				verifyDocumentEntries(m, ec, instruction_output);
			} else{
				run_test_assertion(ec_name, count);
			}
		}
	}



	public void run_test_assertion(String ec_name, int count) throws MetadataException, XdsInternalException  {
		if (ec_name.equals("SSwithOneDoc")) {
			ss1Doc();
			ssApproved();
		} else if (ec_name.equals("SSwithOneDocOnly")) {
			ss1Doc();
			// Cannot call ssApproved since we could be asserting a Submission to a Supporting Sim without a SubmissionSet status
		} else if (ec_name.equals("SSwithTwoDocOnly")) {
			ss2Doc();
			// Cannot call ssApproved since we could be asserting a Submission to a Supporting Sim without a SubmissionSet status
		} else if (ec_name.equals("RegistryResponseIsValid")) {
		   registryResponseIsValid();
		} else if (ec_name.equals("NoSubmissionSet")) {
			hasNoSubmissionSet();
		}
		else if (ec_name.equals("NoDocument")) {
			hasDocuments(0);
		}
		else if (ec_name.equals("SSApproved")) {
			ssApproved();
		}
		else if (ec_name.equals("DocDep")) {
			docsDeprecated();
		}
		else if (ec_name.equals("DocApp")) {
			docsApproved();
		}
		else if (ec_name.equals("HasRPLC")) {
			hasRplc();
		}
		else if (ec_name.equals("HasXFRM_RPLC")) {
		    hasXfrmRplc();
		}
		else if (ec_name.equals("HasAPND")) {
		    hasApnd();
		}
		else if (ec_name.equals("DocRplcDoc")) {
			docRplcDoc();
		}
		else if (ec_name.equals("OneDocDep")) {
			oneDocDeprecated();
		}
		else if (ec_name.equals("OneDocApp")) {
			oneDocApproved();
		}
		else if (ec_name.equals("FolApp")) {
			folsApproved();
		}
		else if (ec_name.equals("SSwithTwoDoc")) {
			ss2Doc();
			ssApproved();
		}
		else if (ec_name.equals("SSwithOneDocOneFol")) {
			sswithOneDocOneFol();
		}
		else if (ec_name.equals("SSwithTwoDocOneFol")) {
			sswithTwoDocOneFol();
		}
		else if (ec_name.equals("SSwithTwoDocOneFolOneDocInFol")) {
			sswithTwoDocOneFolOneDocInFol();
		}
		else if (ec_name.equals("SSwithOneFol")) {
			sswithOneFol();
		}
		else if (ec_name.equals("None")) {
			hasNoSubmissionSet();
			hasDocuments(0);
			hasFolders(0);
			if (hasError())
				return;
			hasAssociations(0);
		}
		else if (ec_name.equals("ObjectRefs")) {
			hasObjectRefs(count);
			hasNoSubmissionSet();
			hasDocuments(0);
			hasFolders(0);
			hasAssociations(0);
		}
		else if (ec_name.equals("Documents")) {
			hasDocuments(count);
		}
		else if (ec_name.equals("Folders")) {
			hasFolders(count);
		}
		else if (ec_name.equals("SubmissionSets")) {
			hasSubmissionSets(count);
		}
		else if (ec_name.equals("Associations")) {
			hasAssociations(count);
		}
		else if (ec_name.equals("HasSnapshotPattern")) {
			hasSnapshotPattern();
		}
		else if (ec_name.equals("FolDep")) {
			folsDeprecated();
		}
		else {
			throw new XdsInternalException("QueryTransaction: validate_expected_contents(): don't understand verification request " + ec_name);
		}

	}

	public boolean verifyDocumentEntries(Metadata m, OMElement ec, OMElement instruction_output) throws XdsInternalException, MetadataException {
		for (Iterator selectiveIt = ec.getChildElements(); selectiveIt.hasNext(); ) {
			OMElement selectionPart = (OMElement) selectiveIt.next();
			String selectionLocalName = selectionPart.getLocalName();

			if ("MustOnlyInclude".equals(selectionLocalName)) {
				verifySubmittedEOIdInRegistryResponse(DocumentEntryFilter.MUST_ONLY_INCLUDE, m, selectionPart, instruction_output);
			} else if ("Include".equals(selectionLocalName)) {
				verifySubmittedEOIdInRegistryResponse(DocumentEntryFilter.INCLUDE, m, selectionPart, instruction_output);
			} else if ("Exclude".equals(selectionLocalName)) {
				verifySubmittedEOIdInRegistryResponse(DocumentEntryFilter.EXCLUDE,m,selectionPart, instruction_output);
			} else if ("DocumentEntryType".equals(selectionLocalName)) { // Looks to see if all EOs in the metadata collection are of this type. The EO Id matching is not used in this case.
				verifyAllEntriesByAttribute("objectType", m, selectionPart);
			}
		}
		return true;
	}

	private boolean verifyAllEntriesByAttribute(String attributeName, Metadata m, OMElement ec) throws XdsInternalException, MetadataException {
		String expectedValue = ec.getText();

		if (expectedValue==null || "".equals(expectedValue)) {
			err("The expected value cannot be null for: " + attributeName);
			return false;
		}
		for (OMElement eo : m.getExtrinsicObjects()) {
			String attributeValue  = eo.getAttributeValue(new QName(attributeName));
			if (!expectedValue.equals(attributeValue)) {
				String eoIdValue  = eo.getAttributeValue(new QName("id"));
				err("Metadata " + attributeName + "=" + attributeValue + " does not match the expected value: " + expectedValue + ". ExtrinsicObject Id=" + eoIdValue);
				return false;
			}
		}
		return true;
	}
	/**
	 *
	 * @param def Verify whether an EO should be included (True) or excluded (False) in the registry response
	 * @param m
	 * @param ec
	 * @return
	 * @throws XdsInternalException
	 * @throws MetadataException
	 */
	private boolean verifySubmittedEOIdInRegistryResponse(DocumentEntryFilter def, Metadata m, OMElement ec, OMElement instruction_output) throws XdsInternalException, MetadataException {
		if (instruction_output==null) {
			throw new IllegalArgumentException("OMElement instruction_output cannot be null.");
		}
		int counter = 0;
		for (Iterator deIt = ec.getChildElements(); deIt.hasNext(); ) {

            OMElement dePart = (OMElement) deIt.next();
            String dePartLocalName = dePart.getLocalName();

            if ("DocumentEntry".equals(dePartLocalName)) {
				OMElement eoInResponse = null;
                for (Iterator dePartChildElementsIt = dePart.getChildElements(); dePartChildElementsIt.hasNext(); ) {

                    OMElement dePartInstruction = (OMElement) dePartChildElementsIt.next();
                    String instructionLocalName = dePartInstruction.getLocalName();

                    if ("UseId".equals(instructionLocalName)) {

                        use_id.add(dePartInstruction);

                        Linkage l = new Linkage(testConfig, instruction_output, m, use_id);
                        HashMap<String, String> myMap = l.compile();

                        if (myMap!=null) {
                        	for (String key : myMap.keySet()) {
								String submittedIdValue = myMap.get(key);

                            if (submittedIdValue==null || "".equals(submittedIdValue)) {
								err("UseId ExtrinsicObject " + " Id value cannot be null.");
								return false;
                            }

                            boolean found = false;
                            for (OMElement eo : m.getExtrinsicObjects()) {
                                String eoIdValue  = eo.getAttributeValue(new QName("id")); // UUID should be all lower cased. See Vol 3. 4.2.3.1.5.
                                if (eoIdValue.equals(submittedIdValue)) {
									found = true;
									eoInResponse = eo;
									counter++;
								}
                            }
                            if ((DocumentEntryFilter.MUST_ONLY_INCLUDE.equals(def) || DocumentEntryFilter.INCLUDE.equals(def))
									&& !found) {
								err("The submitted id ["+ submittedIdValue +"] was not found in the registry response.");
								return false;
                            } else if (DocumentEntryFilter.EXCLUDE.equals(def) && found) {
								err("This id ["+ submittedIdValue +"] is not supposed to included in the registry response but it was found.");
								return false;
							}
                            if (found)
                            	return true;
							}
                        } else {
							err("Missing UseId: This instruction is required to extract the Document Entry UUID.");
						}
                    } else if ("DocumentEntryType".equals(instructionLocalName)) {
						String documentEntryType = dePartInstruction.getText();
						if (eoInResponse!=null) {
							String objectType = eoInResponse.getAttributeValue(new QName("objectType"));
							if (documentEntryType==null || "".equals(documentEntryType)) {
								err("DocumentEntryType does not have a value in the testplan.");
								return false;
							} else {
								if (!documentEntryType.equals(objectType)) {
									err("Expected objectType ["+ documentEntryType +"] does not match the DocumentEntry objectType ["+ objectType +"] in the response.");
									return false;
								}
							}
						} else {
							err("eoInResponse is null!");
							return false;
						}
                    }
                }
            }
        }

		if (DocumentEntryFilter.MUST_ONLY_INCLUDE.equals(def)) {
			// Make sure that the requested Ids are all that were found in the registry metadata and nothing else.
			int regEOSize = m.getExtrinsicObjects().size();
			if (counter != regEOSize) {
				err("Matched ExtrinsicObject (EO) Ids size ["+ counter +"] does not match with the Registry EO size ["+ regEOSize +"] in the response.");
				return false;
			}
		}

		return true;
	}

	// NOTE: This method doesn't seem to be used except for document generation references
	public boolean docRplcDoc() throws MetadataException {
		hasDocuments(2);
		hasAssociations(1); // According to TF 3 Figure 4.2.2.2.3-2 : document replace, Should be 2.
		if (isDocApproved(m.getExtrinsicObject(0))) { // Does this assume the DocumentEntry is always ordered this way?
			hasAssociation(m.getExtrinsicObject(0), m.getExtrinsicObject(1), "RPLC");
			docDeprecated(m.getExtrinsicObject(1));
		} else {
			hasAssociation(m.getExtrinsicObject(1), m.getExtrinsicObject(0), "RPLC");
			docDeprecated(m.getExtrinsicObject(0));
		}
		return !hasError();
	}

	public boolean sswithOneFol() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(0);
		hasFolders(1);
		if ( !hasError()) {
			hasAssociations(1);
			hasAssociation(m.getSubmissionSet(), m.getFolder(0), "HasMember");
		}
		return !hasError();
	}

	protected List<?> clone(List<?> lst) {
		List<Object> lst2 = new ArrayList<Object>();

		for (int i=0; i<lst.size(); i++)
			lst2.add(lst.get(i));

		return lst2;
	}

	public boolean addDocToExistingFolder() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(1);
		hasFolders(0);
		if ( !hasError()) {
			hasAssociations(3);
			OMElement ssA = hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(0), "HasMember");
			String ssId = m.getId(m.getSubmissionSet());

			List<OMElement> otherA = (List<OMElement>) clone(m.getAssociations());
			otherA.remove(ssA);

			OMElement doc = m.getExtrinsicObject(0);
			String docId = m.getId(doc);
			OMElement assocA = otherA.get(0);
			OMElement assocB = otherA.get(1);

			OMElement ssAssocAssoc = null;
			OMElement folDocAssoc = null;

			if (m.getSourceObject(assocA).equals(ssId)) {
				ssAssocAssoc = assocA;
				folDocAssoc = assocB;
			} else {
				ssAssocAssoc = assocB;
				folDocAssoc = assocA;
			}

			if ( 	!m.getSimpleAssocType(ssAssocAssoc).equals("HasMember") ||
					!m.getSourceObject(ssAssocAssoc).equals(ssId) ||
					!m.getTargetObject(ssAssocAssoc).equals(m.getId(folDocAssoc))
			) {
				String msg = "";
				if (!m.getSimpleAssocType(ssAssocAssoc).equals("HasMember")) msg = "association type";
				if (!m.getSourceObject(ssAssocAssoc).equals(ssId)) msg = msg + ", sourceObject";
				if (!m.getTargetObject(ssAssocAssoc).equals(m.getId(folDocAssoc))) msg = msg + ", targetObject is not the Folder-Document association";
				err("A HasMember association must link the SubmissionSet and the new Folder-to-Document Association (" + msg + ")");
			}

			if (	!m.getSimpleAssocType(folDocAssoc).equals("HasMember") ||
					!m.getTargetObject(folDocAssoc).equals(docId)
			)
				err("A HasMember association must link the existing folder and the new Document");

		}
		return !hasError();
	}

	public boolean addExistingDocToExistingFolder() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(0);
		hasFolders(0);
		if ( !hasError()) {
			hasAssociations(2);

			String ssId = m.getId(m.getSubmissionSet());

			List<OMElement> otherA = (List<OMElement>) m.getAssociations();

			OMElement assocA = otherA.get(0);
			OMElement assocB = otherA.get(1);

			OMElement ssAssocAssoc = null;
			OMElement folDocAssoc = null;

			if (m.getSourceObject(assocA).equals(ssId)) {
				ssAssocAssoc = assocA;
				folDocAssoc = assocB;
			} else {
				ssAssocAssoc = assocB;
				folDocAssoc = assocA;
			}

			if ( 	!m.getSimpleAssocType(ssAssocAssoc).equals("HasMember") ||
					!m.getSourceObject(ssAssocAssoc).equals(ssId) ||
					!m.getTargetObject(ssAssocAssoc).equals(m.getId(folDocAssoc))
			) {
				String msg = "";
				if (!m.getSimpleAssocType(ssAssocAssoc).equals("HasMember")) msg = "association type";
				if (!m.getSourceObject(ssAssocAssoc).equals(ssId)) msg = msg + ", sourceObject";
				if (!m.getTargetObject(ssAssocAssoc).equals(m.getId(folDocAssoc))) msg = msg + ", targetObject is not the Folder-Document association";
				err("A HasMember association must link the SubmissionSet and the new Folder-to-Document Association (" + msg + ")");
			}

			if (	!m.getSimpleAssocType(folDocAssoc).equals("HasMember")
			)
				err("A HasMember association must link the existing folder and the existing Document");

		}
		return !hasError();
	}

	public boolean replaceDocument() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(1);
		hasFolders(0);
		if ( !hasError()) {
			hasAssociations(2);

			String ssId = m.getId(m.getSubmissionSet());
			String docId = m.getId(m.getExtrinsicObject(0));

			List<OMElement> otherA = (List<OMElement>) m.getAssociations();

			OMElement assocA = otherA.get(0);
			OMElement assocB = otherA.get(1);

			OMElement ssDocAssoc = null;
			OMElement rplcAssoc = null;

			if (m.getSourceObject(assocA).equals(ssId)) {
				ssDocAssoc = assocA;
				rplcAssoc = assocB;
			} else {
				ssDocAssoc = assocB;
				rplcAssoc = assocA;
			}

			if ( 	!m.getSimpleAssocType(ssDocAssoc).equals("HasMember") ||
					!m.getSourceObject(ssDocAssoc).equals(ssId) ||
					!m.getTargetObject(ssDocAssoc).equals(docId)
			) {
				String msg = "";
				if (!m.getSimpleAssocType(ssDocAssoc).equals("HasMember")) msg = "association type";
				if (!m.getSourceObject(ssDocAssoc).equals(ssId)) msg = msg + ", sourceObject";
				if (!m.getTargetObject(ssDocAssoc).equals(docId)) msg = msg + ", targetObject is not the submitted document";
				err("A HasMember association must link the SubmissionSet and the new Document (" + msg + ")");
			}

			if (	!m.getSimpleAssocType(rplcAssoc).equals("RPLC") ||
					!m.getSourceObject(rplcAssoc).equals(docId)
			)
				err("A RPLC association must link the submitted Document and the existing Document");

		}
		return !hasError();
	}

	public boolean xfrmDocument() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(1);
		hasFolders(0);
		if ( !hasError()) {
			hasAssociations(2);

			String ssId = m.getId(m.getSubmissionSet());
			String docId = m.getId(m.getExtrinsicObject(0));

			List<OMElement> otherA = (List<OMElement>) m.getAssociations();

			OMElement assocA = otherA.get(0);
			OMElement assocB = otherA.get(1);

			OMElement ssDocAssoc = null;
			OMElement rplcAssoc = null;

			if (m.getSourceObject(assocA).equals(ssId)) {
				ssDocAssoc = assocA;
				rplcAssoc = assocB;
			} else {
				ssDocAssoc = assocB;
				rplcAssoc = assocA;
			}

			if ( 	!m.getSimpleAssocType(ssDocAssoc).equals("HasMember") ||
					!m.getSourceObject(ssDocAssoc).equals(ssId) ||
					!m.getTargetObject(ssDocAssoc).equals(docId)
			) {
				String msg = "";
				if (!m.getSimpleAssocType(ssDocAssoc).equals("HasMember")) msg = "association type";
				if (!m.getSourceObject(ssDocAssoc).equals(ssId)) msg = msg + ", sourceObject";
				if (!m.getTargetObject(ssDocAssoc).equals(docId)) msg = msg + ", targetObject is not the submitted document";
				err("A HasMember association must link the SubmissionSet and the new Document (" + msg + ")");
			}

			if (	!m.getSimpleAssocType(rplcAssoc).equals("XFRM") ||
					!m.getSourceObject(rplcAssoc).equals(docId)
			)
				err("A XFRM association must link the submitted Document and the existing Document");

		}
		return !hasError();
	}

	public boolean sswithOneDocOneFol() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(1);
		hasFolders(1);
		if ( !hasError()) {
			hasAssociations(4);
			List<OMElement> unknownAssocs = m.getAssociations();
			List<OMElement> folderDocAssocs = new ArrayList<OMElement>();
			OMElement a;

			a = hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(0), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);

			a = hasAssociation(m.getSubmissionSet(), m.getFolder(0), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);


			a = hasAssociation(m.getFolder(0), m.getExtrinsicObject(0), "HasMember");
			if (a != null) {
				unknownAssocs.remove(a);
				folderDocAssocs.add(a);
			}

			a = hasAssociationWithOneTarget(m.getSubmissionSet(), folderDocAssocs, "HasMember");

		}
		return !hasError();
	}

	public boolean sswithTwoDocOneFol() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(2);
		hasFolders(1);
		if ( !hasError()) {
			hasAssociations(7);
			List<OMElement> unknownAssocs = m.getAssociations();
			List<OMElement> folderDocAssocs = new ArrayList<OMElement>();
			OMElement a;

			a = hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(0), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);

			a = hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(1), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);

			a = hasAssociation(m.getSubmissionSet(), m.getFolder(0), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);


			a = hasAssociation(m.getFolder(0), m.getExtrinsicObject(0), "HasMember");
			if (a != null) {
				unknownAssocs.remove(a);
				folderDocAssocs.add(a);
			}

			a = hasAssociation(m.getFolder(0), m.getExtrinsicObject(1), "HasMember");
			if (a != null) {
				unknownAssocs.remove(a);
				folderDocAssocs.add(a);
			}

			a = hasAssociationWithOneTarget(m.getSubmissionSet(), folderDocAssocs, "HasMember");
			if (a != null) {
				unknownAssocs.remove(a);
				folderDocAssocs.remove(a);
			}

			a = hasAssociationWithOneTarget(m.getSubmissionSet(), folderDocAssocs, "HasMember");

		}
		return !hasError();
	}

	public boolean sswithTwoDocOneFolOneDocInFol() throws MetadataException {
		hasSubmissionSet();
		hasDocuments(2);
		hasFolders(1);
		if ( !hasError()) {
			hasAssociations(5);
			List<OMElement> unknownAssocs = m.getAssociations();
			List<OMElement> folderDocAssocs = new ArrayList<OMElement>();
			OMElement a;

			a = hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(0), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);

			a = hasAssociation(m.getSubmissionSet(), m.getExtrinsicObject(1), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);

			a = hasAssociation(m.getSubmissionSet(), m.getFolder(0), "HasMember");
			if (a != null)
				unknownAssocs.remove(a);

			a = hasAssociationWithOneTarget(m.getFolder(0), m.getExtrinsicObjects(), "HasMember");
			if (a != null) {
				unknownAssocs.remove(a);
				a = this.hasAssociation(m.getSubmissionSet(), a, "HasMember");
				if (a != null)
					unknownAssocs.remove(a);
			}


		}
		return !hasError();
	}

	static void println(String x) { logger.info(x); }

	static void usage() {
		println("metav ");
		println("\t[-h] - print usage message and exit");
		println("\t[-af <assertion file name>]");
		println("\t[-as <assertion set name>]");
		println("\t[-teststep <test step name>]");
		println("\t\tThe input file is inspected assuming it is in the format of a log.xml file. ");
		println("\t\tTestStep <test step name> is found ");
		println("\t\tand the query result is extracted from the Result element.");
		println("\tfile");
		System.exit(-1);
	}

	static public void main(String[] args) {
		String filename = null;
		String assertion_filename = null;
		String assertion_set_name = null;
		String test_step = null;

		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-af")) {
				if (i+1 < args.length) {
					i++;
					assertion_filename = args[i];
				} else
					usage();
			}
			else if (args[i].equals("-h") ) {
				usage();
			}
			else if (args[i].equals("-teststep")) {
				if (i+1 < args.length) {
					i++;
					test_step = args[i];
				} else
					usage();
			}
			else if(args[i].equals("-as")) {
				if (i+1 < args.length) {
					i++;
					assertion_set_name = args[i];
				} else
					usage();
			}
			else if (i == args.length-1) {
				filename = args[i];
			}
			else
				usage();
		}

		if (assertion_filename == null ||
				filename == null)
			usage();

		try {
			Validator v;

			v = new Validator(new File(assertion_filename), assertion_set_name);
			OMElement input = Util.parse_xml(new File(filename));
			if (test_step == null) {
				List<String> path = new ArrayList<String>();
				path.add("TestStep");
				path.add("StoredQueryTransaction");
				path.add("Result");
				path.add("AdhocQueryResponse");
//				path.add("RegistryObjectList");
				OMElement ele_of_focus = v.find_nested_element(input, path);
				v.run_test_assertions(ele_of_focus, null);
			}
			else {
				OMElement step = XmlUtil.firstChildWithLocalName(input, test_step);
				if (step == null) throw new Exception("TestStep " + test_step + " not found in file " + filename);
			}
		}
		catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			System.exit(-1);
		}



	}

	OMElement find_nested_element(OMElement top_element, List<String> path) throws Exception {
		OMElement current_ele = top_element;

		for (String name : path) {
			OMElement ele = XmlUtil.firstChildWithLocalName(current_ele, name);
			if (ele == null) throw new Exception("find_nexted_element: Cannot find element " + name + " in path " + path);
			current_ele = ele;
		}

		return current_ele;
	}

	public TestConfig getTestConfig() {
		return testConfig;
	}



}
