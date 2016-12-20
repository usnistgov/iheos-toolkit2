package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

/**
 *
 */
class ActorTransactionValidation {
    // These are in Gazelle terms
    static Map<String, List<String>> mapping = [
            'ITI-18': ['DOC_REGISTRY', 'INIT_GATEWAY'],
            'ITI-38': ['RESP_GATEWAY'],   // X community query
            'ITI-39': ['RESP_GATEWAY'], // X community retrieve
            'ITI-41': ['DOC_REPOSITORY', 'DOC_RECIPIENT'],
            'ITI-42': ['DOC_REGISTRY'],
            'ITI-43': ['DOC_REPOSITORY', 'INIT_GATEWAY', 'EMBED_REPOS', 'ON_DEMAND_DOC_SOURCE'],
            'ITI-51': ['DOC_REGISTRY'],
            'ITI-57': ['DOC_REGISTRY'],
            'ITI-61': [],
            'ITI-62': ['DOC_REGISTRY'],   // delete document set


            'RAD-55': ['IMG_DOC_SOURCE'],   // WADO Retrieve
            'RAD-68': ['DOC_REPOSITORY'],
            'RAD-69': ['IMG_DOC_SOURCE', 'INIT_IMG_GATEWAY'],   // Retrieve Imaging Doc Set
            'RAD-75': ['RESP_IMG_GATEWAY'],    // Cross GW Retrieve Img Doc Set
    ]

    static boolean accepts(String transactionID, String actor) {
        mapping[transactionID]?.contains(actor)
    }

    static String errorMessage(String transactionID, String actor){
        if (accepts(transactionID, actor)) return null
        return "Error - Transaction ${transactionID} cannot be sent to the ${actor} actor"
    }
}
