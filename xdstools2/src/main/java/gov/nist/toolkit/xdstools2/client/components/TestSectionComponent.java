package gov.nist.toolkit.xdstools2.client.components;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;

/**
 *
 */
public class TestSectionComponent implements IsWidget {
    HorizontalFlowPanel header = new HorizontalFlowPanel();
    HorizontalFlowPanel body = new HorizontalFlowPanel();
    DisclosurePanel panel = new DisclosurePanel(header);

    public TestSectionComponent(SectionOverviewDTO sectionOverview) {
        HTML sectionLabel = new HTML("Section: " + sectionOverview.getName());
        if (sectionOverview.isRun()) {
            if (sectionOverview.isPass())
                header.addStyleName("testOverviewHeaderSuccess");
            else
                header.addStyleName("testOverviewHeaderFail");
        } else
            header.addStyleName("testOverviewHeaderNotRun");
        header.add(sectionLabel);
        if (sectionOverview.isRun()) {
            header.add((sectionOverview.isPass()) ?
                    new Image("icons/ic_done_black_24dp_1x.png")
                    :
                    new Image("icons/ic_warning_black_24dp_1x.png"));

            panel.add(new HTML("Blah Blah Blah"));
        }
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
