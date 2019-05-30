package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.Author;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.AuthorPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthorFieldFilterSelector extends IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> {
    FlowPanel fp = new FlowPanel();

    private final ListBox inputAuthorList = new ListBox();
    private final HTML matchingItems = new HTML();

    private Map<IndexFieldValue, HTML> countLabelMap = new HashMap<>();

    private SimpleCallbackT valueChangeNotification;

    private List<DocumentEntry> result = new ArrayList<>();

    public AuthorFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        this.valueChangeNotification = valueChangeNotification;

        ValueChangeHandler<Boolean> valueChangeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                doValueChangeNotification(new NewSelectedFieldValue(AuthorFieldFilterSelector.this, getSelectedValues()));
            }
        };

        HTML selectorLabel = new HTML(label);
        selectorLabel.addStyleName("labelWidthFmt");
        selectorLabel.addStyleName("inlineBlock");
        fp.add(selectorLabel);
        inputAuthorList.setVisibleItemCount(2);
        fp.add(inputAuthorList);

        HTML editSelectionLabel = new HTML("Edit");
        editSelectionLabel.getElement().getStyle().setMarginTop(12, Style.Unit.PX);
        editSelectionLabel.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);
        editSelectionLabel.addStyleName("roundedButton3");
        editSelectionLabel.addStyleName("inlineBlock");
        editSelectionLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                String title = "Enter Author Person(s) Names (use % to match any characters and _ to match a single character). The string must be formatted as ^first^last^^^.:";
                try {
                    new AuthorPicker(title, inputAuthorList).show();
                } catch (Exception ex) {
                    GWT.log("Error: " + ex.toString());
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
                doValueChangeNotification(new NewSelectedFieldValue(AuthorFieldFilterSelector.this, getSelectedValues()));
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
                                                    inputAuthorList.clear();
                                                    doValueChangeNotification(new NewSelectedFieldValue(AuthorFieldFilterSelector.this, null));
                                                 }
                                            });
        fp.add(clearSelectionLabel);

        fp.add(new HTML("&nbsp;"));
        HTML matchingHeader = new HTML("Matching items:&nbsp;");
        matchingHeader.addStyleName("inlineBlock");
        fp.add(matchingHeader);
        matchingItems.addStyleName("inlineBlock");
        fp.add(matchingItems);

        fp.add(new HTML("<br/>"));

        mapFieldValuesToCounterLabel();
    }

    @Override
    public List<DocumentEntry> getResult() {
        return result;
    }

    @Override
    public DocumentEntryIndexField getFieldType() {
        return DocumentEntryIndexField.AUTHOR_PERSON_NAME;
    }

    public Widget asWidget() { return fp; }


    @Override
    public Set<IndexFieldValue> getSelectedValues() {
        Set<IndexFieldValue> values = new HashSet<>();
        for (int idx = 0; idx < inputAuthorList.getItemCount(); idx++) {
            values.add(new IndexFieldValue(inputAuthorList.getValue(idx)));
        }
        return values;
    }


    @Override
    public void mapFieldValuesToCounterLabel() {
        countLabelMap.put(new IndexFieldValue("author"), matchingItems);
    }
    @Override
    public void doUpdateCount(IndexFieldValue fieldValue, int count) {
        matchingItems.setText(Integer.toString(count));
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
        matchingItems.setText("0");
    }

    @Override
    public boolean isDeferredIndex() {
        return true;
    }

    @Override
    public List<DocumentEntry> filter(List<DocumentEntry> inputList) {
        List<DocumentEntry> result = filterByAuthorPerson(inputList);
        doUpdateCount(null, result.size());
        return result;
    }

    // These utils were copied from the DocEntryCollection server side class
    public List<DocumentEntry> filterByAuthorPerson(List<DocumentEntry> docs) {
        Set<IndexFieldValue> authorPersons = getSelectedValues();
        if (docs.isEmpty()) return docs;
        if (authorPersons == null || authorPersons.isEmpty()) return docs;
        nextDoc:
        for (int i=0; i<docs.size(); i++) {
            DocumentEntry de = docs.get(i);
            for (Author author: de.authors) {
                if (matchAnyAuthor(author.person, authorPersons))
                    continue nextDoc;
            }
            docs.remove(i);
            i--;
        }
        return docs;
    }

    // _ match any character
    // % matches any string
    // this is too complicated to do with Java Regex ...
    private static boolean matchAnyAuthor(String value, Set<IndexFieldValue> authors) {
        for (IndexFieldValue author : authors) {
            if (matchAuthor(value, author.toString()))
                return true;
        }
        return false;
    }

    private static boolean matchAuthor(String value, String pattern) {
        int vi = 0;

        for (int pi=0; pi<pattern.length(); pi++) {
            if (pattern.charAt(pi) == '_') {
                vi++;
            } else if (pattern.charAt(pi) == '%') {
                String after = getAfterText(pattern, pi);
                int afterI = value.indexOf(after, vi);
                if (afterI == -1)
                    return false;
                vi = afterI;
            } else {
                if (pattern.charAt(pi) != value.charAt(vi))
                    return false;
                vi++;
            }
            if (pi + 1 == pattern.length() && pattern.charAt(pi) == '%')
                return true;
            if (pattern.length() == pi+1 && value.length() == vi)
                return true;
            if (pi + 2 == pattern.length() && pattern.charAt(pi + 1) == '%')
                return true;
            if (value.length() == vi)
                return false;
        }

        return false;
    }

    // return text after % char at startAt and before next % (or end if no next %)
    // expect initial % to possibly be %%
    private static String getAfterText(String pattern, int startAt) {
        while (pattern.charAt(startAt) == '%') {
            startAt++;
            if (startAt == pattern.length())
                return "";
        }

        int endAt = startAt;

        while(pattern.charAt(endAt) != '%') {
            endAt++;
            if (endAt == pattern.length())
                return pattern.substring(startAt);
        }
        return pattern.substring(startAt, endAt);
    }


}
