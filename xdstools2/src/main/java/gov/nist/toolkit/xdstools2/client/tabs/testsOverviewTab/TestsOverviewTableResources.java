package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * Created by Diane Azais local on 11/26/2015.
 */
public interface TestsOverviewTableResources extends CellTable.Resources
{
    @Source(value = { CellTable.Style.DEFAULT_CSS, "css/TestsOverviewTableStyle.css" })
    CellTable.Style cellTableStyle();
}

