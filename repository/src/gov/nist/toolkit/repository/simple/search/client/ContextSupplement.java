package gov.nist.toolkit.repository.simple.search.client;

import com.google.gwt.event.dom.client.ClickHandler;

/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public abstract class ContextSupplement<T> implements ClickHandler {
	
	    private T parameter;

	    public ContextSupplement(T p)
	    {
	        this.parameter = p;
	    }

	    public T getParameter()
	    {
	        return parameter;
	    }

	    public void setParameter(T p)
	    {
	        this.parameter = p;
	    }
}
