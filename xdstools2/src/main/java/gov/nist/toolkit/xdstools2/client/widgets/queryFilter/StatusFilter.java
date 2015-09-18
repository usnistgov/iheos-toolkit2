package gov.nist.toolkit.xdstools2.client.widgets.queryFilter;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bill on 9/1/15.
 */
public class StatusFilter extends Widget implements QueryFilter {
    HorizontalPanel hp = new HorizontalPanel();

    final static String approvedString = "Approved";
    final static String deprecatedString = "Deprecated";
    static final String bothString = "Both";

    // label is required non-empty but is never displayed
    public StatusFilter(String label) {
        hp.add(new RadioButton(label, approvedString));
        hp.add(new RadioButton(label, deprecatedString));
        RadioButton all = new RadioButton(label, bothString);
        all.setValue(true);
        hp.add(all);
    }

    public Widget asWidget() { return hp; }

    @Override
    public void addToCodeSpec(Map<String, List<String>> codeSpec, String codeType) {
        codeSpec.put(codeType, getValues());
    }

    List<String> getValues() {
        List<String> status = new ArrayList<>();
        for (int i=0; i<hp.getWidgetCount(); i++) {
            RadioButton rb = (RadioButton) hp.getWidget(i);
            if (rb.getValue()) {
                if (approvedString.equals(rb.getText())) status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
                else if (deprecatedString.equals(rb.getText())) status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated");
                else if (bothString.equals(rb.getText())) {
                    status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
                    status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated");
                }
            }
        }
        return status;
    }
}
