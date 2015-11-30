package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.Button;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Utils;


/**
 * Created by Diane Azais local on 10/12/2015. Inspired by ImagesCell.java by L.Pelov.
 *
 * Custom cell to display the TestButtonsWidget.
 */
public class CommandsCell extends AbstractSafeHtmlCell<String> {
    TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;

    public static String PLAY_ICON_NAME = "PLAY_BUTTON";
    public static String REMOVE_ICON_NAME = "REMOVE_BUTTON";
    public static String TEST_PLAN_BUTTON_NAME = "TEST_PLAN_BUTTON";
    public static String LOG_BUTTON_NAME = "LOG_BUTTON";
    public static String TEST_DESCRIPTION_BUTTON_NAME = "TEST_DESCRIPTION_BUTTON";

    public static String PLAY_BUTTON_TOOLTIP = "Run this test";
    public static String REMOVE_BUTTON_TOOLTIP = "Delete test results (cannot be undone!)";
    public static String TEST_PLAN_BUTTON_TOOLTIP = "Display test plan in new browser window";
    public static String LOG_BUTTON_TOOLTIP = "Display log in new browser window";
    public static String TEST_DESCRIPTION_BUTTON_TOOLTIP = "Display full test description in new browser window";


    SafeHtml PLAY_BUTTON = Utils.makeImage(RESOURCES.getPlayIconWhite24());
    SafeHtml REMOVE_BUTTON = Utils.makeImage(RESOURCES.getDeleteIconWhite24());
    Button TEST_PLAN_BUTTON = new Button("Test Plan");
    Button LOG_BUTTON = new Button("Log");
    Button TEST_DESCRIPTION_BUTTON = new Button("Description");


    public CommandsCell() {
        // declare the consumed events
        super(SimpleSafeHtmlRenderer.getInstance(), "click", "keydown");
    }

    public CommandsCell(SafeHtmlRenderer<String> renderer) {
        // declare the consumed events
        super(renderer, "click", "keydown");
    }


    @Override
    protected void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
        if (data == null) {
            return;
        }

        // ------ generate the composite cell -----
        SafeStyles style = SafeStylesUtils.fromTrustedString("float:left;cursor:pointer;margin:3px;");

        SafeHtml rendered = Utils.buildCustomIconCellWithTooltip(PLAY_ICON_NAME, style, PLAY_BUTTON, PLAY_BUTTON_TOOLTIP);
        sb.append(rendered);

        rendered = Utils.buildCustomIconCellWithTooltip(REMOVE_ICON_NAME, style, REMOVE_BUTTON, REMOVE_BUTTON_TOOLTIP);
        sb.append(rendered);

        rendered = Utils.buildCustomIconCellWithTooltip(TEST_PLAN_BUTTON_NAME, style, Utils.getButtonHtml(TEST_PLAN_BUTTON), TEST_PLAN_BUTTON_TOOLTIP);
        sb.append(rendered);

        rendered = Utils.buildCustomIconCellWithTooltip(LOG_BUTTON_NAME, style, Utils.getButtonHtml(LOG_BUTTON), LOG_BUTTON_TOOLTIP);
        sb.append(rendered);

        rendered = Utils.buildCustomIconCellWithTooltip(TEST_DESCRIPTION_BUTTON_NAME, style, Utils.getButtonHtml(TEST_DESCRIPTION_BUTTON), TEST_DESCRIPTION_BUTTON_TOOLTIP);
        sb.append(rendered);
    }


    /**
     * Called when an event occurs in a rendered instance of this Cell. The
     * parent element refers to the element that contains the rendered cell, NOT
     * to the outermost element that the Cell rendered.
     *
     * Passes the name of the component that was clicked (icon or button) to the update component valueUpdater.
     */
    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
                               Element parent, String value, NativeEvent event,
                               com.google.gwt.cell.client.ValueUpdater<String> valueUpdater) {

        // Let AbstractCell handle the keydown event.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        // Handle the click event.
        if ("click".equals(event.getType())) {

            // Ignore clicks that occur outside of the outermost element.
            EventTarget eventTarget = event.getEventTarget();
            if (parent.isOrHasChild(Element.as(eventTarget))) {

                // use this to get the selected element!!
                Element el = Element.as(eventTarget);

                // check if we really click on the image
                if (el.getNodeName().equalsIgnoreCase("IMG") || el.getNodeName().equalsIgnoreCase("BUTTON")) {
                    doAction(el.getParentElement().getAttribute("name"),
                            valueUpdater);
                }

            }
        }

    };

    /**
     * onEnterKeyDown is called when the user presses the ENTER key will the
     * Cell is selected. You are not required to override this method, but its a
     * common convention that allows your cell to respond to key events.
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent,
                                  String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
        doAction(value, valueUpdater);
    }



    /**
     * Internal action
     *
     * @param value
     *            selected value
     * @param valueUpdater
     *            value updater or the custom value update to be called
     */
    private void doAction(String value, ValueUpdater<String> valueUpdater) {
        // Trigger a value updater. In this case, the value doesn't actually
        // change, but we use a ValueUpdater to let the app know that a value
        // was clicked.
        if (valueUpdater != null)
            valueUpdater.update(value);
    }

}
