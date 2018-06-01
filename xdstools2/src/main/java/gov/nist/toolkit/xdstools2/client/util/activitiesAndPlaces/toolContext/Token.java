package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;

import gov.nist.toolkit.xdsexception.client.TkNotFoundException;

/**
 *
 */
public enum Token {
    TOOLID("toolId"),
    ENVIRONMENT("env"),
    TEST_SESSION("testSession"),
    ACTOR("actor"),
    PROFILE("profile"),
    OPTION("option"),
    /**
     * Only the Id part, without the test session prefix.
     */
    SYSTEM_ID("systemId");

    String name;

    Token(String name) {
        this.name = name;
    }


    static public Token findByPropertyName(String name) throws TkNotFoundException {
        if (name==null) throw new NullPointerException("Name cannot be null.");
        for (Token pn : values()) {
            if (pn.name.equals(name)) return pn;
        }
        throw new TkNotFoundException("Name cannot be found.","Name.");
    }

    @Override
    public String toString() {
        return name;
    }
}
