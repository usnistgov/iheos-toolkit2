package gov.nist.toolkit.configDatatypes.server.datatypes

import gov.nist.toolkit.configDatatypes.server.datatypes.MessageTechnology

/**
 *
 */
class MessageType {
    MessageTechnology messageTechnology
    String soapAction

    MessageType(MessageTechnology messageTechnology1, String soapAction) {
        this.messageTechnology = messageTechnology1
        this.soapAction = soapAction
    }
}
