package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.ArrayList;
import java.util.List;

abstract public class ButtonListSelector implements IsWidget {
    private static final String GWT_BUTTON_STYLE = "gwt-Button";
    private static final String SITE_SELECTED_STYLE = "siteSelected";
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel tablePanel = new FlowPanel();
    private List<Button> buttons = new ArrayList<>();
    private CheckBox showAllCheckBox = new CheckBox("Show All");
    private List<? extends AnnotatedItem> items;
    private String currentSelection;
    private String titleStyleName = null;

    abstract public void doSelected(String label);

    public ButtonListSelector(String title, String titleStyleName) {
        this.titleStyleName = titleStyleName;
        HTML tableTitle = new HTML("<b>" + title + "</b>");
        tableTitle.setWidth("100%");
        if (titleStyleName != null)
            tableTitle.addStyleName(titleStyleName);
        thePanel.add(tableTitle);
        thePanel.add(tablePanel);
        //siteTablePanel.add(new HTML("No appropriate systems to show"));
        showAllCheckBox.setValue(true);
        showAllCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                displayItems();
            }
        });
    }

    public ButtonListSelector(String title) {
        this(title, "tool-section-header");
    }

    public void setNames(List<? extends AnnotatedItem> items) {
        this.items = items;
        tablePanel.add(showAllCheckBox);
        displayItems();
    }

    private void displayItems() {
        //if (!sites.isEmpty())
        while (tablePanel.getWidgetCount() > 0)
            tablePanel.remove(tablePanel.getWidgetCount() - 1);
        buttons.clear();
        for (AnnotatedItem item : items) {
            if (isShowAll() || item.isEnabled()) {
                Button b = new Button(item.getName());
                b.setText(item.getName());
                b.setEnabled(item.isEnabled());
                buttons.add(b);
                tablePanel.add(b);
            }
        }
        bindSites();
    }


    public void refreshEnabledStatus(List<? extends AnnotatedItem> items) {
        if (buttons !=null && buttons.size()>0) { // If there are no buttons maybe the setNames method was not called yet.
            for (AnnotatedItem item : items) {
                Button b = findButton(item.getName());
                if (b != null) {
                    b.setEnabled(item.isEnabled());
                }
            }
        }
    }
    /**
     * I tried using the defined style SiteButtonSelected defined in css
     * but it is ignored so removing the default style gives a dark
     * grey to the background (on MAC) which is good enough for now.  Needs
     * to be tested on Windows and Linux.
     */
    private void bindSites() {
        for (Button b : buttons) {
            b.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Object o = clickEvent.getSource();
                    if (o instanceof Button) {
                        updateItemSelectedView((Button) o);
                    }
                }
            });
        }
    }

    private void updateItemSelectedView(Button button) {
        for (Button u : buttons) {
           u.setStyleName(GWT_BUTTON_STYLE);
        }
        button.setStyleName(SITE_SELECTED_STYLE);
        setCurrentSelection(button.getText());
        doSelected(button.getText());
    }

    public void updateSiteSelectedView(String title) {
        GWT.log("updateSiteSelectedView - " + title);

        updateItemSelectedView(findButton(title));
    }

    private Button findButton(String title) {
        if (title!=null) {
            for (Button b : buttons) {
                if (title.equals(b.getTitle())) return b;
                if (title.equals(b.getText())) return b;
            }
            GWT.log("System Button - " + title + " not found");
            if (buttons.size() > 0)
                return buttons.get(0);
        } else {
            GWT.log("Null title!");
        }
        return new Button("Fake");
    }

    private boolean isShowAll() {
        return showAllCheckBox.getValue();
    }

    public void displayShowAll(boolean isVisible) {
        showAllCheckBox.setVisible(isVisible);
    }


    public String getCurrentSelection() {
        // TODO: Should throw an exception if nothing?
        return currentSelection;
    }

    public List<? extends AnnotatedItem> getItems() {
        return items;
    }

    void setCurrentSelection(String currentSelection) {
        this.currentSelection = currentSelection;
    }

    @Override
    public Widget asWidget() {
        return thePanel;
    }
}
