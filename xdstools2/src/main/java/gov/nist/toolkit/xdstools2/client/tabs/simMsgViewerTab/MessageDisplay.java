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

    public MessageDisplay(Message message) {
        this.message = message;
        init();
    }

    private void init() {
        Tree tree = new Tree();
        menuPanel.add(tree);

        TreeItem root = new TreeItem();
        root.setText("Structure");
        tree.addItem(root);

        TreeItem fullMessage = new TreeItem();
        root.addItem(fullMessage);

        Anchor fullcontentA = new Anchor("Full Message");
        HTML fullContent = AbstractView.htmlize(message.getParts().get(1));
        fullMessage.setWidget(fullcontentA);
        MessagePartDisplay fullDisplay = new MessagePartDisplay(contentPanel, fullContent);
        fullcontentA.addClickHandler(fullDisplay);


        /**************************************
         * Sub-Message display
         **************************************/
        for (SubMessage subMessage : message.getSubMessages()) {
            displaySubMessage(fullMessage, subMessage);
//            TreeItem item = new TreeItem();
//            fullMessage.addItem(item);
//
//            Anchor a = new Anchor(subMessage.getName());
//            HTML content = AbstractView.htmlize(subMessage.getValue());
//            item.setWidget(a);
//            a.addClickHandler(new MessagePartDisplay(contentPanel, content));
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
        MessageDisplayView mdv = new MessageDisplayView("");
        mdv.newMenu().add(getMenuPanel());
        mdv.getContentPanel().add(getContentPanel());
        return mdv.asWidget();


//        HorizontalFlowPanel panel = new HorizontalFlowPanel();
//        FlowPanel menu = getMenuPanel();
//        FlowPanel content = getContentPanel();
//        menu.setWidth("30%");
//        ScrollPanel scrollPanel0 = new ScrollPanel();
//        scrollPanel0.add(menu);
//        panel.add(scrollPanel0);
//        ScrollPanel scrollPanel1 = new ScrollPanel();
//        scrollPanel1.add(content);
//        panel.add(scrollPanel1);
//        return panel;
    }

    @Override
    public Widget asWidget() {
        HorizontalFlowPanel panel = new HorizontalFlowPanel();
        panel.add(menuPanel);
        panel.add(contentPanel);
        return panel;
    }

//    @Override
//    public Widget asWidget() {
//        return panel;
//    }
}
