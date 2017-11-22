package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract class DataTable<T> extends ResizeComposite implements RequiresResize, ProvidesResize, IsWidget {

    private FlowPanel containerPanel = new FlowPanel();

    DataGrid<T> dataTable = new DataGrid<T>();
    ListDataProvider<T> dataProvider = new ListDataProvider<T>();

    DataGrid<T> actionBtnTable = new DataGrid<T>();
    ListDataProvider<T> actionBtnProvider = new ListDataProvider<T>();

    List<CheckBox> columnsToDisplay = new ArrayList<>();
    List<String> columnNames;

    SelectionModel<T> singleSelectionModel;
    SelectionModel<T> multipleSelectionModel;
    CheckBox multiSelect = new CheckBox("Multiple selection");
    CheckBox diffSelect = new CheckBox("Diff");

    ColumnSortEvent.ListHandler<T> columnSortHandler = new ColumnSortEvent.ListHandler<T>(
            dataProvider.getList());

    abstract void addTableColumns();
    abstract void addActionBtnTableColumns();

    protected static final int ROW_BUFFER = 38;
    int pageSize;
    protected T placeHolderRow;

    private ProvidesKey<T> keyProvider;

    abstract ProvidesKey<T> getKeyProvider();
    abstract void defaultDoubleClickAction(T row);
    abstract int getWidthInPx();
    abstract void setData(List<T> objectRefList);

    public DataTable(List<String> columnNames, int pageSize, T placeHolderRow, boolean displayDiff, boolean displayAction) {
        this.columnNames = columnNames;
        this.pageSize = pageSize;
        this.placeHolderRow = placeHolderRow;
        this.keyProvider = keyProvider;

        initSingleSelectionMode();
        if (displayDiff) {
            diffSelect.setVisible(true);
            multiSelect.setVisible(false);
            initDiffSelectionMode();
        } else {
            diffSelect.setVisible(false);
            multiSelect.setVisible(true);
            initMultiSelectionMode();
        }
        // Begin adding to the containerPanel
        addColumnSelectionCheckboxes();
        addSelectionModeCheckBox();

        addDataTable();

        if (displayAction)
            addActionTable();

        addPager(pageSize);
        // end
    }

    protected void addSelectionModeCheckBox() {

        if (diffSelect.isVisible()) {
            diffSelect.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    /*
                    if (dataTable.getSelectionModel() instanceof SingleSelectionModel) {
                        ((SingleSelectionModel)dataTable.getSelectionModel()).getSelectedSet().clear();
                    } else if (dataTable.getSelectionModel() instanceof  MultiSelectionModel) {
                        ((MultiSelectionModel) dataTable.getSelectionModel()).getSelectedSet().clear();
                    }
                    */
                    if (diffSelect.isEnabled() && diffSelect.getValue()) {
                        initSingleSelectionMode();
                        enableDiffSelect();
                        removeTableColumns();
                        addTableColumns();
                        resizeTable();
                    } else {
                        initMultiSelectionMode();
                      enableSingleSelect();
                      removeTableColumns();
                      addTableColumns();
                      resizeTable();
                    }
                }
            });
        } else {
            multiSelect.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    if (multiSelect.isEnabled() && multiSelect.getValue()) {
                        enableMultiSelect();
                    } else {
                        enableSingleSelect();
                    }
                }

            });
        }

        // Default to single selection model
        dataTable.setSelectionModel(singleSelectionModel);

        containerPanel.add(diffSelect);
        containerPanel.add(multiSelect);
    }

    private void addColumnSelectionCheckboxes() {
        FlowPanel columnSelectionPanel = new FlowPanel();
        for (String columnName : columnNames) {
            CheckBox checkBox = new CheckBox(columnName);
//            checkBox.setValue(true);
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    removeTableColumns();
                    addTableColumns();
                    resizeTable();
                }
            });
            columnsToDisplay.add(checkBox);
            columnSelectionPanel.add(checkBox);
        }

        containerPanel.add(columnSelectionPanel);
    }

    void enableSingleSelect() {
        actionBtnProvider.getList().clear();
        actionBtnProvider.getList().add(placeHolderRow);
        dataTable.setSelectionModel(singleSelectionModel);
    }

    void enableMultiSelect() {
        actionBtnProvider.getList().clear();
        actionBtnProvider.getList().add(placeHolderRow);
        dataTable.setSelectionModel(multipleSelectionModel);
    }

    void enableDiffSelect() {
        actionBtnProvider.getList().clear();
        dataTable.setSelectionModel(multipleSelectionModel);
    }

    protected void initSingleSelectionMode() {
        GWT.log("setting up single selection model.");
        this.singleSelectionModel = new SingleSelectionModel<T>(getKeyProvider());

        singleSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                final List<T> list = actionBtnProvider.getList();
                T mySelection = ((SingleSelectionModel<T>) singleSelectionModel).getSelectedObject();
                list.clear();
                if (mySelection!=null) {
                    list.add(mySelection);
                } else {
                    list.add(placeHolderRow);
                }
            }
        });

    }

    protected void initDiffSelectionMode() {
        GWT.log("setting up diff selection model.");

        multipleSelectionModel = new MultiSelectionModel<T>(getKeyProvider());
        multipleSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                final List<T> actionList = actionBtnProvider.getList();
                Set<T> mySelection = ((MultiSelectionModel) multipleSelectionModel).getSelectedSet();
                if (mySelection.size()==2) {
                    for (T item: mySelection) {
                        actionList.add(item);
                    }
                }
            }
        });
        dataTable.setSelectionModel(multipleSelectionModel, DefaultSelectionEventManager.<T> createCheckboxManager());
    }


    protected void initMultiSelectionMode() {
        GWT.log("setting up multiple selection model.");
        this.multipleSelectionModel = new MultiSelectionModel<T>(getKeyProvider());
        dataTable.setSelectionModel(multipleSelectionModel,
                DefaultSelectionEventManager.createCustomManager(
                        new DefaultSelectionEventManager.CheckboxEventTranslator<T>() {
                            @Override
                            public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<T> event) {
                                DefaultSelectionEventManager.SelectAction action = super.translateSelectionEvent(event);
                                if (action.equals(DefaultSelectionEventManager.SelectAction.IGNORE)) {
                                    return DefaultSelectionEventManager.SelectAction.TOGGLE;
                                }
                                return action;
                            }
                        }
                ));

        multipleSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                final List<T> actionList = actionBtnProvider.getList();
                Set<T> mySelection = ((MultiSelectionModel) multipleSelectionModel).getSelectedSet();
                actionList.clear();
                if (mySelection.size()==1) {
                    for (T item: mySelection) {
                        actionList.add(item);
                    }
                } else if (mySelection.size()==0 || mySelection.size()>1) {
                    actionList.add(placeHolderRow);
                }
            }
        });
    }

    protected void addPager(int pageSize) {
        SimplePager simplePager = new SimplePager();
        simplePager.getElement().getStyle().setMarginTop(5, Style.Unit.PX);
        int marginLeftInPx = 50; // getWidthInPx() * .40
        simplePager.getElement().getStyle().setMarginLeft(marginLeftInPx, Style.Unit.PX);
        simplePager.getElement().getStyle().setMarginBottom(5, Style.Unit.PX);
        simplePager.setDisplay(dataTable);
        simplePager.setPageSize(pageSize);

        containerPanel.add(simplePager);
    }

    boolean columnToBeDisplayedIsChecked(String text) {
        if (columnsToDisplay!=null) {
            for (CheckBox checkBox : columnsToDisplay) {
                if (checkBox.getText().equals(text))
                    return checkBox.getValue();
            }
        }
        return false;
    }


    protected void addDataTable() {
        addColumnSortHandler();

        addTableColumns();
        dataProvider.addDataDisplay(dataTable);

//        dataTable.setWidth("640px");
//        dataTable.setHeight("75px");
        dataTable.setSkipRowHoverCheck(true);
        dataTable.setSkipRowHoverFloatElementCheck(true);
        dataTable.getElement().getStyle().setProperty("wordWrap","break-word");

        containerPanel.add(dataTable);

        setupDataTableResizeHandlers();
    }

    void removeTableColumns() {
        while (dataTable.getColumnCount()>0) {
            dataTable.removeColumn(0);
        }
    }

    protected void addActionTable() {
        addActionBtnTableColumns();
        actionBtnProvider.addDataDisplay(actionBtnTable);

        actionBtnTable.setWidth("500px");
        actionBtnTable.setHeight("75px");
        actionBtnTable.getElement().getStyle().setMarginTop(8, Style.Unit.PX);
        actionBtnTable.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        actionBtnTable.setHeaderBuilder(new MyCustomHeaderBuilder(actionBtnTable, false));

        containerPanel.add(actionBtnTable);
    }

    void addColumnSortHandler() {
        dataTable.addColumnSortHandler(columnSortHandler);
    }

    protected void setupDataTableResizeHandlers() {

        Window.addResizeHandler(new ResizeHandler() {
        @Override
        public void onResize(ResizeEvent event) {
            resizeTable();
        }
        });
        dataTable.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {
            @Override
            public void onLoadingStateChanged(LoadingStateChangeEvent event) {
                if(event.getLoadingState() == LoadingStateChangeEvent.LoadingState.LOADED) {
//                    GWT.log("In onLoaded");
                    int rows = dataTable.getRowCount();
//                    GWT.log("rows: " + rows);
                    if (rows > 0) {
                        int rowHeight = 30;
                        rowHeight = getRowHeight(rowHeight);
//                            GWT.log("rowHeight: " + rowHeight);
                        String tableHeightInPx = null;
                        int pxIdx = -1;
                        try {
                            tableHeightInPx = dataTable.getElement().getStyle().getHeight();
                            pxIdx = tableHeightInPx.indexOf("px");
                        } catch (Exception ex) {
                        }
//                            GWT.log("tableHeight: " + tableHeightInPx);
                        if (tableHeightInPx!=null && pxIdx>-1) {
                            int currentHeight = new Integer(tableHeightInPx.substring(0, pxIdx)).intValue();
//                                GWT.log("currentHeight: " + currentHeight);

                            float estimatedHeight = calcTableHeight(rowHeight);
                            if (estimatedHeight != currentHeight) {
                                dataTable.setWidth(calcTableWidth() + "px");
                                dataTable.setHeight(estimatedHeight + "px");
                                    GWT.log("Table resize complete.");
                            }
                        } else {
                            dataTable.setWidth(calcTableWidth() + "px");
                            dataTable.setHeight("100px");
//                                GWT.log("Initial table resize complete.");
                        }
                    }

                }
            }
        });
    }

    public void resizeTable() {
        dataTable.setWidth(calcTableWidth() + "px");
        dataTable.setHeight(guessTableHeight() + "px");
        dataTable.redrawFooters();
    }

    public float guessTableHeight() {
        return calcTableHeight(getRowHeight(40));
    }

    TableRowElement tableRowElement = null;
    protected int getRowHeight(int defaultRowHeight) {
        try {
            if (dataTable.getRowElement(0)!=null)
                return dataTable.getRowElement(0).getClientHeight();
        } catch (Exception ex) {
//            GWT.log("getRowHeight: " + ex.toString());
//            GWT.log("dataTab isVisible? " + dataTable.isVisible());
//            GWT.log("Exception. data rows: " + dataTable.getRowCount());
        }
        return defaultRowHeight;
    }


    public class MyCustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<T> {

        public MyCustomHeaderBuilder(AbstractCellTable<T> table,
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

    public void addDoubleClickAction() {
        dataTable.addCellPreviewHandler(new CellPreviewEvent.Handler<T>() {
            Timer singleClickTimer;
            int clickCount = 0;
            @Override
            public void onCellPreview(CellPreviewEvent<T> cellPreviewEvent) {

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
                        DataGrid<T> grid = (DataGrid<T>) cellPreviewEvent.getSource();
                        int row = grid.getKeyboardSelectedRow();
                        T item = grid.getVisibleItem(row);
                        defaultDoubleClickAction(item);
                        if (dataTable.getSelectionModel() instanceof SingleSelectionModel)
                            dataTable.getSelectionModel().setSelected(item, true);
                        GWT.log("action list size: " + actionBtnProvider.getList().size());

                    }
                }
            }
        });
    }



    protected float calcTableWidth() {
        int containerWidth;

        try {
            containerWidth = (int)(getWidthInPx() * .90); // Window.getClientWidth())
        } catch (Exception ex) {
            GWT.log("calcTableWidth error: " + ex.toString());
            containerWidth = (int)(Window.getClientWidth() * .80);
        }

        return containerWidth;
    }


    protected float calcTableHeight(float rowHeight) {
        int rows = dataTable.getRowCount();
        float estimatedHeight = rowHeight * (rows>pageSize?pageSize:rows) + ROW_BUFFER;
//        GWT.log("estimatedHeight is: " + estimatedHeight);
        return estimatedHeight;
    }
    @Override
    public Widget asWidget() {
        return containerPanel;
    }


    SafeHtml getImgHtml(String iconFilePath, String title, boolean supportsMultiple) {
//        Set<SimInfo> mySelection = ((MultiSelectionModel) newSimTable.getSelectionModel()).getSelectedSet();
        Set<T> mySelection = null;
        SelectionModel selectionModel = dataTable.getSelectionModel();
        if (selectionModel instanceof MultiSelectionModel) {
            mySelection = ((MultiSelectionModel) selectionModel).getSelectedSet();
//        Window.alert("selection size is " + mySelection.size() + "; icon is " + iconFilePath + "; supportsMultiple " + supportsMultiple);
            if (mySelection!=null) {

                if (mySelection.size() == 1 || (mySelection.size() > 1 && supportsMultiple))
                    return new SafeHtmlBuilder().appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"" + title + "\" src=\"" + iconFilePath + "\">").toSafeHtml();
                else
                    return new SafeHtmlBuilder().appendHtmlConstant("<input type=\"image\" style=\"width: 24px; height: 24px; opacity:0.5\" src=\"" + iconFilePath + "\" border=0 disabled/>").toSafeHtml();
            }
        }
        else if (selectionModel instanceof SingleSelectionModel) {
//            final List<SimInfo> list =
            // actionDataProvider.getList();
            T selectedObj =  (T)((SingleSelectionModel) selectionModel).getSelectedObject();
            if (selectedObj==null) {
                return new SafeHtmlBuilder().appendHtmlConstant("<input type=\"image\" style=\"width: 24px; height: 24px; opacity:0.5\" src=\"" + iconFilePath + "\" border=0 disabled/>").toSafeHtml();
            } else {
                return new SafeHtmlBuilder().appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" title=\"" + title +"\" src=\"" + iconFilePath + "\">").toSafeHtml();
            }
        }
        return new SafeHtmlBuilder().appendHtmlConstant("<input type=\"image\" style=\"width: 24px; height: 24px; opacity:0.5\" src=\"" + iconFilePath + "\" border=0 disabled/>").toSafeHtml();
    }

}
