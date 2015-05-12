package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Author;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DetailDisplay {
	VerticalPanel detailPanel;
	MetadataCollection metadataCollection;
	MetadataInspectorTab it;

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
		if (mo instanceof DocumentEntry) 
			displayDetail((DocumentEntry) mo, (DocumentEntry)diff);
		if (mo instanceof Folder) 
			displayDetail((Folder) mo, (Folder) diff);
		if (mo instanceof Association) 
			displayDetail((Association) mo);
		if (mo instanceof ObjectRef) 
			displayDetail((ObjectRef) mo);
	}

	int displayDetail(FlexTable ft, int row, boolean bold, String label, List<String> values, String xml) {
		int startRow = row;
		for (String value : values) {
			if (row == startRow) {
				ft.setHTML(row, 0, bold(label, bold));
			}
			if (xml == null || xml.equals(""))
				ft.setHTML(row, 1, value.replaceAll(" ", "&nbsp;"));
			else
				ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, value, xml));
			row++;
		}
		return row;
	}

	int displayDetail(FlexTable ft, int row, boolean bold, Map<String, List<String>> values, Map<String, String> xmls) {
		int startRow = row;
		for (String name : values.keySet()) {
			for (String value : values.get(name)) {
				String xml = xmls.get(name);
				if (row == startRow) {
					ft.setHTML(row, 0, bold(name, bold));
				}
				if (xml == null || xml.equals(""))
					ft.setHTML(row, 1, value.replaceAll(" ", "&nbsp;"));
				else
					ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, value, xml));
				row++;
			}
		}
		return row;
	}

	int displayDetail(FlexTable ft, int row, boolean bold, String label,List<String> values, List<String> xml) {
		int startRow = row;
		int rowI = 0;
		for (String value : values) {
			if (row == startRow) {
				ft.setHTML(row, 0, bold(label, bold));
			}
			//			ft.setHTML(row, 1, value.replaceAll(" ", "&nbsp;"));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, value, xml.get(rowI)));
			row++;
			rowI++;
		}
		return row;
	}

	int displayDetail(FlexTable ft, int row, boolean bold, List<Author> authors, String xml) {
		for (Author author : authors) {
			ft.setHTML(row, 0, bold("author", bold));
			ft.setText(row, 1, author.person);
			row++;

			row = displayDetail(ft, row, bold, "institutions", author.institutions, xml);
			row = displayDetail(ft, row, bold, "roles", author.roles, xml);
			row = displayDetail(ft, row, bold, "specialties", author.specialties, xml);
		}


		return row;
	}

	int displayDetail(FlexTable ft, int row, boolean bold, List<Author> authors, List<String> xml) {
		int xmlI = 0;
		for (Author author : authors) {
			ft.setHTML(row, 0, bold("author", bold));
			ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, author.person, xml.get(xmlI)));
			//			ft.setText(row, 1, author.person);
			row++;
			xmlI++;

			row = displayDetail(ft, row, bold, "institutions", author.institutions, "");
			row = displayDetail(ft, row, bold, "roles", author.roles, "");
			row = displayDetail(ft, row, bold, "specialties", author.specialties, "");
		}


		return row;
	}
	
	String bold(String msg, boolean condition) {
		if (condition)
			return "<b>" + msg + "</b>";
		return msg;
	}


	void displayDetail(SubmissionSet ss, SubmissionSet diff) {
		detailPanel.add(HyperlinkFactory.addHTML("<h4>Submission Set</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;
		boolean b;
		
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
		row = displayDetail(ft, row,  b, "indendedRecipient", ss.intendedRecipients, ss.intendedRecipientsX);

		b = diff.authors != null;
		row = displayDetail(ft, row, b, ss.authors, ss.authorsX);

		detailPanel.add(ft);
	}

	void displayDetail(DocumentEntry de, DocumentEntry diff) {
		detailPanel.add(HyperlinkFactory.addHTML("<h4>Document Entry</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;
		boolean b;
		
		b = diff.title != null;
		ft.setHTML(row, 0, bold("title",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.title, de.titleX));
		row++;

		b = diff.comments != null;
		ft.setHTML(row, 0, bold("comments",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.comments, de.commentsX));
		row++;

		b = diff.id != null;
		ft.setHTML(row, 0, bold("id",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.id, de.idX));
		row++;

		b = diff.lid != null;
		ft.setHTML(row, 0, bold("lid",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.lid, de.lidX));
		row++;

		b = diff.version != null;
		ft.setHTML(row, 0, bold("version",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.version, de.versionX));
		row++;

		b = diff.uniqueId != null;
		ft.setHTML(row, 0, bold("uniqueId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.uniqueId, de.uniqueIdX));
		row++;

		b = diff.patientId != null;
		ft.setHTML(row, 0, bold("patientId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.patientId, de.patientIdX));
		row++;

		b = diff.status != null;
		ft.setHTML(row, 0, bold("availabilityStatus",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.status, de.statusX));
		row++;

		b = diff.home != null;
		ft.setHTML(row, 0, bold("homeCommunityId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.home, de.homeX));
		row++;

		b = diff.mimeType != null;
		ft.setHTML(row, 0, bold("mimeType",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.mimeType, de.mimeTypeX));
		row++;

		b = diff.hash != null;
		ft.setHTML(row, 0, bold("hash",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.hash, de.hashX));
		row++;

		b = diff.size != null;
		ft.setHTML(row, 0, bold("size",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.size, de.sizeX));
		row++;

		b = diff.repositoryUniqueId != null;
		ft.setHTML(row, 0, bold("repositoryUniqueId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.repositoryUniqueId, de.repositoryUniqueIdX));
		row++;

		b = diff.lang != null;
		ft.setHTML(row, 0, bold("lang",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.lang, de.langX));
		row++;

		b = diff.legalAuth != null;
		ft.setHTML(row, 0, bold("legalAuthenticator",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.legalAuth, de.legalAuthX));
		row++;

		b = diff.serviceStartTime != null;
		ft.setHTML(row, 0, bold("serviceStartTime",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.serviceStartTime, de.serviceStartTimeX));
		row++;

		b = diff.serviceStopTime != null;
		ft.setHTML(row, 0, bold("serviceStopTime",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.serviceStopTime, de.serviceStopTimeX));
		row++;

		b = diff.creationTime != null;
		ft.setHTML(row, 0, bold("creationTime",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.creationTime, de.creationTimeX));
		row++;

		b = diff.sourcePatientId != null;
		ft.setHTML(row, 0, bold("sourcePatientId",b));
		ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.sourcePatientId, de.sourcePatientIdX));
		row++;

		b = diff.sourcePatientInfo != null;
		row = displayDetail(ft, row, b, "sourcePatientInfo", de.sourcePatientInfo, de.sourcePatientInfoX);
		
		// don't know how to handle diffs yet on extra metadata
		b = false;
		row = displayDetail(ft, row, b, de.extra, de.extraX);

		b = diff.classCode != null;
		row = displayDetail(ft, row, b, "classCode", de.classCode, de.classCodeX);
		
		b = diff.confCodes != null;
		row = displayDetail(ft, row, b, "confCodes", de.confCodes, de.confCodesX);
		
		b = diff.eventCodeList != null;
		row = displayDetail(ft, row, b, "eventCodeList", de.eventCodeList, de.eventCodeListX);
		
		b = diff.formatCode != null;
		row = displayDetail(ft, row, b, "formatCode", de.formatCode, de.formatCodeX);
		
		b = diff.hcftc != null;
		row = displayDetail(ft, row, b, "healthcareFacilityType", de.hcftc, de.hcftcX);
		
		b = diff.pracSetCode != null;
		row = displayDetail(ft, row, b, "practiceSetting", de.pracSetCode, de.pracSetCodeX);
		
		b = diff.typeCode != null;
		row = displayDetail(ft, row, b, "typeCode", de.typeCode, de.typeCodeX);

		b = diff.authors != null;
		row = displayDetail(ft, row, b, de.authors, de.authorsX);

		detailPanel.add(ft);

	}

	void displayDetail(Folder fol, Folder diff) {
		detailPanel.add(HyperlinkFactory.addHTML("<h4>Folder</h4>"));
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
		detailPanel.add(HyperlinkFactory.addHTML("<h4>Association</h4>"));
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
		detailPanel.add(HyperlinkFactory.addHTML("<h4>ObjectRef</h4>"));

		detailPanel.add(HyperlinkFactory.addHTML("id = " + o.id));
		detailPanel.add(HyperlinkFactory.addHTML("home = " + o.home));

	}


}
