package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.docref.MetadataTables;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Validator {
	Metadata m;
	StringBuffer errs = new StringBuffer();
	boolean error = false;
	OMElement test_assertions;
	ArrayList<OMElement> use_id = new ArrayList<OMElement>();
	private final static Logger logger = Logger.getLogger(Validator.class);

	TestConfig testConfig = null;

	private enum DocumentEntryFilter {
		MUST_ONLY_INCLUDE,
		INCLUDE,
		EXCLUDE
	};

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

	public boolean hasRplc() throws MetadataException {
		String docUuid = null;
		boolean found = false;

		for (OMElement a : m.getAssociations()) {
			String type = m.getSimpleAssocType(a);
			if (type.equals("RPLC")) {
				found = true;
				if (docUuid == null)
					docUuid = m.getAssocTarget(a);
				else {
					if (docUuid.equals(m.getAssocTarget(a)))
						throw new MetadataException("HasRPLC test: multiple RPLC associations found for same Document", MetadataTables.Doc_relationships);
				}
			}
		}

		return found;
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
		} else {
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

                        if (myMap!=null && myMap.containsKey("$docid$")) {
                            // Iterate the registry response to see if this docid, the one that was previously submitted, exists in the collection!
                            String submittedIdValue = myMap.get("$docid$");

                            if (submittedIdValue==null || "".equals(submittedIdValue)) {
								err("ExtrinsicObject " + "Submitted Id value cannot be null.");
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

	// skb TODO THis method doesn't seem to be used?
	public boolean docRplcDoc() throws MetadataException {
		hasDocuments(2);
		hasAssociations(1); // According to TF 3 Figure 4.2.2.2.3-2 : document replace, Should be 2?
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
