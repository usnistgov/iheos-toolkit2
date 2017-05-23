package gov.nist.toolkit.server.server.gazelle.sysconfig
/**
 *
 */
class V2ResponderParser {
    Collection<V2ResponderDef> values = []

    int size() {
        return values.size()
    }

    V2ResponderDef get(int i) {
        return values[i]
    }

    V2ResponderDef get(String system) {
        if (system == null) return null;
        for (V2ResponderDef value : values) {
            if (system == value.system && !value.secured) {
                return value
            }
        }
        return null
    }

    def parse(String input) {
        java.nio.file.Paths.get(input).withReader { reader ->
            org.apache.commons.csv.CSVParser csv = new org.apache.commons.csv.CSVParser(reader, org.apache.commons.csv.CSVFormat.DEFAULT.withHeader())
            csv.iterator().each { record ->
                def item =     ([configType         : record.'Configuration Type',
                                 company        : record.Company,
                                 system     : record.System,
                                 host     : record.Host,
                                 actor  : record.Actor,
                                 secured   : asBoolean(record.'is secured'),
                                 approved : asBoolean(record.'is approved'),
                                 comment : record.comment,
                                 assigningAuthority : record.assigningAuthority,
                                 rcvApplication : record.'receiving application',
                                 rcvFacility : record.'receiving facility',
                                 port : record.port,
                                 proxyPort : record.'port proxy',
                                 portSecured : record.'port secured'
                ])
                values.add(new V2ResponderDef(item))
            }
        }
    }

    def asBoolean(String x) {
        x.toLowerCase() == 'true'
    }


}
