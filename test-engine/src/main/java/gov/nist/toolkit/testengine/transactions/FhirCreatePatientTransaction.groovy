package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.testengine.engine.PatientIdAllocator
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.engine.UseReportManager
import org.apache.axiom.om.OMElement
import org.hl7.fhir.instance.model.api.IBaseResource

class FhirCreatePatientTransaction extends FhirCreateTransaction {
    FhirCreatePatientTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {

        Pid pid = PatientIdAllocator.getNew('1.2.3432.2.78554')

        if (!useReportManager)
            useReportManager = new UseReportManager(testConfig)

        useReportManager.add('$pid_value$', pid.id)
        useReportManager.add('$pid_system$', pid.ad)

        super.doRun(resource, urlExtension)
    }
}