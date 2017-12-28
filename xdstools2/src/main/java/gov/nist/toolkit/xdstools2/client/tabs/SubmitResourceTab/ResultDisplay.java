package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

public class ResultDisplay {
    static public void display(Result result, ILogger logger) {
        logger.addLog("At " + result.getTimestamp());
        String prefix = "ReportBuilder: ";
        for (AssertionResult ar: result.assertions.assertions) {
            String content = ar.assertion;
            if (content.startsWith(prefix))
                content = content.substring(prefix.length());
            if (ar.status) {
                content = content.trim();
                if (content.startsWith("Ref =")) {
                    String link = content.substring("Ref =".length()).trim();
                    Anchor anchor = new Anchor();
                    anchor.setTarget("_blank");
                    anchor.setHref(link);
                    anchor.setText(link);
                    HorizontalFlowPanel fp = new HorizontalFlowPanel();
                    fp.add(new Label("Ref = "));
                    fp.add(anchor);
                    logger.addLog(fp);
                } else
                    logger.addLog(content);
            }
            else {
                Label l = new Label(content);
                l.setStyleName("testFail");
                logger.addLog(l);
            }
        }
    }

}
