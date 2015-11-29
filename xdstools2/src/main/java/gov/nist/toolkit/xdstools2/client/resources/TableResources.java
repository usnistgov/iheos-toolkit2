package gov.nist.toolkit.xdstools2.client.resources;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * Created by Diane Azais local on 11/29/2015.
 */
 public interface TableResources extends CellTable.Resources {

    interface TableStyle extends CellTable.Style {
    }

    @Override
    @Source({ CellTable.Style.DEFAULT_CSS, "css/tablestyles.css" })
    TableStyle cellTableStyle();


}
