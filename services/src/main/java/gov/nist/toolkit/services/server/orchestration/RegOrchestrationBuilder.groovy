package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.*
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class RegOrchestrationBuilder extends AbstractOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private RegOrchestrationRequest request
    private Util util

    public RegOrchestrationBuilder(ToolkitApi api, Session session, RegOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        switch (request.getPifType()) {
            case PifType.NONE:
                buildTestEnvironment_NoPif()
                break
            case PifType.V2:
                buildTestEnvironment_V2Pif()
                break
        }
    }

    RawResponse buildTestEnvironment_NoPif() {
        // The response contains "messageItems" that really mean a set of orchestration tests.
        // Depending on the orchestration options selected the elements of a set, or tests, can vary.
        RegOrchestrationResponse response = new RegOrchestrationResponse()

        TestInstance testInstanceReadme = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("ManualPif-Readme", request.testSession))
        MessageItem itemReadme = response.addMessage(testInstanceReadme, true, "")

        // If Request parameter has No PIF, no TI is added to the response object
        Map<String, TestInstanceManager> pidNameMap = [
                registerPid:  new TestInstanceManager(request, response, '15817'),
                registerAltPid:  new TestInstanceManager(request, response, '15817b'),
                sqPid:        new TestInstanceManager(request, response, '15818'),
                mpq1Pid:      new TestInstanceManager(request, response, '15819'),
                mpq2Pid:      new TestInstanceManager(request, response, '15820')
        ]

        SimId selfTestRegId = null
        if (request.selfTest()) {
            selfTestRegId = new SimId(request.testSession, 'reg_sut', ActorType.REGISTRY.name, request.environmentName)
        }

        boolean forceNewPatientIds = !request.isUseExistingState()

        // Step 1. Create Patient ID Strings if forceNewPatientIds is set to True
        OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.REGISTRY, pidNameMap.keySet(), forceNewPatientIds)

        // Persist the PIF setting, so it will be restored to the same setting the next time
        orchProps.setProperty("pifType", request.pifType.name())

        SiteSpec registrySut
        if (selfTestRegId) {
            registrySut = selfTestRegId.siteSpec
            api.deleteSimulatorIfItExists(selfTestRegId)
            api.createSimulator(selfTestRegId)
        } else {
            registrySut = request.registrySut
        }
        response.setSut(registrySut)

        // Make Pid objects from String created in Step 1.
        Pid registerPid = PidBuilder.createPid(orchProps.getProperty("registerPid"))
        Pid registerAltPid = PidBuilder.createPid(orchProps.getProperty("registerAltPid"))
        Pid sqPid       = PidBuilder.createPid(orchProps.getProperty("sqPid"))
        Pid mpq1Pid     = PidBuilder.createPid(orchProps.getProperty("mpq1Pid"))
        Pid mpq2Pid     = PidBuilder.createPid(orchProps.getProperty("mpq2Pid"))

        // Assign Pid to the response
        response.setRegisterPid(registerPid)
        response.setRegisterAltPid(registerAltPid)
        response.setSqPid(sqPid)
        response.setMpq1Pid(mpq1Pid)
        response.setMpq2Pid(mpq2Pid)

        // Setup PIF patientid param
        pidNameMap.each { String key, TestInstanceManager value ->
            String pidId = key
            TestInstanceManager testInstanceManager = value
            testInstanceManager.messageItem.params.put('$patientid$', orchProps.getProperty(pidId))
        }

        TestInstance testInstance12346_nopif = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12346", request.testSession))
        MessageItem item12346_nopif = response.addMessage(testInstance12346_nopif, true, "")
        item12346_nopif.params.put('$patientid$', sqPid.toString())
        response.testParams.put(testInstance12346_nopif, item12346_nopif.params)

        TestInstance testInstance12374_nopif = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12374", request.testSession))
        MessageItem item12374base = response.addMessage(testInstance12374_nopif, true, "")
        item12374base.params.put('$patientid$', orchProps.getProperty('registerAltPid'))
        response.testParams.put(testInstance12374_nopif, item12374base.params)

        TestInstance testInstance12361 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12361"))
        MessageItem item12361 = response.addMessage(testInstance12361, true, "")
        item12361.params.put('$patient_id1$', orchProps.getProperty('mpq1Pid'))
        item12361.params.put('$patient_id2$', orchProps.getProperty('mpq2Pid'))
        response.testParams.put(testInstance12361, item12361.params)

        if (orchProps.updated() && !request.isUseExistingState()) {
                List<TestInstance> tILogToBeDeleted = new ArrayList<>()
                pidNameMap.each { String key, TestInstanceManager value ->
                    String pidId = key
                    TestInstanceManager testInstanceManager = value
                    tILogToBeDeleted.add(testInstanceManager.messageItem.testInstance)
                }
                tILogToBeDeleted.add(testInstanceReadme)
                tILogToBeDeleted.add(testInstance12346_nopif)
                tILogToBeDeleted.add(testInstance12374_nopif)
                tILogToBeDeleted.add(testInstance12361)
                session.getXdsTestServiceManager().delTestResults(tILogToBeDeleted, request.getEnvironmentName(), request.getTestSession())

            // Instruct the user to manually Run PIF feed on their system and then run load the test data.
        } else {
            pidNameMap.each { String key, TestInstanceManager value ->
                String pidId = key
                TestInstanceManager testInstanceManager = value
                testInstanceManager.messageItem.setSuccess(api.getTestLogs(testInstanceManager.messageItem.testInstance).isSuccess())
            }
            item12346_nopif.setSuccess(api.getTestLogs(testInstance12346_nopif).isSuccess())
            item12374base.setSuccess(api.getTestLogs(testInstance12374_nopif).isSuccess())
            item12361.setSuccess(api.getTestLogs(testInstance12361).isSuccess())
        }

        orchProps.save()

        return response
    }

    RawResponse buildTestEnvironment_V2Pif() {
        // V2: The response contains "messageItems" that really mean a set of orchestration tests.
        // Depending on the orchestration options selected the elements of a set, or tests, can vary.
        RegOrchestrationResponse response = new RegOrchestrationResponse()

        // V2: If Request parameter has v2 PIF, every call to this TestInstanceManager constructor adds a TestInstance to the response object
        Map<String, TestInstanceManager> pidNameMap = [
                registerPid:  new TestInstanceManager(request, response, '15817'),
                registerAltPid:  new TestInstanceManager(request, response, '15817b'),
                sqPid:        new TestInstanceManager(request, response, '15818'),
                mpq1Pid:      new TestInstanceManager(request, response, '15819'),
                mpq2Pid:      new TestInstanceManager(request, response, '15820')
        ]

        SimId selfTestRegId = null
        if (request.selfTest()) {
            selfTestRegId = new SimId(request.testSession, 'reg_sut', ActorType.REGISTRY.name, request.environmentName)
        }

        boolean forceNewPatientIds = !request.isUseExistingState()

        // V2: Step 1. Create Patient ID Strings if forceNewPatientIds is set to True
        OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.REGISTRY, pidNameMap.keySet(), forceNewPatientIds)

        // V2: Persist the PIF setting, so it will be restored to the same setting the next time
        orchProps.setProperty("pifType", request.pifType.name())

        SiteSpec registrySut
        if (selfTestRegId) {
            registrySut = selfTestRegId.siteSpec
            api.deleteSimulatorIfItExists(selfTestRegId)
            api.createSimulator(selfTestRegId)
        } else {
            registrySut = request.registrySut
        }
        response.setSut(registrySut)

        // V2: Make Pid objects from String created in Step 1.
        Pid registerPid = PidBuilder.createPid(orchProps.getProperty("registerPid"))
        Pid registerAltPid = PidBuilder.createPid(orchProps.getProperty("registerAltPid"))
        Pid sqPid       = PidBuilder.createPid(orchProps.getProperty("sqPid"))
        Pid mpq1Pid     = PidBuilder.createPid(orchProps.getProperty("mpq1Pid"))
        Pid mpq2Pid     = PidBuilder.createPid(orchProps.getProperty("mpq2Pid"))

        // Assign Pid to the response
        response.setRegisterPid(registerPid)
        response.setRegisterAltPid(registerAltPid)
        response.setSqPid(sqPid)
        response.setMpq1Pid(mpq1Pid)
        response.setMpq2Pid(mpq2Pid)

        TestInstance testInstance12346 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12346", request.testSession))
        MessageItem item12346 = response.addMessage(testInstance12346, true, "")
        item12346.params.put('$patientid$', sqPid.toString())
        response.testParams.put(testInstance12346, item12346.params)

        TestInstance testInstance12374 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12374", request.testSession))
        MessageItem item12374 = response.addMessage(testInstance12374, true, "")
        item12374.params.put('$patientid$', orchProps.getProperty('registerAltPid'))
        response.testParams.put(testInstance12374, item12374.params)

        TestInstance testInstance12361 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12361"))
        MessageItem item12361 = response.addMessage(testInstance12361, true, "")
        item12361.params.put('$patient_id1$', orchProps.getProperty('mpq1Pid'))
        item12361.params.put('$patient_id2$', orchProps.getProperty('mpq2Pid'))
        response.testParams.put(testInstance12361, item12361.params)

        if (orchProps.updated() && !request.isUseExistingState()) {
            // V2: Send Patient ID Feed messages based on values in pidNameMap
            new PifSender(api, request.testSession, request.registrySut, orchProps)
                .send(PifType.V2, pidNameMap)

            // Initialize Registry for Stored Query testing
            Map<String, String> parms = new HashMap<>()
            // Make this params put for patientid a test-specific. Other tests should not reuse this param value

            try {
                request.registrySut.isTls = request.isUseTls()
                parms.clear()
                parms.put('$patientid$', sqPid.toString())
                util.submit(request.testSession.value, request.registrySut, testInstance12346, parms)
            } catch (Exception e) {
                item12346.setSuccess(false)
            }

            try {
                request.registrySut.isTls = request.isUseTls()
                // This test submits/uses an separate "alternate" PID that is not part of the PidNameMap above
                // If we exclude the PIF section in Test 12374 (non-base) and use the registerAltPid because it was already created. Unwantedly, the test appears in red if one section is not run.
                parms.clear()
                parms.put('$patientid$', orchProps.getProperty('registerAltPid'))
                util.submit(request.testSession.value, request.registrySut, testInstance12374, parms)
            } catch (Exception e) {
                item12374.setSuccess(false)
            }

            try {
                request.registrySut.isTls = request.isUseTls()
                // This test uses a patient Id from its Test plan UseReport instruction
                // Pass the $patientid$ parameter
                parms.clear()
                parms.put('$patient_id1$', orchProps.getProperty('mpq1Pid'))
                parms.put('$patient_id2$', orchProps.getProperty('mpq2Pid'))
                util.submit(request.testSession.value, request.registrySut, testInstance12361, parms)
            } catch (Exception e) {
                item12361.setSuccess(false)
            }
        } else {
            item12346.setSuccess(api.getTestLogs(testInstance12346).isSuccess())
            item12374.setSuccess(api.getTestLogs(testInstance12374).isSuccess())
            item12361.setSuccess(api.getTestLogs(testInstance12361).isSuccess())
        }

        orchProps.save()

        return response
    }



}
