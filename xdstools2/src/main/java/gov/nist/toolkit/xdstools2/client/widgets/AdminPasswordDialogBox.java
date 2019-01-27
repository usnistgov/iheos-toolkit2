package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;

public class AdminPasswordDialogBox extends DialogBox {
	PasswordTextBox passBox;
	
	public AdminPasswordDialogBox(FlowPanel parent) {
		
		setPopupPosition(200, 200);
		
		HorizontalPanel topPanel = new HorizontalPanel();
		
		Label instructions = new Label("Admin Password: ");
		topPanel.add(instructions);
		
		passBox = new PasswordTextBox();
		passBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent keyUpEvent) {
				if (KeyCodes.KEY_ENTER == keyUpEvent.getNativeKeyCode()) {
					keyUpEvent.getNativeEvent().preventDefault();
					keyUpEvent.getNativeEvent().stopPropagation();
					passBox.cancelKey();

					new OkButtonClicked().onClick(null);
				}
			}
		});

		topPanel.add(passBox);
		
		Button okButton = new Button("Ok");
		okButton.addClickHandler(new OkButtonClicked());
						
		topPanel.add(okButton);
		
		setWidget(topPanel);
		
		parent.add(this);
		
		show();
		
		passBox.setFocus(true);
	}
	
	class OkButtonClicked implements ClickHandler {

		public void onClick(ClickEvent unused) {
			String password = passBox.getText();
			AdminPasswordDialogBox.this.hide();
			PasswordManagement.comparePassword(password);
			
			if (PasswordManagement.isSignedIn) {
				PasswordManagement.callSignInCallbacks();
			}
			else {
				new PopupMessage("Sorry");
				PasswordManagement.clearSignInCallbacks();
			}
		}
		
	}
	
}
