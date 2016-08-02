package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.ui.DisclosurePanel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestDisclosureManager {
    class DisclosurePanelWrapper {
        DisclosurePanel disclosurePanel;
        String testId;

        DisclosurePanelWrapper(String testId, DisclosurePanel disclosurePanel) {
            this.testId = testId;
            this.disclosurePanel = disclosurePanel;
        }
    }

    List<DisclosurePanelWrapper> disclosurePanelWrappers = new ArrayList<>();

    String findTestId(DisclosurePanel disclosurePanel) {
        for (DisclosurePanelWrapper wrapper : disclosurePanelWrappers) {
            if (wrapper.disclosurePanel == disclosurePanel) return wrapper.testId;
        }
        return null;
    }

    public void add(String id, DisclosurePanel panel) {
        disclosurePanelWrappers.add(new TestDisclosureManager.DisclosurePanelWrapper(id, panel));
    }

}
