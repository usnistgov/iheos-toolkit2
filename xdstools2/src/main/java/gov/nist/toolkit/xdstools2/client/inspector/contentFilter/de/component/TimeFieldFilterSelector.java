package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TimeFieldFilterSelector extends IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> {
    FlowPanel fp = new FlowPanel();
    DateBox fromDtBox;
    DateBox toDtBox;

    private List<IndexFieldValue> dates = new ArrayList<>();
    private HTML earliestDateLabel = new HTML();
    private HTML lastDateLabel = new HTML();
    private HTML matchingItems = new HTML();

    DateTimeFormat hl7DTM = DateTimeFormat.getFormat("yyyyMMddHHmm");

    private SimpleCallbackT valueChangeNotification;

    List<DocumentEntry> result = new ArrayList<>();

    public TimeFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        this.valueChangeNotification = valueChangeNotification;

        HTML selectorLabel = new HTML(label);
        selectorLabel.addStyleName("inlineBlock");
        fp.add(selectorLabel);

        HTML fromLabel = new HTML("&nbsp;From: ");
        fromLabel.addStyleName("inlineBlock");
        fp.add(fromLabel);

        DateTimeFormat uiViewFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
        fromDtBox = new DateBox();
        fromDtBox.addStyleName("inlineBlock");
        fromDtBox.setFormat(new DateBox.DefaultFormat(uiViewFormat));
        fp.add(fromDtBox);

        HTML toLabel = new HTML("&nbsp;To: ");
        toLabel.addStyleName("inlineBlock");
        fp.add(toLabel);

        toDtBox = new DateBox();
        toDtBox.addStyleName("inlineBlock");
        toDtBox.setFormat(new DateBox.DefaultFormat(uiViewFormat));
        fp.add(toDtBox);

        // Apply
        HTML applySelectionLabel = new HTML("Apply");
        applySelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        applySelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        applySelectionLabel.addStyleName("roundedButton3");
        applySelectionLabel.addStyleName("inlineBlock");
        applySelectionLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                doValueChangeNotification(new NewSelectedFieldValue(TimeFieldFilterSelector.this, getSelectedValues(), false, false));
            }
        });
        fp.add(applySelectionLabel);


        // Clear
        HTML clearSelectionLabel = new HTML("Clear");
        clearSelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        clearSelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        clearSelectionLabel.addStyleName("roundedButton3");
        clearSelectionLabel.addStyleName("inlineBlock");
        clearSelectionLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                fromDtBox.setValue(null);
                toDtBox.setValue(null);
                doValueChangeNotification(new NewSelectedFieldValue(TimeFieldFilterSelector.this, null, false, true));
            }
        });
        fp.add(clearSelectionLabel);

        fp.add(new HTML("&nbsp;"));

        HTML earliestHeader = new HTML("Earliest available date is:&nbsp;");
        earliestHeader.addStyleName("inlineBlock");
        fp.add(earliestHeader);
        earliestDateLabel.addStyleName("inlineBlock");
        fp.add(earliestDateLabel);
        fp.add(new HTML("&nbsp;"));
        HTML lastDateHeader = new HTML("Last available date is:&nbsp;");
        lastDateHeader.addStyleName("inlineBlock");
        fp.add(lastDateHeader);
        lastDateLabel.addStyleName("inlineBlock");
        fp.add(lastDateLabel);
        fp.add(new HTML("&nbsp;"));
        HTML matchingHeader = new HTML("Matching items:&nbsp;");
        matchingHeader.addStyleName("inlineBlock");
        fp.add(matchingHeader);
        matchingItems.addStyleName("inlineBlock");
        fp.add(matchingItems);

        fp.add(new HTML("<br/>"));

    }

    public Widget asWidget() { return fp; }



    @Override
    public List<DocumentEntry> getResult() {
        return result;
    }

    @Override
    public void addResult(List<DocumentEntry> result) {
        this.result.addAll(result);
    }

    @Override
    public void clearResult() {
        result.clear();
        earliestDateLabel.setText("");
        lastDateLabel.setText("");
        matchingItems.setText("");
    }

    @Override
    public void doUpdateCount(IndexFieldValue fieldValue, int count) {
        if (count>0 && dates.size()>0) {
            Collections.sort(dates);
            earliestDateLabel.setText(dates.get(0).toString());
            lastDateLabel.setText(dates.get(dates.size()-1).toString());
            matchingItems.setText(Integer.toString(count));
        } else {
            earliestDateLabel.setText("");
            lastDateLabel.setText("");
            matchingItems.setText("0");
        }
    }


    @Override
    public Set<IndexFieldValue> getSelectedValues() {
        Set<IndexFieldValue> values = new HashSet<>();
        values.add(new IndexFieldValue(getFromDt()));
        values.add(new IndexFieldValue(getToDt()));

        return values;
    }

    public String getFromDt() {
        if (fromDtBox.getValue()!=null)
             return hl7DTM.format(fromDtBox.getValue());
        else
            return "";
    }

    public String getToDt() {
        if (toDtBox.getValue()!=null)
            return hl7DTM.format(toDtBox.getValue());
        else
            return "";
    }

    @Override
    public void mapFieldValuesToCounterLabel() {

    }

    @Override
    public void doValueChangeNotification(NewSelectedFieldValue newSelectedValue) {
        valueChangeNotification.run(newSelectedValue);
    }

    @Override
    public boolean isDeferredIndex() {
        return true;
    }


    @Override
    public List<DocumentEntry> filter(List<DocumentEntry> inputList) {
        dates.clear();
        List<DocumentEntry> result = new ArrayList<>();
        String fromDt = getFromDt();
        String toDt = getToDt();

        result.addAll(filterByTime(fromDt, toDt, inputList));
        doUpdateCount(null, result.size());
        return result;
    }

    public abstract String getTimeFieldValue(DocumentEntry de);


    public List<DocumentEntry> filterByTime(String from, String to, List<DocumentEntry> docs) {
        List<DocumentEntry> result = new ArrayList<>();
        result.addAll(docs);
        if (result.isEmpty()) return result;

            for (int i=0; i<result.size(); i++) {
                DocumentEntry de = result.get(i);
                String timeFieldValue = getTimeFieldValue(de);
                dates.add(new IndexFieldValue(timeFieldValue));

                if (! ((from == null || from.equals("")) && (to == null || to.equals("")))) {
                    if (timeCompare(timeFieldValue, from, to))
                        continue;
                    result.remove(i);
                    i--;
                }
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
