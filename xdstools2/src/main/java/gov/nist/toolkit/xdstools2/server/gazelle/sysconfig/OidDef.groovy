package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import groovy.transform.ToString

/**
 *
 */
@ToString
class OidDef {
    String system
    String type
    String oid

    // Type values
    static final String OrganizationOid = 'organization OID'
    static final String RepUidOid = 'repositoryUniqueID OID'
    static final String ODDSRepUidOid = 'repositoryUniqueID OID for On-Demand Doc Src'
    static final String IntSrcRepoUidOid = 'repositoryUniqueId-IntegSrcRepos'
    static final String HomeIdOid = 'homeCommunityID OID'
    static final String AssigningAuthorityOid = 'patient ID assigning authority OID'
    static final String SrcIdOid = 'sourceID OID'
}
