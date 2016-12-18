package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import org.apache.commons.csv.CSVParser

import java.nio.file.Paths

import static org.apache.commons.csv.CSVFormat.DEFAULT
/**
 *
 */
class ConfigParser {

    def all=[]

    int size() {
        return all.size()
    }

    ConfigDef get(int i) {
        return all[i]
    }

    def parse(String input) {
        Paths.get(input).withReader { reader ->
            CSVParser csv = new CSVParser(reader, DEFAULT.withHeader())
            csv.iterator().each { record ->
                all <<     ([configType         : record.'Configuration Type',
                             company        : record.Company,
                             system     : record.System,
                             host     : record.Host,
                             actor  : record.Actor,
                             secured   : record.'is secured' as Boolean,
                             approved : record.'is approved' as Boolean,
                             comment : record.comment,
                             url : record.url,
                             assigningAuthority : record.assigningAuthority,
                             wsType : record.'ws-type',
                             port : record.port,
                             proxyPort : record.'port proxy',
                             portSecured : record.'port secured'
                        ])
            }
        }
    }
}
