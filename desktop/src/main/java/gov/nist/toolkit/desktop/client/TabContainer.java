package gov.nist.toolkit.desktop.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.desktop.client.abstracts.GenericMVP;
import gov.nist.toolkit.desktop.client.events.TabOpenedEvent;
import gov.nist.toolkit.desktop.client.events.TabSelectedEvent;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;
import gov.nist.toolkit.desktop.client.home.WelcomePanel;
import gov.nist.toolkit.desktop.client.tools.toy.Toy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.dom.client.Style.Unit.EM;

public class TabContainer {

	private ToolkitEventBus eventBus;

	private PlaceController placeController;

	// holds TabBar and currently selected panel from deck
	// TabBar in North section.  Center holds SimpleLayoutPanel. SimpleLayoutPanel
	// holds one element from the deck.
	private  DockLayoutPanel OUTERPANEL = new DockLayoutPanel(EM);

	private static TabBar TABBAR;

	private  DeckLayoutPanel INNER_DECKPANEL;

	// Each element of TABBAR maps to one element of deck
	// and the same element of activities
	private  List</*DockLayoutPanel*/Widget> deck = new ArrayList<>();
	private List<AbstractToolkitActivity> activities = new ArrayList<>();

	@Inject
	private TabContainer(final PlaceController placeController, ToolkitEventBus eventBus) {
		this.placeController = placeController;
		this.eventBus = eventBus;
		assert(eventBus != null);
		TABBAR = new TabBar();
		INNER_DECKPANEL = new DeckLayoutPanel();
		OUTERPANEL.addNorth(TABBAR, 3.0);
		OUTERPANEL.add(INNER_DECKPANEL);

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
     */
	void addTab(/*DockLayoutPanel*/ Widget w, String title, AbstractToolkitActivity activity) {

		// Without this, every time we create a new tab we get two:
		// The intended one and a copy of WelcomePanel
		// Don't know why. Something to do with history management.
		if (w instanceof WelcomePanel)
			return;

		w.getElement().getStyle().setMarginLeft(4, Style.Unit.PX);
		w.getElement().getStyle().setMarginRight(4, Style.Unit.PX);

		assert(TABBAR != null);
		TABBAR.addTab(buildTabHeaderWidget(title, w));

		deck.add(w);
		activities.add(activity);
		TABBAR.selectTab(TABBAR.getTabCount() - 1);
		selectTab();

		announceOpen(title);
	}

	private void selectTab() {
		Widget dockLp = deck.get(TABBAR.getSelectedTab());

		if (INNER_DECKPANEL.getWidgetIndex(dockLp) == -1) {
			INNER_DECKPANEL.add(dockLp);
		} else {
			String tabName = TABBAR.getTab(TABBAR.getSelectedTab()).toString().split("<div class=\"gwt-HTML\">")[1].split("</div>")[0];
			eventBus.fireEvent(new TabSelectedEvent(tabName));
		}
		INNER_DECKPANEL.showWidget(dockLp);
	}

	private void announceOpen(String title) {
		try {
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

	private Widget buildTabHeaderWidget(String title, final /*DockLayoutPanel*/ Widget content) {
		HorizontalPanel panel = new HorizontalPanel();
		Anchor x = new Anchor("X");
		x.setStyleName("roundedButton2");

		// Handle tab delete
		x.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				int i = deck.indexOf(content);
				deck.remove(i);
				AbstractActivity activity = activities.get(i);
				activities.remove(i);
				INNER_DECKPANEL.remove(i);
				TABBAR.removeTab(i);
				i = deck.size() - 1;
				// select last tab for display
				if (i >= 0) {
					selectTab(i);
					if (activity != null) {
						activity.onStop();
					}
				}
			}
		});
		panel.add(x);
		HTML h = new HTML(title);
		panel.add(h);
		return panel;
	}

	static void setWidth(String width) {
//		TABPANEL.setWidth(width);
	}

	static void setHeight(String width) {
//		TABPANEL.setHeight(width);
	}

	private void selectTab(int tabIndex) {
		TABBAR.selectTab(tabIndex);

		INNER_DECKPANEL.showWidget(tabIndex);
	}

	Widget getTabPanel() {
		return OUTERPANEL;
	}

	protected static int getSelectedTab() {
		return TABBAR.getSelectedTab();
	}

}
