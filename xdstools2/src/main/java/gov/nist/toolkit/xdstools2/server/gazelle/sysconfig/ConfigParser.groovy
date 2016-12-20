package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import org.apache.commons.csv.CSVParser

import java.nio.file.Paths

import static org.apache.commons.csv.CSVFormat.DEFAULT

/**
 *
 */
class ConfigParser {

    Collection<ConfigDef> values = []

    int size() {
        return values.size()
    }

    ConfigDef get(int i) {
        return values[i]
    }

    def parse(String input) {
        Paths.get(input).withReader { reader ->
            CSVParser csv = new CSVParser(reader, DEFAULT.withHeader())
            csv.iterator().each { record ->
                def item =     ([configType         : record.'Configuration Type',
                             company        : record.Company,
                             system     : record.System,
                             host     : record.Host,
                             actor  : record.Actor,
                             secured   : asBoolean(record.'is secured'),
                             approved : asBoolean(record.'is approved'),
                             comment : record.comment,
                             url : record.url,
                             assigningAuthority : record.assigningAuthority,
                             wsType : record.'ws-type',
                             port : record.port,
                             proxyPort : record.'port proxy',
                             portSecured : record.'port secured'
                        ])
                values.add(new ConfigDef(item))
            }
        }
    }

    def asBoolean(String x) {
        x.toLowerCase() == 'true'
    }
}
