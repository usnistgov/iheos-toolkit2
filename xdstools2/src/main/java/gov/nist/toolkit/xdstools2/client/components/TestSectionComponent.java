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
                    new Image("icons2/correct-32.png")
                    :
                    new Image("icons2/cancel-32.png"));

            panel.add(new HTML("Blah Blah Blah"));
        }
        Image play = new Image("icons2/play-32.png");
        play.setTitle("Run");
        header.add(play);
        Image delete = new Image("icons2/remove-32.png");
        delete.setTitle("Delete Log");
        header.add(delete);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
