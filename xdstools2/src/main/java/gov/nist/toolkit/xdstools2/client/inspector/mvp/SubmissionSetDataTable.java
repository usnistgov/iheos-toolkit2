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
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

abstract class SubmissionSetDataTable extends DataTable<SubmissionSet> implements IsWidget {

    private FlowPanel widgetPanel = new FlowPanel();
    private static String ID_COLUMN_NAME = "Id";
    private static String HOME_ID_COLUMN_NAME = "HomeId";
    private static String SUBMISSION_TIME_NAME = "Time";
    private static String SOURCE_ID_NAME = "SourceId";
    private static String CONTENT_TYPE_CODES_NAME = "TypeCodes";
    private static String AUTHORS_NAME  = "Authors";
    private static String INTENDED_RECIPIENTS_NAME = "IntendedRecips";

    private FlowPanel columnSelectionPanel = new FlowPanel();

    private static List<AnnotatedItem> columnList = Arrays.asList(
            new AnnotatedItem(true,ID_COLUMN_NAME),
            new AnnotatedItem(false,HOME_ID_COLUMN_NAME),
            new AnnotatedItem(false, SUBMISSION_TIME_NAME),
            new AnnotatedItem(false, SOURCE_ID_NAME),
            new AnnotatedItem(false, CONTENT_TYPE_CODES_NAME),
            new AnnotatedItem(false, AUTHORS_NAME),
            new AnnotatedItem(false, INTENDED_RECIPIENTS_NAME)
    );

    private static final ProvidesKey<SubmissionSet> KEY_PROVIDER = new ProvidesKey<SubmissionSet>() {
        @Override
        public Object getKey(SubmissionSet item) {
            return item == null ? null : item.id;
        }
    };

    public SubmissionSetDataTable(int pageSize) {
        super(columnList, pageSize, new SubmissionSet(), true, false);
        widgetPanel.add(columnSelectionPanel);
        widgetPanel.add(super.asWidget());
    }


    @Override
    void addTableColumns() {

        if (diffSelect.getValue()) {
            dataTable.addColumn(new Column<SubmissionSet, Boolean>(new CheckboxCell(true, false)) {
                @Override
                public Boolean getValue(SubmissionSet object) {

                        return dataTable.getSelectionModel().isSelected(object);
                }

                @Override
                public void render(Cell.Context context, SubmissionSet object, SafeHtmlBuilder sb) {

                    super.render(context, object, sb);

                }
            }, "Select");
        }

        if (columnToBeDisplayedIsChecked(ID_COLUMN_NAME)) {
            TextColumn<SubmissionSet> idColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.id.toString();
                }

            };
            idColumn.setSortable(true);
            columnSortHandler.setComparator(idColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
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
            TextColumn<SubmissionSet> homeColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.home;
                }
            };
            homeColumn.setSortable(true);
            columnSortHandler.setComparator(homeColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
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


        if (columnToBeDisplayedIsChecked(SUBMISSION_TIME_NAME)) {
            TextColumn<SubmissionSet> submissionTimeColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.submissionTime;
                }
            };
            submissionTimeColumn.setSortable(true);
            columnSortHandler.setComparator(submissionTimeColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.submissionTime!=null) {
                                return (o2 != null && o2.submissionTime!=null) ? o1.submissionTime.toString().compareTo(o2.submissionTime.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(submissionTimeColumn,SUBMISSION_TIME_NAME);
        }

        if (columnToBeDisplayedIsChecked(SOURCE_ID_NAME)) {
            TextColumn<SubmissionSet> sourceIdColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.sourceId;
                }
            };
            sourceIdColumn.setSortable(true);
            columnSortHandler.setComparator(sourceIdColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.sourceId!=null) {
                                return (o2 != null && o2.sourceId!=null) ? o1.sourceId.toString().compareTo(o2.sourceId.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(sourceIdColumn,SOURCE_ID_NAME);
        }

        if (columnToBeDisplayedIsChecked(CONTENT_TYPE_CODES_NAME)) {
            TextColumn<SubmissionSet> contentTypeCodeColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.contentTypeCode.toString();
                }
            };
            contentTypeCodeColumn.setSortable(true);
            columnSortHandler.setComparator(contentTypeCodeColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.contentTypeCode!=null) {
                                return (o2 != null && o2.contentTypeCode!=null) ? o1.contentTypeCode.toString().compareTo(o2.contentTypeCode.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(contentTypeCodeColumn,CONTENT_TYPE_CODES_NAME);
        }

        if (columnToBeDisplayedIsChecked(AUTHORS_NAME)) {
            TextColumn<SubmissionSet> contentTypeCodeColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.authors.toString();
                }
            };
            contentTypeCodeColumn.setSortable(true);
            columnSortHandler.setComparator(contentTypeCodeColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.authors!=null) {
                                return (o2 != null && o2.authors!=null) ? o1.authors.toString().compareTo(o2.authors.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(contentTypeCodeColumn,AUTHORS_NAME);
        }

        if (columnToBeDisplayedIsChecked(INTENDED_RECIPIENTS_NAME)) {
            TextColumn<SubmissionSet> intendedRecipColumn = new TextColumn<SubmissionSet>() {
                @Override
                public String getValue(SubmissionSet submissionSet) {
                    return submissionSet.intendedRecipients.toString();
                }
            };
            intendedRecipColumn.setSortable(true);
            columnSortHandler.setComparator(intendedRecipColumn,
                    new Comparator<SubmissionSet>() {
                        public int compare(SubmissionSet o1, SubmissionSet o2) {
                            if (o1 == o2) {
                                return 0;
                            }

                            if (o1 != null && o1.intendedRecipients!=null) {
                                return (o2 != null && o2.intendedRecipients!=null) ? o1.intendedRecipients.toString().compareTo(o2.intendedRecipients.toString()) : 1;
                            }
                            return -1;
                        }
                    });

            dataTable.addColumn(intendedRecipColumn,INTENDED_RECIPIENTS_NAME);
        }

    }



    @Override
    void addActionBtnTableColumns() {
        // Only the first 10 can be queried due to the max params limit in stored query
        Column<SubmissionSet, SubmissionSet> getDocActionCol =
                new Column<SubmissionSet, SubmissionSet>(new ActionCell<SubmissionSet>("", new ActionCell.Delegate<SubmissionSet>() {

                    @Override
                    public void execute(SubmissionSet submissionSet) {
                        // do action
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, SubmissionSet object, SafeHtmlBuilder sb) {
//                        super.render(context, object, sb);
                        sb.append(getImgHtml("icons2/ic_forward_black_24dp_1x.png", "GetDocuments", true));
                    }

                    @Override
                    public SubmissionSet getValue(SubmissionSet item) {
                        return item;
                    }
                };

        actionBtnTable.addColumn(getDocActionCol, "Action(s)");

    }

    @Override
    ProvidesKey<SubmissionSet> getKeyProvider() {
        return KEY_PROVIDER;
    }


    void setData(List<SubmissionSet> submissionSetList) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(submissionSetList);

    }


    @Override
    public Widget asWidget() {
        return widgetPanel;
    }
}
