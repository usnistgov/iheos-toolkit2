package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.results.client.XdstestLogId;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

class RawLogLoader implements ClickHandler {
	/**
	 * 
	 */
	private final MetadataInspectorTab metadataInspectorTab;
	XdstestLogId logId;
	TreeItem loadLogsTreeItem;
	List<StepResult> stepResults;

	RawLogLoader(MetadataInspectorTab metadataInspectorTab, XdstestLogId logId, List<StepResult> stepResults) {
		this.metadataInspectorTab = metadataInspectorTab;
		this.logId = logId;
		this.stepResults = stepResults;
	}

	void setLoadLogsTreeItem(TreeItem item) {
		loadLogsTreeItem = item;
	}

	public void onClick(ClickEvent event) {
		this.metadataInspectorTab.data.toolkitService.getRawLogs(logId, new AsyncCallback<TestLogs> () {

			public void onFailure(Throwable caught) {
				RawLogLoader.this.metadataInspectorTab.error(caught.getMessage());
			}

			public void onSuccess(TestLogs testLogs) {
				if (testLogs.assertionResult != null && testLogs.assertionResult.status == false) {
					RawLogLoader.this.metadataInspectorTab.error(testLogs.assertionResult.assertion);
				} else  {
					RawLogLoader.this.metadataInspectorTab.installTestLogs(testLogs);
					if (testLogs.size() > 1)
						RawLogLoader.this.metadataInspectorTab.showHistoryOrContents();
					else {
						try {
							Result result = RawLogLoader.this.metadataInspectorTab.findResultbyLogId(testLogs);
							TestLog testLog = testLogs.getTestLog(0);
							String stepName = testLog.stepName;
							StepResult stepResult = result.findStep(stepName);
							RawLogLoader.this.metadataInspectorTab.expandLogMenu(stepResult, loadLogsTreeItem);
						} catch (Exception e) {
							RawLogLoader.this.metadataInspectorTab.showHistoryOrContents();
						}
					}
				}
			}

		});
	}

}