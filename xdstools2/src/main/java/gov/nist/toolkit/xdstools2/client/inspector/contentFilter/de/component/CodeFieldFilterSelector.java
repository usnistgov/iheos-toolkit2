package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.CodeConfiguration;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.CodePicker;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nist.toolkit.http.client.HtmlMarkup.red;

public abstract class CodeFieldFilterSelector extends IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry>  {
    FlowPanel fp = new FlowPanel();

    private final ListBox inputCodeList = new ListBox();
    private final HTML matchingItems = new HTML();

    private SimpleCallbackT valueChangeNotification;

    private List<DocumentEntry> result = new ArrayList<>();

    VerticalPanel resultPanel = new VerticalPanel();
    HTML statusBox = new HTML();

    private final String codeName;

    StatusDisplay statusDisplay = new StatusDisplay() {
        @Override
        public VerticalPanel getResultPanel() {
            return resultPanel;
        }

        @Override
        public void setStatus(String message, boolean status) {
            statusBox.setHTML(HtmlMarkup.bold(red(message, status)));
        }
    };

    public CodeFieldFilterSelector(String label, String codeName, SimpleCallbackT valueChangeNotification) {
        this.codeName = codeName;
        this.valueChangeNotification = valueChangeNotification;

        ValueChangeHandler<Boolean> valueChangeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                doValueChangeNotification(new NewSelectedFieldValue(CodeFieldFilterSelector.this, getSelectedValues()));
            }
        };

        HTML selectorLabel = new HTML(label);
        selectorLabel.addStyleName("inlineBlock");
        fp.add(selectorLabel);
        inputCodeList.addStyleName("uiSpacerMarginLeft");
        inputCodeList.setVisibleItemCount(2);
        fp.add(inputCodeList);

        HTML editSelectionLabel = new HTML("Edit");
        editSelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        editSelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        editSelectionLabel.addStyleName("roundedButton3");
        editSelectionLabel.addStyleName("inlineBlock");
        editSelectionLabel.addClickHandler(new ClickHandler() {
                                               @Override
                                               public void onClick(ClickEvent clickEvent) {
                                                   try {
                                                       new CodePicker(getCodesFromResult(), inputCodeList).show();
                                                   } catch (Exception e) {
                                                       statusDisplay.setStatus(e.getMessage(), false);
                                                   }
                                               }
                                           });
        fp.add(editSelectionLabel);

        // Apply
        HTML applySelectionLabel = new HTML("Apply");
        applySelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        applySelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        applySelectionLabel.addStyleName("roundedButton3");
        applySelectionLabel.addStyleName("inlineBlock");
        applySelectionLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                doValueChangeNotification(new NewSelectedFieldValue(CodeFieldFilterSelector.this, getSelectedValues()));
            }
        });
        fp.add(applySelectionLabel);

        HTML clearSelectionLabel = new HTML("Clear");
        clearSelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        clearSelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        clearSelectionLabel.addStyleName("roundedButton3");
        clearSelectionLabel.addStyleName("inlineBlock");
        clearSelectionLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                inputCodeList.clear();
                doValueChangeNotification(new NewSelectedFieldValue(CodeFieldFilterSelector.this, null));
            }
        });
        fp.add(clearSelectionLabel);

        fp.add(new HTML("&nbsp;"));
        HTML matchingHeader = new HTML("Matching items:&nbsp;");
        matchingHeader.addStyleName("inlineBlock");
        fp.add(matchingHeader);
        matchingItems.addStyleName("inlineBlock");
        fp.add(matchingItems);

        fp.add(new HTML("&nbsp;"));
        resultPanel.add(statusBox);
        fp.add(resultPanel);

        fp.add(new HTML("<br/>"));

        mapFieldValuesToCounterLabel();

    }

    @Override
    public Widget asWidget() {
        return fp;
    }

    public abstract List<String> getFieldValue(DocumentEntry de);

    @Override
    public List<DocumentEntry> getResult() {
        return result;
    }

    @Override
    public void addResult(List<DocumentEntry> result) {
        this.result.addAll(result);
    }


    private CodeConfiguration getCodesFromResult() {
       CodeConfiguration cc = new CodeConfiguration();
        cc.name = codeName;
        cc.codes = new ArrayList<>();

        Set<String> codeSet = new HashSet<>();
        if (! result.isEmpty()) {
            for (DocumentEntry de : result) {
                codeSet.addAll(getFieldValue(de));
            }
            cc.codes.addAll(codeSet);
            Collections.sort(cc.codes);
        }

        return cc;
    }

    @Override
    public void clearResult() {
        this.result.clear();
        matchingItems.setText("0");
    }

    @Override
    public void doUpdateCount(IndexFieldValue fieldValue, int count) {
        matchingItems.setText(Integer.toString(count));
    }

    @Override
    public Set<IndexFieldValue> getSelectedValues() {
        Set<IndexFieldValue> values = new HashSet<>();
        for (int idx = 0; idx < inputCodeList.getItemCount(); idx++) {
            values.add(new IndexFieldValue(inputCodeList.getValue(idx)));
        }
        return values;
    }

    @Override
    public List<DocumentEntry> filter(List<DocumentEntry> inputList) {
        List<DocumentEntry> result = new ArrayList<>();
        result.addAll(filterByClassCode(inputList));
        doUpdateCount(null, result.size());
        return result;
    }

    public List<DocumentEntry> filterByClassCode(List<DocumentEntry> docs) {
        Set<IndexFieldValue> codes = getSelectedValues();
        if (docs.isEmpty()) return docs;
        if (codes == null || codes.isEmpty()) return docs;

        for (int i=0; i<docs.size(); i++) {
            DocumentEntry de = docs.get(i);
            List<IndexFieldValue> ifvList = IndexFieldValue.toIndexFieldValues(getFieldValue(de));
            if (Collections.disjoint(ifvList, codes)) {
                docs.remove(i);
                i--;
            }
        }
        return docs;
    }


    @Override
    public boolean isDeferredIndex() {
        return true;
    }

    @Override
    public void mapFieldValuesToCounterLabel() {
    }

    @Override
    public void doValueChangeNotification(NewSelectedFieldValue newSelectedValue) {
        valueChangeNotification.run(newSelectedValue);
    }
}
