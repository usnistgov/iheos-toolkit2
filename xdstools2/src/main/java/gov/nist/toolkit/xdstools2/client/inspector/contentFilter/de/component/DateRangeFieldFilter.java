package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class DateRangeFieldFilter extends Widget {
    DateTimeFormat hl7DTM = DateTimeFormat.getFormat("yyyyMMddHHmm");
    public abstract String getFromDt();
    public abstract String getToDt();
    public abstract void setAvailableDates();

    public static List<DocumentEntry> filterByCreationTime(String from, String to, List<DocumentEntry> docs) {
        List<DocumentEntry> result = new ArrayList<>();
        result.addAll(docs);
        if (result.isEmpty()) return result;
        if ((from == null || from.equals("")) && (to == null || to.equals(""))) return result;
        for (int i=0; i<result.size(); i++) {
            DocumentEntry de = result.get(i);
            String creationTime = de.creationTime;
            if (timeCompare(creationTime, from, to))
                continue;
            result.remove(i);
            i--;
        }
        return result;
    }

    public static boolean timeCompare(String att, String from, String to) {
        if (att == null || att.equals(""))
            return false;
        if (from != null && !from.equals("")) {
            if ( !  (from.compareTo(att) <= 0))
                return false;
        }
        if (to != null && !to.equals("")) {
            if ( !  (att.compareTo(to) < 0))
                return false;
        }
        return true;
    }


}
