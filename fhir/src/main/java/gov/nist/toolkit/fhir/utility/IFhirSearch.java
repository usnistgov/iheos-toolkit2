package gov.nist.toolkit.fhir.utility;

import org.hl7.fhir.instance.model.api.IBaseResource;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface IFhirSearch {
    Map<URI, IBaseResource> search(String base, String resourceType, List params);
}
