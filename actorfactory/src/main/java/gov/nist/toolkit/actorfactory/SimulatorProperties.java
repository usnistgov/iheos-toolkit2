package gov.nist.toolkit.actorfactory;

/**
 *
 */
public class SimulatorProperties {
    /**
     * Boolean property controlling whether Metadata Update is enabled on Registry Simulator.
     */
    public static final String                 UPDATE_METADATA_OPTION = "Update_Metadata_Option";

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
    public static final String                         updateEndpoint = "update_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                      updateTlsEndpoint = "update_TLS_endpoint";

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
    public static final String                            igrEndpoint = "IGR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                         igrTlsEndpoint = "IGR_TLS_endpoint";

    /**
     * String property defining the name of the Environment holding the configuration
     * defining the valid Affinity Domain codes.  Also defines the TLS certificate for
     * use with a Client Simulator.
     */
    public static final String                       codesEnvironment = "Codes_Environment";

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
    public static final String                      oddsContentSupplyState = "Content Supply State";

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
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                           idsrEndpoint = "IDSR_endpoint";

    /**
     * Endpoint to use to send the indicated transaction to this Simulator.
     * Updates to this property are ignored.
     */
    public static final String                        idsrTlsEndpoint = "IDSR_TLS_endpoint";

}
