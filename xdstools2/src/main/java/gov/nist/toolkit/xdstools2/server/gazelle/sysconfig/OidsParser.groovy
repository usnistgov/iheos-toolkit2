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
                def item =     ([system         : trim(record.'system'),
                             type        : trim(record.'oid requirement'),
                             oid     : trim(record.OID)
                ])
                values.add(new OidDef(item))
            }
        }
    }

    def trim(String s) {
        String omits = '\n\t\r '
        while (s.size() > 0 && omits.indexOf(new String(s.charAt(0))) != -1)
            s = s.substring(1)
        while (s.size() > 0 && omits.indexOf(new String(s.charAt(s.size()-1))) != -1)
            s = s.substring(0, s.size()-1)
        return s
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
