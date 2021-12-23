/*
 * Copyright (c) 2015 Washington University in St. Louis All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. The License is available at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: Initial author: Ralph Moulton / MIR WUSM IHE Development
 * Project moultonr@mir.wustl.edu
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import java.util.logging.Logger;

/**
 * Utility class, replaces variables of form ${varName} in string with values
 * from map. Methods other than .get() return the Plug instance to allow
 * chaining. Use:
 * <ul>
 * <li>Pass string with variables to {@link #Plug(String) constructor}.</li>
 * <li>Use {@link #set(Object, Object) set} method to add name-value pairs to
 * map.</li>
 * <li>Use {@link #get() get} method to perform substitution(s) and retrieve
 * the modified String. For example:
 * 
 * <pre>
 * String query = new Plug(
 *    &quot;select usename from pg_user where usename = lower('${user}');&quot;).set(&quot;user&quot;,
 *    un).get();
 * </pre>
 * </li>
 * 
 * </ul>
 */
public class Plug implements Serializable {
   private static final long serialVersionUID = 1L;

   private static Logger log = Utility.getLog();

   /**
    * String containing plug in variable notations of the form ${name}, which
    * are to be replaced with values from name-value pairs. For example:
    * "create user ${userName}".
    */
   String strng;

   Map <String, String> map = new HashMap <>();

   /**
    * Builds new Plug instance from passed String.
    * 
    * @param string {@link #strng variable string}
    */
   public Plug(String string) {
      this.strng = string;
   }

   /**
    * Builds new Plug instance from contents of file at passed
    * {@link java.nio.file.Path Path}.
    * 
    * @param pathToFile path to file whose contents are to be used as the string
    * to plug. Must be valid readable text file.
    * @throws Exception if pathToFile does not exists, is not a valid file, is
    * not readable, or an I/O error occurs on reading it.
    */
   public Plug(Path pathToFile) throws Exception {
      Utility.isValidPfn("Plug input file", pathToFile, PfnType.FILE, "r");
      File file = pathToFile.toFile();
      this.strng = FileUtils.readFileToString(file, "UTF-8");
   }

   /**
    * Builds new Plug instance from contents of file at passed
    * {@link java.nio.file.Path Path}.
    * 
    * @param file whose contents are to be used as the string to plug. Must be
    * valid readable text file.
    * @throws Exception if pathToFile does not exists, is not a valid file, is
    * not readable, or an I/O error occurs on reading it.
    */
   public Plug(File file) throws Exception {
      Utility.isValidPfn("Plug input file", file, PfnType.FILE, "r");
      this.strng = FileUtils.readFileToString(file, "UTF-8");
   }

   /**
    * Appends the argument string to the {@link #strng variable string}.
    * 
    * @param string to append
    * @return this Plug instance, allowing method chaining.
    */
   public Plug append(String string) {
      this.strng += string;
      return this;
   }

   /**
    * Adds a key-value pair to the map used to replace parameters in the
    * {@link #strng variable string} in ${key} form with the corresponding
    * value. Any type can be used; the {@link #toString()} values will be put
    * into the map. If either of the parameters are null, a warning will be
    * logged and the empty string will be used.
    * 
    * @param key Object whose .toString() value will be the key which will be
    * matched to the string inside the ${} when making substitutions.
    * @param value Object whose .toString() value will be substituted for ${key}
    * in the string.
    * @return this Plug instance, allowing method chaining.
    */
	public Plug set(Object key, Object value) {
		try {
			if (key == null && value == null)
				throw new Exception("key & value null");
			if (key == null)
				throw new Exception("null key, v=" + value.toString());
			if (value == null)
				throw new Exception("k=" + key.toString() + " has null value");
		} catch (Exception e) {
			log.warning(Utility.getEM(e));
		}
		String k = smish(key);
		String v = smish(value);
		map.put(k, v);
		return this;
	}

   /**
    * @return the value of the {@link #strng variable string} after all
    * instances of ${key} where an entry for "key" exists in the map are
    * replaced with the corresponding value from the map.
    */
   public String get() {
      return StrSubstitutor.replace(strng, map);
   }
   
   /** 
    * Gets the value of the {@link #strng variable string} after all
    * instances of ${key} where an entry for "key" exists in the map are
    * replaced with the corresponding value from the map. Also writes the 
    * string to a file using UTF-8 encoding.
    * @param pathToFile {@link java.nio.file.Path Path} to write the string to.
    * @param append boolean, if true string will be appended to file rather than
    * overwriting it.
    * @return The same string written to the file, in case you want it for 
    * anything
    * @throws Exception on error creating or writing to file.
    */
   public String get(Path pathToFile, boolean append) throws Exception {
      String out = StrSubstitutor.replace(strng, map);
      FileUtils.writeStringToFile(pathToFile.toFile(), out, "UTF-8", append);
      return out;
   }
   
   /** 
    * Gets the value of the {@link #strng variable string} after all
    * instances of ${key} where an entry for "key" exists in the map are
    * replaced with the corresponding value from the map. Also writes the 
    * string to a file using UTF-8 encoding.
    * @param file {@link java.io.File File} to write the string to.
    * @param append boolean, if true string will be appended to file rather than
    * overwriting it.
    * @return The same string written to the file, in case you want it for 
    * anything
    * @throws Exception on error creating or writing to file.
    */
   public String get(File file, boolean append) throws Exception {
      String out = StrSubstitutor.replace(strng, map);
      FileUtils.writeStringToFile(file, out, "UTF-8", append);
      return out;
   }

   private String smish(Object o) {
      if (o == null) return "";
      String str = o.toString();
      if (StringUtils.isBlank(str) || str.equalsIgnoreCase("null")) return "";
      return str;
   }

}
