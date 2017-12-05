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
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

abstract class AssocDataTable extends DataTable<Association> implements IsWidget {

    private FlowPanel widgetPanel = new FlowPanel();
    private static String ID_COLUMN_NAME = "Id";
    private static String HOME_ID_COLUMN_NAME = "HomeId";
    private static String VERSION = "Version";
    private static String TYPE = "Type";
    private static String STATUS = "Status";
    private static String PREVIOUS_VERSION = "Previous";
    private static String SOURCE = "Source";
    private static String TARGET = "Target";
    private static String SS_STATUS = "SS Status";
    private static String ASSOC_DOC = "AssocDoc";

    private FlowPanel columnSelectionPanel = new FlowPanel();

    private static List<AnnotatedItem> columnList = Arrays.asList(
            new AnnotatedItem(true,ID_COLUMN_NAME),
            new AnnotatedItem(false,HOME_ID_COLUMN_NAME),
            new AnnotatedItem(false, VERSION),
            new AnnotatedItem(false, TYPE),
            new AnnotatedItem(false, STATUS),
            new AnnotatedItem(false, PREVIOUS_VERSION),
            new AnnotatedItem(false, SOURCE),
            new AnnotatedItem(false, TARGET),
            new AnnotatedItem(false, SS_STATUS),
            new AnnotatedItem(false, ASSOC_DOC)
    );

    private static final ProvidesKey<Association> KEY_PROVIDER = new ProvidesKey<Association>() {
        @Override
        public Object getKey(Association item) {
            return item == null ? null : item.id;
        }
    };

    public AssocDataTable(int pageSize) {
        super(columnList, pageSize, new Association(), true, false);
        widgetPanel.add(columnSelectionPanel);
        widgetPanel.add(super.asWidget());
    }


    @Override
    void addTableColumns() {

        if (diffSelect.getValue()) {
            dataTable.addColumn(new Column<Association, Boolean>(new CheckboxCell(true, false)) {
                @Override
                public Boolean getValue(Association object) {

                        return dataTable.getSelectionModel().isSelected(object);
                }

                @Override
                public void render(Cell.Context context, Association object, SafeHtmlBuilder sb) {

                    super.render(context, object, sb);

                }
            }, "Select");
        }

        if (columnToBeDisplayedIsChecked(ID_COLUMN_NAME)) {
            TextColumn<Association> idColumn = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.id.toString();
                }

            };
            idColumn.setSortable(true);
            columnSortHandler.setComparator(idColumn,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
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
            TextColumn<Association> homeColumn = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.home;
                }
            };
            homeColumn.setSortable(true);
            columnSortHandler.setComparator(homeColumn,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
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
            TextColumn<Association> versionColumn = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.version;
                }
            };
            versionColumn.setSortable(true);
            columnSortHandler.setComparator(versionColumn,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
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

        if (columnToBeDisplayedIsChecked(TYPE)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.type;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.type!=null) {
                                return (o2 != null && o2.type!=null) ? o1.type.toString().compareTo(o2.type.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,TYPE);
        }

        if (columnToBeDisplayedIsChecked(STATUS)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.status;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.status!=null) {
                                return (o2 != null && o2.status!=null) ? o1.status.toString().compareTo(o2.status.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,STATUS);
        }

        if (columnToBeDisplayedIsChecked(PREVIOUS_VERSION)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.previousVersion;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.previousVersion!=null) {
                                return (o2 != null && o2.previousVersion!=null) ? o1.previousVersion.toString().compareTo(o2.previousVersion.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,PREVIOUS_VERSION);
        }

        if (columnToBeDisplayedIsChecked(SOURCE)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.source;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.source!=null) {
                                return (o2 != null && o2.source!=null) ? o1.source.toString().compareTo(o2.source.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,SOURCE);
        }

        if (columnToBeDisplayedIsChecked(TARGET)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.target;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.target!=null) {
                                return (o2 != null && o2.target!=null) ? o1.target.toString().compareTo(o2.target.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,TARGET);
        }

        if (columnToBeDisplayedIsChecked(SS_STATUS)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.ssStatus;
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.ssStatus!=null) {
                                return (o2 != null && o2.ssStatus!=null) ? o1.ssStatus.toString().compareTo(o2.ssStatus.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,SS_STATUS);
        }

        if (columnToBeDisplayedIsChecked(ASSOC_DOC)) {
            TextColumn<Association> column = new TextColumn<Association>() {
                @Override
                public String getValue(Association association) {
                    return association.assocDoc.toString();
                }
            };
            column.setSortable(true);
            columnSortHandler.setComparator(column,
                    new Comparator<Association>() {
                        public int compare(Association o1, Association o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.assocDoc!=null) {
                                return (o2 != null && o2.assocDoc!=null) ? o1.assocDoc.toString().compareTo(o2.assocDoc.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(column,ASSOC_DOC);
        }

    }



    @Override
    void addActionBtnTableColumns() {
        // Only the first 10 can be queried due to the max params limit in stored query
        Column<Association, Association> getDocActionCol =
                new Column<Association, Association>(new ActionCell<Association>("", new ActionCell.Delegate<Association>() {

                    @Override
                    public void execute(Association association) {
                        // do action
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, Association object, SafeHtmlBuilder sb) {
//                        super.render(context, object, sb);
                        sb.append(getImgHtml("icons2/ic_forward_black_24dp_1x.png", "GetDocuments", true));
                    }

                    @Override
                    public Association getValue(Association item) {
                        return item;
                    }
                };

        actionBtnTable.addColumn(getDocActionCol, "Action(s)");

    }

    @Override
    ProvidesKey<Association> getKeyProvider() {
        return KEY_PROVIDER;
    }


    void setData(List<Association> associationList) {
        dataProvider.getList().clear();
        if (associationList!=null) {
            diffSelect.setEnabled(associationList.size()>1);
            dataProvider.getList().addAll(associationList);
        }

    }


    @Override
    public Widget asWidget() {
        return widgetPanel;
    }
}
