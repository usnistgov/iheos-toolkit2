package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import elemental.client.Browser;
import elemental.html.Selection;
import elemental.ranges.Range;
import gov.nist.toolkit.xdstools2.client.resources.IconsResources;

/**
 * Created by onh2 on 1/17/17.
 */
public class CopyButton extends PushButton {

    public CopyButton(String elementIdToCopy){
        super(new Image(IconsResources.INSTANCE.copyButton()));
        setTitle("Copy PID");
        setStyleName("copyBtn");
        addClickHandler(new CopyButtonClickHandler(elementIdToCopy));
    }

    public class CopyButtonClickHandler implements ClickHandler {
        private String elementId;

        public CopyButtonClickHandler(String elementToCopyId){
            elementId=elementToCopyId;
        }

        @Override
        public void onClick(ClickEvent event) {
            // Copy functionality for a label located a manually IDed on the page. Thanks to the import of gwt-elemental.

            final Selection selection = Browser.getWindow().getSelection();
            final Range range = Browser.getDocument().createRange();
            range.selectNodeContents(Browser.getDocument().getElementById(elementId));
            selection.removeAllRanges();
            selection.addRange(range);
            if (!Browser.getWindow().getDocument().execCommand("copy", false, "")){
                Window.alert("Copy does not work with your browser. Try to update it to its latest version, or use another browser.\n" +
                        "Copy is compatible with: Chrome (v.43 or later), Firefox (v.41 or later), IE9, Opera (v.29 or later) and Safari (v.10 or later).");
            }
            selection.removeAllRanges();
        }
    }
}
