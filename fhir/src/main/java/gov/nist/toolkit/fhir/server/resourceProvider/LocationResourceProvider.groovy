package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.IdType
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
    @Create()
    public MethodOutcome createLocation(@ResourceParam Location theLocation,
                                       RequestDetails requestDetails) {

        validateResource(theLocation);

        return new ToolkitResourceProvider(Location.class, requestDetails).createOperation(theLocation)
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
    public Location getResourceById(@IdParam IdType theId,
                                   RequestDetails requestDetails) {

        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        File f = tk.readOperation(theId)

        try {
            return tk.jsonParser.parseResource(getResourceType(), new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new InternalErrorException("File " + f + " not found");
        }
    }

    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. You may have many different method annotated with
     * this annotation, to support many different search criteria. This
     * example searches by location city.
     *
     * @param theAddressCity
     *    This operation takes one parameter which is the search criteria. It is
     *    annotated with the "@Required" annotation. This annotation takes one argument,
     *    a string containing the name of the search criteria. The datatype here
     *    is StringParam, but there are other possible parameter types depending on the
     *    specific search criteria.
     * @return
     *    This method returns a list of Locations. This list may contain multiple
     *    matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> getLocation(
            @RequiredParam(name = Location.SP_ADDRESS_CITY) StringParam theAddressCity,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(Location.SP_ADDRESS_CITY, theAddressCity.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }



    def validateResource(Location theLocation) {}
}
