package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RenameSimFileDialogBox extends DialogBox {
	final protected ToolkitServiceAsync toolkitService = GWT
	.create(ToolkitService.class);
	TextBox newNameBox;
	String oldSimFileSpec;
	AsyncCallback afterRename;

	
	public RenameSimFileDialogBox(VerticalPanel parent, String oldSimFileSpec, AsyncCallback reloadSimMessages) {
		this.oldSimFileSpec = oldSimFileSpec;
		this.afterRename = reloadSimMessages;
		
		setPopupPosition(200, 200);
		
		HorizontalPanel topPanel = new HorizontalPanel();
		
		Label instructions = new Label("New Name: ");
		topPanel.add(instructions);

		newNameBox = new TextBox();
		newNameBox.addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == '\r')
					new OkButtonClicked().onClick(null);
			}
			
		});
		topPanel.add(newNameBox);
		
		Button okButton = new Button("Ok");
		okButton.addClickHandler(new OkButtonClicked());
						
		topPanel.add(okButton);
		
		setWidget(topPanel);
		
		parent.add(this);
		
		show();
		
		newNameBox.setFocus(true);

	}

	class OkButtonClicked implements ClickHandler {

		public void onClick(ClickEvent unused) {
			String newname = newNameBox.getText();
			RenameSimFileDialogBox.this.hide();
			
			toolkitService.renameSimFile(oldSimFileSpec, newname, afterRename);
		}
		
	}
}
