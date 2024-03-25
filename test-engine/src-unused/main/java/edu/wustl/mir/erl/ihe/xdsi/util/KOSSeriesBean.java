package edu.wustl.mir.erl.ihe.xdsi.util;

import java.util.ArrayList;

public class KOSSeriesBean {
	private String seriesUID = null;
	private String retrieveAETitle = null;
	private String retrieveLocationUID = null;
	public String getRetrieveLocationUID() {
		return retrieveLocationUID;
	}

	public void setRetrieveLocationUID(String retrieveLocationUID) {
		this.retrieveLocationUID = retrieveLocationUID;
	}

	private ArrayList<KOSInstanceBean> instanceBeanList = null;
	
	public KOSSeriesBean(String seriesUID, String retrieveAETitle, String retrieveLocationUID,
			ArrayList<KOSInstanceBean> instanceBeanList) {
		super();
		this.seriesUID = seriesUID;
		this.retrieveAETitle = retrieveAETitle;
		this.retrieveLocationUID = retrieveLocationUID;
		this.instanceBeanList = instanceBeanList;
		if (this.instanceBeanList == null) {
			this.instanceBeanList = new ArrayList<>();
		}
	}

	public String getRetrieveAETitle() {
		return retrieveAETitle;
	}

	public void setRetrieveAETitle(String retrieveAETitle) {
		this.retrieveAETitle = retrieveAETitle;
	}

	public String getSeriesUID() {
		return seriesUID;
	}

	public void setSeriesUID(String seriesUID) {
		this.seriesUID = seriesUID;
	}

	public ArrayList<KOSInstanceBean> getInstanceBeanList() {
		return instanceBeanList;
	}

	public void setInstanceBeanList(ArrayList<KOSInstanceBean> instanceBeanList) {
		this.instanceBeanList = instanceBeanList;
	}
	
	public void addInstanceBean(KOSInstanceBean instanceBean) {
		instanceBeanList.add(instanceBean);
	}
	
	

}
