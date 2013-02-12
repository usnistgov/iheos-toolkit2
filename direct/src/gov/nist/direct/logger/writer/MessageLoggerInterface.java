package gov.nist.direct.logger.writer;

import java.io.IOException;

import javax.mail.internet.MimeMessage;

/**
 * Logs full content of messages to the log file structure through LoggerDispatch.
 * @author dazais
 *
 */
public interface MessageLoggerInterface {

	/**
	 * 
	 * @param msg
	 * @param ls
	 * @param transactionType DirectSend or DirectReceive
	 * @param messageType Direct or mdn
	 * @param partType message part, decrypted message, status file, date file
	 * @param username
	 * @param messageId
	 * @throws IOException
	 */
	public void log(Object msg, LogStructure ls, String transactionType, String messageType, String partType, String username, String messageId) throws IOException;
}
