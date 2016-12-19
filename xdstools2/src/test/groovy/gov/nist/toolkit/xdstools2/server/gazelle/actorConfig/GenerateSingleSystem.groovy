package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean

/**
 *
 */
class GenerateSingleSystem {
    GazellePull gazellePull
    GazelleGet getter
    File cache
    ConfigParser cparser = new ConfigParser()
    OidsParser oparser = new OidsParser()
    def nl = '\n'
    def tab = '  '

    GenerateSingleSystem(GazellePull _gazellePull, File _cache) {
        gazellePull = _gazellePull
        cache = _cache
        getter = new GazelleGet(gazellePull, cache) // pull from Gazelle or cache
    }

    GeneratedSystems generate(String systemName, boolean reload) {
        if (reload)
            getter.singleConfigFile(systemName).delete()
        generate(systemName)
    }

    GeneratedSystems generate(String systemName) {
        StringBuilder log = new StringBuilder();
        log.append("System: ${systemName}").append(nl)

        cparser.parse(getter.singleConfigFile(systemName).toString())
        oparser.parse(getter.oidsFile().toString())

        List<ConfigDef> elements = cparser.all.findAll { filter(it) }
        if (!elements) return null

        //*************************************************************
        // Recipient
        //*************************************************************
        Site recipientSite = null
        elements.findAll { it.isRecipient()}.each { ConfigDef config ->
            String system = config.system
            if (recipientSite == null)
                recipientSite = new Site("${system} - REC")

            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)
            log.append(tab).append('TransactionType: ').append(transactionType)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false
            recipientSite.addTransaction(transactionId, endpoint, isSecure, isAsync)
        }

        //*************************************************************
        // ODDS
        //*************************************************************
        Site oddsSite = null
        String oddsOid = null
        elements.findAll { it.isODDS() }.each { ConfigDef config ->
            String system = config.system
            if (oddsSite == null) {
                oddsOid = oparser.getOid(system, OidDef.ODDSRepUidOid)
                log.append(tab).append("ODDS OID = ${oddsOid}")
                if (oddsOid == null) return
                oddsSite = new Site("${system} - ODDS")
            }
            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)
            log.append(tab).append('TransactionType: ').append(transactionType)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            log.append(tab).append('Endpoint')
            if (isSecure) log.append(' (secure) ')
            log.append(endpoint).append(nl)

            oddsSite.addRepository(oddsOid, TransactionBean.RepositoryType.ODDS, endpoint, isSecure, isAsync)
        }

        //*************************************************************
        // OTHER
        //*************************************************************
        // Once the above is singled out, the rest can go into one system config
        Site otherSite = null
        String otherOid = null
        String homeOid = null
        elements.findAll { !it.isODDS() && !it.isRecipient() }.each { ConfigDef config ->
            String transactionId = config.getTransaction()
            if (!transactionId) return
            if (!ConfigDef.TRANSACTIONS_TO_PROCESS.contains(transactionId)) return

            String system = config.system
            if (otherSite == null) {
                otherOid = oparser.getOid(system, OidDef.ODDSRepUidOid)
                log.append(tab).append("Repository OID = ${otherOid}")
                if (otherOid == null) return
                otherSite = new Site("${system}")
                homeOid = oparser.getOid(system, OidDef.HomeIdOid)
                otherSite.setHome(homeOid)
            }

            TransactionType transactionType = TransactionType.find(transactionId)
            log.append(tab).append('TransactionType: ').append(transactionType)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            log.append(tab).append('Endpoint')
            if (isSecure) log.append(' (secure) ')
            log.append(endpoint).append(nl)

            if (config.isRetrieve()) {
                otherSite.addRepository(oddsOid, TransactionBean.RepositoryType.ODDS, endpoint, isSecure, isAsync)
            } else {
                otherSite.addTransaction(transactionId, endpoint, isSecure, isAsync)
            }
        }

        GeneratedSystems systems = new GeneratedSystems()
        if (recipientSite)
            systems.systems.add(recipientSite)
        if (oddsSite)
            systems.systems.add(oddsSite)
        if (otherSite)
            systems.systems.add(otherSite)
        systems.log = log

        return systems
    }


    /**
     *
     * @param config
     * @return true if this should be processed
     */
    boolean filter(ConfigDef config) {
        if (config.isAsync()) return false;
        if (!ConfigDef.TRANSACTIONS_TO_PROCESS.contains(config.getTransaction())) return false
        return true;
    }
}
