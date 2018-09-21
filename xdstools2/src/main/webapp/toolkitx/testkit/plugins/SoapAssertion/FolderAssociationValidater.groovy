package war.toolkitx.testkit.plugins.SoapAssertion

import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.registrymetadata.client.AnyIds
import gov.nist.toolkit.registrymetadata.client.ObjectRef
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager
import gov.nist.toolkit.session.server.services.GetDocuments
import gov.nist.toolkit.session.server.services.GetFolders
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.soap.AbstractSoapValidater

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.results.client.AssertionResult
import gov.nist.toolkit.valregmetadata.coding.Uuid
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement


/**
 * Runs a plugin based on utility tools. (Overwrites user session utility tool logs.)
 */
@TypeChecked
class FolderAssociationValidater extends AbstractSoapValidater {
    /**
     * Total associations in SubmissionSet
     */
    String totalAssociationsInSs
    /**
     * If Local, checks for the Document Entry in the local metadata model. Any other value checks Registry using the target object UUID.
     */
    String docEntryScopeCheck

    FolderAssociationValidater() {
        filterDescription = "Folder association validater"
    }

    @Override
    ValidaterResult validate(SoapSimulatorTransaction sst) {
        reset() // Clear log
        /*
         find an association
        1. where associationType='HasMember'
        2. sourceObject != SSid. When True, record this Id has as a parameter to be used in GetFolder SQ
        3. target = DocId
        */
        boolean requestMatch = false
        if (!sst) {
            String illegalArg = "Null Transaction parameter"
            error(illegalArg)
            throw new IllegalArgumentException(illegalArg)
        } else {
            if (sst.requestBody) {
                Metadata m = MetadataParser.parseNonSubmission(Util.parse_xml(sst.requestBody))
                final int assocSize = m.getAssociations().size()

                if (assocSize == Integer.parseInt(totalAssociationsInSs)) {
                   for (OMElement e : m.getAssociations()) {
                       String assocType = Metadata.getAssocType(e)
                       if (MetadataSupport.assoctype_has_member == assocType) {
                           String targetObjId = Metadata.getAssocTarget(e)
                          String sourceObjId = Metadata.getAssocSource(e) // Pursue this target
                          if (m.getSubmissionSetId() != sourceObjId) { // Should be the Folder Id
                              AssociationUtil util = new AssociationUtil(sst)
                              util.getFolder(sourceObjId)
                              if ("Local" == docEntryScopeCheck) {
                                  if (!m.isDocument(targetObjId))
                                      error("Request", "Association Target Document UUID ${targetObjId} is not found in embedded metadata model")
                              } else {
                                  util.getDocuments(targetObjId)
                              }
                          }
                       }
                   }
                } else {
                    error("Request", "${assocSize} Associations found")
                }

                requestMatch = sst.request instanceof String && !isErrors()
            } else {
                error("Request","Null transactionInstance or its request body is null")
            }
        }

        new ValidaterResult(sst, this.copy(), requestMatch)
    }

    class AssociationUtil {
        TestSession testSession
        String env
        SiteSpec siteSpec
        Session mySession

        AssociationUtil(SoapSimulatorTransaction sst) {
            testSession = sst.simReference.simId.testSession
            env = sst.simReference.simId.environmentName
            siteSpec = sst.simReference.simId.siteSpec
            mySession = new Session(Installation.instance().warHome(), testSession.toString())
            mySession.setEnvironment(env)
            if (mySession.getTestSession() == null)
                mySession.setTestSession(testSession)
            mySession.setSiteSpec(siteSpec)
            mySession.setTls(false)
        }


        void getFolder(String Uuid) {
            ObjectRef objectRef = new ObjectRef(Uuid)
            List<Result> results = new GetFolders(mySession).setObjectRefReturn().run(siteSpec, new AnyIds(objectRef))
            Result result = results ? results.get(0) : null
            checkResult(objectRef, "GetFolders UUID ${Uuid}", result)
        }

        void getDocuments(String Uuid) {
            ObjectRef objectRef = new ObjectRef(Uuid)
            List<Result> results = new GetDocuments(mySession).setObjectRefReturn().run(siteSpec, new AnyIds(objectRef))
            Result result = results ? results.get(0) : null
            checkResult(objectRef, "GetDocuments UUID ${Uuid}", result)
        }


        private void checkResult(ObjectRef objectRef, String queryType, Result result) {
            if (result==null) {
                error("Request", "Null ${queryType} stored query result")
            }

            if (!result.passed()) {
                error("Request","${queryType} stored query failed.")
                if (result.assertions!=null && result.assertions.assertions!=null) {
                    List<AssertionResult> ars = result.assertions.assertions
                    for (int cx=0; cx < ars.size(); cx++) {
                        error(ars.get(cx).toString())
                    }
                }
            } else {
                // If LeafClass then use:
                // String folderId = result.stepResults.get(0).getMetadata().folders.get(0).id // .docEntries.get(0).uniqueId;
                List<ObjectRef> refList = result.stepResults.get(0).getObjectRefs()
                if (refList.contains(objectRef)) {
                    // Cleared
                } else {
                    error("${queryType} StoredQuery did not match the supporting sim Registry")
                }
            }
        }
     }


    AbstractSoapValidater copy() {
        FolderAssociationValidater aefv = new FolderAssociationValidater()
        aefv.errors = errors
        aefv.setLog(new StringBuilder(this.log))
        aefv
    }
}