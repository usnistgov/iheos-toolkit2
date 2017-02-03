package gov.nist.toolkit.xdstools2.client.event.tabContainer;


import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * V2TabOpenedEvent should be raised where V2 is running in a hosted environment mode with another top level tab container such as in Xdstools3 (V3).
 */
public class V2TabOpenedEvent extends GwtEvent<V2TabOpenedEventHandler> {



    public static final Type<V2TabOpenedEventHandler> TYPE = new Type<V2TabOpenedEventHandler>();

    private int tabIndex;
	private String title;
    private VerticalPanel topPanel;

	public String getTitle() {
		return this.title;
	}

    public VerticalPanel getTopPanel() {
        return this.topPanel;
    }


    public V2TabOpenedEvent(VerticalPanel w) {
        this.topPanel = w;
    }

    public V2TabOpenedEvent(VerticalPanel w, String title, int tabIndex) {
        this.tabIndex = tabIndex;
		this.title = title;
        this.topPanel = w;
    }

    @Override
    public Type<V2TabOpenedEventHandler> getAssociatedType() {
        return TYPE;
    }


    @Override
    protected void dispatch(V2TabOpenedEventHandler handler) {

        handler.onV2TabOpened(this);

    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

}



