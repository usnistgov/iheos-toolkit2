package gov.nist.toolkit.desktop.client.tools.getDocuments;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.abstracts.AbstractView;

import java.util.Map;

/**
 *
 */
public class GetDocumentsView extends AbstractView<GetDocumentsPresenter> {
    private TextArea textArea;

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        FlowPanel panel=new FlowPanel();
        panel.add(new HTML("<h2>Get Documents</h2>"));

        FlexTable mainGrid = new FlexTable();
        int row = 0;

        panel.add(mainGrid);

        mainGrid.setWidget(row,0, new HTML("Document Entry UUIDs or UIDs"));

        textArea = new TextArea();
        textArea.setCharacterWidth(40);
        textArea.setVisibleLines(10);
        mainGrid.setWidget(row, 1, textArea);
        row++;

        panel.add(mainGrid);
        return panel;
    }

    @Override
    protected void bindUI() {

    }
}
