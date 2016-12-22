/**
 * 
 */
package gov.nist.toolkit.simulators.servlet;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Wraps Servlet Output Stream to save contents for log
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {

   HttpSimServletResponseWrapper wrapper;
   ServletOutputStream stream;
   
   public ServletOutputStreamWrapper(ServletOutputStream stream, 
      HttpSimServletResponseWrapper wrapper) {
      this.wrapper = wrapper;
      this.stream = stream;
   }
   
   @Override
   public void write(int b) throws IOException {
      stream.write(b);
   }
   
   public void write(byte[] bytes) throws IOException {
      wrapper.addBytes(bytes);
      stream.write(bytes);
   }

}
