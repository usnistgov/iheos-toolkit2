package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

/**
 *
 */
class GazelleGet {
    GazellePull gazellePull
    File cache

    GazelleGet(GazellePull _gazellePull, File _cache) {
        gazellePull = _gazellePull
        cache = _cache

        if (!cache.exists())
            throw new Exception('GazelleGet: Cache directory ' + cache + ' does not exist.')
        if (!cache.isDirectory())
            throw new Exception('GazelleGet: Cache directory ' + cache + ' is not a directory.')
        if (!cache.canWrite())
            throw new Exception('GazelleGet: Cache directory ' + cache + ' is not writable.')
    }

    String getAllOids() {
        File oidsFile = oidsFile()
        if (oidsFile.exists())
            return oidsFile.text
        String oids = gazellePull.getOIDs()
        oidsFile.write(oids)
        return oids
    }

    String getAllConfigs() {
        File configsFile = configFile()
        if (configsFile.exists())
            return configsFile.text
        String configs = gazellePull.getConfigs()
        configsFile.write(configs)
        return configs
    }

    String getSingleConfig(String systemName) {
        File configFile = singleConfigFile(systemName)
        if (configFile.exists())
            return configFile.text
        String config = gazellePull.getSingleConfig(systemName)
        configFile.write(config)
        return config
    }

    String getV2Responder() {
        File configFile = v2ResponderFile()
        if (configFile.exists())
            return configFile.text
        String config = gazellePull.getV2Responder()
        configFile.write(config)
        return config
    }

    File configFile() {
        new File(cache, "Configs.csv")
    }

    File oidsFile() {
        new File(cache, "AllOids.csv")
    }

    File singleConfigFile(String systemName) {
        new File(cache, systemName + '.csv')
    }

    File v2ResponderFile() {
        new File(cache, 'V2Resp.csv')
    }
}
