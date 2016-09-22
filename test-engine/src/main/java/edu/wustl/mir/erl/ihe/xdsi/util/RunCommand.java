/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs an OS command, gets return status, stdout and stderr
 * BLOCKS THREAD UNTIL COMPLETION
 */
public class RunCommand {
   
   private List<String> cmds = null;
   private List<String> out = null;
   private List<String> err = null;
   private int returnStatus = 1;
   
   /**
    * @param cmd List of command and parameter elements
    * @return return status, 1 on Exception
    */
   public int runCommand(List<String> cmd) {
      cmds = cmd;
      out = new ArrayList<>();
      err = new ArrayList<>();
      try {
         ProcessBuilder pb = new ProcessBuilder(cmd);
         Process pr = pb.start();
         pr.waitFor();
         returnStatus = pr.exitValue();
         out = readStream(pr.getInputStream());
         err = readStream(pr.getErrorStream());
      } catch (Exception e) {
         Utility.getLog().warn(Utility.getEM(e));
      }
      return returnStatus;
   }
   
   private List<String> readStream(InputStream is) {
      String s;
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      List<String> l = new ArrayList<>();
      try {
         while ((s = br.readLine()) != null) l.add(s);
      } catch (IOException e) {
         l.add(e.getMessage());
      }
      return l;
   }

   /**
    * @return the {@link #cmds} value.
    */
   public List <String> getCmds() {
      return cmds;
   }

   /**
    * @return the {@link #out} value.
    */
   public List <String> getOut() {
      return out;
   }

   /**
    * @return the {@link #err} value.
    */
   public List <String> getErr() {
      return err;
   }

   /**
    * @return the {@link #returnStatus} value.
    */
   public int getReturnStatus() {
      return returnStatus;
   }
   

}
