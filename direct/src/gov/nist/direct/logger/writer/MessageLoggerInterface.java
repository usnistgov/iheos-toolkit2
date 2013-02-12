package gov.nist.direct.logger.writer;

import java.io.IOException;

/**
 * Logs full content of messages to the log file structure through LoggerDispatch.
 * @author dazais
 *
 */
public interface MessageLoggerInterface {

	public void log(Object o) throws IOException;
}
