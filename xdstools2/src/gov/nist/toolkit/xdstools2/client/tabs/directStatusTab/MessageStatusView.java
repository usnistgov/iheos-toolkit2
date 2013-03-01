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
	static final int MSGIDCOL = 0;
	static final int TIMESENTCOL = 1;
	
	static final int MDNIDCOL = 2;
	static final int EXPIRATIONCOL = 3;
	static final int RECEIVEDCOL = 4;
	static final int STATUSCOL = 5;
		
	@Override
	public void build(List<MessageLog> statuss) {
		if (grid == null) {
			topPanel.add(HtmlMarkup.text(HtmlMarkup.h3("Direct Message Status")));
		} else {
			topPanel.remove(grid);
		}
		
		grid = new FlexTable();
		topPanel.add(grid);
		grid.getFlexCellFormatter().setColSpan(0, 0, 2);
		grid.getFlexCellFormatter().setColSpan(0, 1, 4);
		grid.setBorderWidth(3);
		grid.setCellSpacing(0);
		grid.setHTML(0, 0, HtmlMarkup.bold("Direct Message"));
		grid.setHTML(0, 1, HtmlMarkup.bold("MDN"));
		grid.setHTML(1,MSGIDCOL, HtmlMarkup.bold("Msg ID"));
		grid.setHTML(1,TIMESENTCOL, HtmlMarkup.bold("Time Sent"));
		
		grid.setHTML(1,MDNIDCOL, HtmlMarkup.bold("Msg ID"));
		grid.setHTML(1,EXPIRATIONCOL, HtmlMarkup.bold("Expiration"));
		grid.setHTML(1,RECEIVEDCOL, HtmlMarkup.bold("Received"));
		grid.setHTML(1,STATUSCOL, HtmlMarkup.bold("Status"));
	}


	
	@Override
	public void addRow(MessageLog status) {
		grid.setText(row, MSGIDCOL, status.messageId);
//		grid.setText(row, TIMESENTCOL, status.msg_time_sent);
		
		grid.setText(row, MDNIDCOL, status.label);
		grid.setText(row, EXPIRATIONCOL, status.expirationDate.toString());
		grid.setText(row, RECEIVEDCOL, status.mdnReceivedDate.toString());
		grid.setHTML(row, STATUSCOL, status.status);
		
		row++;
	}	

}
