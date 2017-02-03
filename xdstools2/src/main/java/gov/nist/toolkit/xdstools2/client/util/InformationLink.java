package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.Xdstools2;

/**
 *
 */
public class InformationLink implements IsWidget {
    private Image infoImage = new Image("icons/about-16.png");

    /**
     *
     * @param title - hover over displayed string
     * @param wikiPageName - wiki file name - includes hyphens in name and not spaces
     */
    public InformationLink(String title, String wikiPageName) {
        final String pageName = (wikiPageName.startsWith("/")) ? wikiPageName : "/" + wikiPageName;
        infoImage.setTitle(title);
        infoImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open(Xdstools2.wikiBaseUrl + pageName, "_blank","");
            }
        });

    }

    @Override
    public Widget asWidget() {
        return infoImage;
    }
}
