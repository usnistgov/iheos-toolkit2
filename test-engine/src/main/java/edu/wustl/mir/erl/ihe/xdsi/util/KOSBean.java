package edu.wustl.mir.erl.ihe.xdsi.util;
import java.util.ArrayList;

public class KOSBean {
	private ArrayList<KOSStudyBean> studyBeanList = null;
	private String studyInstanceUID = null;
	private String seriesInstanceUID = null;
	private String sopInstanceUID = null;

	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}

	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}

	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}

	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}

	public String getSopInstanceUID() {
		return sopInstanceUID;
	}

	public void setSopInstanceUID(String sopInstanceUID) {
		this.sopInstanceUID = sopInstanceUID;
	}

	public KOSBean(ArrayList<KOSStudyBean> studyBeanList) {
		super();
		this.studyBeanList = studyBeanList;
		if (this.studyBeanList == null) {
			this.studyBeanList = new ArrayList<>();
		}
	}

	public ArrayList<KOSStudyBean> getStudyBeanList() {
		return studyBeanList;
	}

	public void setStudyBeanList(ArrayList<KOSStudyBean> studyBeanList) {
		this.studyBeanList = studyBeanList;
	}
	
	public void addStudyBean(KOSStudyBean studyBean) {
		studyBeanList.add(studyBean);
	}
	
	public String toString() {
		String nl = "\n";
		StringBuffer buffer = new StringBuffer();
		buffer.append("Study UID:    " + studyInstanceUID + nl);
		buffer.append("Series UID:   " + seriesInstanceUID + nl);
		buffer.append("Instance UID: " + sopInstanceUID + nl);
		buffer.append(nl);
		for (KOSStudyBean study: studyBeanList) {
			buffer.append(" Study: " + study.getStudyUID() + nl);
			for (KOSSeriesBean series: study.getSeriesBeanList()) {
				buffer.append("  Series: " + series.getSeriesUID() + nl);
				buffer.append("    Retrieve AE           " + series.getRetrieveAETitle() + nl);
				buffer.append("    Retrieve Location UID " + series.getRetrieveLocationUID() + nl);

				for (KOSInstanceBean instance: series.getInstanceBeanList()) {
					buffer.append("   Instance: " + instance.getInstanceUID() + nl);
					buffer.append("     SOP Class " + instance.getClassUID() + nl);

				}
			}
		}
		
		return buffer.toString();
	}
	

}
