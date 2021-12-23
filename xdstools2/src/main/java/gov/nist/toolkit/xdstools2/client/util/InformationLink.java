package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

/**
 * Build info link to project wiki
 */
public class InformationLink implements IsWidget {
    private Image infoImage = new Image("icons/about-16.png");
    private HorizontalFlowPanel panel = new HorizontalFlowPanel();

    /**
     *
     * @param title - hover over displayed string
     * @param wikiPageName - wiki file name - all spaces in name will be converted to hyphens
     */
    public InformationLink(String title, String wikiPageName) {
        wikiPageName = wikiPageName.replaceAll(" ", "-");
        final String pageName = (wikiPageName.startsWith("/")) ? wikiPageName : "/" + wikiPageName;
        infoImage.getElement().getStyle().setMarginLeft(4, Style.Unit.PX);
        infoImage.getElement().getStyle().setMarginRight(4, Style.Unit.PX);
        infoImage.setTitle(title);
        infoImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open(Xdstools2.wikiBaseUrl + pageName, "_blank","");
            }
        });
        panel.setWidth("100%");
        Anchor anchor = new Anchor("About this Tool");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open(Xdstools2.wikiBaseUrl + pageName, "_blank","");
            }
        });
        anchor.addStyleName("right");
        panel.add(anchor);
        infoImage.addStyleName("right");
        panel.add(infoImage);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
