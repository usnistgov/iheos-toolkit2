package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Created by bill on 8/25/15.
 */
public class CodeFilter {
    public String codeName;
    public ListBox selectedCodes;
    public Button editButton;

    public CodeFilter(FlexTable mainGrid, int row, int startingCol, String labelText, String codeName, int codeBoxSize) {
        this.codeName = codeName;

        HTML label = new HTML();
        label.setText(labelText);
        mainGrid.setWidget(row, startingCol, label);

        selectedCodes = new ListBox();
        selectedCodes.setVisibleItemCount(codeBoxSize);
        mainGrid.setWidget(row, startingCol+1, selectedCodes);

        editButton = new Button("edit");
        mainGrid.setWidget(row, startingCol+2, editButton);
        editButton.setEnabled(false);
    }
}
