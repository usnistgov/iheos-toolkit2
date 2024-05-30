/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/**
 * Implements HL7V2 Hierarchic Designator Macro as defined by
 * PS 3.3 2011 Table 10-7 (page 110).
 */
@SuppressWarnings("javadoc")
public class HL7V2HierarchicDesignatorMacro {

   /**
    * Identifies an entity within the local namespace or domain. Required if
    * Universal Entity ID (0040,0032) is not present; may be present otherwise.
    */
   private String localId = "";
   /**
    * Universal or unique identifier for an entity. Required if Local Namespace
    * Entity ID (0040,0031) is not present; may be present
    */
   private String uid = "";
   /**
    * Standard defining the format of the Universal Entity ID. Required if
    * Universal Entity ID (0040,0032) is present.<br/>
    * Defined Terms:<ol>
    * <li>DNS An Internet dotted name. Either in ASCII or as integers. 
    * <li>EUI64 An IEEE Extended Unique Identifier.
    * <li>ISO An International Standards Organization Object Identifier
    * <li>URI Uniform Resource Identifier 
    * <li>UUID The DCE Universal Unique Identifier
    * <li>X400 An X.400 MHS identifier 
    * <li>X500 An X.500 directory name</ol>
    */
   private String uidType = "";
   
   public HL7V2HierarchicDesignatorMacro(String localId, String uid, String uidType) {
      this.localId = localId;
      this.uid = uid;
      this.uidType = uidType;
   }
   
   public HL7V2HierarchicDesignatorMacro(Sequence seq) {
      if (seq != null) {
         Attributes attr = seq.get(0);
         if (attr != null) {
            this.localId = attr.getString(Tag.LocalNamespaceEntityID, "");
            this.uid = attr.getString(Tag.UniversalEntityID, "");
            this.uidType = attr.getString(Tag.UniversalEntityIDType, "");
         }
      }
   }

   /**
    * @return the {@link #localId} value.
    */
   public String getLocalId() {
      return localId;
   }

   /**
    * @param localId the {@link #localId} to set
    */
   public void setLocalId(String localId) {
      this.localId = localId;
   }

   /**
    * @return the {@link #uid} value.
    */
   public String getUid() {
      return uid;
   }

   /**
    * @param uid the {@link #uid} to set
    */
   public void setUid(String uid) {
      this.uid = uid;
   }

   /**
    * @return the {@link #uidType} value.
    */
   public String getUidType() {
      return uidType;
   }

   /**
    * @param uidType the {@link #uidType} to set
    */
   public void setUidType(String uidType) {
      this.uidType = uidType;
   }
   /** returns Entity UID, or else local ID. */
   public String getId() {
      if (uid.isEmpty()) return localId;
      return uid;
   }
   /** returns UID Type if Entity UID is present, or else blank. */
   public String getType() {
      if (uid.isEmpty()) return "";
      return uidType;
               
   }
   
}
