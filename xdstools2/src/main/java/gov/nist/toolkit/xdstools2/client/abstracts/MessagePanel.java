package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 *
 */
public class MessagePanel extends DecoratorPanel {
    private FlowPanel outerPanel = new FlowPanel();
    private Button clearButton = new Button("Clear");
    private FlowPanel innerPanel = new FlowPanel();

    public MessagePanel() {
        setWidth("90%");
        add(outerPanel);
        outerPanel.add(new HTML("<b>Messages</b>"));
        outerPanel.add(innerPanel);
        outerPanel.add(clearButton);
        setVisible(false);
        clearButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                clear();
            }
        });
    }

    public void addMessage(HTML msg) {
        innerPanel.insert(msg, 0);
        setVisible(true);
    }

    public void addMessage(String msg) {
        addMessage(new HTML(msg));
    }

    public void clearMessages() {
        setVisible(false);
        innerPanel.clear();
    }

}
