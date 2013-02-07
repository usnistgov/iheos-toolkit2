package gov.nist.direct.logging;

import java.io.File;
import java.io.IOException;

/**
 * Logs full content of messages to the log file structure through LoggerDispatch.
 * @author dazais
 *
 */
public interface MessageLogger {

	// change to void?
	public boolean log(Object o) throws IOException;
}
