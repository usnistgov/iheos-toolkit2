package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.resources.client.ImageResource;
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


}
