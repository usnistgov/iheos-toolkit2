package gov.nist.toolkit.desktop.client.abstracts;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 *
 */
public abstract class AbstractToolkitActivity extends AbstractActivity {
    public abstract GenericMVP getMVP();
    public abstract LayoutPanel onResume();
}
