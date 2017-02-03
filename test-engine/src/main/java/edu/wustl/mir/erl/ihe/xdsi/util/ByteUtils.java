/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * byte array pattern matcher.
 */
public class ByteUtils {
   /**
    * Search the data byte array for the first occurrence of the byte array
    * pattern.
    * 
    * @param data array to search
    * @param pattern to look for
    * @param ignoreSpaces if true, spaces in data are ignored. If true, there
    * should be no spaces in pattern
    * @return index of first occurrence of pattern, or -1 if not found.
    */
   public static int indexOf(byte[] data, byte[] pattern, boolean ignoreSpaces) {
      int[] failure = computeFailure(pattern);
      byte space = " ".getBytes()[0];

      int j = 0;

      for (int i = 0; i < data.length; i++ ) {
         if (ignoreSpaces && data[i] == space) continue;
         while (j > 0 && pattern[j] != data[i]) {
            j = failure[j - 1];
         }
         if (pattern[j] == data[i]) {
            j++ ;
         }
         if (j == pattern.length) { return i - pattern.length + 1; }
      }
      return -1;
   }
   
   /**
    * Search the data byte array for the last occurrence of the byte array
    * pattern.
    * 
    * @param data array to search
    * @param pattern pattern to look for
    * @return index of last occurrence of pattern, or -1 if not found.
    */
   public static int lastIndexOf(byte[] data, byte[]pattern) {
      int returnValue = -1; int p;
      byte[] work = ByteUtils.subbytes(data, 0, false);
      while (true) {
         p = indexOf(work, pattern, false);
         if (p == -1) return returnValue;
         returnValue = p;
         work = subbytes(work, p, false);
      }
   }
   
   /**
    * Concatenates the passed byte[]'s into a single byte[].
    * @param components byte[]'s to concatenate
    * @return new byte[] combining argument byte[]'s
    */
   public static byte[] concat(byte[]... components) {
      List<Byte> bytes = new ArrayList<>();
      for (byte[] component : components) {
         for (byte b : component) {
            bytes.add(b);
         }
      }
      byte[] b = new byte[bytes.size()];
      for (int i = 0; i < bytes.size(); i++) b[i] = bytes.get(i);
      return b;
   }
      
   /**
    * Returns concatenation of passed strings as a single byte[];
    * @param strings to concatenate
    * @return byte[] of strings, using "UTF-8" encoding.
    */
   public static byte[] toBytes(String...strings) {
      String in = "";
      for (String s : strings) in += s;
      try {
         return in.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Computes the failure function using a boot-strapping process, where the
    * pattern is matched against itself.
    */
   private static int[] computeFailure(byte[] pattern) {
      int[] failure = new int[pattern.length];

      int j = 0;
      for (int i = 1; i < pattern.length; i++ ) {
         while (j > 0 && pattern[j] != pattern[i]) {
            j = failure[j - 1];
         }
         if (pattern[j] == pattern[i]) {
            j++ ;
         }
         failure[i] = j;
      }

      return failure;
   }

   /**
    * Return a new byte array containing a sub-portion of the source array
    * 
    * @param srcBegin The beginning index (inclusive)
    * @return The new, populated byte array
    */
   public static byte[] subbytes(byte[] source, int srcBegin, boolean stripWhiteSpace) {
      return subbytes(source, srcBegin, source.length, stripWhiteSpace);
   }

   /**
    * Return a new byte array containing a sub-portion of the source array
    * @param source byte array
    * @param srcBegin The beginning index (inclusive)
    * @param srcEnd The ending index (exclusive)
    * @param stripWhiteSpace if true, whitespace in source is omitted.
    * @return The new, populated byte array
    */
   public static byte[] subbytes(byte[] source, int srcBegin, int srcEnd, boolean stripWhiteSpace) {
      byte destination[];

      try {
      destination = new byte[srcEnd - srcBegin];
      getBytes(source, srcBegin, srcEnd, destination, 0);
      if (stripWhiteSpace) {
         String src = new String(destination, "UTF-8").replaceAll("\\s+", " ");
         destination = src.getBytes("UTF-8");
      }
      return destination;
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
         return null;
      }
   }

   /**
    * Copies bytes from the source byte array to the destination array
    * 
    * @param source The source array
    * @param srcBegin Index of the first source byte to copy
    * @param srcEnd Index after the last source byte to copy
    * @param destination The destination array
    * @param dstBegin The starting offset in the destination array
    */
   public static void getBytes(byte[] source, int srcBegin, int srcEnd, 
         byte[] destination, int dstBegin) {
      System.arraycopy(source, srcBegin, destination, dstBegin, srcEnd - srcBegin);
   }
}
