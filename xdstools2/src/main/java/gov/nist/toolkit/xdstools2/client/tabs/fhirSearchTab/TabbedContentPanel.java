package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;

public class TabbedContentPanel implements IsWidget {
    private FlowPanel thePanel = new FlowPanel();
    private TabLayoutPanel tabPanel = new TabLayoutPanel(1.5, Style.Unit.EM);
    private int baseTabCount = 0;
    private String title;
    private String height;


    public TabbedContentPanel(String title, String height) {
        this.title = title;
        this.height = height;
        buildUI();
    }

    private void buildUI() {
        thePanel.setWidth("100%");

        thePanel.add(stylizedHtml("<b>" + title + "</b>", "tool-section-header"));
        tabPanel.setWidth("100%");
        tabPanel.setHeight(height);
        thePanel.add(tabPanel);
    }

    /**
     * base tabs must be added before removable tabs (clearContent())
     * @param w
     * @param title
     * @return
     */
    public void addBaseTab(Widget w, String title) {
        addTab(w, title);
        baseTabCount++;
    }

    public void addTab(Widget w, String title) {
        tabPanel.add(inScrollPanel(w), title);
    }

    public void clearContent() {
        while (tabPanel.getWidgetCount() > baseTabCount)
            tabPanel.remove(tabPanel.getWidgetCount() - 1);
    }

    private static HTML stylizedHtml(String html, String styleName) {
        HTML h = new HTML(html);
        h.addStyleName(styleName);
        return h;
    }

    private static ScrollPanel inScrollPanel(Widget w) {
        w.setWidth("100%");
        w.setHeight("100%");
        ScrollPanel sp = new ScrollPanel();
        sp.add(w);
        return sp;
    }

    @Override
    public Widget asWidget() {
        return thePanel;
    }
}
