package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEventHandler;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MultiUserTestSessionSelector {
    HTML currentTestSession = new HTML();
    TextBox textBox = new TextBox();
    HorizontalPanel panel;

    public MultiUserTestSessionSelector() {
        build();
        link();
    }

    // Listen on the EventBus in the future
    private void link() {

        // Test sessions reloaded
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionsUpdatedEvent.TYPE, new TestSessionsUpdatedEventHandler() {
            @Override
            public void onTestSessionsUpdated(TestSessionsUpdatedEvent event) {
                listBox.clear();
                for (String i : event.testSessionNames) listBox.addItem(i);
            }
        });


        // SELECT
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
                    listBox.setSelectedIndex(indexOfValue(event.getValue()));
                }
            }
        });
    }

    // Initialize screen now
    private void build() {

        panel = new HorizontalPanel();

        panel.add(new HTML("Test Session: "));

        panel.add(currentTestSession);
        panel.add(new HTML("&nbsp;"));

//        listBox.addChangeHandler(new ChangeHandler() {
//            @Override
//            public void onChange(ChangeEvent changeEvent) {
//                String newValue = listBox.getValue(listBox.getSelectedIndex());
//                if (NONSELECTION.equals(newValue)) return;
//                ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(newValue);
//                ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, newValue));
//            }
//        });

        textBox.removeStyleName("testSessionInputMc");
        textBox.addStyleName("testSessionInputMc");
        panel.add(textBox);
        textBox.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event_)
            {
                boolean enterPressed = KeyCodes.KEY_ENTER == event_
                        .getNativeEvent().getKeyCode();
                if (enterPressed) {
                    String value = textBox.getValue().trim();
                    if (isValidSession) {
                        ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(newValue);
                        ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, newValue));
                    }
                }
            }
        });

        //
        // Change Test Session Button
        //
        Button changeTestSessionButton = new Button("Change");
        panel.add(changeTestSessionButton);
        changeTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                String value = textBox.getValue().trim();
            }
        });

        panel.add(new HTML("&nbsp;&nbsp;"));

        //
        // Add Button
        //
        Button newTestSessionButton = new Button("Create New");
        panel.add(newTestSessionButton);
        newTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                String value = textBox.getValue().trim();
                textBox.setValue("");
                if ("".equals(value)) return;
                ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.ADD, value));
            }
        });

        //
        // Delete Button
        //
        Button delTestSessionButton = new Button("Delete");
        panel.add(delTestSessionButton);
        delTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
//                String value = textBox.getValue(listBox.getSelectedIndex());
//                ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.DELETE, value));
            }
        });
    }

    void add() {
        String value = textBox.getValue().trim();
        value = value.replaceAll(" ", "_");
        textBox.setValue("");
        if ("".equals(value)) return;
        ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.ADD, value));
    }

    public Widget asWidget() { return panel; }

    int indexOfValue(String value) {
        for (int i=0; i<listBox.getItemCount(); i++)
            if (listBox.getItemText(i).equals(value))
                return i;
        return -1;
    }
}
