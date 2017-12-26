package gov.nist.toolkit.session.shared;

import java.util.List;

public interface IMessage {
    List<SubMessage> getSubMessages();
    void addSubMessage(SubMessage subMessage);
}
