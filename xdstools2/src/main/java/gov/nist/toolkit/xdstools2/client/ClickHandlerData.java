package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.event.dom.client.ClickHandler;
/**
 * Created by bill on 9/15/15.
 */
public abstract class ClickHandlerData<T> implements ClickHandler {
    private T data;

    public ClickHandlerData(T data)
    {
        this.data = data;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}