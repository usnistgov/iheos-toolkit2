package gov.nist.toolkit.xdstools2.client;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;

public class PopupMessage  extends DialogBox {
	
	public PopupMessage(String caption) {
		// Set the dialog box's caption.
		setText(caption);

		frameMessage(null);
	}

	private Widget getOkBtn() {
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeFromParent();
			}
		});
		ok.setFocus(true);
		return ok;
	}

	public PopupMessage(SafeHtml caption, Widget content) {
		setHTML(caption);
		frameMessage(content);
	}

	private void frameMessage(Widget content) {
		// DialogBox is a SimplePanel, so you have to set its widget property to
		// whatever you want its contents to be.

		Widget ok = getOkBtn();

		VerticalPanel verticalPanel = new VerticalPanel();
		if (content!=null)
			verticalPanel.add(content);
		verticalPanel.add(ok);

		setWidget(verticalPanel);
		center();
		setModal(true);

		show();
	}

	public PopupMessage(AssertionResults result) {
		VerticalPanel panel = new VerticalPanel();
		setWidget(panel);
		for (AssertionResult ar : result.assertions) {
			if (ar.status) {
				panel.add(addHTML(ar.assertion));
			} else {
				panel.add(addHTML("<font color=\"#FF0000\">" + ar.assertion + "</font>"));
			}
		}
		// DialogBox is a SimplePanel, so you have to set its widget property to
		// whatever you want its contents to be.
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeFromParent();
			}
		});
		panel.add(ok);
		center();
		setModal(true);
		ok.setFocus(true);
		show();

	}


	protected HTML addHTML(String html) {		
		HTML msgBox = new HTML();
		msgBox.setHTML(html);
		return msgBox;		
	}
}

