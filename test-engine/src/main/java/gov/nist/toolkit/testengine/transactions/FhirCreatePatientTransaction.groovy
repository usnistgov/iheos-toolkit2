package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.testengine.engine.PatientIdAllocator
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.engine.UseReportManager
import org.apache.axiom.om.OMElement
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.instance.model.api.IBaseResource

class FhirCreatePatientTransaction extends FhirCreateTransaction {
    FhirCreatePatientTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {

        if (!(resource instanceof Patient)) {
            stepContext.set_error("This transaction can only be run with a Patient resource, a ${resource.class.simpleName} was found instead")
            return
        }
        Patient patient = resource

        Pid pid = PatientIdAllocator.getNew('1.2.3432.2.78554')

        if (!useReportManager)
            useReportManager = new UseReportManager(testConfig)

        useReportManager.add('$pid_value$', pid.id)
        useReportManager.add('$pid_system$', pid.ad)

        patient.identifierFirstRep.system = "urn:oid:${pid.ad}"
        patient.identifierFirstRep.value = pid.id

        super.doRun(resource, urlExtension)
    }
}