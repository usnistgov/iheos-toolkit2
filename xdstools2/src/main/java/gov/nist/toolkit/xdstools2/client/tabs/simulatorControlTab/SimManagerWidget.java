package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.ClickHandlerData;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionInstancesCommand;
import gov.nist.toolkit.xdstools2.client.tabs.SimulatorMessageViewTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od.OddsEditTab;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by skb1 on 08/14/17.
 */
public class SimManagerWidget extends Composite {

    CommandContext commandContext;
    private SimulatorControlTab hostTab;
    private FlowPanel containerPanel = new FlowPanel();

    private CellTable<SimInfo> newSimTable = new CellTable<SimInfo>();
    // Create a data provider.
    private ListDataProvider<SimInfo> dataProvider = new ListDataProvider<SimInfo>();

    public SimManagerWidget() {
        // TODO
        // initWidget
    }

    public SimManagerWidget(CommandContext commandContext, SimulatorControlTab hostTab) {
        this.commandContext = commandContext;
        this.hostTab = hostTab;

        buildTableColumns();
        containerPanel.add(newSimTable);
        SimplePager simplePager = new SimplePager();
        simplePager.setDisplay(newSimTable);
        simplePager.setPageSize(50);
        containerPanel.add(simplePager);


        initWidget(containerPanel);
    }

    protected void popCellTable(List<SimulatorConfig> configs, List<SimulatorStats> statsList) {
        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        final List<SimInfo> list = dataProvider.getList();

        if (list!=null && configs!=null && list.size()!=configs.size()) {
            list.clear();
            int row = 0;
            for (SimulatorConfig config : configs) {
                final SimInfo simInfo = new SimInfo(config, statsList.get(row));
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


                new GetTransactionInstancesCommand(){
                    @Override
                    public void onComplete(List<TransactionInstance> result) {
                        if (result!=null && result.size()>0)  {




                            for (int idx=0; idx<SimInfo.TOP_TRANSACTION_CT; idx++) {
                                if (result.size()>idx) {
                                    simInfo.getTopThreeTransInstances().add(TransactionInstance.copy(result.get(idx)));
                                    try {
                                        newSimTable.redraw();
                                    } catch (Throwable t) {
                                    }
                                }
                            }
                        }

                    }
                }.run(new GetTransactionRequest(commandContext,simInfo.getSimulatorConfig().getId(),"",null));



                list.add(simInfo);
                row++;
            }
        }


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

        SafeHtmlBuilder logIconImgHtml = new SafeHtmlBuilder();
        logIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" src=\"icons2/log-file-format-symbol.png\">");
        Column<SimInfo, SimInfo> logActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>(logIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(SimInfo simInfo) {
                        SimulatorConfig config = simInfo.getSimulatorConfig();
                        SimulatorMessageViewTab viewTab = new SimulatorMessageViewTab();
                        viewTab.onTabLoad(config.getId());
                    }
                })) {
                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }
                };

        SafeHtmlBuilder pidIconImgHtml = new SafeHtmlBuilder();
        pidIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" src=\"icons2/id.png\">");
        Column<SimInfo, SimInfo> idActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>(pidIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(SimInfo simInfo) {
                        SimulatorConfig config = simInfo.getSimulatorConfig();
                        PidEditTab editTab = new PidEditTab(config);
                        editTab.onTabLoad(true, "PIDEdit");
                    }
                })) {
                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }
                };

        Column<SimInfo, SimInfo> editActionCol = getButtonColumn();

        SafeHtmlBuilder trashBinIconImgHtml = new SafeHtmlBuilder();
        trashBinIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\"  title=\"Delete\" src=\"icons2/garbage.png\">");
        Column<SimInfo, SimInfo> trashBinActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>(trashBinIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(final SimInfo simInfo) {
                        SimulatorConfigElement ele = simInfo.getSimulatorConfig().getConfigEle(SimulatorProperties.locked);
                        boolean locked = (ele == null) ? false : ele.asBoolean();
                        if (locked) {
                            if (PasswordManagement.isSignedIn) {
                                doDelete(simInfo.getSimulatorConfig());
                            }
                            else {
                                PasswordManagement.addSignInCallback(
                                        new AsyncCallback<Boolean>() {

                                            public void onFailure(Throwable ignored) {
                                            }

                                            public void onSuccess(Boolean ignored) {
                                                doDelete(simInfo.getSimulatorConfig());
                                            }

                                        }
                                );

                                new AdminPasswordDialogBox(containerPanel);

                                return;
                            }
                        } else {
                            doDelete(simInfo.getSimulatorConfig());
                        }
                    }

                    private void doDelete(SimulatorConfig config) {
                        VerticalPanel body = new VerticalPanel();
                        body.add(new HTML("<p>Delete " + config.getId().toString() + "?</p>"));
                        Button actionButton = new Button("Yes");
                        actionButton.addClickHandler(
                                new ClickHandlerData<SimulatorConfig>(config) {
                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        SimulatorConfig config = getData();
                                        DeleteButtonClickHandler handler = new DeleteButtonClickHandler(hostTab, config);
                                        handler.delete(true);
                                    }
                                }
                        );
                        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                        safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" height=\"16\" width=\"16\"/>");
                        safeHtmlBuilder.appendHtmlConstant("Confirm Delete Simulator");
                        new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionButton);
                    }
                })) {
                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }


                };

        SafeHtmlBuilder downloadIconImgHtml = new SafeHtmlBuilder();
        downloadIconImgHtml.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Download\" src=\"icons2/download.png\">");
        Column<SimInfo, SimInfo> downloadActionCol =
                new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>(downloadIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
                    @Override
                    public void execute(SimInfo simInfo) {
                        Window.open("siteconfig/"+simInfo.getSimulatorConfig().getId().toString(), "_blank","");
                    }
                })) {
                    @Override
                    public SimInfo getValue(SimInfo simInfo) {
                        return simInfo;
                    }
                };





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
        newSimTable.addColumn(logActionCol, "Action");
        newSimTable.addColumn(editActionCol,"");
        newSimTable.addColumn(trashBinActionCol,"");
        newSimTable.addColumn(downloadActionCol,"");

//        newSimTable.addColumn(

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(newSimTable);


    }

    private CompositeCell getButtonsCell() {
        List<HasCell<SimInfo, ?>> cells = new LinkedList<>();

        HasCell<SimInfo,SimInfo> normalEdit =   new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
//                Window.alert("You clicked " + object.getSimulatorConfig().getDefaultName());

//                loadSimStatus();
                SimulatorConfig config = object.getSimulatorConfig();

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

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //

                if (!ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType()) ) {
                    SafeHtmlBuilder mySb = new SafeHtmlBuilder();

                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Edit\" src=\"icons2/edit.png\">");

                    sb.append(mySb.toSafeHtml());
                }

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


                if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType()) ) {
                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Edit RG config\"  src=\"icons2/edit-rg.png\">");
                    sb.append(mySb.toSafeHtml());
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

                if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType()) ) {
                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"Edit OD config\" src=\"icons2/edit-od.png\">");
                    sb.append(mySb.toSafeHtml());
                }

            }
        };

        cells.add(rgEdit);
        cells.add(rgOdEdit);
        cells.add(normalEdit);


        CompositeCell<SimInfo> compositeCell = new CompositeCell<>(cells);

        return compositeCell;
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
}
