/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import edu.wustl.mir.erl.ihe.xdsi.validation.DCMAssertion.TYPE;

/**
 * Base class for detail comparing two sets of dicom documents.
 */
public class DetailDcmSetContent extends DetailDcmSequenceContent  {
   
   private static Logger log = null;

   @Override
   protected void initializeTest() {
      assertions = new ArrayList<>();
      // SOP Class UID (0008,0016)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SOPClassUID));
      // SOP Instance UID (0008,0018)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SOPInstanceUID));
      // Patient ID (0010,0020)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientID));
      // Patient's Birth Date (0010,0030)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientBirthDate));
      // Patient's Sex (0010,0040)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientSex));
      // Study Instance UID (0020,000D)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.StudyInstanceUID));
      // Series Instance UID (0020,000E)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SeriesInstanceUID));
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
      for (DCMAssertion assertion : assertions) {
         a.add(assertion.tag);
         if (assertion.tag == uniqueSequenceTag) flag = true;
      }
      if (flag == false) a.add(uniqueSequenceTag);
      int[] tags = new int[a.size()];
      for (int i = 0; i < a.size(); i++) tags[i] = a.get(i);      
      
      test = loadAttributesList((List<String>) args[0], tags);
      
      std = loadAttributesList((List<String>) args[1], tags);      
   }

   private List<Attributes> loadAttributesList(List<String> pfns, int[]tags) throws Exception {
      List<Attributes> list = new ArrayList<>();
      DicomInputStream din = null;
      try {
         for (String pfn : pfns) {
            din = new DicomInputStream(new File(pfn));
            Attributes dataSet = din.readDataset(-1, -1);
            list.add(new Attributes(dataSet, tags));
            din.close();
         }
         return list;
      } finally {
         if (din != null) din.close();
      }
   }
   
}
