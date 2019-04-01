package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatusFieldFilterSelector extends Widget implements IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> {
    public static final String URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
    public static final String URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
    FlowPanel fp = new FlowPanel();

    static final String approvedLabelString = "Approved";
    static final String deprecatedLabelString = "Deprecated";
    static final String unknownLabelString = "Unknown";

    private final RadioButton approvedRb;
    private final RadioButton deprecatedRb;
    private final RadioButton unknownRb;
    private final HTML approvedCountLabel = new HTML();
    private final HTML deprecatedCountLabel = new HTML();
    private final HTML unknownCountLabel = new HTML();

    private Map<IndexFieldValue, HTML> countLabelMap = new HashMap<>();
    private Map<IndexFieldValue, Integer> unknownFieldValueCountMap = new HashMap<>();

    private SimpleCallbackT valueChangeNotification;

    private List<DocumentEntry> result = new ArrayList<>();

    public StatusFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        this.valueChangeNotification = valueChangeNotification;

        approvedRb = new RadioButton(label, approvedLabelString);
        deprecatedRb = new RadioButton(label, deprecatedLabelString);
        unknownRb = new RadioButton(label, unknownLabelString);

        ValueChangeHandler<Boolean> valueChangeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                doValueChangeNotification(new NewSelectedFieldValue(StatusFieldFilterSelector.this, getSelectedValues(), false, false));
            }
        };

        HTML selectorLabel = new HTML(label);
        selectorLabel.addStyleName("inlineBlock");
        fp.add(selectorLabel);
        fp.add(approvedRb);
        approvedRb.addValueChangeHandler(valueChangeHandler);
        approvedCountLabel.addStyleName("inlineBlock");
        approvedCountLabel.addStyleName("labelMarginLeft");
        fp.add(approvedCountLabel);
        fp.add(deprecatedRb);
        deprecatedRb.addValueChangeHandler(valueChangeHandler);
        deprecatedCountLabel.addStyleName("inlineBlock");
        deprecatedCountLabel.addStyleName("labelMarginLeft");
        fp.add(deprecatedCountLabel);
        unknownRb.setVisible(false);
        unknownCountLabel.addStyleName("inlineBlock");
        unknownCountLabel.addStyleName("labelMarginLeft");
        unknownCountLabel.setVisible(false);
        unknownRb.addValueChangeHandler(valueChangeHandler);
        fp.add(unknownRb);

        HTML clearSelectionLabel = new HTML("Clear");
        clearSelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        clearSelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        clearSelectionLabel.addStyleName("roundedButton3");
        clearSelectionLabel.addStyleName("inlineBlock");
        clearSelectionLabel.addClickHandler(new ClickHandler() {
                                                @Override
                                                public void onClick(ClickEvent clickEvent) {
                                                    for (int i = 0; i< fp.getWidgetCount(); i++) {
                                                        Widget w =  fp.getWidget(i);
                                                        if (w instanceof RadioButton) {
                                                            RadioButton rb = (RadioButton) w;
                                                            rb.setValue(false);
                                                        }
                                                    }
                                                    doValueChangeNotification(new NewSelectedFieldValue(StatusFieldFilterSelector.this, null, false, true));
                                                 }
                                            });
        fp.add(clearSelectionLabel);
        fp.add(new HTML("<br/>"));

        mapFieldValuesToCounterLabel();
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

    public Widget asWidget() { return fp; }


    @Override
    public Set<IndexFieldValue> getSelectedValues() {
        Set<IndexFieldValue> values = new HashSet<>();
        if (approvedRb.getValue()) values.add(new IndexFieldValue(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED));
        else if (deprecatedRb.getValue()) values.add(new IndexFieldValue(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED));
        else if (unknownRb.getValue()) {
            values.addAll(unknownFieldValueCountMap.keySet());
        }
        return values;
    }


    @Override
    public void mapFieldValuesToCounterLabel() {
        countLabelMap.put(new IndexFieldValue(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_APPROVED), approvedCountLabel);
        countLabelMap.put(new IndexFieldValue(URN_OASIS_NAMES_TC_EBXML_REGREP_STATUS_TYPE_DEPRECATED), deprecatedCountLabel);
    }
    @Override
    public void doUpdateCount(IndexFieldValue fieldValue, int count) {
        if (!countLabelMap.containsKey(fieldValue)) {
            // Unknown
            unknownRb.setVisible(true);
            unknownCountLabel.setVisible(true);
           unknownFieldValueCountMap.put(fieldValue, count);
           setUnknownCount(unknownCountLabel);
        } else {
            countLabelMap.get(fieldValue).setText(Integer.toString(count));
        }
    }

    private int setUnknownCount(HTML label) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        if (!unknownFieldValueCountMap.isEmpty()) {
            for (IndexFieldValue ifv : unknownFieldValueCountMap.keySet()) {
                count += unknownFieldValueCountMap.get(ifv);
                sb.append("ifv: " +ifv.toString() + " count: " + unknownFieldValueCountMap.get(ifv));
            }
        }
        label.setText(sb.toString());
        return count;
    }

    @Override
    public void doValueChangeNotification(NewSelectedFieldValue newSelectedValue) {
       valueChangeNotification.run(newSelectedValue);
    }

    @Override
    public void addResult(List<DocumentEntry> result) {
        this.result.addAll(result);
    }

    @Override
    public void clearResult() {
        this.result.clear();
        for (IndexFieldValue ifv : countLabelMap.keySet()) {
            countLabelMap.get(ifv).setText("0");
        }
        unknownCountLabel.setText("");
    }
}
