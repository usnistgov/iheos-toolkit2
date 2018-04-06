package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;

import java.util.ArrayList;
import java.util.List;

public class EditFieldForAuthor {
    ListBox listBox = new ListBox();
    Button editBtn  = new Button("edit");

    int listBoxSize = 2;

    public EditFieldForAuthor() {
        listBox.setVisibleItemCount(listBoxSize);
    }



}
