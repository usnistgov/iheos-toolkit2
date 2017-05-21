package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 */
public interface MyResources extends ClientBundle {
    public static final MyResources INSTANCE =  GWT.create(MyResources.class);

    @Source("images/Blank.png")
    ImageResource blankImage();
}
