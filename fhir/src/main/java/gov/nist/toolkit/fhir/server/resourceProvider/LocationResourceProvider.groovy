package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.annotation.Create
import ca.uhn.fhir.rest.annotation.ResourceParam
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.dstu3.model.Location
import org.hl7.fhir.instance.model.api.IBaseResource
/*
{doco
  "resourceType" : "Location",
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "identifier" : [{ Identifier }], // Unique code or number identifying the location to its users
  "status" : "<code>", // active | suspended | inactive
  "operationalStatus" : { Coding }, // The Operational status of the location (typically only for a bed/room)
  "name" : "<string>", // Name of the location as used by humans
  "alias" : ["<string>"], // A list of alternate names that the location is known as, or was known as in the past
  "description" : "<string>", // Additional details about the location that could be displayed as further information to identify the location beyond its name
  "mode" : "<code>", // instance | kind
  "type" : { CodeableConcept }, // Type of function performed
  "telecom" : [{ ContactPoint }], // Contact details of the location
  "address" : { Address }, // Physical location
  "physicalType" : { CodeableConcept }, // Physical form of the location
  "position" : { // The absolute geographic location
    "longitude" : <decimal>, // R!  Longitude with WGS84 datum
    "latitude" : <decimal>, // R!  Latitude with WGS84 datum
    "altitude" : <decimal> // Altitude with WGS84 datum
  },
  "managingOrganization" : { Reference(Organization) }, // Organization responsible for provisioning and upkeep
  "partOf" : { Reference(Location) }, // Another Location this one is physically part of
  "endpoint" : [{ Reference(Endpoint) }] // Technical endpoints providing access to services operated for the location
}
 */

class LocationResourceProvider implements IResourceProvider{
    @Override
    Class<? extends IBaseResource> getResourceType() {
        return Location.class
    }

    @Create()
    public MethodOutcome createLocation(@ResourceParam Location theLocation,
                                       RequestDetails requestDetails) {

        validateResource(theLocation);

        return new ToolkitResourceProvider(Location.class, requestDetails).createOperation(theLocation)
    }

    def validateResource(Location theLocation) {}
}
