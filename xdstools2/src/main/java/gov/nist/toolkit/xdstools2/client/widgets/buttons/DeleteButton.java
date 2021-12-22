package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.user.client.ui.Image;
import gov.nist.toolkit.xdstools2.client.resources.IconsResources;
import gov.nist.toolkit.xdstools2.client.util.ClientFactoryImpl;

/**
 * Created by Diane Azais local on 11/29/2015.
 */
public class DeleteButton extends IconButton {
    IconsResources RESOURCES = ClientFactoryImpl.getIconsResources();
    private Image REMOVE_ICON = new Image(RESOURCES.getDeleteIconBlack36());


    DeleteButton(String _tooltip){
       super(ButtonType.DELETE_BUTTON, _tooltip);
        setIcon();
    }

    @Override
    protected void setIcon() {
        getElement().appendChild(REMOVE_ICON.getElement());
    }

}
