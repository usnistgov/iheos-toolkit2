package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * General static utility methods. <b>Note:</b> Some of these are temporary
 * implementations which may need to be replaced before distribution.
 */
public class Utility {

   /**
    * The system newline character. Usually '\n'
    */
   public static final String nl = System.getProperty("line.separator");

   /**
    * The system file separator character, used to separate directories in a
    * path. In Windows '\'; in Linux '/'.
    */
   public static final String fs = System.getProperty("file.separator");
   
   /**
    * Charset for UTF-8
    */
   public static final Charset utf8 = Charset.forName("UTF-8");
   
   private static Logger log = getLog();

   /**
    * @return String XDSI root directory path
    */
   public static String getXDSIRoot() {
      return "/opt/xdsi";
   }
   
   /**
    * @return NIST xdstools URL
    */
   public static String getXdstools2URL() {
      return "http://localhost:9280/xdstools2";
   }
   
   /**
    * @return String NIST xdstools library directory path
    */
   public static String getXdstools2LibDir() {
      return "/opt/xdsi/tomcat/apache-tomcat-7.0.65-xdstools2-01/webapps/xdstools2/WEB-INF/lib";
   }
   private static boolean addedXdstools2LibDir = false;
   /**
    * add NIST xdstools library directory path to Application ClassLoader path.
    */
   public static void addXdstools2LibDir() {
      if (addedXdstools2LibDir == false) {
         Utility.addLibraryPath(Utility.getXdstools2LibDir());
         addedXdstools2LibDir = true;
      }
   }
   
   /**
    * @return base command to run dciodvfy (no arguments)
    */
   public static String getDciodvfyCommand() {
      return "/opt/xdsi/dicom3tools/bin/dciodvfy";
   }

   /**
    * @return Path of XDSI root directory.
    */
   public static Path getXDSIRootPath() {
      return Paths.get(getXDSIRoot());
   }

   /**
    * @return Path of runDirectory
    */
   public static Path getRunDirectoryPath() {
      return Paths.get(getXDSIRoot(), "runDirectory");
   }

   private static boolean log4jConfigured = false;

   /**
    * @return SYSTEM log
    */
   public static Logger getLog() {
      return getLog("SYSTEM");
   }

   /**
    * @param logName logger name
    * @return log for passed name
    */
   public static Logger getLog(String logName) {
      if (log4jConfigured == false) {
         String pfn = getRunDirectoryPath().resolve("log4j.properties").toString();
         PropertyConfigurator.configure(pfn);
         log4jConfigured = true;
      }
      return Logger.getLogger(logName);
   }

   /**
    * Return string of form " errorMessage className:lineNumber methodName for
    * passed exception. see {@link #getErrorPoint} for details on the stack
    * trace portion of the messaged.
    * 
    * @param e Exception to check.
    * @return String with error info.
    */
   public static String getEM(Throwable e) {
      String em = e.toString();
      String el = getErrLoc(e);
      String ep = getErrorPoint(e);
      return " " + em + nl + "     at: " + el + nl + "   from: " + ep;
   }
   
   private static String getErrLoc(Throwable exception) {
      StackTraceElement[] stackTrace = exception.getStackTrace();
      StackTraceElement ste = stackTrace[0];
      String loc = "?";
      String clsName = ste.getClassName();
      Class<?> cls = null;
      if (clsName != null) try {cls = Class.forName(clsName);} catch (Exception ign){}
      else clsName = "?";
      if (cls != null) {
         loc = cls.getResource('/'+cls.getName().replace('.', '/')+".class").toString();
      }
      String mtdName = ste.getMethodName();
      int lineNumber = ste.getLineNumber();
      return loc + ":" + clsName + ":" + mtdName + ":" + lineNumber;
   }

   /**
    * Returns a string of the form " className:lineNumber methodName " for the
    * first stack trace line from an "edu.wustl" class. If an error occurs or if
    * no "edu.wustl" class is found in the stack trace a string containing a
    * single space will be returned.
    * 
    * @param exception Exception to check.
    * @return String with error info.
    */
   public static String getErrorPoint(Throwable exception) {
      String errorPoint = " ";
      if (exception == null) return errorPoint;
      StackTraceElement[] stackTrace = exception.getStackTrace();
      if (stackTrace == null) return errorPoint;
      for (StackTraceElement stackTraceElement : stackTrace) {
         String className = stackTraceElement.getClassName();
         if (className == null) continue;
         if (className.startsWith("edu.wustl") == false) continue;
         errorPoint += StringUtils.substringAfterLast(className, ".");
         Integer lineNumber = stackTraceElement.getLineNumber();
         if (lineNumber > 0) errorPoint += ":" + lineNumber;
         String methodName = stackTraceElement.getMethodName();
         if (methodName != null) errorPoint += " " + methodName;
         break;
      }
      errorPoint += " ";
      return errorPoint;
   } // EO getErrorPoint

   /**
    * Validates that a directory or file exists and has needed permissions.
    * 
    * @param name Logical name of file/dir, for error message, for example,
    * "Message file".
    * @param path file/dir path to validate
    * @param pfnType DIRECTORY or FILE
    * @param cds String containing codes for needed permissions: r=read,
    * w=write, x=executable; for example "rw" for read-write permissions needed.
    * Case is ignored.
    * @throws Exception on error containing logical name, path, and error
    * description.
    */
   public static void isValidPfn(String name, Path path, PfnType pfnType, String cds) throws Exception {

      String msg = name + " " + path + " ";
      String c = StringUtils.stripToEmpty(cds).toLowerCase();

      File file = path.toFile();

      if (!file.exists()) throw new Exception(msg + "not found");

      switch (pfnType) {
         case DIRECTORY:
            if (!file.isDirectory()) 
               throw new Exception(callingClassLine(4) + " " + msg + "is not a directory");
            break;
         case FILE:
            if (!file.isFile()) 
               throw new Exception(callingClassLine(4) + " " + msg + "is not a file");
            break;
         default:
            exit("Unrecognized PfnType passed to isValidPfn method.");
      }

      // ----- return all permission errors at once
      String errs = "";
      if (c.contains("x")) if (!file.canExecute()) errs += "is not executable" + nl;

      if (c.contains("r")) if (!file.canRead()) errs += "is not readable" + nl;

      if (c.contains("w")) if (!file.canWrite()) errs += "is not writable" + nl;

      if (errs.length() > 0) throw new Exception(msg + errs);

   } // EO isValidPfn method
   
   /**
    * Log fatal message and exit, status 1
    * @param em error message to log.
    */
   public static void exit(String em) {
      Utility.getLog().fatal(em);
      System.exit(1);
   }
   
   /**
    * Log exception, including message and source file:line and exit, status 1
    * @param e Exception to treat as fatal.
    */
   public static void exit(Exception e) {
      Utility.getLog().fatal(Utility.getEM(e));
      System.exit(1);
   }
   /**
    * Validates that a directory or file exists and has needed permissions.
    * 
    * @param name Logical name of file/dir, for error message, for example,
    * "Message file".
    * @param file file/dir File to validate
    * @param pfnType DIRECTORY or FILE
    * @param cds String containing codes for needed permissions: r=read,
    * w=write, x=executable; for example "rw" for read-write permissions needed.
    * Case is ignored.
    * @throws Exception on error containing logical name, path, and error
    * description.
    */
   public static void isValidPfn(String name, File file, PfnType pfnType,
      String cds) throws Exception {
      Utility.isValidPfn(name, file.toPath(), pfnType, cds);
   }
   
   /**
    * Reads a text file line by line and returns a List of Strings.
    * Each line in the input file is converted to one String in the returned list.
    * Empty lines and lines that begin with '#' are omitted.
    * White space is trimmed from each line.
    * 
    * @param path Path to the file to be opened (absolute or relative)
    * @return A List of Strings where each string corresponds to one row in the input file
    * @throws Exception on error
    */
   public static List<String> readTextLines(String path) throws Exception {
	   List<String> lines = Files.readAllLines(Paths.get(path));
	   List<String> returnLines = new ArrayList<String>();
	   Iterator<String> it = lines.iterator();
	   while (it.hasNext()) {
		   String s = it.next();
		   s = s.trim();
		   if (s.isEmpty()) continue;
		   if (s.startsWith("#")) continue;
		   returnLines.add(s);
	   }
	   return returnLines;
   }
   /**
    * Gets the simple name of a method in its calling stack. For example, in the
    * code below, methodTwo will return:
    * <ul>
    * <li>"callingMethod" if "level" is 1.</li>
    * <li>"methodTwo" if "level" is 2.</li>
    * <li>"methodOne" if "level" is 3.</li>
    * </ul>
    * 
    * <pre>
    * public void methodOne() {
    *    methodTwo();
    * }
    * 
    * public void methodTwo() {
    *    return callingMethod(level);
    * }
    * </pre>
    * 
    * @param level The stack level to examine, 0 through the number of levels in
    * the stack.
    * @return the name of the calling method, or "Unknown".
    */
   public static String callingMethod(int level) {
      if (level < 0) return "Unknown";
      StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
      if (stacktrace.length < (level + 1)) return "Unknown";
      StackTraceElement e = stacktrace[level];
      return e.getMethodName();
   }

   /**
    * Gets the simple name of a class in its calling stack. For example, in the
    * code below, methodTwo will return:
    * <ul>
    * <li>"Util" if "level" is 1.</li>
    * <li>"classTwo" if "level" is 2.</li>
    * <li>"classOne" if "level" is 3.</li>
    * </ul>
    * 
    * <pre>
    *    public class classOne {
    *    ...
    *    public void methodOne() {
    *       methodTwo();
    *    }
    *    ...
    *    public class classTwo {
    *    ...
    *    public void methodTwo() {
    *       return callingMethod(level);
    *    }
    * </pre>
    * 
    * @param level The stack level to examine, 0 through the number of levels in
    * the stack.
    * @param includeLineNumber boolean should line number in calling class be
    * included?
    * @return the name of the calling class, or "Unknown". If includeLineNumber
    * is true
    */
   public static String callingClass(int level, boolean includeLineNumber) {
      if (level < 0) return "Unknown";
      StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
      if (stacktrace.length < (level + 1)) return "Unknown";
      StackTraceElement e = stacktrace[level];
      String className = StringUtils.substringBefore(e.getFileName(), ".");
      int lineNumber = e.getLineNumber();
      if (lineNumber > 0) className += ":" + lineNumber;
      return className;
   }
   /**
    * Convenience method for {@link #callingClass(int, boolean) callingClass}, 
    * which assumes line number is not wanted.
    * @param level The stack level to examine.
    * @return the name of the calling class, or "Unknown".
    */
   public static String callingClass(int level) {
      return callingClass(level, false);
   }
   /**
    * Convenience method for {@link #callingClass(int, boolean) callingClass}, 
    * which assumes line number is wanted.
    * @param level The stack level to examine.
    * @return the name of the calling class, with line number appended if known.
    * "Unknown" if class is not known.
    */
   public static String callingClassLine(int level) {
      return callingClass(level, true);
   }

   /**
    * @return string of the form "simpleClassName.MethodName " for the method
    * which called this method.
    */
   public static String classMethod() {
      return callingClass(3) + "." + callingMethod(3) + " ";
   }

   /**
    * Logs standard class method invoked message to passed Logger. If passed
    * Logger is null, system Logger will be used.
    * 
    * @param lg logger to log message to
    */
   public static void invoked(Logger lg) {
      if (lg == null) lg = getLog();
      lg.trace(callingClass(3) + "." + callingMethod(3) + " invoked");
   }

   /**
    * Logs standard class method invoked message to system Logger
    */
   public static void invoked() {
      getLog().trace(callingClass(3) + "." + callingMethod(3) + " invoked");
   }
   
   /**
    * Add path to Classpath with reflection (URLClassLoader.addURL(URL url)
    * method is protected)
    * @param newPath path to add, absolute or relative to ROOT.
    */
   public static void addLibraryPath(String newPath) {
      try {
      URL url = Utility.getXDSIRootPath().resolve(newPath).toUri().toURL();
      URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
      Class <URLClassLoader> urlClass = URLClassLoader.class;
      Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
      method.setAccessible(true);
      method.invoke(urlClassLoader, new Object[] {url});
      Utility.logClassPath();
      } catch (Exception e) {
         Utility.exit(e);
      }
   }
   
   /**
    * Log the current classpath. (Regardless of current log level).
    */
   public static void logClassPath() {
      ClassLoader cl = ClassLoader.getSystemClassLoader();
      URL[] urls = ((URLClassLoader)cl).getURLs();

      Logger log = Utility.getLog();
      Level lvl = log.getLevel();
      log.setLevel(Level.INFO);
      for(URL url: urls){
         log.info(url.getFile());
      }
      log.setLevel(lvl);
   }
   
   /**
    * Writes the bytes to a file on disc.
    * @param bytes byte[] to write
    * @param outDir directory to write to. Must exist and be rx
    * @param fName file name. If exists, it is overwritten
    * @throws Exception on error
    */
   public static void writeToFile(byte[] bytes, Path dir, String fName) throws Exception{
      Utility.isValidPfn("output directory" , dir, PfnType.DIRECTORY, "rx");
      Path pfn = dir.resolve(fName);
      Files.write(pfn, bytes);
   }
   /**
    * Convenience method for {@link Utility#writeToFile(byte[], String, String)}
    */
   public static void writeToFile(String str, Path dir, String fName) throws Exception{
      Utility.writeToFile(str.getBytes("UTF-8"), dir, fName);
   }
   
   public static String getArg(String[] args, int arg) {
	   if (args.length > arg) {
		   String a = args[arg];
		   if (StringUtils.isBlank(a) || a.equals("-") || a.equals("_") || a.equalsIgnoreCase("null")) return null;
		   return a.trim();
	   }
	   return null;
   }
   private static Pattern pattern = Pattern.compile("\\A[A-Za-z0-9-_]+\\z");
   /**
    * Gets path to base results directory. Creates directory if needed.
    * @param testId - sut and test number. For example "iig-1001".
    * @param label unique messageId for tester. For example, "acme".
    * @return Path to an existing directory. For example: /opt/xdsi/results/acme/iig/1000
    * @throws Exception on error, usually invalid parameter value. 
    */
   public static Path getResultsDirectory(String testId, String label) throws Exception {
      // If no std directory exists, we can't make a test directory for this test
      try { getStdDirectory(testId);
      } catch (Exception e) {
         throw new Exception("No such test: " + testId);
      }
      String test = StringUtils.stripToEmpty(testId);
      Matcher matcher = pattern.matcher(label);
      if (matcher.matches() == false)
         throw new Exception ("[" + label + "] is not a valid user messageId");
      Path testPath = getXDSIRootPath().resolve("results").resolve(label).resolve(test);
      log.info("Test directory: " + testPath.toString());
      Files.createDirectories(testPath);
      return testPath;
   }
   /**
    * Gets path to test's standard directory. Must exist.
    * @param testId - sut and test number. For example "iig-1001".
    * @return Path to an existing directory. For example: /opt/xdsi/std/iig/1000
    * @throws Exception on error, invalid testId or directory not found. 
    */
   public static Path getStdDirectory(String testId) throws Exception {
      String[] s = StringUtils.split(testId, "-_");
      Path stdPath = getXDSIRootPath().resolve("std").resolve(s[0]).resolve(testId);
      log.info("Std directory: " + stdPath);
      Utility.isValidPfn(testId + " std directory", stdPath, PfnType.DIRECTORY, "r");
      return stdPath;
   }
   
  
   /**
    * Does the first string match any of the following strings? <b>Not case
    * sensitive.</b>
    * 
    * @param str String to match
    * @param matches possible matching strings
    * @return boolean <code>true</code> if the first string matches any of the
    * subsequent passed Strings, <code>false</code> otherwise.
    */
   public static boolean isOneOf(String str, String... matches) {
      for (String match : matches) {
         if (str.equalsIgnoreCase(match)) return true;
      }
      return false;
   }

   /**
    * Does the first string match any of the following strings? <b>Case
    * sensitive.</b>
    * 
    * @param str String to match
    * @param matches possible matching strings
    * @return boolean <code>true</code> if the first string matches any of the
    * subsequent passed Strings, <code>false</code> otherwise.
    */
   public static boolean isExactlyOneOf(String str, String... matches) {
      for (String match : matches) {
         if (str.equals(match)) return true;
      }
      return false;
   }
}
