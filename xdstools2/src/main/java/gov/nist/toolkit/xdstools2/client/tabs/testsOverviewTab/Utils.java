package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

/**
 * Created by Diane Azais local on 10/15/2015.
 */
public class Utils {

    /**
     * Make a GWT button available as HTML
     * @param b
     * @return
     */
    public static SafeHtml getButtonHtml(Button b) {
        return SafeHtmlUtils.fromTrustedString(b.toString());
    }

    /**
     * Make buttons available as SafeHtml
     * @param b the button
     * @return SafeHtml code for the button
     */
    public static SafeHtml makeIconButton(Button b){
        return SafeHtmlUtils.fromTrustedString(b.getElement().toString());
    }

    /**
     * Create a GWT button with custom parameters
     * @param title
     * @param icon
     * @return
     */
    public static Button makeIconButton(String label, String title, Image icon){
        Button b = new Button(label);
        b.setTitle(title);
        b.getElement().appendChild(icon.getElement());
        return b;
    }

    /**
     * Create a GWT button with custom parameters
     * @param title
     * @param icon
     * @return
     */
    public static Button makeIconButton(String title, Image icon){
        return makeIconButton("", title, icon);
    }

    /**
     * Make icons available as SafeHtml
     * @param resource the image resource to transform
     * @return SafeHtml code for the image
     */
    public static SafeHtml makeImage(ImageResource resource) {
        AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
        return proto.getSafeHtml();
    }

    /**
     * Create and return a custom SafeHtml template to display icons in table cells
     * @return a custom SafeHtml template
     */
    public static SafeHtml buildCustomIconCell(String name, SafeStyles styles, SafeHtml value){
        // Create a singleton instance of the templates used to render the cell.
        Templates templates = GWT.create(Templates.class);
        return templates.cell(name, styles, value);
    }

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
        @SafeHtmlTemplates.Template("<div name=\"{0}\" style=\"{1}\">{2}</div>") SafeHtml cell(String name, SafeStyles styles, SafeHtml value);
    }


}
