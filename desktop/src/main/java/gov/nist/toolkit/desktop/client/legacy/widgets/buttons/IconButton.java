package gov.nist.toolkit.desktop.client.legacy.widgets.buttons;

import com.google.gwt.user.client.ui.Button;

/**
 * Created by Diane Azais local on 11/29/2015.
 */
public abstract class IconButton extends Button {
    private ButtonType buttonType;
    private String tooltip;

    public IconButton(ButtonType _buttonType, String _tooltip){
        buttonType = _buttonType;
        tooltip = _tooltip;

        setTitle(tooltip);
        setStyleName("iconbutton");
    }


    /**
     * Operations specific to each type of button, to define in each implementation
     */
    protected abstract void setIcon();
}
