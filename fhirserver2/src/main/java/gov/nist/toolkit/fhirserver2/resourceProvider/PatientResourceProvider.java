package gov.nist.toolkit.fhirserver2.resourceProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import gov.nist.toolkit.fhirserver2.servlet.ToolkitContext;
import gov.nist.toolkit.registrymetadata.UuidAllocator;
import org.hl7.fhir.dstu3.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PatientResourceProvider implements IResourceProvider {
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    @Create()
    public MethodOutcome createPatient(@ResourceParam Patient thePatient,
                                       HttpServletRequest theRequest,
                                       HttpServletResponse theResponse) {
        validateResource(thePatient);

        FhirContext ourCtx = FhirContext.forDstu3();

        String theId = UuidAllocator.allocateNaked();
        IdDt newId = new IdDt("Patient", theId, "1");
        thePatient.setId(newId);

        String patientString = ourCtx.newJsonParser().encodeResourceToString(thePatient);

        new ToolkitContext(theRequest).getSimContext()
                .store("Patient", patientString, theId)
                .indexEvent();



        addNewVersion(thePatient, theId);

        // Let the caller know the ID of the newly created resource
        return new MethodOutcome(new IdDt(theId));
    }

    /**
     * Stores a new version of the patient in memory so that it can be retrieved later.
     *
     * @param thePatient
     *            The patient index to store
     * @param theId
     *            The ID of the patient to retrieve
     */
    private void addNewVersion(Patient thePatient, String theId) {
        InstantDt publishedDate = InstantDt.withCurrentTime();
//        if (!myIdToPatientVersions.containsKey(theId)) {
//            myIdToPatientVersions.put(theId, new LinkedList<Patient>());
//            publishedDate = InstantDt.withCurrentTime();
//        } else {
//            Patient currentPatitne = myIdToPatientVersions.get(theId).getLast();
//            Map<ResourceMetadataKeyEnum<?>, Object> resourceMetadata = currentPatitne.getResourceMetadata();
//            publishedDate = (InstantDt) resourceMetadata.get(ResourceMetadataKeyEnum.PUBLISHED);
//        }

		/*
		 * PUBLISHED time will always be set to the time that the first version was stored. UPDATED time is set to the time that the new version was stored.
		 */
//        thePatient.getResourceMetadata().put(ResourceMetadataKeyEnum.PUBLISHED, publishedDate);
//        thePatient.getResourceMetadata().put(ResourceMetadataKeyEnum.UPDATED, InstantDt.withCurrentTime());
//
//        Deque<Patient> existingVersions = myIdToPatientVersions.get(theId);
//
//        // We just use the current number of versions as the next version number
//        String newVersion = Integer.toString(existingVersions.size());

        // Create an ID with the new version and assign it back to the index
        IdDt newId = new IdDt("Patient", theId, "1");
        thePatient.setId(newId);

//        existingVersions.add(thePatient);
    }


    /**
     * The "@Read" annotation indicates that this method supports the
     * read operation. Read operations should return a single resource
     * instance.
     *
     * @param theId
     *    The read operation takes one parameter, which must be of type
     *    IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return
     *    Returns a resource matching this identifier, or null if none exists.
     */
    @Read()
    public Patient getResourceById(@IdParam IdType theId) {
        Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        patient.getIdentifier().get(0).setValue("00002");
        patient.addName();
        patient.getName().get(0).setFamily("Test");
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        return patient;
    }

    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. You may have many different method annotated with
     * this annotation, to support many different search criteria. This
     * example searches by family name.
     *
     * @param theFamilyName
     *    This operation takes one parameter which is the search criteria. It is
     *    annotated with the "@Required" annotation. This annotation takes one argument,
     *    a string containing the name of the search criteria. The datatype here
     *    is StringParam, but there are other possible parameter types depending on the
     *    specific search criteria.
     * @return
     *    This method returns a list of Patients. This list may contain multiple
     *    matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> getPatient(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName) {
        Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setUse(Identifier.IdentifierUse.OFFICIAL);
        patient.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        patient.getIdentifier().get(0).setValue("00001");
        patient.addName();
        patient.getName().get(0).setFamily(theFamilyName.getValue());
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(Enumerations.AdministrativeGender.MALE);
        return Collections.singletonList(patient);
    }

    /**
     * This method just provides simple business validation for resources we are storing.
     *
     * @param thePatient
     *            The patient to validate
     */
    private void validateResource(Patient thePatient) {
		/*
		 * Our server will have a rule that patients must have a family name or we will reject them
		 */
        if (!thePatient.getNameFirstRep().hasFamily()) {
            OperationOutcome outcome = new OperationOutcome();
            outcome.addIssue().setSeverity(OperationOutcome.IssueSeverity.FATAL).setDetails(new CodeableConcept().setText("No family name provided, Patient resources must have at least one family name."));
            throw new UnprocessableEntityException(outcome);
        }
    }
}
