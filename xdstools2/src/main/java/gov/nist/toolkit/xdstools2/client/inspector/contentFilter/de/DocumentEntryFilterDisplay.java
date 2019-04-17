package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.inspector.CommonDisplay;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.FilterFeature;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component.*;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusDisplay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static gov.nist.toolkit.http.client.HtmlMarkup.red;

public abstract class DocumentEntryFilterDisplay extends CommonDisplay implements FilterFeature<DocumentEntry> {

    // Main body
    private FlowPanel featurePanel = new FlowPanel();
    private VerticalPanel previousPanel;
    // controls
    private TextBox pidTxt = new TextBox();
    private TextBox titleTxt = new TextBox();
    private TextBox commentsTxt = new TextBox();
    private TextBox languageCodeTxt = new TextBox();
    private TextBox legalAuthenticatorTxt = new TextBox();
    private ListBox sourcePatientInfoLBox = new ListBox();
    private Button applyFilterBtn;
    private Button removeFilterBtn;
    HTML statusBox = new HTML();
    VerticalPanel resultPanel = new VerticalPanel();
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

    private LinkedList<IndexFieldFilterSelector<DocumentEntryIndexField,DocumentEntry>> filterSelectors;

    private List<DocumentEntry> initialDeList;
    SimpleCallbackT<NewSelectedFieldValue> valueChangeCallback;

    public DocumentEntryFilterDisplay() {

        filterSelectors = new LinkedList<>();


        valueChangeCallback =  new SimpleCallbackT<NewSelectedFieldValue>() {
            @Override
            public void run(NewSelectedFieldValue newSelectedValue) {
                // 1. reIndex
//                filterSelectors.listIterator()
                int idx = filterSelectors.indexOf(newSelectedValue.getFilterSelector());
                ListIterator<IndexFieldFilterSelector<DocumentEntryIndexField,DocumentEntry>> it = filterSelectors.listIterator(idx);
//                GWT.log("idx: " + idx);
                if (it != null) {
                    List<DocumentEntry> list = null;
                    if (it.hasPrevious()) {
                        list = it.previous().getResult();
                        it.next(); // forward to the actual selector that raised this event
                    } else if (idx == 0) {
                        list = new ArrayList<>();
                        list.addAll(initialDeList);
                    }
                    if (list != null) {
                        if (list.isEmpty()) {
                            while (it.hasNext()) {
                                IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> selector = it.next();
                                selector.clearResult();
                            }
                        } else {
                            List<DocumentEntry> filteredResult = new ArrayList<>();
                            filteredResult.addAll(list);
                            while (it.hasNext()) {
                                IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> selector = it.next();
//                                GWT.log("Selector: " + selector.getFieldType().name());
                                selector.clearResult();
                                if (selector.isDeferredIndex()) {
                                    List<DocumentEntry> deList = selector.filter(filteredResult);
                                    selector.addResult(deList);
                                } else {
                                    Map<DocumentEntryIndexField, Map<IndexFieldValue, List<DocumentEntry>>> fieldMap = DocumentEntryIndex.indexMap(selector.getFieldType(), filteredResult);
                                    List<DocumentEntry> deList = selector.filter(fieldMap.get(selector.getFieldType()));
                                    selector.addResult(deList);
                                }
                                filteredResult.clear();
                                filteredResult.addAll(selector.getResult());
                            }
                        }
                    }
                }
            }
        };

        filterSelectors.add(new StatusFieldFilterSelector("DocumentEntry Status", valueChangeCallback));
        filterSelectors.add(new EntryTypeFieldFilterSelector("DocumentEntry Type", valueChangeCallback));
        filterSelectors.add(new CreationTimeFieldFilterSelector("Creation Time", valueChangeCallback));
        filterSelectors.add(new ServiceStartTimeFieldFilterSelector("Service Start Time", valueChangeCallback));
        filterSelectors.add(new ServiceStopTimeFieldFilterSelector("Service Stop Time", valueChangeCallback));
        filterSelectors.add(new AuthorFieldFilterSelector("Author Person", valueChangeCallback));
        filterSelectors.add(new ClassCodeFieldFilterSelection("Class Code", CodesConfiguration.ClassCode, valueChangeCallback));
        filterSelectors.add(new TypeCodeFieldFilterSelection("Type Code", CodesConfiguration.TypeCode, valueChangeCallback));
        filterSelectors.add(new FormatCodeFieldFilterSelection("Format Code", CodesConfiguration.FormatCode, valueChangeCallback));
        filterSelectors.add(new HcftCodeFieldFilterSelection("Healthcare Facility Type Code", CodesConfiguration.HealthcareFacilityTypeCode, valueChangeCallback));
        filterSelectors.add(new PracticeSettingCodeFieldFilterSelection("Practice Setting Code", CodesConfiguration.PracticeSettingCode, valueChangeCallback));
        filterSelectors.add(new ConfidentialityCodeFieldFilterSelection("Confidentiality Code", CodesConfiguration.ConfidentialityCode, valueChangeCallback));
        filterSelectors.add(new EventCodeFieldFilterSelection("Event Code List", CodesConfiguration.EventCodeList, valueChangeCallback));

        applyFilterBtn = new Button(getFilterName());
        applyFilterBtn.setEnabled(true);
        applyFilterBtn.setTitle("Output will be displayed below. Note: DocumentEntry Author information may not be persisted in the SimIndex.");
        applyFilterBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                applyFilter();
                if (isRemoveEnabled()) {
                    applyFilterBtn.setEnabled(false);
                    removeFilterBtn.setEnabled(true);
                }
            }
        });

        if (isRemoveEnabled()) {
            removeFilterBtn = new Button("&nbsp;Remove Filter");
            removeFilterBtn.setEnabled(false);
            removeFilterBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    removeFilter();
                    removeFilterBtn.setEnabled(false);
                    applyFilterBtn.setEnabled(true);
                }
            });
        }
    }


    @Override
    public Widget asWidget() {
        return featurePanel;
    }

    private String displayResultPanel(Result result) {
        StringBuffer buf = new StringBuffer();
        it.assertionsToSb(result, buf);
        clearResultPanel();
        if (! result.passed()) {
            resultPanel.add(new HTML(red(bold("Status: Failed.<br/>",true))));
        } else {
            resultPanel.add(new HTML(bold("Status: Passed.<br/>",true)));
        }
        HTML msgBody = new HTML(buf.toString());
        resultPanel.add(msgBody);
        return buf.toString();
    }

    private void clearResultPanel() {
        resultPanel.clear();
    }

    private void displayResultPanel(Widget widget) {
        clearResultPanel();
        resultPanel.add(widget);
    }

    @Override
    public List<DocumentEntry> getFilteredData() {
        return filterSelectors.getLast().getResult();
    }

    // skb TODO: when view mode is changed to History, warn user that filter will be cleared.



    @Override
    public void setData(List<DocumentEntry> data) {
        initialDeList = data;
        valueChangeCallback.run(new NewSelectedFieldValue(filterSelectors.getFirst(), null));
    }




    @Override
    public void displayFilter() {
        // skb TODO: Clear tree selection because the selected item may not be in the filtered result set.

        clearResultPanel();

        String title = "<b>Trial Version Document Entries Filter</b>";
        featurePanel.add(createTitle(HyperlinkFactory.addHTML(title)));
        featurePanel.add(new HTML("<br/>"));
        FlexTable ft = new FlexTable();
        int row=0;
        boolean b = false;

        try {
            for (IndexFieldFilterSelector<DocumentEntryIndexField,DocumentEntry> fieldSelectionResult : filterSelectors) {
                featurePanel.add(fieldSelectionResult.asWidget());
            }
            featurePanel.add(new HTML("&nbsp;"));

            applyFilterBtn.addStyleName("uiSpacerMarginLeft");
            applyFilterBtn.addStyleName("inlineBlock");
            featurePanel.add(applyFilterBtn);

            if (isRemoveEnabled()) {
                removeFilterBtn.addStyleName("uiSpacerMarginLeft");
                removeFilterBtn.addStyleName("inlineBlock");
                featurePanel.add(removeFilterBtn);
            }

            featurePanel.add(new HTML("<br/>"));
        } catch (Exception ex) {
            new PopupMessage(ex.toString());
        } finally {
            resultPanel.add(statusBox);
            featurePanel.add(resultPanel);
        }
    }


    private void popListBox(ListBox listBox, List<String> values) {
        if ((values != null && ! values.isEmpty())) {
            if (values == null)
                values = new ArrayList<>();
            for (String value : values) {
                listBox.addItem(value);
            }
        } else {
            listBox.setVisible(false);
        }
    }



}
