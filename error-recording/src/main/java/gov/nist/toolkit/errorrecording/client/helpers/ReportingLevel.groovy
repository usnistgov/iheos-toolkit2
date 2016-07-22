package gov.nist.toolkit.errorrecording.client.helpers

import com.google.gwt.user.client.rpc.IsSerializable

/**
 * Groovy enumeration
 * Created by diane on 7/19/2016.
 */
enum ReportingLevel implements IsSerializable {
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