package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.registrymetadata.client.Author;
import gov.nist.toolkit.registrymetadata.client.Difference;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CommonDisplay {
    VerticalPanel detailPanel;
    MetadataCollection metadataCollection;
    protected MetadataInspectorTab it;

    protected FlowPanel createTitle(HTML title) {
        FlowPanel flowPanel = new FlowPanel();
        title.addStyleName("left");
        flowPanel.add(title);
        if (it.dataNotification != null) {
            if (it.dataNotification.inCompare()) {
                HTML closeX = new HTML("X");
                closeX.setTitle("Close");
                closeX.addStyleName("requiredFieldLabel");
                closeX.addStyleName("outsetBorder");
                closeX.addStyleName("right");
                closeX.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        it.dataNotification.onCloseOffDetail(it.currentSelectedTreeItem);
                    }
                });
                flowPanel.add(closeX);
            }
        }
        return flowPanel;
    }

    int displayDetail(FlexTable ft, int row, boolean bold, String label, List<String> values, String xml) {
        int startRow = row;
        if (values == null)
            values = new ArrayList<>();
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
        if (values != null) {
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
        }
        return row;
    }

    int displayDetail(FlexTable ft, int row, boolean bold, String label, List<String> values, List<String> xml) {
        if (values == null)
            values = new ArrayList<>();
        if (xml == null)
            xml = new ArrayList<>();
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
            row = displayDetail(ft, row, bold, "telecom", author.telecom, xml);
        }


        return row;
    }

    int displayDetail(FlexTable ft, int row, boolean bold, List<Author> authors, List<String> xml) {
        if (authors == null)
            authors = new ArrayList<>();
        if (xml == null)
            xml = new ArrayList<>();
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
            row = displayDetail(ft, row, bold, "telecom", author.telecom, "");
        }

        return row;
    }

    int displayDetail(FlexTable ft, int row, List<Difference> diffs, List<Author> authors, List<String> xml) {
        if (authors == null)
            authors = new ArrayList<>();
        if (xml == null)
            xml = new ArrayList<>();
        int xmlI = 0;
        if (diffs.contains(new Difference("author"))) {
            ft.setHTML(row, 0, highlight("author(s)"));   // this is only a hint/general indicator until we compare the full details of Author
            row++;
        }
        for (Author author : authors) {
            ft.setHTML(row, 0, "author");
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, author.person, xml.get(xmlI)));
            //			ft.setText(row, 1, author.person);
            row++;
            xmlI++;

            row = displayDetail(ft, row, false, "institutions", author.institutions, "");
            row = displayDetail(ft, row, false, "roles", author.roles, "");
            row = displayDetail(ft, row, false, "specialties", author.specialties, "");
            row = displayDetail(ft, row, false, "telecom", author.telecom, "");
        }

        return row;
    }

    protected String bold(String msg, boolean condition) {
        if (condition)
            return "<b>" + msg + "</b>";
        return msg;
    }

    void displayText(String title, String html) {
        detailPanel.add(createTitle(HyperlinkFactory.addHTML("<h4>" + title + "</h4>")));
        detailPanel.add(new HTML(html));
    }

    String highlight(String msg, List<Difference> diffs) {
        if (diffs!=null && diffs.contains(new Difference(msg)))
            return highlight(msg);
        return msg;
    }

    String highlight(String txt) {
        return "<span class='test-row-yellow'>" + txt + "</span>";
    }
}
