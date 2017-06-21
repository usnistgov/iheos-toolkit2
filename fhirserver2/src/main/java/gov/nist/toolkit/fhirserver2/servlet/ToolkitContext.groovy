package gov.nist.toolkit.fhirserver2.servlet;

import gov.nist.toolkit.fhir.support.SimContext;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class ToolkitContext {
    HttpServletRequest theRequest;

    static final private String SIMULATOR_CONTEXT = "simulator-context";


    public ToolkitContext(HttpServletRequest theRequest) {
        this.theRequest = theRequest;
    }

    public SimContext getSimContext() {
        SimContext simContext = (SimContext) theRequest.getAttribute(SIMULATOR_CONTEXT);
        assert simContext
        return simContext
    }

    public ToolkitContext setSimContext(SimContext simContext) {
        theRequest.setAttribute(SIMULATOR_CONTEXT, simContext);
        return this;
    }
}
