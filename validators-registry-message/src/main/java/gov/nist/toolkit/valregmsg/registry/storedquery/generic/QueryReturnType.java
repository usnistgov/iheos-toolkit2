package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

/**
 *
 */
public enum QueryReturnType {
    OBJECTREF("ObjectRef"), LEAFCLASS("LeafClass"), LEAFCLASSWITHDOCUMENT("LeafClassWithDocument");

    String returnTypeString;

    QueryReturnType(String returnTypeString) {
        this.returnTypeString = returnTypeString;
    }

    public String getReturnTypeString() {
        return returnTypeString;
    }
}
