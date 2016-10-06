package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import gov.nist.toolkit.configDatatypes.client.Pid;

/**
 * Created by onh2 on 7/26/16.
 */
public class PidWidget implements IsWidget{
    FlowPanel container = new FlowPanel();
    TextBox pidInput = new TextBox();
    Button expandBtn = new Button("Expand");

    FlowPanel favContainer = new FlowPanel();
    PidFavoritesCellList favoritePidWidget=new PidFavoritesCellList();

    public PidWidget(){
        HorizontalPanel horizontalPanel=new HorizontalPanel();
        horizontalPanel.add(pidInput);
        horizontalPanel.add(expandBtn);

        container.add(horizontalPanel);

        container.add(favContainer);
        bindWidget();
    }

    private void bindWidget() {
        expandBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                favContainer.add(favoritePidWidget);
            }
        });
        favoritePidWidget.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                Pid selectedPid=favoritePidWidget.getSelectedPid();
                pidInput.setText(selectedPid.toString());
            }
        });
        pidInput.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent focusEvent) {
                favoritePidWidget.clearSelection();
            }
        });
    }

    @Override
    public Widget asWidget() {
        return container;
    }

    public void setText(String text) {
        pidInput.setText(text);
    }

    public void setWidth(String width) {
        pidInput.setWidth(width);
        favoritePidWidget.setWidth(width);
    }

    public void addChangeHandler(ChangeHandler changeHandler) {
        pidInput.addChangeHandler(changeHandler);
    }

    public String getText() {
        return pidInput.getText();
    }


    public String getValue() {
        return pidInput.getValue();
    }
}
