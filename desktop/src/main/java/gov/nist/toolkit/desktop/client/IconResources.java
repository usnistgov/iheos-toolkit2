package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 */
public interface IconResources extends ClientBundle {
    public static final IconResources INSTANCE =  GWT.create(IconResources.class);

    @Source("images/Blank.png")
    ImageResource blankImage();
}
