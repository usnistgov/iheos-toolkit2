/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for detail comparing two sets of dicom documents.
 */
public class DetailDcmSetContent extends DetailDcmSequenceContent  {
   
   private static Logger log = null;

   @Override
   protected void initializeTest() {
//      assertions = new ArrayList<>();
//      // SOP Class UID (0008,0016)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SOPClassUID));
//      // SOP Instance UID (0008,0018)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SOPInstanceUID));
//      // Patient ID (0010,0020)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientID));
//      // Patient's Birth Date (0010,0030)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientBirthDate));
//      // Patient's Sex (0010,0040)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientSex));
//      // Study Instance UID (0020,000D)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.StudyInstanceUID));
//      // Series Instance UID (0020,000E)
//      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SeriesInstanceUID));
   }

   /**
    * args[0] {@code List<String>} of absolute pfns of test images.<br/>
    * args[1] {@code List<String>} of absolute pfns of std images.<br/>
    * args[2] {@code List<DCMAssretion>} of assertions to be applied to images.
    */
   @SuppressWarnings("unchecked")
   @Override
   protected void initializeDetail(Object[] args) throws Exception {
      uniqueSequenceTag = Tag.SOPInstanceUID;
      desc = "dicom files";
      assertions = (List<DCMAssertion>) args[2];
      List<Integer> a = new ArrayList<>();
      boolean flag = false;
      boolean checkHash = false;
      boolean checkAttr = false;
      for (DCMAssertion assertion : assertions) {
         if (assertion.tag == -1) {
            checkHash = true;
            continue;
         }
         checkAttr = true;
         a.add(assertion.tag);
         if (assertion.tag == uniqueSequenceTag) flag = true;
      }
      if (flag == false) a.add(uniqueSequenceTag);
      int[] tags = new int[a.size()];
      for (int i = 0; i < a.size(); i++) tags[i] = a.get(i);      
      
      test = loadAttributesList((List<String>) args[0], tags, checkHash, checkAttr);
      
      std = loadAttributesList((List<String>) args[1], tags, checkHash, checkAttr);
   }

   private List<AttributesAndHash> loadAttributesList(List<String> pfns, int[]tags, boolean checkHash, boolean checkAttr) throws Exception {
      List<AttributesAndHash> list = new ArrayList<>();
      DicomInputStream din = null;
      try {
         for (String pfn : pfns) {
            AttributesAndHash attributesAndHash = new AttributesAndHash();
            if (checkAttr) {
               din = new DicomInputStream(new File(pfn));
               Attributes dataSet = din.readDataset(-1, -1);
               attributesAndHash.attributes = new Attributes(dataSet, tags);
            }
            if (checkHash)
               attributesAndHash.hash = getMd5ForFile(new File(pfn));
            list.add(attributesAndHash);
         }
         return list;
      } finally {
         if (din != null) din.close();
      }
   }

   public static class AttributesAndHash {
      Attributes attributes = null;
      String hash = null;

      public AttributesAndHash() {}
      public AttributesAndHash(Attributes attrs) {
         attributes = attrs;
      }
      public AttributesAndHash(String hash) {
         this.hash = hash;
      }
      public AttributesAndHash(Attributes attrs, String hash) {
         attributes = attrs;
         this.hash = hash;
      }
   }

   /**
    * Calculate MD5 hash string for file
    * @param file to calculate hash for
    * @return hash string, or null on error, for example file not found, io error.
    */
   private String getMd5ForFile(File file) {
      String md5 = null;
      try (FileInputStream is = new FileInputStream(file)) {
         return DigestUtils.md5Hex(IOUtils.toByteArray(is));
      } catch (Exception e) {
         return null;
      }
   }
   
}
