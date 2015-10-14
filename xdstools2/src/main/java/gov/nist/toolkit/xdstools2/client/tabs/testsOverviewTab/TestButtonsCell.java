package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;

/**
 * Created by Diane Azais local on 10/12/2015. Inspired by ImagesCell.java by L.Pelov.
 *
 * Custom cell to display the TestButtonsWidget.
 */
public class TestButtonsCell extends AbstractSafeHtmlCell<String> {
    TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;

    SafeHtml PLAY_ICON = makeImage(RESOURCES.getPlayIcon());
    SafeHtml REMOVE_ICON = makeImage(RESOURCES.getRemoveIcon());

    Button TEST_PLAN_BUTTON = new Button("Test Plan");
    Button LOG_BUTTON = new Button("Log");
    Button TEST_DESCRIPTION_BUTTON = new Button("Full Test Description");


    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
        /**
         * The template for this Cell, which includes styles and a value.
         *
         * @param styles
         *            the styles to include in the style attribute of the div
         * @param value
         *            the safe value. Since the value type is {@link SafeHtml},
         *            it will not be escaped before including it in the
         *            template. Alternatively, you could make the value type
         *            String, in which case the value would be escaped.
         * @return a {@link SafeHtml} instance
         */
        @SafeHtmlTemplates.Template("<div name=\"{0}\" style=\"{1}\">{2}</div>")
        SafeHtml cell(String name, SafeStyles styles, SafeHtml value);
    }

    public TestButtonsCell() {
        // declare the consumed events
        super(SimpleSafeHtmlRenderer.getInstance(), "click", "keydown");
    }

    public TestButtonsCell(SafeHtmlRenderer<String> renderer) {
        // declare the consumed events
        super(renderer, "click", "keydown");
    }

    /**
     * Create a singleton instance of the templates used to render the cell.
     */
    private static Templates templates = GWT.create(Templates.class);


    @Override
    protected void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
           if (data == null) {
            return;
        }

        // ------ generate the composite cell -----
        SafeStyles style = SafeStylesUtils.fromTrustedString("float:left;cursor:pointer;margin:3px;");

        SafeHtml rendered = templates.cell("PLAY_ICON", style, PLAY_ICON);
        sb.append(rendered);

        rendered = templates.cell("REMOVE_ICON", style, REMOVE_ICON);
        sb.append(rendered);

        SafeHtml testplanButtonHtml = makeButton(TEST_PLAN_BUTTON);
        rendered = templates.cell("TEST_PLAN_BUTTON", style, testplanButtonHtml);
        sb.append(rendered);

        SafeHtml logButtonHtml = makeButton(LOG_BUTTON);
        rendered = templates.cell("LOG_BUTTON", style, logButtonHtml);
        sb.append(rendered);

        SafeHtml testDescrButtonHtml = makeButton(TEST_DESCRIPTION_BUTTON);
        rendered = templates.cell("TEST_DESCRIPTION_BUTTON", style, testDescrButtonHtml);
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
     * Make icons available as SafeHtml
     * @param resource the image resource to transform
     * @return SafeHtml code for the image
     */
    private static SafeHtml makeImage(ImageResource resource) {
        AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
        return proto.getSafeHtml();
    }

    /**
     * Make buttons available as SafeHtml
     * @param b the button
     * @return SafeHtml code for the button
     */
    private static SafeHtml makeButton(Button b){
        return SafeHtmlUtils.fromTrustedString(b.getElement().toString());
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
