package gov.nist.toolkit.xdstools2.client.widgets.queryFilter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/28/2015.
 */
public class TimeFilter extends Widget implements QueryFilter {
    HorizontalPanel hp = new HorizontalPanel();
    Label errorLabel;
    DateBox box;

    // label is required non-empty but is never displayed
    public TimeFilter(Label error_label, String label) {
        errorLabel = error_label;

        box = new DateBox();
        DateTimeFormat uiViewFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
        box.setFormat(new DateBox.DefaultFormat(uiViewFormat));
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

    /**
     * Converts the date entered to the HL7DTM format accepted by the server
     * @return the array of possible values. Here, the array only contains one date. However, it still exists for
     * compability with the rest of the data model.
     */
    List<String> getValues() {
        List<String> times = new ArrayList<>();

        Date date = box.getValue();
        DateTimeFormat hl7DTM = DateTimeFormat.getFormat("yyyyMMddHHmm");
        String dateStr = hl7DTM.format(date);

        // TODO Check the format of the Date entered by the user, use regex in Shared classes (not supported in client)
        // , use the DocEntryEditor Shared Model classes

        // TODO Display an error, later, when date validation is added
        //errorLabel.setText(e.getMessage());

        times.add(dateStr);
        return times;
    }
}
