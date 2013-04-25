/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.direct.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LoggerUtils {
    
    public static ArrayList<String> listFilesForFolder(String folder) {
        String s = LogPathsSingleton.getLOG_ROOT() + folder;
        System.out.println("main logs path: " + s);
        File f = new File(s);
        ArrayList<String> list = new ArrayList<String>();
 
        try {
        	File[] listOfFiles = f.listFiles();
            // if(f.listFiles() != null) {
            for (File fileEntry : listOfFiles) {
                if (fileEntry.isDirectory()) {
                	String temp = fileEntry.getName();
                    list.add(temp);
                    // if the element is not a folder, ignore it.
                }
            }   
        } 
        catch (Throwable e) {}
        return list;
}

   
        
        /**
         * Not working probably because of JDK bug.
         * @param str
         * @return
         */
        public static Date parseLoggedDate(String str){
        	System.out.println(str);
        DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
        sdf.setLenient(true);
        //	DateFormat  sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
      //  String dateStr = sdf.format(str);
        
        Date d = null;
    	//System.out.println(dateStr);
    	try {
    		d = sdf.parse(str);
    	} catch (ParseException e) {
    		e.printStackTrace();
    	}
    	return d;
        }
        
        
        
        public static String readTextFileFirstLine(String path){
        	BufferedReader br = null;
        	String str = null;
      	  try {
      		 br = new BufferedReader(new FileReader(path));
      	} catch (FileNotFoundException e) {
      		// TODO Auto-generated catch block
      		e.printStackTrace();
      	}
      	try {
      		str =  br.readLine();
      	} catch (IOException e) {
      		// TODO Auto-generated catch block
      		e.printStackTrace();
      	}
      	try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      	return str;
        }
        
    }
