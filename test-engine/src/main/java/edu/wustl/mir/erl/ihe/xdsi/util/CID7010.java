package edu.wustl.mir.erl.ihe.xdsi.util;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;

@SuppressWarnings("javadoc")
public enum CID7010 {
	
	        DCM_113000 ("Of Interest"),
	        DCM_113001 ("Rejected for Quality Reasons"),
	        DCM_113002 ("For Referring Provider"),
	        DCM_113003 ("For Surgery"),
	        DCM_113004 ("For Teaching"),
	        DCM_113005 ("For Conference"),
	        DCM_113006 ("For Therapy"),
	        DCM_113007 ("For Patient"),
	        DCM_113008 ("For Peer Review"),
	        DCM_113009 ("For Research"),
	        DCM_113010 ("Quality Issue"),
	        DCM_113013 ("Best In Set"),
	        DCM_113018 ("For Printing"),
	        DCM_113020 ("For Report Attachment"),
	        DCM_113030 ("Manifest"),
	        DCM_113031 ("Signed Manifest"),
	        DCM_113032 ("Complete Study Content"),
	        DCM_113033 ("Signed Complete Study Content"),
	        DCM_113034 ("Complete Acquisition Content"),
	        DCM_113035 ("Signed Complete Acquisition Content"),
	        DCM_113036 ("Group of Frames for Display"),
	        DCM_113037 ("Rejected for Patient Safety Reasons"),
	        DCM_113038 ("Incorrect Modality Worklist Entry"),
	        DCM_113039 ("Data Retention Policy Expired");
	
	private String description;
	private static String codingSchemeDesignator = "DCM";
	
	private CID7010(String desc) {
		description = desc;
	}
	
	public String getCodeValue() {
	   return name().substring(4);
	}
	public String getCodeDescription() {
	   return description;
	}
	public String getCodingSchemeDesignator() {
	   return codingSchemeDesignator;
	}
	
	public Attributes toCodeItem() {
      Attributes attrs = new Attributes(3);
      attrs.setString(Tag.CodeValue, VR.SH, getCodeValue());
      attrs.setString(Tag.CodingSchemeDesignator, VR.SH, getCodingSchemeDesignator());
      attrs.setString(Tag.CodeMeaning, VR.LO, getCodeDescription());
      return attrs;
	}
	
	public static CID7010 getCodeInstance(String codeValue) throws IllegalArgumentException {
      String n = codingSchemeDesignator + "_" + codeValue;
      for (CID7010 cid : CID7010.values()) {
         if(cid.name().equals(n)) return cid;
      }
      throw new IllegalArgumentException ("CID 7010: inv code value " + codeValue);
	   
	}
	
	public static String getDescription(String codeValue) throws Exception {
		CID7010 cid = CID7010.getCodeInstance(codeValue);
		return cid.description;
	}

	public static Attributes toCodeItem(String codeValue) throws Exception {
      CID7010 cid = CID7010.getCodeInstance(codeValue);
      return cid.toCodeItem();
	}
	
	public static Attributes toCodeItem(CID7010 cid) {
	   if (cid == null) return null;
	   return cid.toCodeItem();
	}

}
