package gov.nist.toolkit.xdstools2.client.tabs.search;


import gov.nist.toolkit.commondatatypes.client.MetadataTypes;

import gov.nist.toolkit.repository.simple.search.client.ContextSupplement;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm;
import gov.nist.toolkit.repository.simple.search.client.SimpleData;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria.Criteria;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;



public class SearchTab extends GenericQueryTab {
	
	
	Integer callCt = 0;
	Label lblTxt = new Label();
	SimpleData sd = new SimpleData();
	SearchCriteria sc = new SearchCriteria(Criteria.AND);
	
	FlexTable msgs = new FlexTable();
	FlexTable grid = new FlexTable();
	FlexTable existingGrid = new FlexTable();

	String required        = new String("*Required fields");


	TextBox addDirectFrom = new TextBox();
	TextBox cert = new TextBox();

	

	public ListBox reposLeft = new ListBox(true);
	public Button moveRight = new Button("&rArr;");
	public Button moveLeft = new Button("&lArr;");
	public ListBox reposRight = new ListBox(true);
	public Button moveUp = new Button("Up");
	public Button moveDown = new Button("Down");
	
	
	static String DEFAULTWIDTH = "30em";
	static String DEFAULTTITLEWIDTH = "15em";


	VerticalPanel criteriaPanel = new VerticalPanel();
	VerticalPanel scPanel = new VerticalPanel();
	
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

		
		toolkitService.getRepositoryDisplayTags(myCall1);
		
		topPanel.add(reposList());
		
		topPanel.add(searchPanel());
		topPanel.add(scPanel);

	}
	
	
	AsyncCallback<Map<String, String>> myCall1 = new AsyncCallback<Map<String, String>> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Repositories could not be loaded: " + arg0.getMessage());
		}

	

		@Override
		public void onSuccess(Map<String, String> m) {
			
			for (Map.Entry<String, String> entry : m.entrySet()) {				
				reposLeft.addItem(entry.getKey().substring(24) + " - " + entry.getValue(), entry.getKey() );				
			}
			
			setRepositorySelectorCbs();
			
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
		int firstIdx = reposRight.getSelectedIndex();
		int itemCt = l.getItemCount();
		
		if (itemCt==1) return;
		if (firstIdx==0 && order==-1) return;
		if (firstIdx==itemCt-1 && order>1) return;

		int newIdx = firstIdx + order;
		
		if (newIdx<=itemCt) {
			l.insertItem(reposRight.getItemText(firstIdx), reposRight.getValue(firstIdx), newIdx);
			
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
	FlexTable criteriaGrid = new FlexTable();
	
	protected SearchCriteria getScByPath(String path, int limit) {		
			
		if (path==null || "".equals(path)) {
			return sc;
		}
		
		String ids[] = path.split("\\.");
		
		SearchCriteria scObj = sc;
		if (Math.abs(limit) >= ids.length) {
			return null;
		}
				
		for (int cx=0; cx< (ids.length + (limit)); cx++) {
			scObj = scObj.getSearchCriteria().get(Integer.parseInt(ids[cx]));
		}
		return scObj;
	}
	
	protected SearchCriteria getScByPath(String path) {
		return getScByPath(path,0);
	}
	
	protected void addNewSearchTerm(String id) {
		
		// Fix assignment
		// assign by id parse & attach to right obj
		
		SearchCriteria scObj = getScByPath(id);
		
		SearchTerm st = new SearchTerm("my prop",SearchTerm.Operator.EQUALTO,callCt.toString());
		scObj.append(st);

		scPanel.clear();
		printTable(sc, "");
		topPanel.add(scPanel);
	}
	
	protected void addNewGroup(String id) {
		
		// Fix assignment
		// assign by id parse & attach to right obj
		
		
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
		
		scPanel.clear();
		printTable(sc, "");
		topPanel.add(scPanel);
				
		
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
				
		

		scPanel.clear();
		printTable(sc, "");
		topPanel.add(scPanel);

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
			
			
			String groupId = "";
			
			if (ancestorId!=null && !"".equals(ancestorId)) {
				groupId = ancestorId;
			}
			
			addNewStBtn.getElement().setId(newSeachTermTxt + groupId);			
			addNewStBtn.addClickHandler(new ContextSupplement<String>(groupId) {
			
				public void onClick(ClickEvent event) {
						lblTxt.setText("setting" + getParameter());
						addNewSearchTerm(getParameter());
				}
			});
			
			addNewGroup.getElement().setId(newCriteriaGroupTxt + groupId);			
			addNewGroup.addClickHandler(new ContextSupplement<String>(groupId) {
			
				public void onClick(ClickEvent event) {
						lblTxt.setText("setting" + getParameter());
						addNewGroup(getParameter());
				}
			});
			
			
			appendCriteria.getElement().setId(appendCriteriaTxt + groupId);
			appendCriteria.addClickHandler(new ContextSupplement<String>(groupId) {
			
				public void onClick(ClickEvent event) {
						lblTxt.setText("setting" + getParameter());
						appendCriteria(getParameter());
				}
			});
			
			if (sc.getSearchCriteria().size()>1) {
				// TODO
				// disable here
				// search terms can only be added to free-standing sc's
			}
			
			ListBox cSelector = new ListBox();
			cSelector.addItem("And");
			cSelector.addItem("Or");
			
			cmdGroup.setWidget(0, 0, cSelector);
			cmdGroup.setWidget(0, 1, addNewStBtn);
			cmdGroup.setWidget(0, 2, addNewGroup);
			cmdGroup.setWidget(0, 3, appendCriteria);
			
			stGroup.getFlexCellFormatter().setColSpan(0, 0, 3); // stGroup is just empty container here
			stGroup.setWidget(stRow++, 0, cmdGroup);
									
			
			setGroup(ancestorId, stGroup);
			
		
		} else {
			FlexTable stGroup = new FlexTable();
			if (sc.getSearchTerms()!=null && !sc.getSearchTerms().isEmpty()) {

				int stRow=0;
			
				stGroup.addStyleName("searchCriteriaGroup");

				
				FlexTable cmdGroup =  new FlexTable();
				
				Button addNewStBtn = new Button(newSeachTermTxt);
				Button addNewGroup = new Button(newCriteriaGroupTxt);
				
				
				
				String groupId = "";
				
				if (ancestorId!=null && !"".equals(ancestorId)) {
					groupId = ancestorId;
				}
				
				addNewStBtn.getElement().setId(groupId);
				
				addNewStBtn.addClickHandler(new ContextSupplement<String>(groupId) {
				
					public void onClick(ClickEvent event) {
							lblTxt.setText(getParameter());
							addNewSearchTerm(getParameter());
					}
				});
				
				ListBox cSelector = new ListBox();
				cSelector.addItem("And");
				cSelector.addItem("Or");
				
				cmdGroup.setWidget(0, 0, cSelector);				
				cmdGroup.setWidget(0, 1, addNewStBtn);
				cmdGroup.setWidget(0, 2, addNewGroup);
				
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
					stPn.addItem("displayName");
					stPn.addItem("description");
					stPn.addItem("type");
					
					
					for (SearchTerm.Operator e : SearchTerm.Operator.values()) {
						stOp.addItem(e.toString());
					}
					
					String nodeId = ancestorId + "." + (32768 | leafIndex++);
					stPn.getElement().setId(nodeId); // assign to the delete button
					
					Button remove = new Button("<font size='2'>Remove</font>");
					remove.addClickHandler(new ContextSupplement<String>(nodeId) {
						
						public void onClick(ClickEvent event) {
								lblTxt.setText("remove " + getParameter());								
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
					// stNestedGroup.setWidget(0, cx, new HTML("<font style='left-margin:20px'>&nbsp;&nbsp;&nbsp;&nbsp;</font>"));
				}
				stNestedGroup.setWidget(0, ancestorCt, stGroup);
				scPanel.add(stNestedGroup);
			}
		} else {
			scPanel.add(stGroup);	
		}
	}

	
	String newSeachTermTxt = "Add New Search Term";
	String newCriteriaGroupTxt = "Add New Criteria Group";
	String appendCriteriaTxt = "Append New Criteria";
	Button newSeachTerm = new Button(newSeachTermTxt);
	Button newCriteriaGroup = new Button(newCriteriaGroupTxt);
	

	protected VerticalPanel searchPanel() {
		
		criteriaPanel.add(new HTML("<h3>Add Search Criteria</h3>"));
		criteriaPanel.add(new HTML("  " ));
		
		

		int row = 0;
		int col = 0;
		HTML h;

		
			
			newSeachTerm.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
//					callCt++;
//					// sd.addItem(callCt.toString());
//					
//					
//					sd.addItem(MetadataTypes.getMetadataTypeName(callCt.intValue()) + " -- " +  callCt.toString());
//					
//					// lblTxt.setText(sd.getMyList().get(0));
//					
//					SearchTerm st = new SearchTerm("my prop",SearchTerm.Operator.EQUALTO,callCt.toString());
//					
//					sc.append(st);
//					
//					lblTxt.setText( sc.toString() );
					
					SearchTerm st = new SearchTerm("my prop",SearchTerm.Operator.EQUALTO,callCt.toString());
					sc.append(st);
					 st = new SearchTerm("my prop",SearchTerm.Operator.EQUALTO,callCt.toString());
					sc.append(st);
					 st = new SearchTerm("my prop",SearchTerm.Operator.EQUALTO,callCt.toString());
					sc.append(st);
					
					printTable(sc,"");
					stGrid.setWidget(++stGridRow, 0, mainTable);
					



					
					criteriaGrid.setWidget(0, 0, stGrid);
					criteriaPanel.add(criteriaGrid);
					
					topPanel.add(criteriaPanel);
					
					
					//new PopupMessage(DOM.gete);
				}
			});
			
		
			
//			FlexTable cmdGroup =  new FlexTable();
//			
//				cmdGroup.setWidget(0, 0, newSeachTerm);
//				cmdGroup.setWidget(0, 1, newCriteriaGroup);
//				// cmdGroup.setWidget(0, 2, lblTxt);
//				
//		criteriaGrid.setWidget(row, col, cmdGroup);		

//	
//			ListBox st = new ListBox();
//			st.setVisibleItemCount(1);		
//			
//			// Populate the search terms here...
//			st.addItem("displayName");
//			st.addItem("description");
//			st.addItem("type");
//			
//	
//			ListBox op = new ListBox();
//			st.setVisibleItemCount(1);		
//			
//			for (SearchTerm.Operator e : SearchTerm.Operator.values()) {
//				op.addItem(e.toString());
//			}
//			
//			
//			TextBox sv = new TextBox();	
//			sv.setWidth(DEFAULTWIDTH);
//	
//			//grid.setWidget(row, col++, st);
//			//grid.setWidget(row, col++, op);
//			//grid.setWidget(row, col++, sv);
//		
//		
//			scGroup.setWidget(1, 0, st);
//			scGroup.setWidget(1, 1, op);
//			scGroup.setWidget(1, 2, sv);
//			
//			
//		FlexTable scGroup2 = new FlexTable();
//			ListBox st2 = new ListBox();
//			ListBox st2op = new ListBox();
//			TextBox st2txt = new TextBox();
//			
//			st2txt.setWidth(DEFAULTWIDTH);
//			st2.addItem("displayName");
//			st2.addItem("description");
//			st2.addItem("type");
//			for (SearchTerm.Operator e : SearchTerm.Operator.values()) {
//				st2op.addItem(e.toString());
//			}
//			
//			scGroup2.setWidget(0, 0, st2);
//			scGroup2.setWidget(0, 1, st2op);
//			scGroup2.setWidget(0, 2, st2txt);
//
//			
//		
//			
//			FlexTable mainTable =  new FlexTable();				
//				mainTable.setWidget(1, 0, scGroup);
//				mainTable.setWidget(2, 0, scGroup2);
//		
//		
//		criteriaGrid.setWidget(++row, 0, mainTable);
//		
		//Button newSt = new Button("New Search Term");			
		//grid.setWidget(row, col++, newSt);
		
		criteriaPanel.add(criteriaGrid);
		
		printTable(sc, "");
		
		return criteriaPanel;


	}
	
	

	@Override
	public String getWindowShortName() {
		return "Search";
	}
}
