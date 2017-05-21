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
public class ReturnTypeFilter extends Widget implements QueryFilter {
    HorizontalPanel hp = new HorizontalPanel();

    static final String leafClassString = "LeafClass";
    static final String objectRefString = "ObjectRef";

    public ReturnTypeFilter(String label) {
        RadioButton lc = new RadioButton(label, leafClassString);
        lc.setValue(true);
        hp.add(lc);
        hp.add(new RadioButton(label, objectRefString));
    }

    public Widget asWidget() { return hp; }

    @Override
    public void addToCodeSpec(Map<String, List<String>> codeSpec, String codeType) {
        List<String> status = new ArrayList<>();
        codeSpec.put(codeType, status);
        for (int i=0; i<hp.getWidgetCount(); i++) {
            RadioButton rb = (RadioButton) hp.getWidget(i);
            if (rb.getValue()) {
                if (leafClassString.equals(rb.getText())) status.add("LeafClass");
                if (objectRefString.equals(rb.getText())) status.add("ObjectRef");
            }
        }
    }
}
