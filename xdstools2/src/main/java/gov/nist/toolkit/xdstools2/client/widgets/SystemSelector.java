package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.ASite;

import java.util.ArrayList;
import java.util.List;

/**
 * Present systems for selection
 */
abstract public class SystemSelector implements IsWidget {
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel siteTablePanel = new FlowPanel();
    private List<Button> siteButtons = new ArrayList<>();

    abstract public void doSiteSelected(String label);


    public SystemSelector(String title) {
        HTML siteTableTitle = new HTML("<b>" + title + "</b>");
        siteTableTitle.setWidth("100%");
        siteTableTitle.addStyleName("tool-section-header");
        thePanel.add(siteTableTitle);
        thePanel.add(siteTablePanel);
    }


    public void setSiteNames(List<ASite> sites) {
        for (ASite site : sites) {
            Button b = new Button(site.getName());
            b.setText(site.getName());
            b.setEnabled(site.isEnabled());
            siteButtons.add(b);
            siteTablePanel.add(b);
        }
        bindSites();
    }

    /**
     * I tried using the defined style SiteButtonSelected defined in css
     * but it is ignored so removing the default style gives a dark
     * grey to the background (on MAC) which is good enough for now.  Needs
     * to be tested on Windows and Linux.
     */
    private void bindSites() {
        for (Button b : siteButtons) {
            b.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Object o = clickEvent.getSource();
                    if (o instanceof Button) {
                        updateSiteSelectedView((Button) o);
                    }
                }
            });
        }
    }

    private void updateSiteSelectedView(Button button) {
        for (Button u : siteButtons) {
            u.setStyleName("gwt-Button");
        }
        button.setStyleName("siteSelected");
        doSiteSelected(button.getText());
    }

    @Override
    public Widget asWidget() {
        return thePanel;
    }
}
