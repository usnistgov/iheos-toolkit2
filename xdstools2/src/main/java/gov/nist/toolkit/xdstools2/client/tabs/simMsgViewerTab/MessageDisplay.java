package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.session.shared.SubMessage;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

/**
 * can be used as widget or individal menu and content panels but not both.
 */
public class MessageDisplay implements IsWidget {
    private FlowPanel menuPanel = new FlowPanel();
    private FlowPanel contentPanel = new FlowPanel();
    private Message message;
    private String title;

    public MessageDisplay(Message message) {
        this(message, "Structure");
    }

    public MessageDisplay(Message message, String title) {
        this.message = message;
        this.title = title;
        if (!message.isEmpty())
            init();
    }

    private void init() {
        Tree tree = new Tree();
        menuPanel.add(tree);

        TreeItem root = new TreeItem();
        root.setText(title);
        tree.addItem(root);

        TreeItem fullMessage = new TreeItem();
        root.addItem(fullMessage);

        Anchor fullcontentA = new Anchor("Full Message");
        String part = (message.getParts().size() == 2) ?  message.getParts().get(1) : message.getParts().get(0);
        HTML fullContent = AbstractView.htmlize(part);
        fullMessage.setWidget(fullcontentA);
        MessagePartDisplay fullDisplay = new MessagePartDisplay(contentPanel, fullContent);
        fullcontentA.addClickHandler(fullDisplay);


        /**************************************
         * Sub-Message display
         **************************************/
        for (SubMessage subMessage : message.getSubMessages()) {
            displaySubMessage(fullMessage, subMessage);
        }

        root.setState(true);
        fullMessage.setState(true);
        fullDisplay.onClick(null);
    }

    private void displaySubMessage(TreeItem parent, SubMessage subMessage) {
        TreeItem item = new TreeItem();
        parent.addItem(item);

        Anchor a = new Anchor(subMessage.getName());
        HTML content = AbstractView.htmlize(subMessage.getValue());
        item.setWidget(a);
        a.addClickHandler(new MessagePartDisplay(contentPanel, content));

        for (SubMessage sm : subMessage.getSubMessages()) {
            displaySubMessage(item, sm);
        }
        item.setState(true);
    }

    public FlowPanel getMenuPanel() {
        return menuPanel;
    }

    public FlowPanel getContentPanel() {
        return contentPanel;
    }

    public Widget asSinglePanel() {
        if (message.isEmpty()) {
            return new Label("No content");
        }
        MessageDisplayView mdv = new MessageDisplayView("x");
        mdv.newMenu().add(getMenuPanel());
        mdv.getContentPanel().add(getContentPanel());
        return mdv.asWidget();
    }

    @Override
    public Widget asWidget() {
        HorizontalFlowPanel panel = new HorizontalFlowPanel();
        panel.add(menuPanel);
        panel.add(contentPanel);
        return panel;
    }
}
