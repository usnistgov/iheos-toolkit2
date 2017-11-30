package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Present systems for selection
 */
abstract public class ButtonListSelector implements IsWidget {
    private static final String GWT_BUTTON_STYLE = "gwt-Button";
    private static final String SITE_SELECTED_STYLE = "siteSelected";
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel tablePanel = new FlowPanel();
    private List<Button> buttons = new ArrayList<>();
    private String currentSelection;

    abstract public void doSelected(String label);


    public ButtonListSelector(String title, String type) {
        HTML tableTitle = new HTML("<b>" + title + "</b>");
        tableTitle.setWidth("100%");
        tableTitle.addStyleName("tool-section-header");
        thePanel.add(tableTitle);
        thePanel.add(tablePanel);
        tablePanel.add(new HTML("No appropriate "+ type + " to show"));
    }


    public void setNames(List<? extends AnnotatedItem> items) {
        if (!items.isEmpty()) tablePanel.clear();
        for (AnnotatedItem item : items) {
            Button b = new Button(item.getName());
            b.setText(item.getName());
            b.setEnabled(item.isEnabled());
            buttons.add(b);
            tablePanel.add(b);
        }
        bindList();
    }

    public void refreshEnabledStatus(List<? extends AnnotatedItem> items) {
        for (AnnotatedItem item : items) {
            Button b = findButton(item.getName());
            if (b!=null) {
               b.setEnabled(item.isEnabled());
            }
        }
    }

    /**
     * I tried using the defined style SiteButtonSelected defined in css
     * but it is ignored so removing the default style gives a dark
     * grey to the background (on MAC) which is good enough for now.  Needs
     * to be tested on Windows and Linux.
     */
    private void bindList() {
        for (Button b : buttons) {
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
        for (Button u : buttons) {
            u.setStyleName(GWT_BUTTON_STYLE);
        }
        button.setStyleName(SITE_SELECTED_STYLE);
        setCurrentSelection(button.getText());
        doSelected(button.getText());
    }

    public void updateSiteSelectedView(String title) {
        GWT.log("updateSiteSelectedView - " + title);

        for (Button u : buttons) {
            u.setStyleName(GWT_BUTTON_STYLE);
        }
        Button button = findButton(title);
        button.setStyleName(SITE_SELECTED_STYLE);
        setCurrentSelection(button.getText());
        doSelected(title);
    }

    private Button findButton(String title) {
        for (Button b : buttons) {
            if (b.getTitle().equals(title)) return b;
            if (b.getText().equals(title)) return b;
        }
        GWT.log("ButtonListSelector Button - " + title + " not found");
        if (buttons.size() > 0)
            return buttons.get(0);
        return new Button("Fake");
    }

    public String getCurrentSelection() {
        return currentSelection;
    }

    private void setCurrentSelection(String currentSelection) {
        this.currentSelection = currentSelection;
    }

    @Override
    public Widget asWidget() {
        return thePanel;
    }
}
