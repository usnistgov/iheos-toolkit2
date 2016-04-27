package gov.nist.toolkit.toolkitServicesCommon.resource.xdm;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@XmlRootElement
public class XdmReportResource implements XdmReport {
    String report = null;
    List<XdmItem> items = new ArrayList<>();
    boolean pass;

    @Override
    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    @Override
    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public List<XdmItem> getItems() {
        return items;
    }

    public void setItems(List<XdmItem> items) {
        this.items = items;
    }

    public void addItem(XdmItem item) {
        items.add(item);
    }
}
