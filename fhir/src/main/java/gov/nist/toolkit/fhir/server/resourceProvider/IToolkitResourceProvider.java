package gov.nist.toolkit.fhir.server.resourceProvider;

import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Patient;

/**
 *
 */
public interface IToolkitResourceProvider extends IResourceProvider {
    MethodOutcome create(DomainResource theResource, RequestDetails requestDetails);
}
