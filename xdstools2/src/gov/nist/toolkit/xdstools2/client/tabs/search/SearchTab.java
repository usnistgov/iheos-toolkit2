package gov.nist.toolkit.xdstools2.client.tabs.search;


import gov.nist.toolkit.repository.simple.search.client.Asset;
import gov.nist.toolkit.repository.simple.search.client.ContextSupplement;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria.Criteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm.Operator;
import gov.nist.toolkit.repository.simple.search.client.SimpleData;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;



public class SearchTab extends GenericQueryTab {
	
	private static final String PROP_VALUE = "propValue";
	private static final String PROP_NAME = "propName";
	private static final String OPERATOR = "Operator";
	private static final String CRITERIA = "Criteria";
	
	private static final int NODETAG = 32768;
	Integer callCt = 0;
	Label lblTxt = new Label();
	SimpleData sd = new SimpleData();
	SearchCriteria sc = new SearchCriteria(Criteria.AND);
	
	FlexTable msgs = new FlexTable();
	FlexTable grid = new FlexTable();
	FlexTable existingGrid = new FlexTable();
	DialogBox db = new DialogBox();
	
	
	String required        = new String("*Required fields");



	public ListBox reposLeft = new ListBox(true);
	public Button moveRight = new Button("&rArr;");
	public Button moveLeft = new Button("&lArr;");
	public ListBox reposRight = new ListBox(true);
	public Button moveUp = new Button("Before");
	public Button moveDown = new Button("After");
	protected ArrayList<String> propNames = new ArrayList<String>();
	
	static String DEFAULTWIDTH = "30em";
	static String DEFAULTTITLEWIDTH = "15em";


	VerticalPanel criteriaPanel = new VerticalPanel();
	VerticalPanel scPanel = new VerticalPanel();
	VerticalPanel resultPanel = new VerticalPanel();
	
    RadioButton simpleQuery = new RadioButton("queryMode", "Simple");
    RadioButton advancedQuery = new RadioButton("queryMode", "Advanced");

	
	public SearchTab(BaseSiteActorManager siteActorManager) {
		super(new NullSiteActorManager());
	}

	public SearchTab() {
		super(new NullSiteActorManager());
		moveUp.setWidth("4em");
		moveDown.setWidth("4em");
	}


	@Override
	public void onTabLoad(TabContainer container, boolean select,
			String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		disableEnvMgr();
		disableTestSesMgr();
		
		container.addTab(topPanel, "Search", select);
		addCloseButton(container,topPanel, null);


		HTML title = new HTML();
		title.setHTML("<h2>Artifact Repository Search</h2>");
		topPanel.add(title);
		
//		topPanel.add(new HTML("How to use Search feature:"));
//		topPanel.add(new HTML("<p>" + required));

		grid.setCellSpacing(20);

		
		toolkitService.getRepositoryDisplayTags(reposSetup);
		toolkitService.getIndexablePropertyNames(propsSetup);
		
		topPanel.add(reposList());
		

	}
	
	
	AsyncCallback<Map<String, String>> reposSetup = new AsyncCallback<Map<String, String>> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Repositories could not be loaded: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(Map<String, String> m) {
			
			for (Map.Entry<String, String> entry : m.entrySet()) {				
				reposLeft.addItem(entry.getKey().substring(0,3) + "..." + entry.getKey().substring(24) + " - " + entry.getValue(), entry.getKey() );				
			}
			
			setRepositorySelectorCbs();
			
		}
	};

	AsyncCallback<List<String>> propsSetup = new AsyncCallback<List<String>> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("No indexeable properties found: It is possible Asset Type's are not configured. propNames could not be loaded: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(List<String> props) {
			propNames.addAll(props);

			topPanel.add(searchPanel());
			topPanel.add(scPanel);
			
		}
	};

	
	AsyncCallback<List<Asset>> searchResults = new AsyncCallback<List<Asset>> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("propNames could not be loaded: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(List<Asset> result) {
			resultPanel.clear();
			
			resultPanel.add(new HTML("&nbsp;"));
			
			FlexTable resultFt = new FlexTable();
			int row = 1;
			
				
				try {
					
					if (result!=null && result.size()>0) {

						
						resultFt.setWidget(0, 0, new HTML("Repository Id"));
						resultFt.setWidget(0, 1, new HTML("Asset Id"));
						resultFt.setWidget(0, 2, new HTML("Display Name"));
						resultFt.setWidget(0, 3, new HTML("Description"));
						
	
						for (Asset a : result) {
							resultFt.setWidget(row,0,  new HTML(a.getRepId()));
							resultFt.setWidget(row,1,  new HTML(a.getAssetId()));
							resultFt.setWidget(row,2,  new HTML(a.getDisplayName()));
							resultFt.setWidget(row++,3,  new HTML(a.getDescription()));
						}
					}	
					
				} catch (Exception e) {
					new PopupMessage(e.toString());
				}
				
				resultFt.setWidget(row, 0, new HTML(""+ result.size() + " record(s) found."));
				if (result.size()>0) {
					resultFt.setCellSpacing(3);
					resultFt.getFlexCellFormatter().setStyleName(0, 0, "searchCriteriaGroup");
					resultFt.getFlexCellFormatter().setStyleName(0, 1, "searchCriteriaGroup");
					resultFt.getFlexCellFormatter().setStyleName(0, 2, "searchCriteriaGroup");
					resultFt.getFlexCellFormatter().setStyleName(0, 3, "searchCriteriaGroup");					
					
					resultFt.getFlexCellFormatter().setColSpan(row, 0, 4);
					
					resultFt.setWidth(
							scPanel.getWidget(0).getElement().getStyle().getWidth()
					);
					
				}
				
				resultPanel.add(resultFt);
				topPanel.add(resultPanel);
				
//				scPanel.add(new HTML("&nbsp;"));
//				scPanel.add(resultFt);


		}
	};

	protected void setRepositorySelectorCbs() {
		
		moveRight.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				moveItem(reposLeft, reposRight);
			}
		});
		
		moveLeft.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				moveItem(reposRight, reposLeft);
			}
		});
		
		moveUp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				moveVertical(reposRight, -1);
			}
		});
		
		moveDown.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				moveVertical(reposRight, 2);
			}
		});
	}

	protected void moveVertical(ListBox l, int order) {
		int firstIdx = l.getSelectedIndex();
		int itemCt = l.getItemCount();
		
		if (itemCt<2) return;
		if (firstIdx==0 && order==-1) return;
		if (firstIdx==itemCt-1 && order>1) return;

		int newIdx = firstIdx + order;
		
		if (newIdx<=itemCt) {
			l.insertItem(l.getItemText(firstIdx), l.getValue(firstIdx), newIdx);
			
			if (order>1) {
				l.setSelectedIndex(newIdx);
				l.removeItem(firstIdx);
			} else {				
				l.removeItem(firstIdx+1);
				l.setSelectedIndex(newIdx);
			}
			
						
		}

	}

	protected void moveItem(ListBox l, ListBox r) {
		int firstIdx = l.getSelectedIndex();   
		if (firstIdx > -1) {
			int itemCt = l.getItemCount();
			
			for (int cx=firstIdx; cx<itemCt; cx++) {
				if (l.isItemSelected(cx)) {
					r.addItem(l.getItemText(cx), l.getValue(cx));								
				}
			}
			
			for (int cx=itemCt-1; cx>-1; cx--) {
				if (l.isItemSelected(cx)) {
					l.removeItem(cx);
				}
			}
		}
		
	}
	protected VerticalPanel reposList() {
		VerticalPanel panel = new VerticalPanel();
		FlexTable grid = new FlexTable();

		panel.add(new HTML("<h3>Select Repositories</h3>"));
		panel.add(new HTML("  " ));

		panel.add(grid);

		int row = 0;
		int col = 0;
		HTML h;
		
		reposLeft.setVisibleItemCount(5);
		reposLeft.setWidth(DEFAULTWIDTH);		
		
		reposRight.setVisibleItemCount(5);
		reposRight.setWidth(DEFAULTWIDTH);
		
		
		grid.setWidget(row, col++, new HTML("Available Repositories"));
		grid.setWidget(row, col++, new HTML("&nbsp;"));
		grid.setWidget(row, col++, new HTML("Selected Repositories"));
		grid.setWidget(row++, col, new HTML("Search Order"));
		
		col=0;
		
		grid.setWidget(row, col++, reposLeft);
		
		FlexTable miniTable = new FlexTable();		
		
		miniTable.setWidget(0, 0, moveRight);
		miniTable.setWidget(1, 0, moveLeft);
		
		grid.setWidget(row, col++, miniTable);
				
		
		grid.setWidget(row, col++, reposRight);
		
		
		
		miniTable = new FlexTable();
		miniTable.setWidget(0, 0, moveUp);
		miniTable.setWidget(1, 0, moveDown);
		
		grid.setWidget(row, col++, miniTable);
		
		
		grid.setWidget(++row, 0, lblTxt);
		grid.getFlexCellFormatter().setColSpan(row, 0, col);

		grid = new FlexTable();
		panel.add(grid);
		
		return panel;
	}
	

	int stGridRow = 0;
	FlexTable mainTable =  new FlexTable();				
	FlexTable stGrid = new FlexTable();
	
	protected SearchCriteria getScByPath(String path, int limit)  {		
			
		if (path==null || "".equals(path)) {
			return sc;
		}
		
		String ids[] = path.split("\\.");
		
		SearchCriteria scObj = sc;
		if (Math.abs(limit) > ids.length) {
			return null;
		}
				
		for (int cx=0; cx< (ids.length + (limit)); cx++) {
			int index = Integer.parseInt(ids[cx]);
			
			if (isLeafNode(index)) {
				return scObj;
			}
			
			scObj = scObj.getSearchCriteria().get(index);
		}
		return scObj;
	}

	/**
	 * @param index
	 * @return
	 */
	protected boolean isLeafNode(int index) {
		return (index & NODETAG) == NODETAG;
	}
	
	protected int getLeafId(int index) {
		return (index ^ NODETAG);
	}
	
	protected String makeLeafNodeId(int index) {
		return "" + (NODETAG | index);
	}
	
	protected int getLeafNodeId(String path) {
		String ids[] = path.split("\\.");
		
		int leaf = Integer.parseInt(ids[ids.length-1]);
		if (isLeafNode(leaf)) { 
			return getLeafId(leaf);
		} else {
			new PopupMessage("Error: Not a leaf node " + path);
		}
		return -1;
	}
	
	protected void removeLeafNode(String path) {
		SearchCriteria scObj = getScByPath(path);
		scObj.getSearchTerms().remove(getLeafNodeId(path));
		redrawTable();
	}
	
	protected void setOpSelector(int index, String path) {
		Operator e = Operator.values()[index];
		getSt(path).setOperator(e);		
	}
	
	protected void setPropNameSelector(int index, String path) {
		String s = getPropNames().get(index);
		getSt(path).setPropName(s);		
	}
	
	protected void setStValue(String value, String path) {	
		getSt(path).setValues(new String[]{value});		
	}
	
	private SearchTerm getSt(String path) {
		int leafNodeId =  getLeafNodeId(path);
		SearchCriteria scObj = getScByPath(path);
		return scObj.getSearchTerms().get(leafNodeId);
	}
	
	protected void setCSelector(int index, String path) {
		Criteria e = Criteria.values()[index];
		SearchCriteria scObj = getScByPath(path);
		scObj.setCriteria(e);		
	}
	
	protected SearchCriteria getScByPath(String path) {
		return getScByPath(path,0);
	}
	
	
	
	protected void addNewSearchTerm(String id) {

		// Assignment
		// assign by id parse & attach to the right obj
		
		SearchCriteria scObj = getScByPath(id);
		
		SearchTerm st = new SearchTerm(getPropNames().get(0),SearchTerm.Operator.EQUALTO,"");
		scObj.append(st);

		redrawTable();
	}

	/**
	 * 
	 */
	public void redrawTable() {
		scPanel.clear();
		printTable(sc, "");
		// topPanel.add(scPanel);
	}

	/**
	 * 
	 */
	private void enterFixedBuilderMode() {
		// resultPanel.clear();
		simpleQuery.setEnabled(false);
		advancedQuery.setEnabled(false);
	}
	
	protected void addNewGroup(String id) {
		

		// Assignment
		// assign by id parse & attach to the right obj
		
		
		if ("".equals(id)) {
			SearchCriteria parent = new SearchCriteria(Criteria.AND);
			
			SearchCriteria child2 = new SearchCriteria(Criteria.AND);
			
			parent.append(sc);
			parent.append(child2);
			sc = parent;
						
		} else if (id!=null) {
			SearchCriteria parent = new SearchCriteria(Criteria.AND);
			
			SearchCriteria scObj = getScByPath(id);
			SearchCriteria child2 = new SearchCriteria(Criteria.AND);
			
			parent.append(scObj);
			parent.append(child2);
			
			
			 
			if (id.length()==1) { // off the root node ex. "0"
				sc.getSearchCriteria().remove(Integer.parseInt(id));
				// sc.getSearchCriteria().add(parent);
				
				sc.getSearchCriteria().add(Integer.parseInt(id), parent);
				
			} else {
				SearchCriteria superObj = getScByPath(id, -1);
				
				String[] ids =  id.split("\\.");
				int nodeIdx = Integer.parseInt(ids[ids.length-1]);
				superObj.getSearchCriteria().remove(nodeIdx);
				superObj.getSearchCriteria().add(nodeIdx, parent);
			}
			
		}
		
		redrawTable();
				
		
	}
	
	protected void appendCriteria(String id) {
				
		
		SearchCriteria scObj = getScByPath(id,-1);
		
		if (sc.equals(scObj) && scObj.getSearchCriteria().size()==0) {
			SearchCriteria parent = new SearchCriteria(Criteria.AND);
			
			SearchCriteria child2 = new SearchCriteria(Criteria.AND);
			
			parent.append(sc);
			parent.append(child2);
			sc = parent;
		} else {
			SearchCriteria scNew = new SearchCriteria(Criteria.AND);
			scObj.append(scNew);
		}	
				
		

		redrawTable();

	}

	/**
	 * 
	 */
	public void debugTxt(Object o) {
		// lblTxt.setText("setting " + o.toString());
		// new PopupMessage(o.toString());
	}
	
	protected void printTable(SearchCriteria sc, String ancestorId) {
	
		if (sc.getSearchTerms().isEmpty()) {
			int stRow=0;
			FlexTable stGroup = new FlexTable();
			// stGroup.addStyleName("searchCriteriaGroup");

			
			FlexTable cmdGroup =  new FlexTable();
			
			Button addNewStBtn = new Button(newSeachTermTxt);
			Button addNewGroup = new Button(newCriteriaGroupTxt);
			Button appendCriteria = new Button(appendCriteriaTxt);
			Button removeGroupBtn = newRemoveGroupBtn(ancestorId);
			
			if (simpleQuery.getValue()) {
				addNewGroup.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);				
				appendCriteria.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
				removeGroupBtn.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
			}
			
	
			addNewGroup.addClickHandler(newGroupHandler(ancestorId));				
			appendCriteria.addClickHandler(appendCriteriaHandler(ancestorId));				
			addNewStBtn.addClickHandler(newSearchTermHandler(ancestorId));
			
			// search terms can only be added to free-standing sc's
			if (sc.getSearchCriteria()!=null && sc.getSearchCriteria().size()>0) {			
				addNewStBtn.setVisible(false);
			} else  {
				addNewStBtn.setVisible(true);
			}
			
			if (sc.getSearchCriteria()!=null && sc.getSearchCriteria().size()>0) {				
				appendCriteria.setVisible(true);
				appendCriteria.getElement().getStyle().setDisplay(Style.Display.BLOCK);
			} else  {
				appendCriteria.setVisible(false);
				appendCriteria.getElement().getStyle().setDisplay(Style.Display.NONE);
			}	
			
			ListBox cSelector = getCSelector();
			cSelector.setSelectedIndex(sc.getCriteria().ordinal());
			cSelector.addChangeHandler(new CSelectorChangeHandler(CRITERIA, ancestorId));
		
			int col=0;
			cmdGroup.setWidget(0, col++, cSelector);
			cmdGroup.setWidget(0, col++, addNewStBtn);
			cmdGroup.setWidget(0, col++, addNewGroup);
			
			if (appendCriteria.isVisible()) {
				cmdGroup.setWidget(0, col++, appendCriteria);
			}
			cmdGroup.setWidget(0, col++, removeGroupBtn);
					
			
			stGroup.getFlexCellFormatter().setColSpan(0, 0, 3); // stGroup is just empty container here
			
			stGroup.setWidget(stRow++, 0, cmdGroup);
									
			stGroup.addStyleName("searchCriteriaGroup");
			setGroup(ancestorId, stGroup);
			
		
		} else {
			
			FlexTable stGroup = new FlexTable();
			if (sc.getSearchTerms()!=null && !sc.getSearchTerms().isEmpty()) {

				int stRow=0;
			
				stGroup.addStyleName("searchCriteriaGroup");

				
				FlexTable cmdGroup =  new FlexTable();
				
				Button addNewStBtn = new Button(newSeachTermTxt);
				Button addNewGroup = new Button(newCriteriaGroupTxt);
				Button appendCriteria = new Button(appendCriteriaTxt);
				Button removeGroupBtn = newRemoveGroupBtn(ancestorId);
				
				if (simpleQuery.getValue()) {						
					addNewGroup.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);				
					appendCriteria.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
					appendCriteria.getElement().getStyle().setDisplay(Style.Display.NONE);
					removeGroupBtn.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
				} else {
					
					if (sc.getSearchCriteria()!=null && sc.getSearchCriteria().size()>0) {				
						appendCriteria.setVisible(true);
						appendCriteria.getElement().getStyle().setDisplay(Style.Display.BLOCK);
					} else  {
						appendCriteria.setVisible(false);
						appendCriteria.getElement().getStyle().setDisplay(Style.Display.NONE);
					}					
				}


				
				
				addNewGroup.addClickHandler(newGroupHandler(ancestorId));				
				appendCriteria.addClickHandler(appendCriteriaHandler(ancestorId));				
				addNewStBtn.addClickHandler(newSearchTermHandler(ancestorId));
				
				
				if (ancestorId!=null && "".equals(ancestorId)) {
					addNewStBtn.getElement().setId(newSeachTermTxt);
					addNewGroup.getElement().setId(newCriteriaGroupTxt);
					appendCriteria.getElement().setId(appendCriteriaTxt);
				} 
				
				
				ListBox cSelector = getCSelector();
				cSelector.setSelectedIndex(sc.getCriteria().ordinal());
				cSelector.addChangeHandler(new CSelectorChangeHandler(CRITERIA, ancestorId));


				
				int col=0;
				cmdGroup.setWidget(0, col++, cSelector);				
				cmdGroup.setWidget(0, col++, addNewStBtn);
				cmdGroup.setWidget(0, col++, addNewGroup);
				
				if (appendCriteria.isVisible()) {
					cmdGroup.setWidget(0, col++, appendCriteria);	
				}				
				cmdGroup.setWidget(0, col++, removeGroupBtn);
						
				
				stGroup.getFlexCellFormatter().setColSpan(0, 0, 4);
				stGroup.setWidget(stRow++, 0, cmdGroup);
				

				
				stGroup.setWidget(stRow, 0, new HTML("Property Name"));
				stGroup.setWidget(stRow, 1, new HTML("Operator"));
				stGroup.setWidget(stRow++, 2, new HTML("Value"));
				

				int leafIndex = 0;
				for (SearchTerm st : sc.getSearchTerms()) {
					
					ListBox stPn = new ListBox();
					ListBox stOp = new ListBox();
					TextBox stTxt = new TextBox();
					
					stTxt.setWidth(DEFAULTWIDTH);
					
					
					String nodeId = makeLeafNodeId(leafIndex++);
					
					if (!"".equals(ancestorId)) {
						nodeId = ancestorId + "." + nodeId;
					}
					
					stPn.getElement().setId(nodeId); // assign to the delete button
					int pnIdx = 0;
					
					for (int cx=0; cx<getPropNames().size();cx++) {
						if (getPropNames().get(cx).equals(st.getPropName())) {
							pnIdx = cx; 
						}
						stPn.addItem(getPropNames().get(cx));
					}					
					stPn.setSelectedIndex(pnIdx);
					stPn.addChangeHandler(new CSelectorChangeHandler(PROP_NAME, nodeId));					
					
					for (SearchTerm.Operator e : SearchTerm.Operator.values()) {
						stOp.addItem(e.toString());
					}					
					stOp.setSelectedIndex(st.getOperator().ordinal());
					stOp.addChangeHandler(new CSelectorChangeHandler(OPERATOR, nodeId));
					
					if (st.getValues()!=null && st.getValues().length>0) {
						stTxt.setValue(st.getValues()[0]);
					}
					stTxt.addChangeHandler(new CSelectorChangeHandler(PROP_VALUE, nodeId));
					
					
					Button remove = new Button("Remove");
					remove.addClickHandler(new ContextSupplement<String>(nodeId) {
						
						public void onClick(ClickEvent event) {
							debugTxt("remove " + getParameter());				
							removeLeafNode(getParameter());
						}
					});

					
							
					stGroup.setWidget(stRow, 0, stPn);
					stGroup.setWidget(stRow, 1, stOp);
					stGroup.setWidget(stRow, 2, stTxt);
					stGroup.setWidget(stRow++, 3, remove);
				
				}
			
				
				setGroup(ancestorId, stGroup);
				
				// mainTable.setWidget(0, 0, stGroup);
									
			  	  
			}

	}
		
		if (sc.getSearchCriteria()!=null) {
			Integer rootIndex = new Integer(0);
			for (SearchCriteria criteria : sc.getSearchCriteria()) {
				String groupId = rootIndex.toString();
				if (ancestorId != null && !"".equals(ancestorId)) {
					groupId = ancestorId + "." + rootIndex.toString();
				}
				printTable(criteria, groupId );
				rootIndex++;
			}
		}
		
		
	
	}

	/**
	 * @param cmdGroup
	 */
	private Button newRemoveGroupBtn(String path) {
		
			Button removeGroupBtn = new Button(removeGroupTxt);			
			
			
			if ("".equals(path)) {
				removeGroupBtn.getElement().setId(removeGroupTxt);
			}
			
			
			
			removeGroupBtn.addClickHandler(new ContextSupplement<String>(path) {
				
				public void onClick(ClickEvent event) {
					HorizontalPanel hpDialog = new HorizontalPanel();
						
						FlexTable ft = new FlexTable();
						Button yesBtn = new Button("Yes");
						Button noBtn = new Button("No");
						
						ft.setWidget(0, 0, yesBtn);
						ft.setWidget(0, 1, noBtn);
						
						hpDialog.add(new HTML("Remove group?"));
						hpDialog.add(new HTML("&nbsp;"));
						hpDialog.add(ft);
						
						db.clear();
						db.setWidget(hpDialog);
						
						Button src = (Button)event.getSource();
						db.setPopupPosition(src.getAbsoluteLeft(),src.getAbsoluteTop());
						db.show();
						
						yesBtn.addClickHandler(new ContextSupplement<String>(getParameter()) {
							
							public void onClick(ClickEvent event) {

							//	new PopupMessage("" + getParameter());
								
								SearchCriteria scPar = getScByPath(getParameter(),-1);
								SearchCriteria scObj = getScByPath(getParameter());
								
								
								
								if (!scPar.equals(scObj)) {
									scPar.getSearchCriteria().remove(scObj);							
								} else {
									resetSearchCriteria();
								}
								db.hide();
								redrawTable();
								
							}	
						});
						
						noBtn.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								db.hide();
							}
						});
					
						
				}
			});
			
		return removeGroupBtn;
	}

	/**
	 * @param ancestorId
	 * @return
	 */
	protected ContextSupplement<String> newSearchTermHandler(String ancestorId) {
		return new ContextSupplement<String>(ancestorId) {
		
			public void onClick(ClickEvent event) {
					debugTxt(getParameter());
					enterFixedBuilderMode();
					addNewSearchTerm(getParameter());
			}
		};
	}

	/**
	 * @param ancestorId
	 * @return
	 */
	protected ContextSupplement<String> appendCriteriaHandler(String ancestorId) {
		return new ContextSupplement<String>(ancestorId) {
			
			public void onClick(ClickEvent event) {
					debugTxt(getParameter());
					enterFixedBuilderMode();
					appendCriteria(getParameter());
			}
		};
	}

	/**
	 * @param ancestorId
	 * @return
	 */
	protected ContextSupplement<String> newGroupHandler(String ancestorId) {
		return new ContextSupplement<String>(ancestorId) {
			
			public void onClick(ClickEvent event) {
					debugTxt(getParameter());
					enterFixedBuilderMode();
					addNewGroup(getParameter());
			}
		};
	}

	/**
	 * @return
	 */
	private ListBox getCSelector() {
		ListBox cSelector = new ListBox();
		for (SearchCriteria.Criteria e : SearchCriteria.Criteria.values()) {
			cSelector.addItem(e.toString());
		}		

		return cSelector;
	}

	/**
	 * @param ancestorId
	 * @param stGroup
	 */
	private void setGroup(String ancestorId, FlexTable stGroup) {
		if (ancestorId!=null && !"".equals(ancestorId)) {
			int ancestorCt = ancestorId.split("\\.").length;
			
			if (ancestorCt>0) {
				
				Element e = DOM.getElementById(ancestorId);
				if (e!=null) {
					e.setAttribute("disabled", "true");
				}
				
				FlexTable stNestedGroup = new FlexTable();
				stNestedGroup.setCellPadding(10);
				for (int cx =0; cx< ancestorCt; cx++) {
					stNestedGroup.setWidget(0, cx, new Hidden("h_" + ancestorId));
				
				}
				stNestedGroup.setWidget(0, ancestorCt, stGroup);
				scPanel.add(stNestedGroup);
			}
		} else {
			scPanel.add(stGroup);	
		}
	}
	
	/**
	 * 
	 */
	public void resetSearchCriteria() {
		sc = new SearchCriteria(Criteria.AND);
		
	
		SearchTerm st = new SearchTerm(getPropNames().get(0),Operator.EQUALTO,"");
		sc.append(st);
//		st = new SearchTerm(getPropNames().get(0),Operator.EQUALTO,"");
//		sc.append(st);
		

		
		
		
		simpleQuery.setEnabled(true);
		advancedQuery.setEnabled(true);

		advancedBuilderOption(advancedQuery.getValue());
		
		// Preserve builder mode
		// simpleQuery.setValue(true);
		
		
		resultPanel.clear();
		scPanel.clear();
		printTable(sc, "");
	}

	/**
	 * 
	 */
	public void advancedBuilderOption(boolean abo) {
		
		Style.Visibility v = (abo)?Style.Visibility.VISIBLE:Style.Visibility.HIDDEN;
		Style.Display d = (abo)?Style.Display.BLOCK:Style.Display.NONE;
		
		Element e = Document.get().getElementById(newCriteriaGroupTxt);	
		e.getStyle().setVisibility(v);
		e.getStyle().setDisplay(d);
		
//		e = Document.get().getElementById(appendCriteriaTxt);
//		e.getStyle().setVisibility(v);
//		e.getStyle().setDisplay(d);
//		
		e = Document.get().getElementById(removeGroupTxt);
		e.getStyle().setVisibility(v);
		e.getStyle().setDisplay(d);
		
		
	}

	
	String newSeachTermTxt = "New Search Term";
	String newCriteriaGroupTxt = "Sub-criteria";
	String appendCriteriaTxt = "Append Criteria";
	String removeGroupTxt = "Remove Group";
	Button newSeachTerm = new Button(newSeachTermTxt);
	Button newCriteriaGroup = new Button(newCriteriaGroupTxt);


	protected VerticalPanel searchPanel() {
		
		if (getPropNames()==null || (getPropNames()!=null && getPropNames().size()==0)) {			
			return new VerticalPanel();
		}
		
		criteriaPanel.add(new HTML("<h3>Add Search Criteria</h3>"));
		criteriaPanel.add(new HTML("  " ));
		
		FlexTable queryMode = new FlexTable();
		
	    
	    simpleQuery.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				
				advancedBuilderOption(false);
												
			}

		});
	    
	    advancedQuery.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
								
				advancedBuilderOption(true);
				
			}
		});
	    
	    Button resetCriteriaBtn = new Button("Reset");
	    resetCriteriaBtn.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				resetSearchCriteria();
				
			}
		});
	    
	    Button searchBtn = new Button("Search");
	    searchBtn.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				
				int itemCt = reposRight.getItemCount();
				
				if (itemCt==0) {
					new PopupMessage("Please select at least one repository.");
					return;
				} else {
					String[] selectedRepos = new String[itemCt];
					for (int cx=0; cx<itemCt; cx++ ) {
						selectedRepos[cx] = reposRight.getValue(cx);	
					}
					 toolkitService.search(selectedRepos, sc, searchResults);
				}
				

				
			}
		});
		
	    // simpleQuery.setValue(true);
	    
		queryMode.setWidget(0, 0, new HTML("Criteria Builder Mode")); //		
		queryMode.setWidget(1, 0, simpleQuery);
		queryMode.setWidget(1, 1, advancedQuery);
		queryMode.setWidget(1, 2, resetCriteriaBtn);
		queryMode.setWidget(1, 3, searchBtn);
		queryMode.getFlexCellFormatter().setColSpan(0, 0, 4);
		
				
		criteriaPanel.add(queryMode);
		criteriaPanel.add(new HTML("&nbsp;"));				
		
		// Start in simple builder mode
		simpleQuery.setValue(true);
		
		resetSearchCriteria();
		
		return criteriaPanel;


	}
	
	

	@Override
	public String getWindowShortName() {
		return "Search";
	}
	
	class CSelectorChangeHandler implements ChangeHandler {

		private String nodePath; 
		private String propType; 
		
		CSelectorChangeHandler(String type, String path) {
			this.propType = type;
			this.nodePath = path;
		}

		@Override
		public void onChange(ChangeEvent event) {
			
			
			
			
			if (CRITERIA.equals(this.propType)) {
				setCSelector(((ListBox)event.getSource()).getSelectedIndex(),this.nodePath);	
			} else if (OPERATOR.equals(this.propType)) {
				setOpSelector(((ListBox)event.getSource()).getSelectedIndex(),this.nodePath);	
			} else if (PROP_NAME.equals(this.propType)) {
				setPropNameSelector(((ListBox)event.getSource()).getSelectedIndex(),this.nodePath);
			} else if (PROP_VALUE.equals(this.propType)) {
				setStValue(((TextBox)event.getSource()).getValue(),this.nodePath);
			}							
			
		}
	}

	public ArrayList<String> getPropNames() {
		return propNames;
	}

	public void setPropNames(ArrayList<String> propNames) {
		this.propNames = propNames;
	}

}
