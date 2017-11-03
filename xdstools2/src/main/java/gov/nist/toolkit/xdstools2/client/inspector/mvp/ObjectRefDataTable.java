package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;

import java.util.Comparator;
import java.util.List;

abstract class ObjectRefDataTable extends DataTable<ObjectRef> implements IsWidget {
    private FlowPanel mainPanel = new FlowPanel();

    private static final ProvidesKey<ObjectRef> KEY_PROVIDER = new ProvidesKey<ObjectRef>() {
        @Override
        public Object getKey(ObjectRef item) {
            return item == null ? null : item.getId();
        }
    };

    public ObjectRefDataTable(int pageSize) {
        super(pageSize, new ObjectRef());
        mainPanel.add(super.asWidget());
    }

    @Override
    void addTableColumns() {

        TextColumn<ObjectRef> idColumn = new TextColumn<ObjectRef>() {
            @Override
            public String getValue(ObjectRef objectRef) {
                return objectRef.getId().toString();
            }
        };
        idColumn.setSortable(true);
        columnSortHandler.setComparator(idColumn,
                new Comparator<ObjectRef>() {
                    public int compare(ObjectRef o1, ObjectRef o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the name columns.
                        if (o1 != null && o1.getId()!=null) {
                            return (o2 != null && o2.getId()!=null) ? o1.getId().toString().compareTo(o2.getId().toString()) : 1;
                        }
                        return -1;
                    }
                });

        TextColumn<ObjectRef> homeColumn = new TextColumn<ObjectRef>() {
            @Override
            public String getValue(ObjectRef objectRef) {
                return objectRef.home;
            }
        };
        homeColumn.setSortable(true);
        columnSortHandler.setComparator(homeColumn,
                new Comparator<ObjectRef>() {
                    public int compare(ObjectRef o1, ObjectRef o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the name columns.
                        if (o1 != null && o1.home!=null) {
                            return (o2 != null && o2.home!=null) ? o1.home.toString().compareTo(o2.home.toString()) : 1;
                        }
                        return -1;
                    }
                });

        dataTable.addColumn(idColumn, "Id");
        dataTable.addColumn(homeColumn,"HomeId");

    }



    @Override
    void addActionBtnTableColumns() {
        // Only the first 10 can be queried due to the max params limit in stored query
        Column<ObjectRef, ObjectRef> getDocActionCol =
                new Column<ObjectRef, ObjectRef>(new ActionCell<ObjectRef>("", new ActionCell.Delegate<ObjectRef>() {

                    @Override
                    public void execute(ObjectRef objectRef) {
                        // do action
                        doGetDocuments(actionBtnProvider.getList());
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, ObjectRef object, SafeHtmlBuilder sb) {
//                        super.render(context, object, sb);
                        sb.append(getImgHtml("icons2/ic_forward_black_24dp_1x.png", "GetDocuments", true));
                    }

                    @Override
                    public ObjectRef getValue(ObjectRef item) {
                        return item;
                    }
                };

        actionBtnTable.addColumn(getDocActionCol, "Action(s)");

    }

    @Override
    ProvidesKey<ObjectRef> getKeyProvider() {
        return KEY_PROVIDER;
    }

    abstract void doGetDocuments(List<ObjectRef> objectRefs);

    void setData(List<ObjectRef> objectRefList) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(objectRefList);
    }


    @Override
    public Widget asWidget() {
        return mainPanel;
    }
}
