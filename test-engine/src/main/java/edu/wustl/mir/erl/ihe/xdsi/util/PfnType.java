package edu.wustl.mir.erl.ihe.xdsi.util;

import java.nio.file.Path;

/**
 * Enum used to codify the distinction between a file and a directory for:
 * <ul>
 * <li/>The {@link Utility#isValidPfn(String,Path,PfnType,String) isValidPfn} 
 * methods, in which case the entry must be of this type.
 * </ul>
 */
public enum PfnType {
   /** A directory or a link to a directory. */
   DIRECTORY, 
   /** A file or a link to a file. */
   FILE
}