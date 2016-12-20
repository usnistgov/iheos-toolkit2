package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import static org.apache.commons.csv.CSVFormat.DEFAULT

/**
 *
 */
class OidsParser {
//    def all=[]
    Collection<OidDef> values = []

    int size() {
        return values.size()
    }

    OidDef get(int i) {
        return values[i]
    }

    def parse(String input) {
        java.nio.file.Paths.get(input).withReader { reader ->
            org.apache.commons.csv.CSVParser csv = new org.apache.commons.csv.CSVParser(reader, DEFAULT.withHeader())
            csv.iterator().each { record ->
                def item =     ([system         : record.'system',
                             type        : record.'oid requirement',
                             oid     : record.OID
                ])
                values.add(new OidDef(item))
            }
        }
    }

    boolean hasDef(String system, String type) {
        values.find { ele -> ele.system == system && ele.type == type }
    }

    String getOid(String system, String type) {
        OidDef odef = values.find { OidDef ele -> ele.system == system && ele.type == type }
        if (odef == null) return null;
        return odef.oid
    }
}
