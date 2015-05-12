package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataDiff;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DiffDisplay {
	MetadataInspectorTab tab;
	MetadataCollection mc;
	
	List<RadioButton> ssFromRadioButtons = new ArrayList<RadioButton>();
	List<RadioButton> deFromRadioButtons = new ArrayList<RadioButton>();
	List<RadioButton> folFromRadioButtons = new ArrayList<RadioButton>();
	List<RadioButton> aFromRadioButtons = new ArrayList<RadioButton>();
	
	List<RadioButton> ssToRadioButtons = new ArrayList<RadioButton>();
	List<RadioButton> deToRadioButtons = new ArrayList<RadioButton>();
	List<RadioButton> folToRadioButtons = new ArrayList<RadioButton>();
	List<RadioButton> aToRadioButtons = new ArrayList<RadioButton>();
	
	List<RadioButton> getAllFromDiffTypeButtons() {
		List<RadioButton> all = new ArrayList<RadioButton>();
		
		all.addAll(aFromRadioButtons);
		all.addAll(ssFromRadioButtons);
		all.addAll(folFromRadioButtons);
		all.addAll(deFromRadioButtons);
		
		return all;
	}

	List<RadioButton> getAllToDiffTypeButtons() {
		List<RadioButton> all = new ArrayList<RadioButton>();
		
		all.addAll(aToRadioButtons);
		all.addAll(ssToRadioButtons);
		all.addAll(folToRadioButtons);
		all.addAll(deToRadioButtons);
		
		return all;
	}
	
	String fromColumn = "fromColumn";
	String toColumn = "toColumn";
	
	void showDiff(VerticalPanel historyPanel) {
		if (mc == null)
			return;

		historyPanel.clear();
		
		ssFromRadioButtons.clear();
		deFromRadioButtons.clear();
		folFromRadioButtons.clear();
		aFromRadioButtons.clear();

		ssToRadioButtons.clear();
		deToRadioButtons.clear();
		folToRadioButtons.clear();
		aToRadioButtons.clear();

		HTML title = new HTML();
		title.setHTML("<h3>Diff</h3>");
		historyPanel.add(title);

		tab.addHistoryContentsSelector(historyPanel);

		tab.selectHistory.setValue(false);
		tab.selectContents.setValue(false);
		tab.selectDiff.setValue(true);
		
		FlexTable dispTab = new FlexTable();
		
		int row = 0;
		
		dispTab.setHTML(row, 0, bold("Content"));
		dispTab.setHTML(row, 1, bold("From"));
		dispTab.setHTML(row, 2, bold("To"));
		row++;
		
		RadioButton rb;
		for (SubmissionSet ss : mc.submissionSets) {
			dispTab.setText(row, 0, ss.displayName());
			
			rb = new RadioButton(fromColumn, "");
			ssFromRadioButtons.add(rb);
			dispTab.setWidget(row, 1, rb);
			rb.addClickHandler(new DiffFromChoiceClickHandler(tab, ss, ssToRadioButtons));
			
			rb = new RadioButton(toColumn, "");
			ssToRadioButtons.add(rb);
			dispTab.setWidget(row, 2, rb);
			rb.addClickHandler(new DiffToChoiceClickHandler(tab, ss));
			
			row++;
		}
		
		for (DocumentEntry de : mc.docEntries) {
			dispTab.setText(row, 0, de.displayName());
			
			rb = new RadioButton(fromColumn, "");
			deFromRadioButtons.add(rb);
			dispTab.setWidget(row, 1, rb);
			rb.addClickHandler(new DiffFromChoiceClickHandler(tab, de, deToRadioButtons));
			
			rb = new RadioButton(toColumn, "");
			deToRadioButtons.add(rb);
			dispTab.setWidget(row, 2, rb);
			rb.addClickHandler(new DiffToChoiceClickHandler(tab, de));
			
			row++;
		}
		
		for (Folder fol : mc.folders) {
			dispTab.setText(row, 0, fol.displayName());
			
			rb = new RadioButton(fromColumn, "");
			folFromRadioButtons.add(rb);
			dispTab.setWidget(row, 1, rb);
			rb.addClickHandler(new DiffFromChoiceClickHandler(tab, fol, folToRadioButtons));
			
			rb = new RadioButton(toColumn, "");
			folFromRadioButtons.add(rb);
			dispTab.setWidget(row, 2, rb);
			rb.addClickHandler(new DiffToChoiceClickHandler(tab, fol));
			
			row++;
		}
		
//		for (Association a : mc.assocs) {
//			dispTab.setText(row, 0, a.displayName());
//			
//			rb = new RadioButton(fromColumn, "");
//			aFromRadioButtons.add(rb);
//			dispTab.setWidget(row, 1, rb);
//			rb.addClickHandler(new DiffFromChoiceClickHandler(tab, a, aToRadioButtons));
//			
//			rb = new RadioButton(toColumn, "");
//			aToRadioButtons.add(rb);
//			dispTab.setWidget(row, 2, rb);
//			rb.addClickHandler(new DiffToChoiceClickHandler(tab, a));
//			
//			row++;
//		}
		
		
		historyPanel.add(dispTab);
		
	}
	
	MetadataObject fromSelection;
	MetadataObject toSelection;
	
	class DiffFromChoiceClickHandler implements ClickHandler {
		List<RadioButton> sameTypeToButtons;  
		MetadataInspectorTab tab;
		MetadataObject mo;
		
		DiffFromChoiceClickHandler(MetadataInspectorTab tab, MetadataObject mo, List<RadioButton> sameTypeToButtons) {
			this.sameTypeToButtons = sameTypeToButtons;
			this.tab = tab;
			this.mo = mo;
		}
		
		public void onClick(ClickEvent event) {
			List<RadioButton> allTo = getAllToDiffTypeButtons();
			fromSelection = mo;

			MetadataObject diff = MetadataDiff.nullObject(fromSelection);
			if (toSelection != null) {
				diff = MetadataDiff.diff(fromSelection, toSelection);
				new DetailDisplay(tab, tab.structPanel).displayDetail(toSelection, diff);
			}

			new DetailDisplay(tab, tab.detailPanel).displayDetail(mo, diff);
			
			for (RadioButton b : allTo) {
				if (sameTypeToButtons.contains(b))
					b.setEnabled(true);
				else
					b.setEnabled(false);
			}
			
			for (RadioButton b : allTo) {
				if (b.getValue() && !b.isEnabled())
					b.setValue(false);
			}
			
		}
		
	}

	class DiffToChoiceClickHandler implements ClickHandler {
		MetadataInspectorTab tab;
		MetadataObject mo;

		DiffToChoiceClickHandler(MetadataInspectorTab tab, MetadataObject mo) {
			this.tab = tab;
			this.mo = mo;
		}
		
		public void onClick(ClickEvent event) {
			toSelection = mo;
			MetadataObject diff = MetadataDiff.diff(fromSelection, toSelection);
			new DetailDisplay(tab, tab.detailPanel).displayDetail(fromSelection, diff);
			new DetailDisplay(tab, tab.structPanel).displayDetail(toSelection, diff);
		}
		
	}
	
	public DiffDisplay(MetadataInspectorTab tab, MetadataCollection mc) {
		this.tab = tab;
		this.mc = mc;
	}
	
	String bold(String msg) {
		return "<b>" + msg + "</b>";
	}
}
