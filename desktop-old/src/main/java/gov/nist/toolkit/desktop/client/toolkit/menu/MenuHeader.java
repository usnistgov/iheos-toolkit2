package gov.nist.toolkit.desktop.client.toolkit.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 *
 */
public class MenuHeader extends Composite {
    interface MenuHeaderUiBinder extends UiBinder<SimplePanel, MenuHeader> {
    }

    private static MenuHeaderUiBinder ourUiBinder = GWT.create(MenuHeaderUiBinder.class);

    @UiField(provided = true)
    Image image;

    @UiField(provided = true)
    HTML html;

    @UiConstructor
    public MenuHeader(String text, ImageResource imageResource) {
        image = new Image(imageResource);
        html = new HTML(text);
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}