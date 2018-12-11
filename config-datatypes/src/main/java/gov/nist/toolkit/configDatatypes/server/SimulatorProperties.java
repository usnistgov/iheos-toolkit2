package gov.nist.toolkit.configDatatypes.server;

/**
 * These are properties to be used with gov.nist.toolkit.toolkitServicesCommon.SimConfig
 * to control the operation of Simulators through the API.
 * @see gov.nist.toolkit.toolkitServicesCommon.SimConfig#setProperty()
 */
public class SimulatorProperties {

    static public boolean isTlsEndpoint(String name) { return name != null && name.indexOf("TLS") != -1; }

    
    /**
     * Boolean property controlling whether Remove Metadata is enabled on Registry Simulator.
     */
//    public static final String                 REMOVE_METADATA = "Remove_Metadata_Remote_Registry_Option";
    
    /**
     * Boolean property controlling whether Metadata Update is enabled on Registry Simulator.
     */
    public static final String                 UPDATE_METADATA_OPTION = "Update_Metadata_Option";
    public static final String RESTRICTED_UPDATE_METADATA_OPTION = "Restricted_Update_Metadata_Option";
    public static final String REMOVE_METADATA = "Remove_Metadata";
    public static final String REMOVE_DOCUMENTS = "Remove_Documents_Option";

    /**
     * String property identifying port number for V2 Patient Identity Feed on the Registry Simulator.
     * Updates to this property are ignored.
     */
    public static final String                               PIF_PORT = "Patient_Identity_Feed_Port";

    /**
     * Boolean property labeling a Registry Simulator as part of the implementation of a Document
     * Recipient Simulator.
     * Updates to this property are ignored.
     */
    public static final String                      PART_OF_RECIPIENT = "Part_of_Recipient";

    public static final String                      METADATA_LIMITED = "Metadata Limited";

    public static final String                      VALIDATE_AS_RECIPIENT = "Validate_as_Recipient";

    public static final String                      MULTI_PATIENT_QUERY = "Multi_Patient_Query";

    /**
     * Boolean property controlling whether Registry Simulator validates Affinity Domain codes defined
     * in the Simulator Environment.
     */
    public static final String                         VALIDATE_CODES = "Validate_Codes";

    /**
     * Boolean property controlling whether Registry Simulator validates Register transactions against
     * Patient IDs received in a prior Patient Identity Feed transaction.
     */
    public static final String VALIDATE_AGAINST_PATIENT_IDENTITY_FEED = "Validate_Against_Patient_Identity_Feed";

    /**
     * String property controlling the location of the test engine notification extension where transaction
     * notifications are sent. If empty/not set then notifications are not sent. Used in conjunction with
     * TRANSACTION_NOTIFICATION_CLASS.
     */
    public static final String           TRANSACTION_NOTIFICATION_URI = "Transaction_Notification_URI";

    /**
     * String property controlling the Java class to be created/called as part of the processing of a transaction
     * notification.  If empty/not set then notifications are not sent. Used in conjunction with
     * TRANSACTION_NOTIFICATION_URI.
     */
    public static final String         TRANSACTION_NOTIFICATION_CLASS = "Transaction_Notification_Class";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                            pnrEndpoint = "PnR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         pnrTlsEndpoint = "PnR_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                       retrieveEndpoint = "Retrieve_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                    retrieveTlsEndpoint = "Retrieve_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                       registerEndpoint = "Register_endpoint";

    public static final String                       registerOddeEndpoint = "RegisterOdde_endpoint";


    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                    registerTlsEndpoint = "Register_TLS_endpoint";

    public static final String                    registerOddeTlsEndpoint = "RegisterOdde_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                    storedQueryEndpoint = "StoredQuery_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                 storedQueryTlsEndpoint = "StoredQuery_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         updateEndpoint = "Update_endpoint";
    public static final String                         rmuEndpoint = "Restricted_update_endpoint";

    public static final String                          multiPatientQueryEndpoint = "Multipatient_query_endpoint";

    public static final String                          multiPatientQueryTlsEndpoint = "Multipatient_query_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                      updateTlsEndpoint = "Update_TLS_endpoint";
    public static final String                      rmuTlsEndpoint = "Restricted_update_TLS_endpoint";

    /**
     * Profile RMD: ITI-62
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         removeMetadataEndpoint = "Remove_metadata_endpoint";

    public static final String                         removeDocumentsEndpoint = "Remove_documents_endpoint";

    /**
     * Profile RMD: ITI-62
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                      removeMetadataTlsEndpoint = "Remove_metadata_TLS_endpoint";

    public static final String                         removeDocumentsTlsEndpoint = "Remove_documents_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                           xcqrEndpoint = "XCQR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                        xcqrTlsEndpoint = "XCQR_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                            xcqEndpoint = "XCQ_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         xcqTlsEndpoint = "XCQ_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                            igqEndpoint = "IGQ_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         igqTlsEndpoint = "IGQ_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                            xcrEndpoint = "XCR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         xcrTlsEndpoint = "XCR_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                            xcirEndpoint = "XCIR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         xcirTlsEndpoint = "XCIR_TLS_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                            igrEndpoint = "IGR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         igrTlsEndpoint = "IGR_TLS_endpoint";

    public static final String                        xcpdEndpoint = "XCPD_endpoint";

    public static final String                        xcpdTlsEndpoint = "XCPD_TLS_endpoint";

    /**
     * String property defining the name of the Environment holding the configuration
     * defining the valid Affinity Domain codes.  Also defines the TLS certificate for
     * use with a Client Simulator.
     */
    public static final String                       codesEnvironment = "Codes_Environment";

    /**
     * Class name of custom plugin to validate metadata on Register transaction
     */

    public static final String                      metadataValidatorClass = "Register_Metadata_Validator_Class_Name";

    /**
     * Boolean property controlling whether a Registry Simulator should support the Extra Metadata option.
     */
    public static final String                 extraMetadataSupported = "Extra_Metadata_Supported";

    /**
     * String property defining the Repository UniqueId of a Repository simulator.
     * Updates to this property are ignored.
     */
    public static final String                     repositoryUniqueId = "repositoryUniqueId";

    /**
     * See  ITI Vol. 3, Rev. 12, 4.1.1, pg. 10, 350
     */
    public static final String                     PERSISTENCE_OF_RETRIEVED_DOCS = "Persistence of Retrieved Documents Option";

    /**
     * The current index state of the content bundle. The index number is the item ordinal from the bundle index.
     */
    public static final String                      currentContentBundleIdx = "Current Content Bundle Idx";

    /**
     * Content bundle
     * Should be in the format of "9999/Trans/ContentBundle" where 9999 is a test number.
     * @See https://bitbucket.org/iheos/toolkit/wiki/blog/odds_overview
     */
    public static final String                      contentBundle = "Content Bundle";
    /**
     * Testplan to register On-Demand Document Entry and supply content
     * Look up will be in this order: "{Test plan#}/{section[0]}/ContentBundle" Example: 15812/Register_OD/ContentBundle
     * @See https://bitbucket.org/iheos/toolkit/wiki/blog/odds_overview
     */
    public static final String                      TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT = "Testplan to Register and Supply Content";

    /**
     * On-demand document entry Patient Id
     */
    public static final String                      oddePatientId = "On-Demand Document Entry Patient ID";

    /**
     * Registry for the ODDE registration
     */
    public static final String                      oddsRegistrySite = "Registry";

    /**
     * The repository site to store the stable snapshot associated with the on-demand document entry.
     */
    public static final String                      oddsRepositorySite = "Repository";


    /**
     * String property controlling the definition of the Home Community ID of a Responding Gateway.
     */
    public static final String                        homeCommunityId = "homeCommunityId";

    /**
     * String property defining the time the simulator was created.
     * Updates to this property are ignored.
     */
    public static final String                           creationTime = "Creation Time";

    public static final String                           respondingGateways = "Responding Gateways";

    public static final String                           repositories = "Repositories";

    public static final String                           respondingImagingGateways = "Responding Imaging Gateways";
    
    public static final String imagingDocumentSources = "Imaging Document Sources";

    /**
     * Each transaction request throws this error.
     */
    public static final String                          errors = "Throw Error";

    /**
     * Each transaction request for a particular patient throws this error.
     */
    public static final String                          errorForPatient = "Throw Error for Patient ID";

    public static final String                         FORCE_FAULT = "Force Fault";

    /**
     * String property defining the Repository UniqueId of a Repository simulator.
     * Updates to this property are ignored.
     */
    public static final String                     idsRepositoryUniqueId = "IDS Repository UniqueId";

    /**
     * String property defining the Image Cache root directory for an IDS 
     * simulator, absolute or relative to the toolkit image cache directory.
     */
    public static final String                     idsImageCache = "IDS Image Cache";

    /*
     * These two are for RAD-55's sent to Imaging Document Sources
     */
     public static final String      wadoEndpoint = "WADO_endpoint";
     public static final String      wadoTlsEndpoint = "WADO_TLS_endpoint";

   /*
    * These two are for RAD-69's sent to Imaging Document Sources
    */
    public static final String      idsrEndpoint = "IDSR_endpoint";
    public static final String      idsrTlsEndpoint = "IDSR_TLS_endpoint";
    /*
     * These two are for RAD-69's sent to Initiating Imaging Gateways
     */
     public static final String      idsrIigEndpoint    = "IDSR to IIG endpoint";
     public static final String      idsrIigTlsEndpoint = "IDSR to IIG TLS endpoint";

    public static final String                        environment = "Environment";

    /**
     * Requires STS SAML.
     * SoapFault is thrown if SAML is required but missing in the request's soap header.
     */
    public static final String      requiresStsSaml = "Require SAML?";

    /*
     * On the IG simulator - remove homeCommunityId from all objects before returned in query
     * Simulates faulty IG
     */
    public static final String    returnNoHome = "Force no homeCommunityId attribute";

    /*
     * Requires admin to edit config.  While locked no updating transactions are permitted
     */
    public static final String locked = "Locked";

    public static final String pdbEndpoint = "Provide DocumentBundle Endpoint";

    public static final String pdbTlsEndpoint = "Provide DocumentBundle TLS Endpoint";

    public static final String fhirTlsEndpoint = "FHIR TLS Endpoint";

    public static final String fhirEndpoint = "FHIR Endpoint";

    public static final String fdrEndpoint = "Find Document Reference Endpoint";

    public static final String fdrTlsEndpoint = "Find Document Reference Endpoint";

    public static final String rdrEndpoint = "Read Document Reference Endpoint";

    public static final String rdrTlsEndpoint = "Read Document Reference Endpoint";

    public static final String rdBinaryEndpoint = "Read Binary Endpoint";

    public static final String rdBinaryTlsEndpoint = "Read Binary Endpoint";

    public static final String proxyForwardSite = "Proxy Forward Site";

    public static final String proxyPartner = "Proxy Partner";

    public static final String isProxyFrontEnd = "Is Proxy Front End";

    public static final String simProxyTransformations = "SimProxy Transformations";
    public static final String simProxyResponseTransformations = "SimProxy Response Transformations";

    public static final String simulatorGroup = "Simulator Group";

    public static final String replyFile = "Reply File";

    public static final String mimeType = "Mime Type";

    public static final String mtomResponse = "MTOM response";
}
