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
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

abstract class FoldersDataTable extends DataTable<Folder> implements IsWidget {

    private FlowPanel widgetPanel = new FlowPanel();
    private static String ID_COLUMN_NAME = "Id";
    private static String HOME_ID_COLUMN_NAME = "HomeId";
    private static String VERSION = "Version";
    private static String LAST_UPDATE_TIME = "LastUpdate";
    private static String CODE_LIST = "CodeList";

    private FlowPanel columnSelectionPanel = new FlowPanel();

    private static List<AnnotatedItem> columnList = Arrays.asList(
            new AnnotatedItem(true,ID_COLUMN_NAME),
            new AnnotatedItem(false,HOME_ID_COLUMN_NAME),
            new AnnotatedItem(false, VERSION),
            new AnnotatedItem(false, LAST_UPDATE_TIME),
            new AnnotatedItem(false, CODE_LIST)
    );

    private static final ProvidesKey<Folder> KEY_PROVIDER = new ProvidesKey<Folder>() {
        @Override
        public Object getKey(Folder item) {
            return item == null ? null : item.id;
        }
    };

    public FoldersDataTable(int pageSize) {
        super(columnList, pageSize, new Folder(), true, false);
        widgetPanel.add(columnSelectionPanel);
        widgetPanel.add(super.asWidget());
    }


    @Override
    void addTableColumns() {

        if (diffSelect.getValue()) {
            dataTable.addColumn(new Column<Folder, Boolean>(new CheckboxCell(true, false)) {
                @Override
                public Boolean getValue(Folder object) {

                        return dataTable.getSelectionModel().isSelected(object);
                }

                @Override
                public void render(Cell.Context context, Folder object, SafeHtmlBuilder sb) {

                    super.render(context, object, sb);

                }
            }, "Select");
        }

        if (columnToBeDisplayedIsChecked(ID_COLUMN_NAME)) {
            TextColumn<Folder> idColumn = new TextColumn<Folder>() {
                @Override
                public String getValue(Folder folder) {
                    return folder.id.toString();
                }

            };
            idColumn.setSortable(true);
            columnSortHandler.setComparator(idColumn,
                    new Comparator<Folder>() {
                        public int compare(Folder o1, Folder o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            // Compare the name columns.
                            if (o1 != null && o1.id != null) {
                                return (o2 != null && o2.id != null) ? o1.id.toString().compareTo(o2.id.toString()) : 1;
                            }
                            return -1;
                        }
                    });
            dataTable.addColumn(idColumn, ID_COLUMN_NAME);
        }

        if (columnToBeDisplayedIsChecked(HOME_ID_COLUMN_NAME)) {
            TextColumn<Folder> homeColumn = new TextColumn<Folder>() {
                @Override
                public String getValue(Folder folder) {
                    return folder.home;
                }
            };
            homeColumn.setSortable(true);
            columnSortHandler.setComparator(homeColumn,
                    new Comparator<Folder>() {
                        public int compare(Folder o1, Folder o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.home!=null) {
                                return (o2 != null && o2.home!=null) ? o1.home.toString().compareTo(o2.home.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(homeColumn,HOME_ID_COLUMN_NAME);
        }

        if (columnToBeDisplayedIsChecked(VERSION)) {
            TextColumn<Folder> versionColumn = new TextColumn<Folder>() {
                @Override
                public String getValue(Folder folder) {
                    return folder.version;
                }
            };
            versionColumn.setSortable(true);
            columnSortHandler.setComparator(versionColumn,
                    new Comparator<Folder>() {
                        public int compare(Folder o1, Folder o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.version!=null) {
                                return (o2 != null && o2.version!=null) ? o1.version.toString().compareTo(o2.version.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(versionColumn,VERSION);
        }

        if (columnToBeDisplayedIsChecked(LAST_UPDATE_TIME)) {
            TextColumn<Folder> column = new TextColumn<Folder>() {
                @Override
                public String getValue(Folder folder) {
                    return folder.lastUpdateTime;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Folder>() {
                        public int compare(Folder o1, Folder o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.lastUpdateTime!=null) {
                                return (o2 != null && o2.lastUpdateTime!=null) ? o1.lastUpdateTime.toString().compareTo(o2.lastUpdateTime.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,LAST_UPDATE_TIME);
        }

        if (columnToBeDisplayedIsChecked(CODE_LIST)) {
            TextColumn<Folder> column = new TextColumn<Folder>() {
                @Override
                public String getValue(Folder folder) {
                    return folder.codeList.toString();
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Folder>() {
                        public int compare(Folder o1, Folder o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.codeList!=null) {
                                return (o2 != null && o2.codeList!=null) ? o1.codeList.toString().compareTo(o2.codeList.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,CODE_LIST);
        }

    }



    @Override
    void addActionBtnTableColumns() {
        // Only the first 10 can be queried due to the max params limit in stored query
        Column<Folder, Folder> getDocActionCol =
                new Column<Folder, Folder>(new ActionCell<Folder>("", new ActionCell.Delegate<Folder>() {

                    @Override
                    public void execute(Folder folder) {
                        // do action
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, Folder object, SafeHtmlBuilder sb) {
//                        super.render(context, object, sb);
                        sb.append(getImgHtml("icons2/ic_forward_black_24dp_1x.png", "GetDocuments", true));
                    }

                    @Override
                    public Folder getValue(Folder item) {
                        return item;
                    }
                };

        actionBtnTable.addColumn(getDocActionCol, "Action(s)");

    }

    @Override
    ProvidesKey<Folder> getKeyProvider() {
        return KEY_PROVIDER;
    }


    void setData(List<Folder> folderList) {
        dataProvider.getList().clear();
        if (folderList!=null) {
            diffSelect.setEnabled(folderList.size()>1);
            dataProvider.getList().addAll(folderList);
        }

    }


    @Override
    public Widget asWidget() {
        return widgetPanel;
    }
}
