package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class OrchSupport {

    public static Widget tableHeader(String text) {
       HTML h = new HTML(text);
       h.addStyleName("table-header");
       return h;
   }

    public static FlexTable buildTable() {
       FlexTable table = new FlexTable();
       stylize(table);
        return table;
   }

    public static void stylize(FlexTable table) {
       table.setBorderWidth(2);
       table.addStyleName("border-collapse");
   }
}
