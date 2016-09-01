package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class HorizontalFlowPanel extends FlowPanel {

    @Override
    public void add(Widget w) {
        if (w != null) {
            if (this.getWidgetCount() > 0) {
                super.add(new InlineHTML(" "));
            }
            super.add(w);
            w.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        }
    }


    public void fullWidth() {
        this.setWidth("100%");
    }

}
