package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 *
 */
public interface BasicStepOverview  extends IsSerializable {
    String getTransaction();
    List<String> getErrors();
}
