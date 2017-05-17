package gov.nist.toolkit.toolkitFramework.client.toolSupport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.toolkitFramework.client.events.TabOpenedEvent;
import gov.nist.toolkit.toolkitFramework.client.events.TabSelectedEvent;
import gov.nist.toolkit.toolkitFramework.client.injector.ToolkitEventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.dom.client.Style.Unit.EM;

public class TabContainer {

	private ToolkitEventBus eventBus;

	// holds TabBar and currently selected panel from deck
	// TabBar in North section.  Center holds SimpleLayoutPanel. SimpleLayoutPanel
	// holds one element from the deck.
	private  DockLayoutPanel OUTERPANEL = new DockLayoutPanel(EM);

	private static TabBar TABBAR;

	private  DeckLayoutPanel INNER_DECKPANEL;

	// Each element of TABBAR maps to one element of deck
	private  List<DockLayoutPanel> deck;

	@Inject
	private TabContainer(ToolkitEventBus eventBus) {
		this.eventBus = eventBus;
		GWT.log("starting tabcontainer");
		TABBAR = new TabBar();
		deck = new ArrayList<>();
		INNER_DECKPANEL = new DeckLayoutPanel();
		OUTERPANEL.addNorth(TABBAR, 3.0);
		OUTERPANEL.add(INNER_DECKPANEL);
		addTab(new DockLayoutPanel(EM),"Default", true);
		TABBAR.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> selectionEvent) {
				selectTab();
			}
		});
	}

	/**
	 * Create a new tab/tool.
	 * @param w - content
	 * @param title - title to appear in the little tab at the top
	 * @param select - should be selected upon creation (ignored)
     */
	public void addTab(DockLayoutPanel w, String title, boolean select) {

		w.getElement().getStyle().setMarginLeft(4, Style.Unit.PX);
		w.getElement().getStyle().setMarginRight(4, Style.Unit.PX);

		assert(TABBAR != null);
		TABBAR.addTab(buildTabHeaderWidget(title, w));

		deck.add(w);
		TABBAR.selectTab(TABBAR.getTabCount() - 1);
		selectTab();

//		XdsTools2Presenter.data().resizeToolkit();

		announceOpen(title);
	}

	public void selectTab() {
		Widget dockLp = deck.get(TABBAR.getSelectedTab());

		if (INNER_DECKPANEL.getWidgetIndex(dockLp)==-1) {
			INNER_DECKPANEL.add(dockLp);
		}else {
            String tabName=TABBAR.getTab(TABBAR.getSelectedTab()).toString().split("<div class=\"gwt-HTML\">")[1].split("</div>")[0];
            eventBus.fireEvent(new TabSelectedEvent(tabName));
		}
		INNER_DECKPANEL.showWidget(dockLp);
	}

	private void announceOpen(String title) {
		try {
//			int index = TABPANEL.getWidgetCount() - 1;
			int index = TABBAR.getTabCount() - 1;
			if (index>0) {
				eventBus.fireEvent(new TabOpenedEvent(title /* this will be the dynamic tab code */,index));
			}
		} catch (Throwable t) {
			Window.alert("TabOpenedEvent error: " +t.toString());
		}
	}

	private static void deleteTab(int index) {

	}

	private Widget buildTabHeaderWidget(String title, final DockLayoutPanel content) {
		HorizontalPanel panel = new HorizontalPanel();
		Anchor x = new Anchor("X");
		x.setStyleName("roundedButton2");
		x.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				int i = deck.indexOf(content);
				deck.remove(i);
				INNER_DECKPANEL.remove(i);
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

	public void selectTab(int tabIndex) {
		TABBAR.selectTab(tabIndex);

		INNER_DECKPANEL.showWidget(tabIndex);
		/*
		INNERPANEL.clear();
		INNERPANEL.addTest(deck.get(tabIndex));
		*/

//		TABPANEL.selectTab(tabIndex);
	}

	public Widget getTabPanel() {
		return OUTERPANEL;
	}

	protected static int getSelectedTab() {
		return TABBAR.getSelectedTab();
	}
//	protected static Widget getWidget(int tabIndex) {
//		return INNERPANEL.getWidget(tabIndex);
//	}

}
