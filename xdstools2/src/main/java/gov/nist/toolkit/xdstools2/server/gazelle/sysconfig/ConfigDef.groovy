package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import groovy.transform.ToString
import groovy.transform.TypeChecked

/**
 * This matches the WebService configuration offered by Gazelle.
 * Updates to this must be matched in ConfigParser.groovy
 */
@TypeChecked
@ToString
class ConfigDef {
    String configType
    String company
    String system
    String host
    String actor
    boolean secured
    boolean approved
    String comment
    String url
    String assigningAuthority
    String wsType
    String port
    String proxyPort
    String portSecured

    boolean isAsync() {
        wsType.contains(':Async')
    }

    boolean isIgq() { wsType.contains(':igq') }
    boolean isIgr() { wsType.contains(':igr') }
    boolean isROD() { wsType.contains(':rod.b') }   // register on demand
    boolean isIDS() { actor == 'IMG_DOC_SOURCE' }    // Image Document source
    boolean isODDS() { actor == 'ON_DEMAND_DOC_SOURCE' }
    boolean isEMBED_REPOS() { actor == 'EMBED_REPOS' }
    boolean isRepository() { actor == 'DOC_REPOSITORY' }
    boolean isRegistry() { actor == 'DOC_REGISTRY' }
    boolean isRecipient() { actor == 'DOC_RECIPIENT' }
    boolean isRespondingGateway() { actor == 'RESP_GATEWAY' }
    boolean isInitiatingGateway() { actor == 'INIT_GATEWAY' }
    boolean isPDB() { wsType.contains('ITI-65')}
    boolean isResponder() { actor == 'DOC_RESPONDER'}

    boolean isRetrieve() { 'Retrieve.b' == getTransactionName() }

    static boolean hasRepository(List<ConfigDef> elements) { elements.find { it.isRepository() } }
    static boolean hasODDS(List<ConfigDef> elements) { elements.find { it.isODDS() }}
    static boolean hasRecipient(List<ConfigDef> elements) { elements.find { it.isRecipient() } }

    /**
     *
     * @return transaction ID (ITI-18 for example)
     */
    String getTransaction() {
        def parts = wsType.split(':')
        if (parts.size() > 0)
            return parts[0]
        return ''
    }

    String getTransactionName() {
        def parts = wsType.split(':')
        if (parts.size() > 1)
            return parts[1]
        return ''
    }

    String getToolkitTransactionCode() {
        def parts = wsType.split(':')
        if (parts.size() > 2)
            return parts[2]
        return ''
    }

    /* wsTypes from Bochum event    Imaging:  68, 69, 75, 55   Cleveland: ???
RAD-75:Cross GW Retrieve Img Doc Set  Y
ITI-39:Cross Gateway Retrieve:xcr
ITI-58:Provider Info Query
ITI-43:Initiating Gateway Retrieve:igr
ITI-18:Async Initiating Gateway Stored Query:igq:as
RAD-75:Async Cross GW Retrieve Img Doc Set
RAD-69:Async Retrieve Imaging Doc Set  Y
ITI-54:Doc Metadata Publish
ITI-63:Cross Gateway Fetch:xcf
ITI-60:Retrieve Multiple Value Sets
ITI-68:RetrieveDoc
ITI-36:Archive Form
ITI-57:Update Doc Set:upd.b
ITI-43:Async Initiating Gateway Retrieve:igr:as
RAD-108:RAD-108:Store Instances over the Web
ITI-67:FindDocReferences
ITI-40:Provide X-User Assertion
ITI-43:Retrieve.b:ret.b
RAD-104:Store Imaging Report Template
ITI-34:Retrieve Form
ITI-38:Async Cross Gateway Query:xcq:as
ITI-18:Stored Query:sq.b
ITI-53:Doc Metadata Notify
ITI-79:Authorization Decisions Query
Browse eCR :Browser eCR
ITI-18:Initiating Gateway Stored Query:igq
ITI-65:ProvideDocBundle
ITI-37:Retrieve Clarifications
RAD-105:Query Imaging Report Template
RAD-106:Invoke Image Display
Provide eCR Document:Provide a Document to the eCR
ITI-83:PIXm Query
ITI-41:Provide and Register.b:pr.b
ITI-51:Multi-Patient Query:MPQ
ITI-66:FindDocManifests
Retrieve eCR Document:Retrieve a Document from eCR
ITI-51:Async Multi-Patient Query:MPQ.as
ITI-63:Async Cross Gateway Fetch:xcf.as
ITI-18:Async Stored Query:sq.as
RAD-55:WADO Retrieve  Y
ITI-42:Async Register.b:r.as
ITI-62:Delete Doc Set:del.b
ITI-43:Async Retrieve:ret.as
Initialize eCR:Creation of eCR
ITI-42:Register.b:r.b
ITI-52:Doc Metadata Subscribe
ITI-78:Query Patient Resource
ITI-41:Async Provide and Register.b:pr.as
ITI-80:Cross-Gateway Doc Provide
ITI-59:Provider Info Feed
RAD-68:P&R Imaging Doc Set MTOM/XOP   Y
ITI-81:Retrieve ATNA Audit SimResource
RAD-69:Retrieve Imaging Doc Set
RAD-107:WADO-RS Retrieve
ITI-38:Cross Gateway Query:xcq
ITI-71:Get Authorization Token
ITI-82:Retrieve Syslog SimResource
ITI-72:ITI-72 Webservice
ITI-48:Retrieve Value Set
ITI-55:Cross GW Patient Discovery
RAD-103:Retrieve Imaging Report Template
RAD-63:STOWRS - Storage Over the Web
Forward eCR Query:Forward eCR
ITI-39:Async Cross Gateway Retrieve:xcr:as
ITI-61:Register On-Demand Doc:rod.b
ITI-35:Submit Form
ITI-70:Pull Notification
ITI-69:Create/Destroy Pull Point
    */
    // Transactions to process
    static final TRANSACTIONS_TO_PROCESS = [
            'ITI-39', 'ITI-43', 'ITI-18', 'ITI-57', 'ITI-38', 'ITI-41', 'ITI-51', 'ITI-42', 'ITI-61', 'ITI-65', 'ITI-67',
            'RAD-68', 'RAD-69', 'RAD-75', 'RAD-55'   // imaging
    ]
}
