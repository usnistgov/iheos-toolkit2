package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionInstancesCommand;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewer;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od.OddsEditTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.NewToolLauncher;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by skb1 on 08/14/17.
 */
public class SimManagerWidget2 extends Composite {

    protected static final int ROW_BUFFER = 38;
    private static final int PAGE_SIZE = 25;

    CommandContext commandContext;
    private SimulatorControlTab hostTab;
    private FlowPanel containerPanel = new FlowPanel();

//    private CellTable<SimInfo> newSimTable = new CellTable<SimInfo>();
    private DataGrid<SimInfo> newSimTable = new DataGrid<SimInfo>();
    private Timer singleClickTimer;
    private int clickCount = 0;
    private DataGrid<SimInfo> actionTable = new DataGrid<SimInfo>();
    // Create a data provider.
    private ListDataProvider<SimInfo> dataProvider = new ListDataProvider<SimInfo>();
    private ListDataProvider<SimInfo> actionDataProvider = new ListDataProvider<SimInfo>();
    private SimInfo placeHolderSimInfo = new SimInfo();
    private String testSession = "";


    SelectionModel<SimInfo> selectionModel;
    private int rows;
    CheckBox multiSelect = new CheckBox("Multiple selection");

    public SimManagerWidget2() {
    }

    public SimManagerWidget2(CommandContext commandContext, SimulatorControlTab hostTab) {
        this.commandContext = commandContext;
        this.hostTab = hostTab;


        actionDataProvider.getList().add(placeHolderSimInfo);
        buildTableColumns();

        newSimTable.addCellPreviewHandler(new CellPreviewEvent.Handler<SimInfo>() {
            @Override
            public void onCellPreview(CellPreviewEvent<SimInfo> cellPreviewEvent) {

               if (Event.getTypeInt(cellPreviewEvent.getNativeEvent().getType()) == Event.ONCLICK) {
                    clickCount++;
                    if (clickCount == 1) {
                        singleClickTimer = new Timer() {

                            @Override
                            public void run() {
                                clickCount = 0; // Cancel clickCount if Double click did not happen within the specified timeframe
                            }
                        };
                        singleClickTimer.schedule(300);
                    } else if (clickCount == 2) {
                        GWT.log("Double clicked table.");
                        singleClickTimer.cancel();
                        clickCount = 0;
                        DataGrid<SimInfo> grid = (DataGrid<SimInfo>) cellPreviewEvent.getSource();
                        int row = grid.getKeyboardSelectedRow();
                        SimInfo item = grid.getVisibleItem(row);
//                        Window.alert("Do Something Here" + item.getSimulatorConfig().getId().toString());
                        defaultEditTabAction(item.getSimulatorConfig());
                        getSelectionModel().setSelected(item, true);
//                        actionDataProvider.getList().remove(placeHolderSimInfo);
//                        actionDataProvider.getList().add(item);
//                        actionTable.redraw();
                        GWT.log("action list size: " + actionDataProvider.getList().size());

                    }
                }
            }
        });

        newSimTable.setWidth("640px");
//        newSimTable.setHeight("400px");
        newSimTable.setSkipRowHoverCheck(true);
        newSimTable.setSkipRowHoverFloatElementCheck(true);
//        txTable.setSkipRowHoverStyleUpdate(true);
//        txTable.setStyleName("txDataGridNoTableSpacing");
        newSimTable.getElement().getStyle().setProperty("wordWrap","break-word");

        multiSelect.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                if (multiSelect.isEnabled() && multiSelect.getValue()) {
                    setupMultiSelectionMode();
                } else {
                    setupSingleSelectionMode();
                }
            }

        });
        containerPanel.add(multiSelect);
        containerPanel.add(newSimTable);



        actionTable.setWidth("500px");
        actionTable.setHeight("75px");
//        actionTableTop.getElement().getStyle().setMarginLeft(40, Style.Unit.PCT);
        actionTable.getElement().getStyle().setMarginTop(8, Style.Unit.PX);
        actionTable.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        actionTable.setHeaderBuilder(new MyCustomHeaderBuilder(actionTable, false));
        containerPanel.add(actionTable);
//        actionTableBottom.setWidth("500px");
//        actionTableBottom.setHeight("100px");
//        containerPanel.add(actionTableBottom);
        SimplePager simplePager = new SimplePager();
        simplePager.getElement().getStyle().setMarginTop(7, Style.Unit.PX);
        simplePager.getElement().getStyle().setMarginLeft(40, Style.Unit.PCT);
        simplePager.setDisplay(newSimTable);
        simplePager.setPageSize(PAGE_SIZE);
        containerPanel.add(simplePager);

        initWidget(containerPanel);
    }

    protected int popCellTable(String testSession, List<SimulatorConfig> configs, List<SimulatorStats> statsList) {
        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        final List<SimInfo> list = dataProvider.getList();

        boolean reload = (!this.testSession.equals(testSession)) ||  (list!=null && configs!=null && list.size()!=configs.size());
//        Window.alert(" testSession: " + testSession + " reload " + reload);
        if (reload) {
            setTestSession(testSession);
            list.clear();
            rows = 0;
            int rowCt = 0;
            for (SimulatorConfig config : configs) {
                final SimInfo simInfo = new SimInfo(config, statsList.get(rowCt));
                if (simInfo.getSimulatorConfig()!=null && simInfo.getSimulatorConfig().get(SimulatorProperties.creationTime)!=null) {
                    String creationTime = simInfo.getSimulatorConfig().get(SimulatorProperties.creationTime).asString();
                    String dateParts[] = creationTime.split(" ");
                    String newTimeWoTz = null;
                    if (dateParts.length==6) {// Expected format Mon Jul 31 18:18:27 EDT 2017
                        newTimeWoTz = dateParts[0] + " " + dateParts[1] + " " + dateParts[2] + " " + dateParts[3] + " " + dateParts[5];
                        Date newTime = DateTimeFormat.getFormat("EEE MMM dd HH:mm:ss yyyy").parse(newTimeWoTz); // Local Tz
                        String hl7TimeWoTz = DateTimeFormat.getFormat("yyyyMMddHHmmss").format(newTime).toString();
                        /**
                         * TODO: fix this later when GWT supports timezone parsing.
                         * Assumption
                         * As of August 2017, using GWT 2.7 and according to the latest docs
                         * In the current implementation, timezone parsing only supports GMT:hhmm, GMT:+hhmm, and GMT:-hhmm.
                         *
                         * Since GWT doesn't fully support timezone parsing at the client side,
                         * for comparison purposes, assume all timestamps are local.
                         *
                         http://www.gwtproject.org/javadoc/latest/com/google/gwt/i18n/client/DateTimeFormat.html

                         This throws an Exception.
                         Window.alert(DateTimeFormat.getFormat("EEE MMM dd HH:mm:ss Z").parse("Mon Jul 31 18:18:27 EDT").toString());
                         */
                        simInfo.setCreatedDtHl7fmt(hl7TimeWoTz);
                    }

                }

                /*

                 */

                try {

                    new GetTransactionInstancesCommand() {
                        @Override
                        public void onComplete(List<TransactionInstance> result) {
                            if (result != null && result.size() > 0) {


                                for (int idx = 0; idx < SimInfo.TOP_TRANSACTION_CT; idx++) {
                                    if (result.size() > idx) {
                                        simInfo.getTopThreeTransInstances().add(TransactionInstance.copy(result.get(idx)));
//                                        try {
                                            // xx
//                                            newSimTable.redraw();
//                                            actionTable.redraw();
//                                        } catch (Exception ex) {
//                                        }
                                    }
                                }
                            }

                        }
                    }.run(new GetTransactionRequest(commandContext, simInfo.getSimulatorConfig().getId(), "", null));
                } catch (Exception ex) {}


//                Window.alert("adding " + simInfo.getSimulatorConfig().getId().toString());

                list.add(simInfo);
                rowCt++;
            }
            if (rowCt>0) {
                rows = rowCt;
            }
        }

        multiSelect.setEnabled(rows>1);
        return rows;

    }


    public abstract class AbstractActionButton<SimInfo> implements HasCell<SimInfo, SimInfo> {

        @Override
        public Cell<SimInfo> getCell() {
            return new ActionCell<SimInfo>("Button title", new ActionCell.Delegate<SimInfo>() {
                @Override
                public void execute(SimInfo object) {
                    AbstractActionButton.this.execute(object);
                }
            }) {
                @Override
                public void render(Context context, SimInfo data, SafeHtmlBuilder sb) {
                    AbstractActionButton.this.render(context, data, sb);
                }
            };
        }

        //Replaced by delegate but still need to be overriden
        @Override
        public FieldUpdater<SimInfo, SimInfo> getFieldUpdater() {
            return null;
        }

        @Override
        public SimInfo getValue(SimInfo object) {
            return object;
        }

        /**
         * You can override this method to render your button differently. Not mandatory
         * @param context
         * @param data
         * @param sb
         */
        public abstract void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb);

        /**
         * Called when the button is clicked
         * @param object
         */
        public abstract void execute(SimInfo object);
    }

    private Column<SimInfo, SimInfo> getButtonColumn() {
        return new Column<SimInfo, SimInfo>(getButtonsCell()) {
            @Override
            public SimInfo getValue(SimInfo object) {
                return object;
            }
        };
    }


    private void buildTableColumns() {

        TextColumn<SimInfo> nameColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getSimulatorConfig().getDefaultName();
            }
        };




        // Add a ColumnSortEvent.ListHandler to connect sorting to the
        // java.util.List.
        ColumnSortEvent.ListHandler<SimInfo> columnSortHandler = new ColumnSortEvent.ListHandler<SimInfo>(
                dataProvider.getList());
        newSimTable.addColumnSortHandler(columnSortHandler);


        TextColumn<SimInfo> idColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getSimulatorConfig().getId().toString();
            }
        };
        idColumn.setSortable(true);
        columnSortHandler.setComparator(idColumn,
                new Comparator<SimInfo>() {
                    public int compare(SimInfo o1, SimInfo o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the name columns.
                        if (o1 != null && o1.getSimulatorConfig().getId()!=null) {
                            return (o2 != null && o2.getSimulatorConfig().getId()!=null) ? o1.getSimulatorConfig().getId().toString().compareTo(o2.getSimulatorConfig().getId().toString()) : 1;
                        }
                        return -1;
                    }
                });

        TextColumn<SimInfo> createdDtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getSimulatorConfig().get(SimulatorProperties.creationTime).asString();
            }
        };
        createdDtColumn.setSortable(true);
        createdDtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(createdDtColumn,
                new Comparator<SimInfo>() {
                    public int compare(SimInfo o1, SimInfo o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the created dt columns.
                        if (o1.getCreatedDtHl7fmt() != null) {
                            return (o2.getCreatedDtHl7fmt() != null) ? o1.getCreatedDtHl7fmt().compareTo(o2.getCreatedDtHl7fmt()) : 1;
                        }
                        return -1;
                    }
                });



        TextColumn<SimInfo> lastTranColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getTopThreeTransInstances()!=null && simInfo.getTopThreeTransInstances().size()>0 ) {
                    TransactionInstance ti = simInfo.getTopThreeTransInstances().get(0);
                    return  ti.actorType.getShortName() + "/" + ti.trans;
                }
                return null;
            }
        };

        TextColumn<SimInfo> lastAccessedDtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getTopThreeTransInstances()!=null && simInfo.getTopThreeTransInstances().size()>0 ) {
                    TransactionInstance ti = simInfo.getTopThreeTransInstances().get(0);
                    return ti.labelInterpretedAsDate;
                }
                return null;
            }
        };
        lastAccessedDtColumn.setSortable(true);
        lastAccessedDtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(lastAccessedDtColumn,
                new Comparator<SimInfo>() {
                    public int compare(SimInfo o1, SimInfo o2) {
                        if (o1 == o2) {
                            return 0;
                        }


                        if (o1==null || o2==null)
                            return -1;


                        // Compare the last acc dt columns.
                        if (o1.getTopThreeTransInstances()!=null && o1.getTopThreeTransInstances().size()>0  && o1.getTopThreeTransInstances().get(0).messageId!=null) {
                            return (o2.getTopThreeTransInstances()!=null && o2.getTopThreeTransInstances().size()>0 &&  o2.getTopThreeTransInstances().get(0).messageId!=null) ? o1.getTopThreeTransInstances().get(0).messageId.compareTo(o2.getTopThreeTransInstances().get(0).messageId) : 1;
                        }
                        return -1;
                    }
                });

        TextColumn<SimInfo> typeColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return ActorType.findActor(simInfo.getSimulatorConfig().getActorType()).getName();
            }
        };
        typeColumn.setSortable(true);
        columnSortHandler.setComparator(typeColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                if (o1 == o2) {
                    return 0;
                }

                String o1type = ActorType.findActor(o1.getSimulatorConfig().getActorType()).getName();
                String o2type = ActorType.findActor(o2.getSimulatorConfig().getActorType()).getName();

                if (o1type==o2type)
                    return 0;



                if (o1type!= null) {
                    return (o2type!= null) ? new Integer(o1type.compareTo(o2type)) : 1;
                }
                return -1;
            }
        });

        TextColumn<SimInfo> pifPortColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {

                SimulatorConfigElement portConfig = simInfo.getSimulatorConfig().get(SimulatorProperties.PIF_PORT);
                if (portConfig!=null)
                    return portConfig.asString();
                return null;
            }
        };
        pifPortColumn.setSortable(true);
        pifPortColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(pifPortColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the pif port columns.
                SimulatorConfigElement o1pif = o1.getSimulatorConfig().get(SimulatorProperties.PIF_PORT);
                SimulatorConfigElement o2pif = o2.getSimulatorConfig().get(SimulatorProperties.PIF_PORT);

                if (o1pif==o2pif)
                    return 0;



                if (o1pif != null) {
                    return (o2pif != null) ? new Integer(o1pif.asString()).compareTo(new Integer(o2pif.asString())) : 1;
                }
                return -1;
            }
        });

        TextColumn<SimInfo> ssCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.SUBMISSION_SET_COUNT);
                }
                return null;
            }
        };
        ssCtColumn.setSortable(true);
        ssCtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(ssCtColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                return compareStats(SimulatorStats.SUBMISSION_SET_COUNT,o1,o2);
            }
        });

        TextColumn<SimInfo> desCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.DOCUMENT_ENTRY_COUNT);
                }
                return null;
            }
        };
        desCtColumn.setSortable(true);
        desCtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(desCtColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                return compareStats(SimulatorStats.DOCUMENT_ENTRY_COUNT,o1,o2);
            }
        });

        TextColumn<SimInfo> foldersCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.FOLDER_COUNT);
                }
                return null;
            }
        };
        foldersCtColumn.setSortable(true);
        foldersCtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(foldersCtColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                return compareStats(SimulatorStats.FOLDER_COUNT,o1,o2);
            }
        });




        TextColumn<SimInfo> docsCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.DOCUMENT_COUNT);
                }
                return null;
            }
        };
        docsCtColumn.setSortable(true);
        docsCtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(docsCtColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                return compareStats(SimulatorStats.DOCUMENT_COUNT,o1,o2);
            }
        });



        TextColumn<SimInfo> pidsCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.PATIENT_ID_COUNT);
                }
                return null;
            }
        };
        pidsCtColumn.setSortable(true);
        pidsCtColumn.setDefaultSortAscending(false);
        columnSortHandler.setComparator(pidsCtColumn, new Comparator<SimInfo>() {
            public int compare(SimInfo o1, SimInfo o2) {
                return compareStats(SimulatorStats.PATIENT_ID_COUNT,o1,o2);
            }
        });



        /*
        Column<SimInfo, ImageResource> logImgColumn =
                new Column<SimInfo, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(SimInfo object) {
                        return ScTabResources.INSTANCE.getLogIcon();
                    }

                };
        */


//        SafeHtmlBuilder logIconImgHtml = new SafeHtmlBuilder();
//        logIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"View Transaction Logs\" src=\"icons2/log-file-format-symbol.png\">");
        Column<SimInfo, SimInfo> logActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>("", new ActionCell.Delegate<SimInfo>() {

                    @Override
                    public void execute(SimInfo simInfo) {
                        SimulatorConfig config = simInfo.getSimulatorConfig();
                        // Use the newer Sim Log Viewer
                        new NewToolLauncher().launch(new SimMsgViewer(config.getId().toString()));
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, SimInfo object, SafeHtmlBuilder sb) {
//                        super.render(context, object, sb);
                        sb.append(getImgHtml("icons2/log-file-format-symbol.png", "View Transaction Logs", false));
                    }

                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }
                };

//        SafeHtmlBuilder pidIconImgHtml = new SafeHtmlBuilder();
//        pidIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Edit Patient Ids\" src=\"icons2/id.png\">");
        Column<SimInfo, SimInfo> pidActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>("", new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(SimInfo simInfo) {
                        SimulatorConfig config = simInfo.getSimulatorConfig();
                        PidEditTab editTab = new PidEditTab(config);
                        editTab.onTabLoad(true, "PIDEdit");
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, SimInfo object, SafeHtmlBuilder sb) {
                        sb.append(getImgHtml("icons2/id.png", "Edit Patient Ids", false));
                    }

                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }
                };
//        newSimTable.setColumnWidth(pidActionCol, "27px");

        Column<SimInfo, SimInfo> editActionCol = getButtonColumn();

//        SafeHtmlBuilder trashBinIconImgHtml = new SafeHtmlBuilder();
//        trashBinIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\"  title=\"Delete\" src=\"icons2/garbage.png\">");
        Column<SimInfo, SimInfo> trashBinActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>("", new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(final SimInfo simInfo) {

//                        DeleteSimInfo deleteSimInfo = new DeleteSimInfo(containerPanel,hostTab);
//                        deleteSimInfo.setSimInfoList(Arrays.asList(new SimInfo[]{simInfo}));
//                        deleteSimInfo.delete();

                        Set<SimInfo> mySelection = null;
                        boolean multiple = false;
                        if (getSelectionModel() instanceof MultiSelectionModel) {
                            multiple = true;
                            mySelection = ((MultiSelectionModel) newSimTable.getSelectionModel()).getSelectedSet();
                        } else {
                           mySelection = new HashSet<SimInfo>(actionDataProvider.getList());
                        }

                        DeleteSimInfo deleteSimInfo = new DeleteSimInfo(containerPanel,hostTab);
                        deleteSimInfo.setSimInfoList(new ArrayList<SimInfo>(mySelection));
                        deleteSimInfo.delete();

                        if (multiple)
                            ((MultiSelectionModel) newSimTable.getSelectionModel()).clear();
                        else
                            ((SingleSelectionModel) newSimTable.getSelectionModel()).clear();

                        // When the newSimTable selection model list gets cleared, the object is also removed from the actionDataProvider. Insert a placeholder into the actionData.
                        actionDataProvider.getList().clear();
                        actionDataProvider.getList().add(placeHolderSimInfo);
                        // xx
                        actionTable.redraw();

                    }


                })) {

                    @Override
                    public void render(Cell.Context context, SimInfo object, SafeHtmlBuilder sb) {
                        sb.append(getImgHtml("icons2/garbage.png","Delete", true));
                    }

                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }


                };

//        SafeHtmlBuilder downloadIconImgHtml = new SafeHtmlBuilder();
//        downloadIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Download\" src=\"icons2/download.png\">");
        Column<SimInfo, SimInfo> downloadActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>("", new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(SimInfo simInfo) {
                        Window.open("siteconfig/"+simInfo.getSimulatorConfig().getId().toString(), "_blank","");
                    }
                })) {
                    @Override
                    public void render(Cell.Context context, SimInfo object, SafeHtmlBuilder sb) {
                        sb.append(getImgHtml("icons2/download.png", "Download configuration", false));
                    }

                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }
                };

        // Selection model can be coded here
        setupSingleSelectionMode();


        Column<SimInfo, Boolean> checkColumn =
                new Column<SimInfo, Boolean>(new CheckboxCell(true, false)) {
                    @Override
                    public Boolean getValue(SimInfo object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };


        SafeHtmlBuilder trashBinSmIconImgHtml = new SafeHtmlBuilder();
        trashBinSmIconImgHtml.appendHtmlConstant("<img style=\"width: 18px; height: 18px;\"  title=\"Delete\" src=\"icons2/garbage.png\">");
        Header<SimInfo> delBtnFooter = new Header<SimInfo>(new ActionCell<SimInfo>(trashBinSmIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
            @Override
            public void execute(SimInfo object) {
                try {
                    String selectionIds = "";
                    Set<SimInfo> mySelection = ((MultiSelectionModel) newSimTable.getSelectionModel()).getSelectedSet();
//                    for (SimInfo s : mySelection) {
//                       selectionIds += s.getSimulatorConfig().getId().toString() + " ";
//                    }
//                    Window.alert(selectionIds);

                    DeleteSimInfo deleteSimInfo = new DeleteSimInfo(containerPanel,hostTab);
                    deleteSimInfo.setSimInfoList(new ArrayList<SimInfo>(mySelection));
                    deleteSimInfo.delete();

                    ((MultiSelectionModel) newSimTable.getSelectionModel()).clear();

                } catch (Exception ex) {
//                    Window.alert(ex.toString());
                }

            }

        })) {
            @Override
            public SimInfo getValue() {
               return null;
            }
        };
//        newSimTable.addColumn(checkColumn, new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Delete")), delBtnFooter );

        /*
         * Columns
         * Name	ID	[created dt] [last tran] [last accessed dt == filter on date],  Type	Patient Feed Port	SubmissionSets	DocumentEntries	Folders	Documents	PatientIds	Action
         *
         * Sorting
         * Use the Hl7 date values in the comparator
         */

//        newSimTable.addColumn(nameColumn, "Name");
        newSimTable.addColumn(idColumn, "ID");
        newSimTable.addColumn(createdDtColumn,"Created Date");
        newSimTable.addColumn(typeColumn, "Type");
        newSimTable.addColumn(lastTranColumn, "Last Tran");
        newSimTable.addColumn(lastAccessedDtColumn, "Last Tran Date");
        newSimTable.addColumn(pifPortColumn, "Patient Feed Port");
        newSimTable.addColumn(ssCtColumn, "Submission Sets");
        newSimTable.addColumn(desCtColumn, "Doc Entries");
        newSimTable.addColumn(foldersCtColumn, "Folders");
        newSimTable.addColumn(docsCtColumn, "Docs");
        newSimTable.addColumn(pidsCtColumn, "PIds");

        // Sim info
//        newSimTable.setColumnWidth(checkColumn, "4%");
//        newSimTable.setColumnWidth(idColumn, "15%");
//        newSimTable.setColumnWidth(createdDtColumn, "10%");
//        newSimTable.setColumnWidth(typeColumn, "11%");
//        newSimTable.setColumnWidth(lastTranColumn, "7%");
//        newSimTable.setColumnWidth(lastAccessedDtColumn, "10%");
//        newSimTable.setColumnWidth(pifPortColumn, "6%");

        // Sim stats
//        newSimTable.setColumnWidth(ssCtColumn, "5%");
//        newSimTable.setColumnWidth(desCtColumn, "5%");
//        newSimTable.setColumnWidth(foldersCtColumn, "5%");
//        newSimTable.setColumnWidth(docsCtColumn, "5%");
//        newSimTable.setColumnWidth(pidsCtColumn, "5%");

        /*
        // Action
        newSimTable.setColumnWidth(logActionCol, "5%");
        newSimTable.setColumnWidth(pidActionCol,"4%");
        newSimTable.setColumnWidth(editActionCol,"5%");
        newSimTable.setColumnWidth(trashBinActionCol,"4%");
        newSimTable.setColumnWidth(downloadActionCol,"4%");
        */

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(newSimTable);

//        actionTable.getElement().getStyle().setMarginLeft(40, Style.Unit.PCT);
        actionTable.addColumn(logActionCol, "Action(s)");
        actionTable.addColumn(pidActionCol,"");
        actionTable.addColumn(editActionCol,"");
        actionTable.addColumn(trashBinActionCol,"");
        actionTable.addColumn(downloadActionCol,"");

        actionDataProvider.addDataDisplay(actionTable);


//        actionTableBottom.addColumn(logActionCol, "Action(s)");
//        actionTableBottom.addColumn(pidActionCol,"");
//        actionTableBottom.addColumn(editActionCol,"");
//        actionTableBottom.addColumn(trashBinActionCol,"");
//        actionTableBottom.addColumn(downloadActionCol,"");
//        actionDataProvider.addDataDisplay(actionTableBottom);
    }

    SafeHtml getImgHtml(String iconFilePath, String title, boolean supportsMultiple) {
//        Set<SimInfo> mySelection = ((MultiSelectionModel) newSimTable.getSelectionModel()).getSelectedSet();
        Set<SimInfo> mySelection = null;
        if (getSelectionModel() instanceof MultiSelectionModel) {
             mySelection = ((MultiSelectionModel) getSelectionModel()).getSelectedSet();
//        Window.alert("selection size is " + mySelection.size() + "; icon is " + iconFilePath + "; supportsMultiple " + supportsMultiple);
         if (mySelection!=null) {

            if (mySelection.size() == 1 || (mySelection.size() > 1 && supportsMultiple))
                return new SafeHtmlBuilder().appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"" + title + "\" src=\"" + iconFilePath + "\">").toSafeHtml();
            else
                return new SafeHtmlBuilder().appendHtmlConstant("<input type=\"image\" style=\"width: 24px; height: 24px; opacity:0.5\" src=\"" + iconFilePath + "\" border=0 disabled/>").toSafeHtml();
         }
        }
        else if (getSelectionModel() instanceof SingleSelectionModel) {
//            final List<SimInfo> list =
                    // actionDataProvider.getList();
             SimInfo selectedObj =  (SimInfo)((SingleSelectionModel) getSelectionModel()).getSelectedObject();
            if (selectedObj==null) {
                return new SafeHtmlBuilder().appendHtmlConstant("<input type=\"image\" style=\"width: 24px; height: 24px; opacity:0.5\" src=\"" + iconFilePath + "\" border=0 disabled/>").toSafeHtml();
            } else {
                return new SafeHtmlBuilder().appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"" + title +"\" src=\"" + iconFilePath + "\">").toSafeHtml();
            }
        }
        return new SafeHtmlBuilder().appendHtmlConstant("<input type=\"image\" style=\"width: 24px; height: 24px; opacity:0.5\" src=\"" + iconFilePath + "\" border=0 disabled/>").toSafeHtml();
    }


    private static final ProvidesKey<SimInfo> KEY_PROVIDER = new ProvidesKey<SimInfo>() {
        @Override
        public Object getKey(SimInfo item) {
            return item == null ? null : item.getSimulatorConfig().getId().toString();
        }
    };


    private CompositeCell getButtonsCell() {
        List<HasCell<SimInfo, ?>> cells = new LinkedList<>();

        HasCell<SimInfo,SimInfo> normalEdit =   new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
//                Window.alert("You clicked " + object.getSimulatorConfig().getDefaultName());

//                loadSimStatus();
                SimulatorConfig config = object.getSimulatorConfig();
                defaultEditTabAction(config);


            }

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //
                if (data.getSimulatorConfig()!=null) {
                    if (!ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType())) {
                        sb.append(getImgHtml("icons2/edit.png", "Edit", false));
                    }
                } else
                    sb.append(getImgHtml("icons2/edit.png", "", false));
            }
        };

        HasCell<SimInfo,SimInfo> rgEdit = new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
//                Window.alert("You clicked RG" + object.getSimulatorConfig().getDefaultName());

                SimulatorConfig config = object.getSimulatorConfig();
                // Generic state-less type simulators
                GenericQueryTab editTab = new EditTab(hostTab, config);
                editTab.onTabLoad(true, "SimConfig");
            }

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //
                SafeHtmlBuilder mySb = new SafeHtmlBuilder();


                if (data.getSimulatorConfig()!=null) {
                    if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType())) {
//                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Edit RG config\"  src=\"icons2/edit-rg.png\">");
                        sb.append(getImgHtml("icons2/edit-rg.png", "Edit RG configuration", false));
                    }
                }
            }
        };

        HasCell<SimInfo,SimInfo> rgOdEdit = new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
//                Window.alert("You clicked RGOD" + object.getSimulatorConfig().getDefaultName());

//                loadSimStatus();
                SimulatorConfig config = object.getSimulatorConfig();
                // This simulator requires content state initialization
                OddsEditTab editTab;
                editTab = new OddsEditTab(hostTab, config);
                editTab.onTabLoad(true, "ODDS");

            }

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //
                SafeHtmlBuilder mySb = new SafeHtmlBuilder();

                if (data.getSimulatorConfig()!=null) {
                    if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType())) {
//                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Edit OD config\" src=\"icons2/edit-od.png\">");
                        sb.append(getImgHtml("icons2/edit-od.png", "Edit OD configuration", false));
                    }
                }
            }
        };

        cells.add(rgEdit);
        cells.add(rgOdEdit);
        cells.add(normalEdit);


        CompositeCell<SimInfo> compositeCell = new CompositeCell<>(cells);

        return compositeCell;
    }

    private void defaultEditTabAction(SimulatorConfig config) {
        //							GenericQueryTab editTab;
        if (ActorType.ONDEMAND_DOCUMENT_SOURCE.getShortName().equals(config.getActorType())
                ) {
            // This simulator requires content state initialization
            OddsEditTab editTab;
            editTab = new OddsEditTab(hostTab, config);
            editTab.onTabLoad(true, "ODDS");
        } else {
            // Generic state-less type simulators
            GenericQueryTab editTab = new EditTab(hostTab, config);
            editTab.onTabLoad(true, "SimConfig");
        }
    }


    protected static int compareStats(String code, SimInfo o1, SimInfo o2) {
        if (o1 == o2) {
            return 0;
        }

        SimulatorStats o1ss = o1.getSimulatorStats();
        SimulatorStats o2ss = o2.getSimulatorStats();

        if (o1ss==o2ss)
            return 0;


        if (o1ss != null) {
            if (o2ss != null)  {

                Map<String,String> o1map = o1ss.getStats();
                Map<String,String> o2map = o2ss.getStats();

                if (o1map==o2map)
                    return 0;

                String val1 = o1map!=null? o1map.get(code) : null;
                String val2 = o2map!=null? o2map.get(code) : null;

                if (val1==val2)
                    return  0;


                if (val1!=null)
                    return (val2!=null) ? new Integer(val1).compareTo(new Integer(val2)) : 1;
            }
        }
        return -1;
    }

    // Flaticon credits
    // <div>Icons made by <a href="http://www.flaticon.com/authors/madebyoliver" title="Madebyoliver">Madebyoliver</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
    // <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
    // <div>Icons made by <a href="http://www.flaticon.com/authors/retinaicons" title="Retinaicons">Retinaicons</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
    // <div>Icons made by <a href="http://www.flaticon.com/authors/gregor-cresnar" title="Gregor Cresnar">Gregor Cresnar</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>

    void resizeTable(int widthInPx) {
        if (newSimTable!=null) {
            newSimTable.setWidth(widthInPx + "px");

            float rowHeight = 30;
            try {
                rowHeight = newSimTable.getRowElement(0).getClientHeight();
            } catch (Exception ex) {
//                Window.alert(ex.toString());
            }

//            Window.alert("resize is called with " + widthInPx + ". rowHeight is " + rowHeight + ". rows " + rows);

            float tableHeight = 100F;
            if (rows>0)
                tableHeight = calcTableHeight(rowHeight);

            newSimTable.setHeight("" + tableHeight + "px");
            // xx
            newSimTable.redraw();
            actionTable.redraw();
        }
    }

    protected float calcTableHeight(float rowHeight) {
        return rowHeight * (rows>PAGE_SIZE?PAGE_SIZE:rows) + ROW_BUFFER;
    }


    public class MyCustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<SimInfo> {

        public MyCustomHeaderBuilder(AbstractCellTable<SimInfo> table,
                                     boolean isFooter) {
            super(table, isFooter);
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean buildHeaderOrFooterImpl() {
            TableRowBuilder row = startRow();
            TableCellBuilder th = row.startTH(); //.colSpan(5);
            DivBuilder div = th.startDiv();


            div.html(SafeHtmlUtils.fromTrustedString("<b>Action(s):</b>"));

            div.end();
            th.endTH();
            row.endTR();


            return true;
        }
    }

    protected void setupSingleSelectionMode() {
        GWT.log("setting up single selection model.");
        getActionDataProvider().getList().clear();
        getActionDataProvider().getList().add(placeHolderSimInfo);
        setSelectionModel(new SingleSelectionModel<SimInfo>(SimManagerWidget2.getKeyProvider()));
        getNewSimTable().setSelectionModel(getSelectionModel());
        getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                final List<SimInfo> list = getActionDataProvider().getList();
                SimInfo mySelection = ((SingleSelectionModel<SimInfo>) getSelectionModel()).getSelectedObject();
                list.clear();
                if (mySelection!=null) {
                    list.add(mySelection);
                } else {
                    list.add(placeHolderSimInfo);
                }
            }
        });
    }
    protected void setupMultiSelectionMode() {
        GWT.log("setting up multiple selection model.");
        // Add a selection model so we can select cells.
        getActionDataProvider().getList().clear();
        getActionDataProvider().getList().add(placeHolderSimInfo);
        setSelectionModel(new MultiSelectionModel<SimInfo>(SimManagerWidget2.getKeyProvider()));

        getNewSimTable().setSelectionModel(getSelectionModel(),
                DefaultSelectionEventManager.createCustomManager(
                        new DefaultSelectionEventManager.CheckboxEventTranslator<SimInfo>() {
                            @Override
                            public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<SimInfo> event) {
                                DefaultSelectionEventManager.SelectAction action = super.translateSelectionEvent(event);
                                if (action.equals(DefaultSelectionEventManager.SelectAction.IGNORE)) {
//                                    selectionModel.clear();
                                    return DefaultSelectionEventManager.SelectAction.TOGGLE;
                                }
                                return action;
//                                return DefaultSelectionEventManager.SelectAction.DEFAULT;
                            }
                        }
                ));


        getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                final List<SimInfo> list = getActionDataProvider().getList();
                Set<SimInfo> mySelection = ((MultiSelectionModel) getSelectionModel()).getSelectedSet();
                list.clear();
                if (mySelection.size()==1) {

                    for (SimInfo simInfo : mySelection) {
                        list.add(simInfo);
                    }

                } else if (mySelection.size()==0 || mySelection.size()>1) {
                    list.add(getPlaceHolderSimInfo());
                }
            }
        });
    }

    public String getTestSession() {
        return testSession;
    }

    public void setTestSession(String testSession) {
        if (testSession!=null)
            this.testSession = testSession;
    }

    public DataGrid<SimInfo> getNewSimTable() {
        return newSimTable;
    }

    public DataGrid<SimInfo> getActionTable() {
        return actionTable;
    }

    public static ProvidesKey<SimInfo> getKeyProvider() {
        return KEY_PROVIDER;
    }

    public SelectionModel<SimInfo> getSelectionModel() {
        return selectionModel;
    }

    public void setSelectionModel(SelectionModel<SimInfo> selectionModel) {
        this.selectionModel = selectionModel;
    }

    public ListDataProvider<SimInfo> getActionDataProvider() {
        return actionDataProvider;
    }

    public SimInfo getPlaceHolderSimInfo() {
        return placeHolderSimInfo;
    }

    public ListDataProvider<SimInfo> getDataProvider() {
        return dataProvider;
    }

    public int getRows() {
        return rows;
    }
}
