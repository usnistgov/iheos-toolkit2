package gov.nist.toolkit.desktop.client.tools.toy;

import gov.nist.toolkit.desktop.client.abstracts.AbstractPresenter;

/**
 *
 */
public class ToyPresenter extends AbstractPresenter<ToyView> {

    static int counter = 0;
    int myIndex;
    String name = "";

    @Override
    public void init() {
        setTitle(name);
    }
}
