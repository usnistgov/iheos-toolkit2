package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.event.tabContainer.V2TabOpenedEvent;

import java.util.ArrayList;
import java.util.List;

public class TabContainer {
	private static TabContainer me = new TabContainer();

	//	private static TabLayoutPanel tabPanel = new TabLayoutPanel(1.5, Style.Unit.EM);
//	private static TabPanel TABPANEL = new TabPanel();

	// holds TabBar and currently selected panel from deck
	// TabBar in North section.  Center holds SimpleLayoutPanel. SimpleLayoutPanel
	// holds one element from the deck.
	private static DockLayoutPanel OUTERPANEL = new DockLayoutPanel(Style.Unit.EM);

	private static TabBar TABBAR = new TabBar();

//	// this hosts one element from deck at a time
	private static SimpleLayoutPanel INNERPANEL = new SimpleLayoutPanel();

	// Each element of TABBAR maps to one element of deck
	private static List<DockLayoutPanel> deck = new ArrayList<>();

	static {
		OUTERPANEL.addNorth(TABBAR, 2.0);
		OUTERPANEL.addNorth(new HTML("<hr style=\"background:#6495ED; border:0; height:5px\" />"), 1.0);
		OUTERPANEL.add(INNERPANEL);

		TABBAR.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> selectionEvent) {
				selectTab();
			}
		});
	}

	private TabContainer() {}

	public static TabContainer instance() { return me; }

//	public void addTab(VerticalPanel w, String title, boolean select) {
//		HTML left = new HTML();
//		left.setHTML("&nbsp");
//
//		HTML right = new HTML();
//		right.setHTML("&nbsp");
//
//		HorizontalPanel wrapper = new HorizontalPanel();
//
//		wrapper.add(left);
//		wrapper.add(w);
//		wrapper.add(right);
//		wrapper.setCellWidth(left, "1%");
//		wrapper.setCellWidth(right, "1%");
//
////		TABPANEL.add(wrapper, buildTabHeaderWidget(title, w));
//
//
////		if (select)
////			selectLastTab();
//		Xdstools2.getInstance().resizeToolkit();
//
//		announceOpen(title);
//	}

	public void addTab(DockLayoutPanel w, String title, boolean select) {
		TABBAR.addTab(buildTabHeaderWidget(title, w));
		deck.add(w);
		TABBAR.selectTab(TABBAR.getTabCount() - 1);
		selectTab();

		Xdstools2.getInstance().resizeToolkit();

		announceOpen(title);
	}

	public static void selectTab() {
		INNERPANEL.setWidget(deck.get(TABBAR.getSelectedTab()));
	}

	private void announceOpen(String title) {
		try {
//			int index = TABPANEL.getWidgetCount() - 1;
			int index = TABBAR.getTabCount() - 1;
			if (Xdstools2.getInstance().getIntegrationEventBus()!=null && index>0) {
				Xdstools2.getInstance().getIntegrationEventBus().fireEvent(new V2TabOpenedEvent(null,title /* this will be the dynamic tab code */,index));
			}
		} catch (Throwable t) {
			Window.alert("V2TabOpenedEvent error: " +t.toString());
		}
	}

	private static void deleteTab(int index) {

	}

	private Widget buildTabHeaderWidget(String title, final DockLayoutPanel content) {
		HorizontalPanel panel = new HorizontalPanel();
		Anchor x = new Anchor("X");
		x.setStyleName("roundedButton1");
		x.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				int i = deck.indexOf(content);
				deck.remove(i);
				TABBAR.removeTab(i);
				i = deck.size() - 1;
				selectTab(i);
			}
		});
		panel.add(x);
		HTML h = new HTML(title);
		panel.add(h);
		return panel;
	}

	public static void setWidth(String width) {
//		TABPANEL.setWidth(width);
	}

	public static void setHeight(String width) {
//		TABPANEL.setHeight(width);
	}

	public static void selectTab(int tabIndex) {
		TABBAR.selectTab(tabIndex);
		INNERPANEL.clear();
		INNERPANEL.add(deck.get(tabIndex));
//		TABPANEL.selectTab(tabIndex);
	}

	protected static Widget getTabPanel() {
		return OUTERPANEL;
	}

	protected static int getSelectedTab() {
		return TABBAR.getSelectedTab();
	}
//	protected static Widget getWidget(int tabIndex) {
//		return INNERPANEL.getWidget(tabIndex);
//	}

}
