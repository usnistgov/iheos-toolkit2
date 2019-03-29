package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.QueryFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatusFieldFilterSelector extends Widget implements QueryFilter, DocumentEntryFieldComponent {
    HorizontalPanel hp = new HorizontalPanel();

    final static String approvedString = "Approved";
    final static String deprecatedString = "Deprecated";
    static final String bothString = "Both";

    private final HTML approvedCountLabel = new HTML();
    private final HTML deprecatedCountLabel = new HTML();
    private final HTML bothCountLabel = new HTML();

    private DocumentEntryIndexField field = DocumentEntryIndexField.STATUS;
    private Map<IndexFieldValue, HTML> countLabelMap = new HashMap<>();

    private SimpleCallbackT valueChangeNotification;

    public StatusFieldFilterSelector(String label, SimpleCallbackT<NewSelectedValue> valueChangeNotification) {
        this.valueChangeNotification = valueChangeNotification;

        hp.add(new RadioButton(label, approvedString));
        hp.add(approvedCountLabel);
        hp.add(new RadioButton(label, deprecatedString));
        hp.add(deprecatedCountLabel);
        RadioButton all = new RadioButton(label, bothString);
        all.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                                      @Override
                                      public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                                            doValueChangeNotification(new NewSelectedValue(field, getSelectedValues()));
                                      }
                                  });
                hp.add(all);
        hp.add(bothCountLabel);


        mapFieldValueToCountLabel();
    }


    // skb TODO: create a constructor(label:string, impl:changeNotificationInterface)
    // store impl in local variable
    // on value change, return getValues(). This will be used to count the matching items.

    public Widget asWidget() { return hp; }

    @Override
    public void addToCodeSpec(Map<String, List<String>> codeSpec, String codeType) {
        codeSpec.put(codeType, getSelectedValues());
    }

    List<String> getSelectedValues() {
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


    @Override
    public Set<IndexFieldValue> getFieldValues() {
        return countLabelMap.keySet();
    }

    @Override
    public void mapFieldValueToCountLabel() {
        countLabelMap.put(new IndexFieldValue(approvedString), approvedCountLabel);
        countLabelMap.put(new IndexFieldValue(deprecatedString), deprecatedCountLabel);
        countLabelMap.put(new IndexFieldValue(bothString), bothCountLabel);
    }
    @Override
    public void doUpdateCount(IndexFieldValue fieldValue, int count) {
       countLabelMap.get(fieldValue).setText(Integer.toString(count));
    }

    @Override
    public void doValueChangeNotification(NewSelectedValue newSelectedValue) {
       valueChangeNotification.run(newSelectedValue);
    }
}
