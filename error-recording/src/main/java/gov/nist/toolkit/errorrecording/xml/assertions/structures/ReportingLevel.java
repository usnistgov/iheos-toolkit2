package gov.nist.toolkit.errorrecording.xml.assertions.structures;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used in the ErrorRecorders
 * Created by diane on 7/19/2016.
 */
public enum ReportingLevel implements IsSerializable {
   SECTIONHEADING,
   CHALLENGE,
   EXTERNALCHALLENGE,
   DETAIL,
   ERROR,
   WARNING,
   D_SUCCESS,
   D_INFO,
   D_ERROR,
   D_WARNING
}