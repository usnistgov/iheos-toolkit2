package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

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
    V2ResponderParser vparser = new V2ResponderParser()
    def nl = '\n'
    def tab = '  '
    def tab2 = tab + tab
    boolean hasErrors = false

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
        getter.getSingleConfig(systemName)  // load into cache
        log.append("System: ${systemName}").append(nl)

        cparser.parse(getter.singleConfigFile(systemName).toString())
        oparser.parse(getter.oidsFile().toString())
        vparser.parse(getter.v2ResponderFile().toString())

        List<ConfigDef> elements = cparser.values.findAll { filter(it) }
        if (!elements) return null

        //*************************************************************
        // Recipient
        //*************************************************************
        Site recipientSite = null
        elements.findAll { it.isRecipient() && it.approved }.each { ConfigDef config ->
            String system = config.system
            String tkSystem = "${system} - REC"
            if (recipientSite == null) {
                log.append(tab).append('Toolkit system: ').append(tkSystem).append(nl)
                recipientSite = new Site(tkSystem)
            }
            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            logit(log, transactionType, null, endpoint, isSecure)

            recipientSite.addTransaction(transactionId, endpoint, isSecure, isAsync)
        }

        //*************************************************************
        // Register On Demand
        //*************************************************************
        Site rodSite = null
        elements.findAll { it.isROD() && it.approved }.each { ConfigDef config ->
            String system = config.system
            String tkSystem = "${system} - ROD"
            if (rodSite == null) {
                log.append(tab).append('Toolkit system: ').append(tkSystem).append(nl)
                rodSite = new Site(tkSystem)
            }
            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            logit(log, transactionType, null, endpoint, isSecure)

            rodSite.addTransaction(transactionId, endpoint, isSecure, isAsync)
        }

        //*************************************************************
        // Image document source
        //*************************************************************
        Site idsSite = null
        elements.findAll { it.isIDS() && it.approved }.each { ConfigDef config ->
            String system = config.system
            String tkSystem = "${system} - IDS"
            if (idsSite == null) {
                log.append(tab).append('Toolkit system: ').append(tkSystem).append(nl)
                idsSite = new Site(tkSystem)
            }
            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            logit(log, transactionType, null, endpoint, isSecure)

            idsSite.addTransaction(transactionId, endpoint, isSecure, isAsync)
        }


        //*************************************************************
        // EMBED_REPOS
        //*************************************************************
        Site embedSite = null
        String embedOid = null
        elements.findAll { it.isEMBED_REPOS() && it.approved }.each { ConfigDef config ->
            String system = config.system
            String tkSystem = "${system} - EMBED" // in toolkit
            if (embedSite == null) {
                log.append(tab).append('Toolkit system: ').append(tkSystem).append(nl)
                embedOid = oparser.getOid(system, OidDef.IntSrcRepoUidOid)
                if (embedOid == null) return
                embedSite = new Site(tkSystem)
            }
            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            logit(log, transactionType, embedOid, endpoint, isSecure)

            embedSite.addRepository(embedOid, TransactionBean.RepositoryType.REPOSITORY, endpoint, isSecure, isAsync)
        }

        //*************************************************************
        // ODDS
        //*************************************************************
        Site oddsSite = null
        String oddsOid = null
        elements.findAll { it.isODDS() && it.approved }.each { ConfigDef config ->
            String system = config.system
            String tkSystem = "${system} - ODDS" // in toolkit
            if (oddsSite == null) {
                log.append(tab).append('Toolkit system: ').append(tkSystem).append(nl)
                oddsOid = oparser.getOid(system, OidDef.ODDSRepUidOid)
                if (oddsOid == null) return
                oddsSite = new Site(tkSystem)
            }
            String transactionId = config.getTransaction()
            if (!transactionId) return
            TransactionType transactionType = TransactionType.find(transactionId)

            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            logit(log, transactionType, oddsOid, endpoint, isSecure)

            oddsSite.addRepository(oddsOid, TransactionBean.RepositoryType.ODDS, endpoint, isSecure, isAsync)
        }

        //*************************************************************
        // OTHER
        //*************************************************************
        // Once the above are singled out, the rest can go into one system config
        Site otherSite = null
        String otherOid = null
        String homeOid = null
        elements.findAll { !it.isODDS() && !it.isRecipient() && !it.isEMBED_REPOS() && !it.isROD() && !it.isIDS() && it.approved }.each { ConfigDef config ->
            boolean forceNotRetrieve = false
            String transactionId = config.getTransaction()
            if (!transactionId) return
            if (!ConfigDef.TRANSACTIONS_TO_PROCESS.contains(transactionId)) return

            String system = config.system
            String tkSystem = system // in toolkit
            if (otherSite == null) {
                log.append(tab).append('Toolkit system: ').append(tkSystem).append(nl)
                otherOid = oparser.getOid(system, OidDef.RepUidOid)
                if (otherOid == null) return
                otherSite = new Site(tkSystem)
                homeOid = oparser.getOid(system, OidDef.HomeIdOid)
                otherSite.setHome(homeOid)
            }

            if (!ActorTransactionValidation.accepts(transactionId, config.actor)) {
                log.append(ActorTransactionValidation.errorMessage(transactionId, config.actor)).append(nl)
                hasErrors = true
                return
            }

            //*************************************************************
            // Handle overloading of transaction labels
            //*************************************************************
            TransactionType transactionType = null
            if (transactionId == 'ITI-18') {
                if (config.actor == 'INIT_GATEWAY' || config.getToolkitTransactionCode() == 'igq') {
                    transactionType = TransactionType.IG_QUERY
                    transactionId = TransactionType.IG_QUERY
                }
            }
            if (transactionId == 'ITI-43') {
                if (config.actor == 'INIT_GATEWAY') {
                    transactionType = TransactionType.IG_RETRIEVE
                    transactionId = TransactionType.IG_RETRIEVE
                    forceNotRetrieve = true
                }
            }
            if (!transactionType)
                transactionType = TransactionType.find(transactionId)
            //*************************************************************


            String endpoint = config.url
            boolean isSecure = config.secured
            boolean isAsync = false

            if (config.isRetrieve() && !forceNotRetrieve) {
                logit(log, transactionType, otherOid, endpoint, isSecure)

                otherSite.addRepository(oddsOid, TransactionBean.RepositoryType.REPOSITORY, endpoint, isSecure, isAsync)
            } else {
                logit(log, transactionType, null, endpoint, isSecure)

                otherSite.addTransaction(transactionId, endpoint, isSecure, isAsync)
            }
        }

        GeneratedSystems systems = new GeneratedSystems()
        systems.hasErrors = hasErrors
        if (recipientSite)
            systems.systems.add(recipientSite)
        if (rodSite)
            systems.systems.add(rodSite)
        if (idsSite)
            systems.systems.add(idsSite)
        if (embedSite)
            systems.systems.add(embedSite)
        if (oddsSite)
            systems.systems.add(oddsSite)
        if (otherSite)
            systems.systems.add(otherSite)
        systems.log = log

        return systems
    }

    def logit(StringBuilder log, def transactionType, def oid, def endpoint, def secure) {
        log.append(tab2).append('TransactionType ').append(transactionType)
        if (oid)
            log.append(" OID ${oid}")
        log.append(tab2).append('Endpoint ')
        if (secure) log.append('(secure) ')
        log.append(endpoint).append(nl)

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
