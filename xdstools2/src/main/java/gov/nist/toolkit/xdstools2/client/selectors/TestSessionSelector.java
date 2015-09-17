package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEventHandler;

import java.util.List;

/**
 * Created by bill on 9/16/15.
 */
public class TestSessionSelector {
    ListBox listBox = new ListBox();
    TextBox textBox = new TextBox();
    HorizontalPanel panel;

    public TestSessionSelector(List<String> initialContents, String initialSelection) {
        build(initialContents, initialSelection);
        link();
    }

    private void link() {
        Xdstools2.getEventBus().addHandler(TestSessionsUpdatedEvent.TYPE, new TestSessionsUpdatedEventHandler() {
            @Override
            public void onTestSessionsUpdated(TestSessionsUpdatedEvent event) {
                listBox.clear();
                for (String i : event.testSessionNames) listBox.addItem(i);
            }
        });

        Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT) {
                    listBox.setSelectedIndex(indexOfValue(event.value));
                }
            }
        });
    }

    private void build(List<String> initialContents, String initialSelection) {
        panel = new HorizontalPanel();

        HTML testSessionLabel = new HTML();
        testSessionLabel.setText("TestSession: ");
        panel.add(testSessionLabel);

        //
        // List Box
        //
        for (String i : initialContents) listBox.addItem(i);
        listBox.setSelectedIndex(initialContents.indexOf(initialSelection));
        panel.add(listBox);
        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                String newValue = listBox.getSelectedValue();
                Xdstools2.getTestSessionManager().setCurrentTestSession(newValue);
                Xdstools2.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, newValue));
            }
        });

        panel.add(textBox);

        //
        // Add Button
        //
        Button addTestSessionButton = new Button("Add");
        panel.add(addTestSessionButton);
        addTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                String value = textBox.getValue().trim();
                textBox.setValue("");
                if ("".equals(value)) return;
                Xdstools2.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.ADD, value));
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
                String value = listBox.getSelectedValue();
                Xdstools2.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.DELETE, value));
            }
        });
    }

    public Widget asWidget() { return panel; }

    int indexOfValue(String value) {
        for (int i=0; i<listBox.getItemCount(); i++)
            if (listBox.getItemText(i).equals(value))
                return i;
        return -1;
    }
}
