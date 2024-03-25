package edu.wustl.mir.erl.ihe.xdsi.util;

import java.util.ArrayList;

public class KOSStudyBean {
   private String studyUID = null;
   private ArrayList <KOSSeriesBean> seriesBeanList = null;

   public KOSStudyBean(String studyUID, ArrayList <KOSSeriesBean> seriesBeanList) {
      super();
      this.studyUID = studyUID;
      this.seriesBeanList = seriesBeanList;
      if (this.seriesBeanList == null) {
         this.seriesBeanList = new ArrayList <>();
      }
   }

   public String getStudyUID() {
      return studyUID;
   }

   public void setStudyUID(String studyUID) {
      this.studyUID = studyUID;
   }

   public ArrayList <KOSSeriesBean> getSeriesBeanList() {
      return seriesBeanList;
   }

   public void setSeriesBeanList(ArrayList <KOSSeriesBean> seriesBeanList) {
      this.seriesBeanList = seriesBeanList;
   }

   public void addSeriesBean(KOSSeriesBean seriesBean) {
      seriesBeanList.add(seriesBean);
   }

}
