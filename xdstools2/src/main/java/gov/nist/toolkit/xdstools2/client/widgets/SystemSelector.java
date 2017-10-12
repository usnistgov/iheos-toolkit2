package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.util.ASite;

import java.util.ArrayList;
import java.util.List;

/**
 * Present systems for selection
 */
abstract public class SystemSelector implements IsWidget {
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel siteTablePanel = new FlowPanel();
    private List<Button> siteButtons = new ArrayList<>();
    private CheckBox showAllCheckBox = new CheckBox("Show All");
    private List<ASite> sites;

    abstract public void doSiteSelected(String label);


    public SystemSelector(String title) {
        HTML siteTableTitle = new HTML("<b>" + title + "</b>");
        siteTableTitle.setWidth("100%");
        siteTableTitle.addStyleName("tool-section-header");
        thePanel.add(siteTableTitle);
        thePanel.add(siteTablePanel);
        //siteTablePanel.add(new HTML("No appropriate systems to show"));
        showAllCheckBox.setValue(true);
        showAllCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                displaySites();
            }
        });
    }

    public void setSiteNames(List<ASite> sites) {
        this.sites = sites;
        siteTablePanel.add(showAllCheckBox);
        displaySites();
    }

    private void displaySites() {
        //if (!sites.isEmpty())
        while (siteTablePanel.getWidgetCount() > 1)
            siteTablePanel.remove(siteTablePanel.getWidgetCount() - 1);
        for (ASite site : sites) {
            if (isShowAll() || site.isEnabled()) {
                Button b = new Button(site.getName());
                b.setText(site.getName());
                b.setEnabled(site.isEnabled());
                siteButtons.add(b);
                siteTablePanel.add(b);
            }
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

    public void updateSiteSelectedView(String title) {
        GWT.log("updateSiteSelectedView - " + title);

        for (Button u : siteButtons) {
            u.setStyleName("gwt-Button");
        }
        Button button = findButton(title);
        button.setStyleName("siteSelected");
        doSiteSelected(title);
    }

    private Button findButton(String title) {
        for (Button b : siteButtons) {
            if (b.getTitle().equals(title)) return b;
            if (b.getText().equals(title)) return b;
        }
        GWT.log("System Button - " + title + " not found");
        if (siteButtons.size() > 0)
            return siteButtons.get(0);
        return new Button("Fake");
    }

    private boolean isShowAll() {
        return showAllCheckBox.getValue();
    }

    @Override
    public Widget asWidget() {
        return thePanel;
    }
}
