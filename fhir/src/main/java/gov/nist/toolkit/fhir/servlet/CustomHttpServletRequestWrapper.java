package gov.nist.toolkit.fhir.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 *
 */

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = Logger.getLogger(CustomHttpServletRequestWrapper.class);
    private final String body;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();

            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                char[] charBuffer = new char[128];
                int bytesRead = -1;

                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            logger.error("Error reading the request body...");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    logger.error("Error closing bufferedReader...");
                }
            }
        }

        body = stringBuilder.toString();
    }

    @Override
    public ServletInputStream getInputStream () throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

        ServletInputStream inputStream = new ServletInputStream() {
            public int read () throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return inputStream;
    }
}