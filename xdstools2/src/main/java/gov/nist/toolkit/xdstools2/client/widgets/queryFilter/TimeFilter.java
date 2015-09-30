package gov.nist.toolkit.xdstools2.client.widgets.queryFilter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/28/2015.
 */
public class TimeFilter extends Widget implements QueryFilter {
    HorizontalPanel hp = new HorizontalPanel();

    // label is required non-empty but is never displayed
    public TimeFilter(String label) {
        DateBox box = new DateBox();
        DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
        box.setFormat(new DateBox.DefaultFormat(format));
        //box.setToolTip("This value is required. The format of these values is defined as following: YYYY[MM[DD[hh[mm[ss]]]]]; YYYY is the four digit year (ex: 2014); MM is the two digit month 01-12, where January is 01, December is 12; DD is the two digit day of the month 01-31; HH is the two digit hour, 00-23, where 00 is midnight, 01 is 1 am, 12 is noon, 13 is 1 pm; mm is the two digit minute, 00-59; ss is the two digit seconds, 00-59");
        //box.setValue("YYYY[MM[DD[hh[mm[ss]]]]] (ex: 201103160830)");
       hp.add(box);
    }

    public Widget asWidget() { return hp; }

    @Override
    /**
     * Adds the values selected by the user to a list of accepted values for the document search
     */
    public void addToCodeSpec(Map<String, List<String>> codeSpec, String codeType) {
        codeSpec.put(codeType, getValues());
    }

    List<String> getValues() {
        List<String> times = new ArrayList<>();
        //TODO generate times between two dates
        return times;
    }
}
