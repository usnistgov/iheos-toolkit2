package gov.nist.toolkit.desktop.client.legacy.widgets.queryFilter;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bill on 9/1/15.
 */
public class OnDemandFilter extends Widget implements QueryFilter  {
    HorizontalPanel hp = new HorizontalPanel();

    static final String stableString = "Stable";
    static final String onDemandString = "On-Demand";
    String bothString = "Both";

    public OnDemandFilter(String label) {
       init(label, null);
    }
    public OnDemandFilter(String label, String newBothString) {
        init(label, newBothString);
    }

    protected void init(String label, String newBothString) {
        RadioButton _static = new RadioButton(label, stableString);
        hp.add(_static);
        hp.add(new RadioButton(label, onDemandString));

        if (newBothString!=null && !bothString.equals(newBothString)) {
            bothString = newBothString;
        }

        RadioButton both = new RadioButton(label, bothString);
        both.setValue(true);
        hp.add(both);
    }

    public Widget asWidget() { return hp; }

    @Override
    public void addToCodeSpec(Map<String, List<String>> codeSpec, String codeType) {
        List<String> status = new ArrayList<>();
        codeSpec.put(codeType, status);
        for (int i=0; i<hp.getWidgetCount(); i++) {
            RadioButton rb = (RadioButton) hp.getWidget(i);
            if (rb.getValue()) {
                if (stableString.equals(rb.getText())) status.add("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
                if (onDemandString.equals(rb.getText())) status.add("urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248");
                if (bothString.equals(rb.getText())) {
                    status.add("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
                    status.add("urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248");
                }
            }
        }
    }
}
