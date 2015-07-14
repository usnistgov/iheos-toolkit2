package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Panel {
	HorizontalPanel h = null;
	VerticalPanel v = null;

	public Panel(HorizontalPanel h) {
		this.h = h;
	}

	public Panel(VerticalPanel v) {
		this.v = v;
	}

	public void add(Widget w) {
		if (h != null)
			h.add(w);
		else if (v != null)
			v.add(w);
	}

	public void clear() {
		if (h != null)
			h.clear();
		else if (v != null)
			v.clear();
	}
}
