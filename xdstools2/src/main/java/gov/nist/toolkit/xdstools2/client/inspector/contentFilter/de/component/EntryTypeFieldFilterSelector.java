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

public class EntryTypeFieldFilterSelector extends Widget implements IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> {
    public static final String URN_UUID_STABLE_DOCUMENT_ENTRY_TYPE = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";
    public static final String URN_UUID_ONDEMAND_DOCUMENT_ENTRY_TYPE = "urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248";
    FlowPanel fp = new FlowPanel();

    static final String stableLabelString = "Stable";
    static final String onDemandLabelString = "On-Demand";
    static final String unknownLabelString = "Unknown";

    private final RadioButton stableRb;
    private final RadioButton onDemandRb;
    private final RadioButton unknownRb;
    private final HTML stableCountLabel = new HTML();
    private final HTML onDemandCountLabel = new HTML();
    private final HTML unknownCountLabel = new HTML();

    private Map<IndexFieldValue, HTML> countLabelMap = new HashMap<>();
    private Map<IndexFieldValue, Integer> unknownFieldValueCountMap = new HashMap<>();

    private SimpleCallbackT valueChangeNotification;

    private List<DocumentEntry> result = new ArrayList<>();

    public EntryTypeFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        this.valueChangeNotification = valueChangeNotification;

        stableRb = new RadioButton(label, stableLabelString);
        onDemandRb = new RadioButton(label, onDemandLabelString);
        unknownRb = new RadioButton(label, unknownLabelString);

        ValueChangeHandler<Boolean> valueChangeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                doValueChangeNotification(new NewSelectedFieldValue(EntryTypeFieldFilterSelector.this, getSelectedValues()));
            }
        };

        HTML selectorLabel = new HTML(label);
        selectorLabel.addStyleName("inlineBlock");
        fp.add(selectorLabel);
        fp.add(stableRb);
        stableRb.addValueChangeHandler(valueChangeHandler);
        stableCountLabel.addStyleName("inlineBlock");
        fp.add(stableCountLabel);
        fp.add(onDemandRb);
        onDemandRb.addValueChangeHandler(valueChangeHandler);
        onDemandCountLabel.addStyleName("inlineBlock");
        fp.add(onDemandCountLabel);
        unknownRb.setVisible(false);
        unknownCountLabel.addStyleName("inlineBlock");
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
                                                    doValueChangeNotification(new NewSelectedFieldValue(EntryTypeFieldFilterSelector.this, null));
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
        return DocumentEntryIndexField.OBJECT_TYPE;
    }

    public Widget asWidget() { return fp; }


    @Override
    public Set<IndexFieldValue> getSelectedValues() {
        Set<IndexFieldValue> values = new HashSet<>();
        if (stableRb.getValue()) values.add(new IndexFieldValue(URN_UUID_STABLE_DOCUMENT_ENTRY_TYPE));
        else if (onDemandRb.getValue()) values.add(new IndexFieldValue(URN_UUID_ONDEMAND_DOCUMENT_ENTRY_TYPE));
        else if (unknownRb.getValue()) {
            values.addAll(unknownFieldValueCountMap.keySet());
        }
        return values;
    }


    @Override
    public void mapFieldValuesToCounterLabel() {
        countLabelMap.put(new IndexFieldValue(URN_UUID_STABLE_DOCUMENT_ENTRY_TYPE), stableCountLabel);
        countLabelMap.put(new IndexFieldValue(URN_UUID_ONDEMAND_DOCUMENT_ENTRY_TYPE), onDemandCountLabel);
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
            countLabelMap.get(ifv).setText("");
        }
        unknownCountLabel.setText("");
    }
}
