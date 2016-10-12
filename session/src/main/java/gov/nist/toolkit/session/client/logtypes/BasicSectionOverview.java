package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 *
 */
public interface BasicSectionOverview  extends IsSerializable {
    boolean isRun();
    String getSite();
    String getName();
    List<String> getStepNames();
    BasicStepOverview getStep(String stepName);
    boolean isPass();
    String getDisplayableTime();
}
