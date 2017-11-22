package gov.nist.toolkit.fhir.simulators.mhd

import java.text.SimpleDateFormat

class DateTransform {
    static String dtmPattern = 'yyyyMMddHHmmss'
    static fhirPattern = "yyyy-MM-dd'T'HH:mm:ss"
    static SimpleDateFormat dtmFormat = new SimpleDateFormat('yyyyMMddHHmmss')
    static SimpleDateFormat fhirFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    static String dtmToFhir(String dtm) {
        int dsize = dtm.size()
        Date date = dtmToDate(dtm)
        int fsize = fsizeFromDsize(dsize)
        return fhirFormat.format(date).substring(0, fsize)
    }

    static Date dtmToDate(String dtm) {
        int dsize = dtm.size()
        String pattern = dtmPattern.substring(0, dsize)
        SimpleDateFormat dtmFormat = new SimpleDateFormat(pattern)
        dtmFormat.parse(dtm)
    }

    static String fhirToDtm(String fhir) {
        int fsize = (fhir.size() > 10) ? fhir.size() + 2 : fhir.size()
        String pattern = fhirPattern.substring(0, fsize)
        SimpleDateFormat fhirFormat = new SimpleDateFormat(pattern)
        Date date = fhirFormat.parse(fhir)
        int dsize = dsizeFromFsize(fsize)
        return dtmFormat.format(date).substring(0, dsize)
    }

    static int dsizeMax = dtmPattern.size()
    static int dsizeFromFsize(int fsize) {
        switch (fsize) {
            case 4 : return 4
            case 7: return 6
            case 10: return 8
            case 16: return 10
            case 18: return 12
            default: return dsizeMax
        }
    }

    static int fsizeMax = "yyyy-mm-ddThh:mm:ss".size()
    static int fsizeFromDsize(int dsize) {
        switch (dsize) {
            case 4: return 4
            case 6: return 7
            case 8: return 10
            case 12: return 16
            default: return fsizeMax
        }
    }

}
