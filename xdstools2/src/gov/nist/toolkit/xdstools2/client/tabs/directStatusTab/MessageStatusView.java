package gov.nist.toolkit.xdstools2.client.tabs.directStatusTab;

import gov.nist.direct.client.MessageLog;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.xdstools2.client.SmtpMessageStatus;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageStatusView implements DirectStatusTab.IMessageStatusView {
	VerticalPanel topPanel;
	FlexTable grid = null;
	DirectStatusTab dtab;
	
	public MessageStatusView(VerticalPanel topPanel, DirectStatusTab dtab) {
		this.topPanel = topPanel;
		this.dtab = dtab;
	}	
	
	int row = 2;
	static final int TRANSACTIONCOL = 0;
	
	static final int DIRECTIDCOL = 1;
	static final int DIRECTSENTDATECOL = 2;
	
	static final int MDNIDCOL = 3;
	static final int MDNRECEIVEDDATECOL = 4;
	static final int MDNEXPIRATIONDATECOL = 5;

	static final int STATUSCOL = 6;
		
	@Override
	public void build(List<MessageLog> statuss) {
		if (grid == null) {
			topPanel.add(HtmlMarkup.text(HtmlMarkup.h3("Direct Message Status")));
		} else {
			topPanel.remove(grid);
		}
		
		grid = new FlexTable();
		topPanel.add(grid);
		grid.getFlexCellFormatter().setColSpan(0, 0, 3);
		grid.getFlexCellFormatter().setColSpan(0, 1, 3);
		grid.setBorderWidth(3);
		grid.setCellSpacing(0);
		grid.setCellPadding(5);
		grid.setHTML(0, 0, HtmlMarkup.bold("Direct Message"));
		grid.setHTML(0, 1, HtmlMarkup.bold("MDN"));
		grid.setHTML(1,TRANSACTIONCOL, HtmlMarkup.bold("Transaction type"));
		grid.setHTML(1,DIRECTIDCOL, HtmlMarkup.bold("Message ID"));
		grid.setHTML(1,DIRECTSENTDATECOL, HtmlMarkup.bold("Time Sent"));
		
		grid.setHTML(1,MDNIDCOL, HtmlMarkup.bold("Message ID"));
		grid.setHTML(1,MDNRECEIVEDDATECOL, HtmlMarkup.bold("Time Received"));
		grid.setHTML(1,MDNEXPIRATIONDATECOL, HtmlMarkup.bold("Expiration time"));
		
		grid.setHTML(0, 2, HtmlMarkup.bold("Status"));
		grid.setHTML(1,STATUSCOL, HtmlMarkup.bold(" "));
	}


	
	@Override
	public void addRow(MessageLog status) {
		grid.setText(row, TRANSACTIONCOL, status.transactionType);
		
		grid.setText(row, DIRECTIDCOL, status.messageId);
		grid.setText(row, DIRECTSENTDATECOL, status.directSendDate);
		
		grid.setText(row, MDNIDCOL, status.mdnMessageID);
		grid.setText(row, MDNRECEIVEDDATECOL, status.mdnReceivedDate);
		grid.setText(row, MDNEXPIRATIONDATECOL, status.expirationDate);

		
		grid.setText(row, STATUSCOL, status.status);
		
		row++;
	}	

}
