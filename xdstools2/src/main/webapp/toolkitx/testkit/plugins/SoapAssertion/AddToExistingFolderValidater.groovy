package war.toolkitx.testkit.plugins.SoapAssertion

import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.registrymetadata.client.ObjectRef
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.soap.AbstractSoapValidater

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.results.client.AssertionResult


import org.apache.axiom.om.OMElement


/**
 * Runs a plugin based on GetFolders utility. (Overwrites GetFolders log.)
 */
class AddToExistingFolderValidater extends AbstractSoapValidater {

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

                //  Three Associations: 1. ss-de 2. ss-hasMember (hm) 3. hm:[expected fd]-de
                if (assocSize == 3) {
                   for (OMElement e : m.getAssociations()) {
                       String assocType = Metadata.getAssocType(e)
                       if (MetadataSupport.assoctype_has_member == assocType) {
                           String targetObjId = Metadata.getAssocTarget(e)
                          String sourceObjId = Metadata.getAssocSource(e) // Pursue this target
                          if (m.getSubmissionSetId() != sourceObjId) { // Should be the Folder Id
                              if (m.isDocument(targetObjId)) {
                                    TestSession testSession = sst.simReference.simId.testSession
                                    String env = sst.simReference.simId.environmentName
                                    SiteSpec siteSpec = sst.simReference.simId.siteSpec

                                  Map<String, String> params = new HashMap<String, String>()
                                  params.put('$returnType$', "ObjectRef") // Another option: LeafClass

                                  String prefix = "uid"
                                  for (int i=0; i<10; i++) {
                                      params.put('$' + prefix + i + '$', '')
                                  }

                                  params.put('$id0$', sourceObjId)

                                    List<Result> results = queryReg(env, siteSpec, testSession, "GetFolders", "XDS", params, false)

                                  Result result = results?results.get(0):null

                                  if (result==null) {
                                      error("Request", "Null GetFolders stored query result")
                                  }

                                  if (!result.passed()) {
                                      error("Request","GetFolders stored query failed.")
                                      if (result.assertions!=null && result.assertions.assertions!=null) {
                                          List<AssertionResult> ars = result.assertions.assertions
                                          for (int cx=0; cx < ars.size(); cx++) {
                                              error(ars.get(cx).toString())
                                          }
                                      }
                                  } else {
//                                      String folderId = result.stepResults.get(0).getMetadata().folders.get(0).id // .docEntries.get(0).uniqueId;
                                      List<ObjectRef> refList = result.stepResults.get(0).getObjectRefs()
                                      if (refList.contains(sourceObjId)) {
                                              // Cleared
                                      } else {
                                        error("GetFolders StoredQuery for Folder Id " + sourceObjId + " did not match the supporting sim Registry")
                                      }
                                  }
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

    List<Result> queryReg(String environmentName, SiteSpec siteSpec, TestSession testSession,  String tpName, String testSection, Map<String, String> params, boolean persistResult) {
        Session mySession = new Session(Installation.instance().warHome(), testSession.toString())
        mySession.setEnvironment(environmentName)

        if (mySession.getTestSession() == null)
            mySession.setTestSession(testSession)
        mySession.setSiteSpec(siteSpec)
        mySession.setTls(false)

        // Site must exist
        // String tpName = "GetFolders"
        // TP with SQ to GetFolders

        TestInstance testInstance = new TestInstance(tpName, testSession)

        List<String> sections = new ArrayList<>()
        sections.add(testSection)

        XdsTestServiceManager xtsm = new XdsTestServiceManager(mySession)
        List<Result> results =  XdsTestServiceManager.runTestplan(environmentName,testSession,siteSpec,testInstance,sections,params,true, xtsm, persistResult)

        results
    }

    AbstractSoapValidater copy() {
        AddToExistingFolderValidater aefv = new AddToExistingFolderValidater()
        aefv.errors = errors
        aefv.setLog(new StringBuilder(this.log))
        aefv
    }
}