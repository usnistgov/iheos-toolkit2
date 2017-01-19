package gov.nist.toolkit.xdstools2.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by onh2 on 1/17/17.
 */
public interface IconsResources extends ClientBundle {
    IconsResources INSTANCE = GWT.create(IconsResources.class);

        @Source("icons/copy_button_21px.png")
        ImageResource copyButton();
}
