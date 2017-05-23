package gov.nist.toolkit.desktop.client.legacy.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import elemental.client.Browser;
import elemental.html.Selection;
import elemental.ranges.Range;
import gov.nist.toolkit.desktop.client.IconsResources;

/**
 * Class that creates a GWT Push button with a copy icon that copies the content of an IDed element
 * on the currently displayed web page of the application to the user's clipboard.
 * Created by onh2 on 1/17/17.
 */
public class CopyButton extends PushButton {

    /**
     * GWT Push button with a copy icon that copies the content of an IDed element on the web page
     * of the application to the user's clipboard.
     * @param elementIdToCopy id of the element whose content needs to be copied.
     */
    public CopyButton(String elementIdToCopy){
        super(new Image(IconsResources.INSTANCE.copyButton()));
        // add tooltip to the button
        setTitle("Copy PID");
        // add custom css style to the button
        setStyleName("copyBtn");
        addClickHandler(new CopyButtonClickHandler(elementIdToCopy));
    }

    /**
     * Click handler class that actually handles the copy to the clipboard using gwt-elemental.
     */
    public class CopyButtonClickHandler implements ClickHandler {
        // id of the element whose content needs to be copied.
        private String elementId;

        /**
         * Handler's defaults constructor.
         * @param elementToCopyId id of the element whose content needs to be copied.
         */
        public CopyButtonClickHandler(String elementToCopyId){
            elementId=elementToCopyId;
        }

        /**
         * OnClick actions containing that actual code of the copy to the clipboard.
         * @param event
         */
        @Override
        public void onClick(ClickEvent event) {
            // Copy functionality for a manually IDed element located on the page.
            // (using gwt-elemental)
            final Selection selection = Browser.getWindow().getSelection();
            // clear all current selections.
            selection.removeAllRanges();
            // create a selection of the text in the IDed label.
            final Range range = Browser.getDocument().createRange();
            range.selectNodeContents(Browser.getDocument().getElementById(elementId));
            selection.addRange(range);
            // copy the selected text to the clipboard.
            if (!Browser.getWindow().getDocument().execCommand("copy", false, "")){
                Window.alert("Copy does not work with your browser. Try to update it to its latest version, or use another browser.\n" +
                        "Copy is compatible with: Chrome (v.43 or later), Firefox (v.41 or later), IE9, Opera (v.29 or later) and Safari (v.10 or later).");
            }
            // clear the selection.
            selection.removeAllRanges();
        }
    }
}
