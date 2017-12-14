package gov.nist.toolkit.xdstools2.client.inspector;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.xdstools2.client.command.command.GetRawLogsCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallback;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRawLogsRequest;

import java.util.List;


class RawLogLoader implements ClickHandler {
	/**
	 * 
	 */
	private final MetadataInspectorTab metadataInspectorTab;
	TestInstance logId;
	TreeItem loadLogsTreeItem;
	List<StepResult> stepResults;
	SimpleCallback onComplete;

	RawLogLoader(MetadataInspectorTab metadataInspectorTab, TestInstance logId, List<StepResult> stepResults) {
		this.metadataInspectorTab = metadataInspectorTab;
		this.logId = logId;
		this.stepResults = stepResults;
	}

	RawLogLoader(MetadataInspectorTab metadataInspectorTab, TestInstance logId, List<StepResult> stepResults, SimpleCallback callback) {
	this(metadataInspectorTab,logId,stepResults);
	this.onComplete = callback;
	}

	void setLoadLogsTreeItem(TreeItem item) {
		loadLogsTreeItem = item;
	}

	public void onClick(ClickEvent event) {
		loadTestLogs();
	}

	void loadTestLogs() {
		/*this.metadataInspectorTab.data.*/
		new GetRawLogsCommand(){
			@Override
			public void onFailure(Throwable caught) {
				RawLogLoader.this.metadataInspectorTab.error(caught.getMessage());
			}

			@Override
			public void onComplete(TestLogs testLogs) {
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
				if (onComplete!=null)
					onComplete.run();
			}
		}.run(new GetRawLogsRequest(ClientUtils.INSTANCE.getCommandContext(),logId));
	}

}