package gov.nist.toolkit.xdstools2.client;

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

	private static TabBar TABBAR = new TabBar();

	private static FlowPanel OUTERPANEL = new FlowPanel();

	// this hosts one element from deck at a time
	private static FlowPanel INNERPANEL = new FlowPanel();

	// Each element of TABBAR maps to one element of deck
	private static List<FlowPanel> deck = new ArrayList<>();

	static {
		OUTERPANEL.add(TABBAR);
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

	public void addTab(FlowPanel w, String title, boolean select) {

//		TABPANEL.add(h, buildTabHeaderWidget(title, h));

		TABBAR.addTab(buildTabHeaderWidget(title, w));
		int index = TABBAR.getSelectedTab();
		INNERPANEL.clear();
		INNERPANEL.add(w);
		deck.add(w);
		TABBAR.selectTab(TABBAR.getTabCount() - 1);

//		if (select)
//			selectLastTab();
		Xdstools2.getInstance().resizeToolkit();

		announceOpen(title);
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

	private Widget buildTabHeaderWidget(String title, final FlowPanel content) {
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

	public static void selectTab() {
		INNERPANEL.clear();
		INNERPANEL.add(deck.get(TABBAR.getSelectedTab()));
	}

	protected static Widget getTabPanel() {
		return OUTERPANEL;
	}

	protected static int getSelectedTab() {
		return TABBAR.getSelectedTab();
	}
	protected static Widget getWidget(int tabIndex) {
		return INNERPANEL.getWidget(tabIndex);
	}

}
