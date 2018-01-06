package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import gov.nist.toolkit.registrymetadata.client.ResourceItem;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

abstract class ResourceDataTable extends DataTable<ResourceItem> implements IsWidget  {
    abstract void doGetDocuments(List<ResourceItem> resourceItems);

    private FlowPanel widgetPanel = new FlowPanel();
    private static String TYPE_COLUMN_NAME = "Type";
    private FlowPanel columnSelectionPanel = new FlowPanel();

    private static List<AnnotatedItem> columnList = Arrays.asList(
            new AnnotatedItem(true,TYPE_COLUMN_NAME)
    );

    private static final ProvidesKey<ResourceItem> KEY_PROVIDER = new ProvidesKey<ResourceItem>() {
        @Override
        public Object getKey(ResourceItem item) {
            return item == null ? null : item.id;
        }
    };

    public ResourceDataTable(int pageSize) {
        super(columnList, pageSize, new ResourceItem(), true, false);
        widgetPanel.add(columnSelectionPanel);
        widgetPanel.add(super.asWidget());
    }

    @Override
    void addTableColumns() {
        if (diffSelect.getValue()) {
            dataTable.addColumn(new Column<ResourceItem, Boolean>(new CheckboxCell(true, false)) {
                @Override
                public Boolean getValue(ResourceItem object) {
                    return dataTable.getSelectionModel().isSelected(object);
                }

                @Override
                public void render(Cell.Context context, ResourceItem object, SafeHtmlBuilder sb) {
                    super.render(context, object, sb);

                }
            }, "Select");
        }

        if (columnToBeDisplayedIsChecked(TYPE_COLUMN_NAME)) {
            TextColumn<ResourceItem> idColumn = new TextColumn<ResourceItem>() {
                @Override
                public String getValue(ResourceItem objectRef) {
                    return objectRef.getType();
                }

            };
            idColumn.setSortable(true);
            columnSortHandler.setComparator(idColumn,
                    new Comparator<ResourceItem>() {
                        public int compare(ResourceItem o1, ResourceItem o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            // Compare the name columns.
                            if (o1 != null && o1.getType() != null) {
                                return (o2 != null && o2.getType() != null) ? o1.getType().compareTo(o2.getType()) : 1;
                            }
                            return -1;
                        }
                    });
            dataTable.addColumn(idColumn, TYPE_COLUMN_NAME);
        }

    }

    @Override
    void addActionBtnTableColumns() {
        // Only the first 10 can be queried due to the max params limit in stored query
        Column<ResourceItem, ResourceItem> getDocActionCol =
                new Column<ResourceItem, ResourceItem>(new ActionCell<ResourceItem>("", new ActionCell.Delegate<ResourceItem>() {

                    @Override
                    public void execute(ResourceItem objectRef) {
                        // do action
                        doGetDocuments(actionBtnProvider.getList());
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, ResourceItem object, SafeHtmlBuilder sb) {
//                        super.render(context, object, sb);
                        sb.append(getImgHtml("icons2/ic_forward_black_24dp_1x.png", "GetDocuments", true));
                    }

                    @Override
                    public ResourceItem getValue(ResourceItem item) {
                        return item;
                    }
                };

        actionBtnTable.addColumn(getDocActionCol, "Action(s)");


    }

    @Override
    ProvidesKey<ResourceItem> getKeyProvider() {
        return KEY_PROVIDER;
    }

    void setData(List<ResourceItem> resourceItems) {
        dataProvider.getList().clear();
        if (resourceItems!=null) {
            diffSelect.setEnabled(resourceItems.size()>1);
            dataProvider.getList().addAll(resourceItems);
        }

    }

//    @Override
//    void defaultSingleClickAction(ResourceItem row) {
//
//    }
//
//    @Override
//    void defaultDoubleClickAction(ResourceItem row) {
//
//    }
//
//    @Override
//    void setupDiffMode(boolean isSelected) {
//
//    }
//
//    @Override
//    void diffAction(ResourceItem left, ResourceItem right) {
//
//    }
//
//    @Override
//    int getWidthInPx() {
//        return 0;
//    }

    @Override
    public Widget asWidget() {
        return widgetPanel;
    }

}
