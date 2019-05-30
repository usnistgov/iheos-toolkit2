package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

/**
 * Metadata update selector
 */
class MuClickHandler implements ClickHandler {
	DocumentEntry de;
	MetadataInspectorTab it;
	QueryOrigin queryOrigin;

	MuClickHandler(MetadataInspectorTab it, DocumentEntry de, QueryOrigin queryOrigin) {
		this.de = de;
		this.it = it;
		this.queryOrigin = queryOrigin;
	}

	public void onClick(ClickEvent event) {
		// Symbolic Id is indicative of submission data, not as it was stored by the target registry. This is the case where Inspector was launched from a Submission tool.
		// Warn user about symbolic id
		final String realObjectPrefixId = "urn:uuid:";
		boolean isSymbolicId = de.id != null && !de.id.startsWith(realObjectPrefixId);
		if (isSymbolicId) {
			SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
			safeHtmlBuilder.appendHtmlConstant("<img src=\"icons/about-16.png\" title=\"Information\" />");
			safeHtmlBuilder.appendHtmlConstant("&nbsp;Symbolic Id detected");

			VerticalPanel body = new VerticalPanel();
			body.add(new HTML("<p>Id does not start with \"" + realObjectPrefixId + "\". Symbolic Id is indicative of submission data not as it was stored by the target registry.</p>"
					+"<p>Do you wish to proceed to Metadata Update?<br/></p>"));
			Button actionBtn = new Button("Ok");
			actionBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent clickEvent) {
				    launchEdit();
				}
			});
			new PopupMessage(safeHtmlBuilder.toSafeHtml(), body, actionBtn);
		} else {
			launchEdit();
		}
	}

	private void launchEdit() {
		try {
			EditDisplay editDisplay = new EditDisplay(it, de, queryOrigin);
			editDisplay.editDetail();
		} catch (Exception ex) {
			new PopupMessage(ex.toString());
		}
	}

}

