package gov.nist.toolkit.soap.wsseToolkitAdapter.log4jToErrorRecorder;
import java.util.ArrayList;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public/* static */class CustomAppender extends AppenderSkeleton {
	ArrayList<LoggingEvent> eventsList = new ArrayList();

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLevel() == Level.INFO) {
			System.out.println(event.getRenderedMessage());
		}
		else if(event.getLevel() == Level.DEBUG)
			System.err.println(event.getRenderedMessage());
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

}
