package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de;

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
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.inspector.CommonDisplay;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.FilterFeature;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component.DocumentEntryFieldComponent;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component.DocumentEntryFieldFilterSelector;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component.NewSelectedValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component.StatusFieldFilterSelector;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilterBank;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nist.toolkit.http.client.HtmlMarkup.red;

public class DocumentEntryFilterDisplay extends CommonDisplay implements FilterFeature<DocumentEntry> {
    private Button applyFilterBtn = new Button("Apply Filter");
    private Button cancelBtn = new Button("Cancel");
    Map<String, List<String>> codeSpecMap = new HashMap<String, List<String>>();

    // Main body
    boolean isFilterApplied = false;
    private FlowPanel featurePanel = new FlowPanel();
    private VerticalPanel previousPanel;
    // controls
    private TextBox pidTxt = new TextBox();
    private TextBox titleTxt = new TextBox();
    private TextBox commentsTxt = new TextBox();
    private TextBox languageCodeTxt = new TextBox();
    private TextBox legalAuthenticatorTxt = new TextBox();
    private ListBox sourcePatientInfoLBox = new ListBox();
    CodeFilterBank codeFilterBank;
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

    private LinkedList<DocumentEntryFieldFilterSelector> filterSelectors;
    private Map<DocumentEntryIndexField, Map<IndexFieldValue, List<DocumentEntry>>> fieldIndexMap;

    // enum of fields

    // index field:
    //  type:Enum
    // search widgets:
    // a doubly linked list of
    //  enum
    //  widget, call the constructor with the enum type and a callback to doIndex(indexField:enum,selectedFieldValues:list<String>).
    //  result of doIndex:list<de>. This is an aggregate list.
    // refresh widget result count:
    // for each fieldValue code result, call widget.updateCount(fieldValue:String,count:Int)

    // fill the search screen in the order of the linked list

   // skb TODO: map here
    // initial state just shows independent count for each field-value.
    // when a field value is selected, changed:
    // doIndex(searchField:Enum, list<de>):
    //      skip all previous fields and doIndex for the field that changed and if other field values are present after this one.
    //          Use the list<de> from the previous field if exists else use the main Mc.
    //      from list<de> this map is created: map[field:Enum, map[field-value:String,list<de>]
    //          example:                       DocEntryStatus, [stable=list,ondemand=list,both=list]
    //      Sort the map.keySet for Time fields
    // iM = initial map
    //
    // fM = filtered map
    // initially fM is a deep copy of iM
    // on search field S selected:
    // fmTemp: list<de> = new temporary storage
    //      get fM map by field
    //         for each selected-value
    //          get value-map by the field-value
    //              add to fMTemp(list<de>)
    //          count fMTemp, if > 0, fM = doIndex(fMTemp), lock the search field S so it cannot be changed until it is cleared.  When a search field is cleared, the iM map must be used!
    // locking the search field:
    //      field becomes read-only with a count and an X to clear search term.
    // map[field, map[code,list<de>]

    public DocumentEntryFilterDisplay() {
        filterSelectors = new LinkedList<>();

        DocumentEntryFieldComponent statusFilterSelector = new StatusFieldFilterSelector("DocumentEntries", new SimpleCallbackT<NewSelectedValue>() {
            @Override
            public void run(NewSelectedValue newSelectedValue) {
                // update count
            }
        });
        filterSelectors.add(new DocumentEntryFieldFilterSelector(DocumentEntryIndexField.STATUS, statusFilterSelector));
    }

    @Override
    public Widget asWidget() {
        return featurePanel;
    }
    public void addToCodeSpec(Map<String, List<String>> codeSpec) {
        codeFilterBank.addToCodeSpec(codeSpec);
    }

    private String displayResult(Result result) {
        StringBuffer buf = new StringBuffer();
        it.assertionsToSb(result, buf);
        clearResult();
        if (!result.passed()) {
            resultPanel.add(new HTML(red(bold("Status: Failed.<br/>",true))));
        } else {
            resultPanel.add(new HTML(bold("Status: Passed.<br/>",true)));
        }
        HTML msgBody = new HTML(buf.toString());
        resultPanel.add(msgBody);
        return buf.toString();
    }

    private void clearResult() {
        resultPanel.clear();
    }

    private void displayResult(Widget widget) {
        clearResult();
        resultPanel.add(widget);
    }

    // skb TODO: on cancel filter, restore last selected item, and restore the normal view.
    /** skb TODO: when filter is applied
          a) show rounded label with an X to remove filter. Place this label next to Contents.
          b) show an edit label, which will restore the filter view
        run advanced mode in single mode
        clear current table selection
     **/
    // skb TODO: when view mode is changed to History, warn user that filter will be cleared.

    // skb TODO: handle show hidden view
    @Override
    public void hideFilter() {
        featurePanel.setVisible(false);
    }

//    void setData(List<? extends MetadataObject> metadataObjects);
//    @Override
//    public void setData(List<? extends MetadataObject> metadataObjects) {
//        List<DocumentEntry> documentEntries = (List<DocumentEntry>)metadataObjects;
//
//    }


    @Override
    public void setData(List<DocumentEntry> data) {
      fieldIndexMap = DocumentEntryIndex.indexMap(data);
      for (DocumentEntryFieldFilterSelector selector : filterSelectors) {
          Map<IndexFieldValue, List<DocumentEntry>> valueMap = fieldIndexMap.get(selector.getField());
          if (valueMap!=null && !valueMap.isEmpty()) {
              for (IndexFieldValue ifv : valueMap.keySet()) {
                  selector.getComponent().doUpdateCount(ifv, valueMap.get(ifv).size());
              }
          }
      }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void displayFilter() {
        featurePanel.setVisible(true);
        // skb TODO: Clear tree selection because the selected item may not be in the filtered result set.

        String title = "<h4>Trial Version Document Entries Filter</h4>";
        featurePanel.add(createTitle(HyperlinkFactory.addHTML(title)));
        FlexTable ft = new FlexTable();
        int row=0;
        boolean b = false;

        try {

            // TODO: iterate the selector display components

                    // skb TODO: Count the documents with codes for which we do not have a mapping in our codes.xml
            for (DocumentEntryFieldFilterSelector fieldSelectionResult : filterSelectors) {
                featurePanel.add(fieldSelectionResult.getComponent().asWidget());
            }

//            ft.setWidget(row, 0, applyFilterBtn);
//            ft.setWidget(row, 1, cancelBtn);
//            row++;
//            ft.setWidget(row, 0, statusBox);
//            row++;
//            ft.setWidget(row, 0, resultPanel);
//            ft.getFlexCellFormatter().setColSpan(row, 0, 3);
//            row++;
//            featurePanel.add(ft);
        } catch (Exception ex) {
            new PopupMessage(ex.toString());
        } finally {
        }
    }

    @Override
    public boolean applyFilter() {
        return false;
    }

    @Override
    public boolean removeFilter() {
        return false;
    }


    private void popListBox(ListBox listBox, List<String> values) {
        if ((values != null && !values.isEmpty())) {
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
