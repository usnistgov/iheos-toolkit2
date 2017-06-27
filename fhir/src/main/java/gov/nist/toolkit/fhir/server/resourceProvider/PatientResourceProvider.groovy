package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.model.primitive.IdDt
import ca.uhn.fhir.model.primitive.InstantDt
import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException
import gov.nist.toolkit.fhir.search.BaseQuery
import gov.nist.toolkit.fhir.search.SearchByTypeAndId
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.CodeableConcept
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.dstu3.model.Patient

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
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
                                       RequestDetails requestDetails,
                                       HttpServletRequest theRequest,
                                       HttpServletResponse theResponse) {
        println '***************************'
        println '*'
        println ' CREATE PATIENT'
        println '*'
        println '***************************'


        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)
        validateResource(thePatient);

        IdDt newId = tk.addResource(thePatient)
        tk.flushIndex()

        tk.displayIndex()

        // Let the caller know the ID of the newly created resource
        return new MethodOutcome(newId);
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
    public Patient getResourceById(@IdParam IdType theId,
                                   RequestDetails requestDetails,
                                   HttpServletRequest theRequest,
                                   HttpServletResponse theResponse) {

        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        String id = theId.getIdPart();

        println '***************************'
        println '*'
        println " GET PATIENT ${id}"
        println '*'
        println '***************************'

        tk.displayIndex()

        List<String> paths = new SearchByTypeAndId(tk.simContext).run(tk.resourceTypeAsString(), id);

        if (paths.size() == 0)
            return null;

        if (paths.size() > 1)
            throw new InternalErrorException("Multiple results found");

        File f = new File(paths.get(0));

        try {
            return tk.jsonParser.parseResource(Patient.class, new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new InternalErrorException("File " + paths.get(0) + " not found");
        }
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
    public List<Patient> getPatient(
            @RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName,
            @OptionalParam(name = Patient.SP_GIVEN) StringParam theGivenName,
            RequestDetails requestDetails,
                                    HttpServletRequest theRequest,
                                    HttpServletResponse theResponse) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        tk.displayIndex()

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(Patient.SP_FAMILY, theFamilyName.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, BooleanClause.Occur.MUST )

        if (theGivenName) {
            term = new Term(Patient.SP_GIVEN, theGivenName.value)
            termQuery = new TermQuery(term)
            builder.add(termQuery, BooleanClause.Occur.MUST)
        }


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }

    /**  NOT LINKED TO RESDB YET
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
