package gov.nist.toolkit.fhir.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 */
public class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper
{
    private static final Logger logger = Logger.getLogger(CustomHttpServletResponseWrapper.class);
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private OutputStream outputStream;

    public CustomHttpServletResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.outputStream = response.getOutputStream();
    }

    public byte[] toByteArray() {
        String msg = new String(buffer.toByteArray());
//        System.out.println("Msg is " + msg);
        return buffer.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream () throws IOException {
        return new MyServletOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(getOutputStream());
    }

    private class MyServletOutputStream extends ServletOutputStream {

        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
//            System.out.println("write: " + new String(buffer.toByteArray()));
            outputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b);
//            System.out.println("write: " + new String(buffer.toByteArray()));
            outputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
//            System.out.println("write: " + new String(buffer.toByteArray()));
            outputStream.write(b, off, len);
        }
    }
}
