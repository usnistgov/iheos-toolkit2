package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.QueryFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatusFieldFilterSelector extends Widget implements QueryFilter, IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> {
    public static final String URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
    public static final String URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
    HorizontalPanel hp = new HorizontalPanel();

    final static String approvedLabelString = "Approved";
    final static String deprecatedLabelString = "Deprecated";
    static final String bothLabelString = "Both";

    private final HTML approvedCountLabel = new HTML();
    private final HTML deprecatedCountLabel = new HTML();
    private final HTML bothCountLabel = new HTML();

    private Map<IndexFieldValue, HTML> countLabelMap = new HashMap<>();

    private SimpleCallbackT valueChangeNotification;

    private List<DocumentEntry> result = new ArrayList<>();

    public StatusFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        this.valueChangeNotification = valueChangeNotification;

        hp.add(new RadioButton(label, approvedLabelString));
        hp.add(approvedCountLabel);
        hp.add(new RadioButton(label, deprecatedLabelString));
        hp.add(deprecatedCountLabel);
        RadioButton all = new RadioButton(label, bothLabelString);
        all.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                                      @Override
                                      public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                                            doValueChangeNotification(new NewSelectedFieldValue(StatusFieldFilterSelector.this, getSelectedValues()));
                                      }
                                  });
                hp.add(all);
        hp.add(bothCountLabel);


        mapFieldValueToCountLabel();
    }

    @Override
    public List<DocumentEntry> getResult() {
        return result;
    }

    @Override
    public DocumentEntryIndexField getFieldType() {
        return DocumentEntryIndexField.STATUS;
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
                if (approvedLabelString.equals(rb.getText())) status.add(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED);
                else if (deprecatedLabelString.equals(rb.getText())) status.add(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED);
                else if (bothLabelString.equals(rb.getText())) {
                    status.add(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED);
                    status.add(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED);
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
        countLabelMap.put(new IndexFieldValue(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED), approvedCountLabel);
        countLabelMap.put(new IndexFieldValue(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED), deprecatedCountLabel);
        countLabelMap.put(new IndexFieldValue(bothLabelString), bothCountLabel);
    }
    @Override
    public void doUpdateCount(IndexFieldValue fieldValue, int count) {
        if (!countLabelMap.containsKey(fieldValue)) {
            GWT.log("Error: there is no Counter label defined for this value: " + fieldValue.toString());
        } else {
            countLabelMap.get(fieldValue).setText(Integer.toString(count));
        }
    }

    @Override
    public void doValueChangeNotification(NewSelectedFieldValue newSelectedValue) {
       valueChangeNotification.run(newSelectedValue);
    }

}
