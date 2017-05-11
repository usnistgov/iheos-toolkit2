package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.command.command.RenameSimFileCommand;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.RenameSimFileRequest;


public class RenameSimFileDialogBox extends DialogBox {
	TextBox newNameBox;
	String oldSimFileSpec;
	AsyncCallback afterRename;

	
	public RenameSimFileDialogBox(DockLayoutPanel parent, String oldSimFileSpec, AsyncCallback reloadSimMessages) {
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

			new RenameSimFileCommand(){
				@Override
				public void onComplete(Void result) {
					afterRename.onSuccess(result);
				}
			}.run(new RenameSimFileRequest(FrameworkInitialization.data().getCommandContext(),oldSimFileSpec,newname));
		}
		
	}
}
