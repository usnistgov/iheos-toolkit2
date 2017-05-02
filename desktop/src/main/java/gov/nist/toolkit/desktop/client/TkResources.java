package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 */
public interface TkResources extends ClientBundle {

    public static final TkResources INSTANCE = GWT.create(TkResources.class);

    @Source("images/ic_menu_black_24dp_1x.png")
    ImageResource menu();

    @Source("images/Blank.png")
    ImageResource blank();

    @Source("images/ic_close_black_16dp_1x.png")
    ImageResource close();

}
