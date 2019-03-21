package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.tabs.findDocuments2Tab.FindDocuments2Params;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilterBank;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.toolkit.http.client.HtmlMarkup.red;

public class ContentsModeDocumentEntriesFilterDisplay extends CommonDisplay implements FilterFeature {
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
    /**
     * A place holder for the search parameters.
     * Note the reuse of FindDocuments2Params class without the intent to run FindDocuments.
      */
    FindDocuments2Params queryParams;
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


    public ContentsModeDocumentEntriesFilterDisplay(MetadataInspectorTab it) {
        this.detailPanel = it.detailPanel;
        this.metadataCollection = it.data.combinedMetadata;
        this.it = it;
        // TODO add click handlers
    }

    // skb TODO: when filter is selected, hide the column selection, hide the table, tree panel and structure panel
    // skb TODO: when filter is unchecked/deselected, warn user to remove filter
    // skb TODO: on cancel filter, restore last selected item, and restore the normal view.
    /** skb TODO: when filter is applied
          a) show rounded label with an X to remove filter. Place this label next to Contents.
          b) show an edit label, which will restore the filter view
     **/
    // skb TODO: when view mode is changed to History, warn user that filter will be cleared.

    // skb TODO: handle show hidden view
    @Override
    public void hideFilter() {
    }

    @Override
    public void displayFilter() {
        // skb TODO: Clear tree selection because the selected item may not be in the filtered result set.

        String title = "<h4>Trial Version Document Entries Filter</h4>";
        featurePanel.add(createTitle(HyperlinkFactory.addHTML(title)));
        FlexTable ft = new FlexTable();
        int row=0;
        boolean b = false;



        try {
            // skb TODO: Count the documents with codes for which we do not have a mapping in our codes.xml
            queryParams = new FindDocuments2Params(statusDisplay);
            featurePanel.add(queryParams.asWidget());

            ft.setWidget(row, 0, applyFilterBtn);
            ft.setWidget(row, 1, cancelBtn);
            row++;
//            ft.setHTML(row, 0, bold("objectType", b));
//            ft.setWidget(row, 1, );
//            row++;


            ft.setWidget(row, 0, statusBox);
            row++;
            ft.setWidget(row, 0, resultPanel);
            ft.getFlexCellFormatter().setColSpan(row, 0, 3);
            row++;
            featurePanel.add(ft);

            previousPanel = detailPanel; // problem display is lost due to clear
            detailPanel.clear();
            detailPanel.add(featurePanel);

        } catch (Exception ex) {
            new PopupMessage(ex.toString());
        } finally {

        }



        return false;
    }

    @Override
    public boolean applyFilter() {
        return false;
    }

    @Override
    public boolean removeFilter() {

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
