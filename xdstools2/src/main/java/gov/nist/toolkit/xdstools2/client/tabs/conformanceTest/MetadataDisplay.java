package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.xdstools2.client.command.command.LoadTestPartContentCommand;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.LoadTestPartContentRequest;

import static gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TestPlanDisplay.getShHtml;

/**
 *
 */
class MetadataDisplay extends FlowPanel {
    final private static String viewMetadataLabel = "&boxplus;View Metadata";
    final private static String hideMetadataLabel = "&boxminus;Hide Metadata";

    private final HTML metadataCtl = new HTML(viewMetadataLabel);
    private TestPartFileDTO testPartFileDTO;
    private ScrollPanel metadataViewerPanel = new ScrollPanel();
    private String testSession;
    private TestInstance testInstance;
    private String section;

    MetadataDisplay(TestPartFileDTO sectionTp, String testSession, TestInstance testInstance, String section) {
        this.testPartFileDTO = sectionTp;
        this.testSession = testSession;
        this.testInstance = testInstance;
        this.section = section;
        metadataViewerPanel.setVisible(false);
        metadataCtl.addStyleName("iconStyle");
        metadataCtl.addStyleName("inlineLink");
        metadataCtl.addClickHandler(new ViewMetadataClickHandler());
        add(metadataViewerPanel);
    }

    HTML getLabel() { return metadataCtl; }

    private class ViewMetadataClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent clickEvent) {
            metadataViewerPanel.setVisible(!metadataViewerPanel.isVisible());
            if (!metadataViewerPanel.isVisible()) {
                metadataCtl.setHTML(viewMetadataLabel);
            } else {
                metadataCtl.setHTML(hideMetadataLabel);
            }
            if (metadataViewerPanel.getWidget()==null) {
                new LoadTestPartContentCommand(){
                    @Override
                    public void onComplete(TestPartFileDTO testPartFileDTO) {
                        String metadataStr = testPartFileDTO.getHtlmizedContent().replace("<br/>", "\r\n");
                        metadataViewerPanel.add(getShHtml(metadataStr));
                    }
                }.run(new LoadTestPartContentRequest(XdsTools2Presenter.data().getCommandContext(),testPartFileDTO));
            }
        }
    }


}
