package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Difference;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.DocumentEntryDiff;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ResourceItem;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;

import java.util.ArrayList;
import java.util.List;

public class DetailDisplay extends CommonDisplay {
	public DetailDisplay(MetadataInspectorTab it) {
		this.detailPanel = it.detailPanel;
		this.metadataCollection = it.data.combinedMetadata;
		this.it = it;
	}

	public DetailDisplay(MetadataInspectorTab it, VerticalPanel panel) {
		this.detailPanel = panel;
		this.metadataCollection = it.data.combinedMetadata;
		this.it = it;
	}

	void displayDetail(AssertionResults asserts) {
		detailPanel.add(HyperlinkFactory.addHTML("<h4>Assertion Details</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;

		for (AssertionResult ar : asserts.assertions) {
			if (ar.status) {
				ft.setText(row, 0, ar.assertion);
			} else {
				ft.setHTML(row, 0, "<font color=\"#FF0000\">" + ar.assertion + "</font>");
			}
			row++;
		}

		detailPanel.add(ft);
	}

	void displayDetail(MetadataObject mo, MetadataObject diff) {
		detailPanel.clear();
		if (mo instanceof SubmissionSet) 
			displayDetail((SubmissionSet) mo, (SubmissionSet)diff);
		if (mo instanceof DocumentEntry) {
			DocumentEntry compareTo = null;
			if (it.dataNotification!=null) {
				compareTo = (DocumentEntry)it.dataNotification.getComparable();
			}
			displayDetail((DocumentEntry) mo, compareTo);

		}
		if (mo instanceof Folder) 
			displayDetail((Folder) mo, (Folder) diff);
		if (mo instanceof Association) 
			displayDetail((Association) mo);
		if (mo instanceof ObjectRef) 
			displayDetail((ObjectRef) mo);
		if (mo instanceof ResourceItem)
			displayDetail((ResourceItem) mo);
	}


	void displayDetail(SubmissionSet ss, SubmissionSet diff) {
//		detailPanel.add(HyperlinkFactory.addHTML("<h4>Submission Set</h4>"));
        addTitle(HyperlinkFactory.addHTML("<h4>Submission Set</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;
		boolean b;

        detailPanel.add(ft);

        b = diff.title != null;
		ft.setHTML(row, 0, bold("title",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.title, ss.titleX));
		row++;

		b = diff.comments != null;
		ft.setHTML(row, 0, bold("comments",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.comments, ss.commentsX));
		row++;

		b = diff.id != null;
		ft.setHTML(row, 0, bold("id",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.id, ss.idX));
		row++;

		b = diff.uniqueId != null;
		ft.setHTML(row, 0, bold("uniqueId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.uniqueId, ss.uniqueIdX));
		row++;

		b = diff.patientId != null;
		ft.setHTML(row, 0, bold("patientId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.patientId, ss.patientIdX));
		row++;

		b = diff.status != null;
		ft.setHTML(row, 0, bold("availabilityStatus",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.status, ss.statusX));
		row++;

		b = diff.home != null;
		ft.setHTML(row, 0, bold("home",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.home, ss.homeX));
		row++;

		b = diff.submissionTime != null;
		ft.setHTML(row, 0, bold("submissionTime",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.submissionTime, ss.submissionTimeX));
		row++;

		b = diff.sourceId != null;
		ft.setHTML(row, 0, bold("sourceId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, ss.sourceId, ss.sourceIdX));
		row++;
		
		// don't know how to handle diffs yet on extra metadata
		b = false;
		row = displayDetail(ft, row, b, ss.extra, ss.extraX);

		b = diff.contentTypeCode != null;
		row = displayDetail(ft, row, b, "contentTypeCode", ss.contentTypeCode, ss.contentTypeCodeX);
		
		b = diff.intendedRecipients != null;
		row = displayDetail(ft, row,  b, "intendedRecipient", ss.intendedRecipients, ss.intendedRecipientsX);

		b = diff.authors != null;
		row = displayDetail(ft, row, b, ss.authors, ss.authorsX);

	}

	private void displayDetail(ResourceItem ri) {
		String title = "<h4>" + ri.getType() + "</h4>";
		addTitle(HyperlinkFactory.addHTML(title));
		detailPanel.add(new HTML(ri.getHtmlizedJson()));
	}

	private void displayDetail(DocumentEntry de, DocumentEntry diff) {
		List<Difference> diffs = null;
		String diffsLabel = "";
		if (diff!=null) {
			diffs = new DocumentEntryDiff().compare(de, diff);
			if (diffs!=null) {
				diffsLabel = " (" + diffs.size() + " difference(s))";
			}
		} else {
			diffs = new ArrayList<>();
		}

//		detailPanel.add(HyperlinkFactory.addHTML("<h4>Document Entry</h4>"));
		String title = (de.isFhir) ? "<h4>Document Entry (translated from DocumentReference)</h4>" : "<h4>Document Entry" + diffsLabel + "</h4>";
		addTitle(HyperlinkFactory.addHTML(title));
		FlexTable ft = new FlexTable();
		int row=0;
		boolean b = false;

		try {
			if (!de.isFhir) {
				ft.setHTML(row, 0, bold("objectType", b));
				ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.objectType, de.objectTypeX));
				row++;
			}

			ft.setHTML(row, 0, highlight("title", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.title, de.titleX));
			row++;

			ft.setHTML(row, 0, highlight("comments", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.comments, de.commentsX));
			row++;

			ft.setHTML(row, 0, highlight("id", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.id, de.idX));
			row++;

			if (!de.isFhir) {
				ft.setHTML(row, 0, highlight("lid", diffs));
				ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.lid, de.lidX));
				row++;
			}

			if (!de.isFhir) {
				ft.setHTML(row, 0, highlight("version", diffs));
				ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.version, de.versionX));
				row++;
			}

			ft.setHTML(row, 0, highlight("uniqueId", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.uniqueId, de.uniqueIdX));
			row++;

			ft.setHTML(row, 0, highlight("patientId", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.patientId, de.patientIdX));
			row++;

			ft.setHTML(row, 0, highlight("availabilityStatus", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.status, de.statusX));
			row++;

			if (!de.isFhir) {
				ft.setHTML(row, 0, highlight("homeCommunityId", diffs));
				ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.home, de.homeX));
				row++;
			}

			ft.setHTML(row, 0, highlight("mimeType", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.mimeType, de.mimeTypeX));
			row++;

			ft.setHTML(row, 0, highlight("hash", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.hash, de.hashX));
			row++;

			ft.setHTML(row, 0, highlight("size", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.size, de.sizeX));
			row++;

			title = (de.isFhir) ? "content.url" : "repositoryUniqueId";
			ft.setHTML(row, 0, highlight(title, diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.repositoryUniqueId, de.repositoryUniqueIdX));
			row++;

			ft.setHTML(row, 0, highlight("lang", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.lang, de.langX));
			row++;

			ft.setHTML(row, 0, highlight("legalAuthenticator", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.legalAuth, de.legalAuthX));
			row++;

			ft.setHTML(row, 0, highlight("serviceStartTime", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.serviceStartTime, de.serviceStartTimeX));
			row++;

			ft.setHTML(row, 0, highlight("serviceStopTime", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.serviceStopTime, de.serviceStopTimeX));
			row++;

			ft.setHTML(row, 0, highlight("creationTime", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.creationTime, de.creationTimeX));
			row++;

			ft.setHTML(row, 0, highlight("sourcePatientId", diffs));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.sourcePatientId, de.sourcePatientIdX));
			row++;

			row = displayDetail(ft, row, false, highlight("sourcePatientInfo", diffs), de.sourcePatientInfo, de.sourcePatientInfoX);

			// don't know how to handle diffs yet on extra metadata
			b = false;
			row = displayDetail(ft, row, b, de.extra, de.extraX);

			row = displayDetail(ft, row, false, highlight("classCode", diffs), de.classCode, de.classCodeX);

			row = displayDetail(ft, row, false, highlight("confCodes", diffs), de.confCodes, de.confCodesX);

			row = displayDetail(ft, row, false, highlight("eventCodeList", diffs), de.eventCodeList, de.eventCodeListX);

			row = displayDetail(ft, row, false, highlight("formatCode", diffs), de.formatCode, de.formatCodeX);

			row = displayDetail(ft, row, false, highlight("healthcareFacilityType", diffs), de.hcftc, de.hcftcX);

			row = displayDetail(ft, row, false, highlight("practiceSetting", diffs), de.pracSetCode, de.pracSetCodeX);

			row = displayDetail(ft, row, false, highlight("typeCode", diffs), de.typeCode, de.typeCodeX);

			row = displayDetail(ft, row, diffs, de.authors, de.authorsX);

		} finally {
			detailPanel.add(ft);
		}

	}

	void displayDetail(Folder fol, Folder diff) {
//		detailPanel.add(HyperlinkFactory.addHTML("<h4>Folder</h4>"));
        addTitle(HyperlinkFactory.addHTML("<h4>Folder</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;
		boolean b;
		
		b = diff.title != null;
		ft.setHTML(row, 0, bold("title",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.title, fol.titleX));
		row++;

		b = diff.comments != null;
		ft.setHTML(row, 0, bold("comments",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.comments, fol.commentsX));
		row++;

		b = diff.id != null;
		ft.setHTML(row, 0, bold("id",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.id, fol.idX));
		row++;

		b = diff.lid != null;
		ft.setHTML(row, 0, bold("lid",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.lid, fol.lidX));
		row++;

		b = diff.version != null;
		ft.setHTML(row, 0, bold("version",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.version, fol.versionX));
		row++;

		b = diff.uniqueId != null;
		ft.setHTML(row, 0, bold("uniqueId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.uniqueId, fol.uniqueIdX));
		row++;

		b = diff.patientId != null;
		ft.setHTML(row, 0, bold("patientId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.patientId, fol.patientIdX));
		row++;

		b = diff.status != null;
		ft.setHTML(row, 0, bold("availabilityStatus",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.status, fol.statusX));
		row++;

		b = diff.home != null;
		ft.setHTML(row, 0, bold("home",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.home, fol.homeX));
		row++;

		b = diff.lastUpdateTime != null;
		ft.setHTML(row, 0, bold("lastUpdateTime",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, fol.lastUpdateTime, fol.lastUpdateTimeX));
		row++;
		
		// don't know how to handle diffs yet on extra metadata
		b = false;
		row = displayDetail(ft, row, b, fol.extra, fol.extraX);


		b = diff.codeList != null;
		row = displayDetail(ft, row, b, "codeList", fol.codeList, fol.codeListX);

		detailPanel.add(ft);


	}

	void displayDetail(Association assoc) {
//		detailPanel.add(HyperlinkFactory.addHTML("<h4>Association</h4>"));
        addTitle(HyperlinkFactory.addHTML("<h4>Association</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;

		ft.setText(row, 0, "id");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.id, assoc.idX));
		row++;

		ft.setText(row, 0, "lid");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.lid, assoc.idX));
		row++;

		ft.setText(row, 0, "version");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.version, assoc.versionX));
		row++;

		ft.setText(row, 0, "home");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.home, assoc.homeX));
		row++;

		ft.setText(row, 0, "type");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.type, assoc.typeX));
		row++;

		ft.setText(row, 0, "source");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.source, assoc.sourceX));
		row++;

		ft.setText(row, 0, "target");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.target, assoc.targetX));
		row++;
		
		ft.setText(row, 0, "availabilityStatus");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.status, assoc.statusX));
		row++;

		ft.setText(row, 0, "previousVersion");
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.previousVersion, assoc.previousVersionX));
		row++;



		if (assoc.ssStatus != null && !assoc.ssStatus.equals("")) {
			ft.setText(row, 0, "SubmissionSetStatus");
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, assoc.ssStatus, assoc.ssStatusX));
			row++;
		}
		
		row = displayDetail(ft, row, false, "documentation", assoc.assocDoc, assoc.assocDocX);

		// don't know how to handle diffs yet on extra metadata
		row = displayDetail(ft, row, false, assoc.extra, assoc.extraX);

		
		detailPanel.add(ft);

	}

	void displayDetail(ObjectRef o) {
//		detailPanel.add(HyperlinkFactory.addHTML("<h4>ObjectRef</h4>"));
		addTitle(HyperlinkFactory.addHTML("<h4>ObjectRef</h4>"));

		detailPanel.add(HyperlinkFactory.addHTML("id = " + o.id));
		detailPanel.add(HyperlinkFactory.addHTML("home = " + o.home));

	}

}
