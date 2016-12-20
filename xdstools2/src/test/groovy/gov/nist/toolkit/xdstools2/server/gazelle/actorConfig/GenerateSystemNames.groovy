package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import org.apache.http.annotation.Obsolete

/**
 *
 */
class GenerateSystemNames {
    GazellePull gazellePull
    GazelleGet getter
    File cache

    GenerateSystemNames(GazellePull _gazellePull, File _cache) {
        gazellePull = _gazellePull
        cache = _cache
        getter = new GazelleGet(gazellePull, cache) // pull from Gazelle or cache

        getter.getAllConfigs()
        getter.getAllOids()
        getter.getV2Responder()
    }

    Set<String> getSystemNames() {
        ConfigParser parser = new ConfigParser()
        parser.parse(getter.configFile().toString())

        Set<String> systemNames = new HashSet<String>()

        parser.values.each { ConfigDef config ->
            systemNames.add(config.system)
        }
        return systemNames
    }

    @Obsolete
    GeneratedSystems generateAllSystems() {
        StringBuilder log = new StringBuilder()

        ConfigParser parser = new ConfigParser()
        parser.parse(getter.configFile().toString())

        Set<String> systemNames = new HashSet<String>()

        parser.values.each { ConfigDef config ->
            systemNames.add(config.system)
        }

        log.append('Systems: ').append(systemNames).append("\n\n")

        GeneratedSystems output = new GeneratedSystems()
        systemNames.each { String systemName ->
            if (systemName == 'PACS_synedra_2016')
                println systemName
            GenerateSingleSystem singleSystemGenerator = new GenerateSingleSystem(gazellePull, cache)
            GeneratedSystems gen = singleSystemGenerator.generate(systemName)
            if (!gen) return
            gen.systems.each { output.systems.add(it) }
            log.append(gen.log)
        }
        output.log = log

        return output
    }

    @Obsolete
    GeneratedSystems generateSingleSystem(String systemName) {
        new GenerateSingleSystem(gazellePull, cache).generate(systemName)
    }
}
