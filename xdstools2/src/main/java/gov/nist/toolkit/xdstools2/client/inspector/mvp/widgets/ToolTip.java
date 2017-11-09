package gov.nist.toolkit.xdstools2.client.inspector.mvp.widgets;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

import java.util.List;

public class ToolTip extends PopupPanel {

        int MAX_TOOLTIPS = 5;

        private HTML contents;
        private Timer timer;

        public ToolTip() {
            super(true);
            contents = new HTML();
            add(contents);
//            setStyleName(WidgetsSampleBundle.INSTANCE.getCss().tooltip());
        }

        void setContents(List<String> text) {

            contents.setHTML("");

            if (text!=null)  {
                int count = text.size();
                for (int cx=0; cx<count; cx++) {
                    contents.setHTML(contents.getHTML() + "<p>" + text.get(cx) + "</p>");
                    if (cx== MAX_TOOLTIPS) {
                        contents.setHTML(contents.getHTML() + "<p>"+ (count- MAX_TOOLTIPS) +" more...</p>");
                    }
                }
            }
        }

        public void show(int x, int y, final List<String> text, final int delay) {

            if (text==null) return;

            setContents(text);
            setPopupPosition(x, y);
            super.show();

            if (delay>0) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer() {
                    public void run() {
                        hide();
                        timer = null;
                    }
                };
                timer.schedule(delay);
            }
        }
}
