package gov.nist.toolkit.docAdmin.util

/**
 *
 */
class ListUtil {

    static int compare(List a, List b) {
        if (a.size() < b.size()) return -1
        if (a.size() > b.size()) return 1
        for (int i=0; i<a.size(); i++) {
            int x = a[i].compareTo(b[i])
            if (x != 0) return x
        }
        return 0
    }
}
