package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.statusCell;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Utils;

/**
 * Created by Diane Azais local on 11/25/2015.
 */
public class StatusCell extends AbstractSafeHtmlCell<String> {

    TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;

    private SafeHtml NOT_RUN_ICON = Utils.makeImage(RESOURCES.getCircleOutlineIconWhite());
    private SafeHtml HAS_WARNINGS_ICON = Utils.makeImage(RESOURCES.getInfoIconWhite());
    private SafeHtml PASSED_ICON = Utils.makeImage(RESOURCES.getCheckIconWhite());
    private SafeHtml FAILED_ICON = Utils.makeImage(RESOURCES.getDangerIconWhite());

    public static String NOT_RUN_ICON_NAME = "NOT_RUN_ICON";
    public static String HAS_WARNINGS_ICON_NAME = "HAS_WARNINGS_ICON";
    public static String PASSED_ICON_NAME = "PASSED_ICON";
    public static String FAILED_ICON_NAME = "FAILED_ICON";

    public static String NOT_RUN_ICON_TOOLTIP = "Not run";
    public static String HAS_WARNINGS_ICON_TOOLTIP = "Run with warnings";
    public static String PASSED_ICON_TOOLTIP = "Passed";
    public static String FAILED_ICON_TOOLTIP = "Failed";


    public StatusCell() {
        // declare the consumed events - no event handling necessary in this cell
        super(SimpleSafeHtmlRenderer.getInstance(), "", "");
    }



    @Override
    protected void render(Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {

        // ------ generate the composite cell -----
        SafeStyles style = SafeStylesUtils.fromTrustedString("");

        if (data == null) {
            return;
        }
        //TODO use common source for the labels Pass, Fail etc
        else if (data.asString() == "not run") {
            SafeHtml rendered = Utils.buildCustomIconCellWithTooltip(NOT_RUN_ICON_NAME, style, NOT_RUN_ICON, NOT_RUN_ICON_TOOLTIP);
            sb.append(rendered);
            return;
        }
        else if (data.asString() == "run with warnings"){
            SafeHtml rendered = Utils.buildCustomIconCellWithTooltip(HAS_WARNINGS_ICON_NAME, style, HAS_WARNINGS_ICON, HAS_WARNINGS_ICON_TOOLTIP);
            sb.append(rendered);
            return;
        }
        else if (data.asString() == "pass"){
            SafeHtml rendered = Utils.buildCustomIconCellWithTooltip(PASSED_ICON_NAME, style, PASSED_ICON, PASSED_ICON_TOOLTIP);
            sb.append(rendered);
            return;
        }
        else if (data.asString() == "failed") {
            SafeHtml rendered = Utils.buildCustomIconCellWithTooltip(FAILED_ICON_NAME, style, FAILED_ICON, FAILED_ICON_TOOLTIP);
            sb.append(rendered);
            return;
        }
        else return;
    }

}
