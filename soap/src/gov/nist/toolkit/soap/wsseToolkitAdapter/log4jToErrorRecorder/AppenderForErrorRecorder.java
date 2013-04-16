package gov.nist.toolkit.soap.wsseToolkitAdapter.log4jToErrorRecorder;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public/* static */class AppenderForErrorRecorder extends AppenderSkeleton {
	
	private ValidationContext vc;
	private ErrorRecorder er;
	private MessageValidatorEngine mvc;

	public AppenderForErrorRecorder(ValidationContext vc, ErrorRecorder er, MessageValidatorEngine mvc){
		this.vc = vc;
		this.er = er;
		this.mvc = mvc;
	}
	
	@Override
	protected void append(LoggingEvent event) {
		
		if (event.getLevel() == Level.ERROR) {
			er.err(XdsErrorCode.Code.NoCode,event.getRenderedMessage(),event.getLoggerName(),event.getLevel().toString());
		}
		else if(event.getLevel() == Level.INFO || event.getLevel() == Level.DEBUG)
			er.detail(event.getRenderedMessage());
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

}
