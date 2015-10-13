package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.Button;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;

/**
 * Created by Diane Azais local on 10/12/2015. Inspired by ImagesCell.java by L.Pelov.
 *
 * Custom cell to display the TestButtonsWidget.
 */
public class TestButtonsCell extends AbstractSafeHtmlCell<String> {

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

        // TODO this should be some kind of parameter on which to base the rendering of the cell
        // SafeHtml safeValue = SafeHtmlUtils.fromString(value);

        // generate the composite cell
        SafeStyles style = SafeStylesUtils.fromTrustedString("float:left;cursor:hand;cursor:pointer;");

        // Retrieve the widget to display in HTML form
        //TestButtonsWidget widget = new TestButtonsWidget();
       // Button bt1 = new Button("bt1");
        String widgetStr = bt1.getElement().toString();
        SafeHtml widgetHTML = SafeHtmlUtils.fromString(widgetStr);

        TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;

        RESOURCES.getPlayIcon();

        SafeHtml rendered = templates.cell("ICON_PDF", style, widgetHTML);
        sb.append(rendered);
    }
}
