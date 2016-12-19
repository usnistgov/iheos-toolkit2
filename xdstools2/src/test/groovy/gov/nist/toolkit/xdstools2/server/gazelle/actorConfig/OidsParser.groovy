package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import static org.apache.commons.csv.CSVFormat.DEFAULT

/**
 *
 */
class OidsParser {
    def all=[]

    int size() {
        return all.size()
    }

    OidDef get(int i) {
        return all[i]
    }

    def parse(String input) {
        java.nio.file.Paths.get(input).withReader { reader ->
            org.apache.commons.csv.CSVParser csv = new org.apache.commons.csv.CSVParser(reader, DEFAULT.withHeader())
            csv.iterator().each { record ->
                all <<     ([system         : record.'system',
                             type        : record.'oid requirement',
                             oid     : record.OID
                ])
            }
        }
    }

    boolean hasDef(String system, String type) {
        all.find { ele -> ele.system == system && ele.type == type }
    }

    String getOid(String system, String type) {
        OidDef odef = all.find { OidDef ele -> ele.system == system && ele.type == type }
        if (odef == null) return null;
        return odef.oid
    }
}
