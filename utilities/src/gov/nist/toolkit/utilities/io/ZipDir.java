package gov.nist.toolkit.utilities.io;

import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDir {

	public ByteArrayOutputStream toByteArrayOutputStream(String dir2zip) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			toOutputStream(dir2zip, baos);
//			//create a ZipOutputStream to zip the data to 
//			ZipOutputStream zos = new 
////			ZipOutputStream(new FileOutputStream("/Users/bill/tmp/zipwork/zip.zip"));
//			ZipOutputStream(baos);
//			//assuming that there is a directory named inFolder (If there 
//			//isn't create one) in the same directory as the one the code 
//			// runs from, 
//			//call the zipDir method 
//			zipDir(dir2zip, zos); 
//			//close the stream 
//			zos.close(); 
			return baos;
	}
	
	public void toOutputStream(String dir2zip, OutputStream baos) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//create a ZipOutputStream to zip the data to 
		ZipOutputStream zos = new 
//		ZipOutputStream(new FileOutputStream("/Users/bill/tmp/zipwork/zip.zip"));
		ZipOutputStream(baos);
		//assuming that there is a directory named inFolder (If there 
		//isn't create one) in the same directory as the one the code 
		// runs from, 
		//call the zipDir method 
		zipDir(dir2zip, zos); 
		//close the stream 
		zos.close(); 
//		return baos;
}

	public void toFile(String dir2zip, File outfile) throws IOException {
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//create a ZipOutputStream to zip the data to 
		ZipOutputStream zos = new 
		ZipOutputStream(new FileOutputStream(outfile));
		//ZipOutputStream(baos);
		//assuming that there is a directory named inFolder (If there 
		//isn't create one) in the same directory as the one the code 
		// runs from, 
		//call the zipDir method 
		zipDir(dir2zip, zos); 
		//close the stream 
		zos.close(); 
	}
	
	void zipDir(String dir2zip, ZipOutputStream zos) {
		File d = new File(dir2zip);
		File parent = d.getParentFile();
		int parentSize = parent.toString().length();
		zipDir2(dir2zip, parentSize, zos);
	}

	void zipDir2(String dir2zip, int parentSize, ZipOutputStream zos) 
	{ 
		try 
		{ 
			//create a new File object based on the directory we 
			// have to zip    
			File zipDir = new File(dir2zip); 
			//get a listing of the directory content 
			String[] dirList = zipDir.list(); 
			byte[] readBuffer = new byte[2156]; 
			int bytesIn = 0; 
			//loop through dirList, and zip the files 
			for(int i=0; i<dirList.length; i++) 
			{ 
				File f = new File(zipDir, dirList[i]); 
				if(f.isDirectory()) 
				{ 
					//if the File object is a directory, call this 
					//function again to add its content recursively 
					String filePath = f.getPath();
					zipDir2(filePath, parentSize, zos); 
					//loop again 
					continue; 
				} 
				//if we reached here, the File object f was not a directory 
				//create a FileInputStream on top of f 
				FileInputStream fis = new FileInputStream(f); 
				// create a new zip entry 
				ZipEntry anEntry = new ZipEntry(f.getPath().substring(parentSize)); 
				//place the zip entry in the ZipOutputStream object 
				zos.putNextEntry(anEntry); 
				//now write the content of the file to the ZipOutputStream 
				while((bytesIn = fis.read(readBuffer)) != -1) 
				{ 
					zos.write(readBuffer, 0, bytesIn); 
				} 
				//close the Stream 
				fis.close(); 
			} 
		} 
		catch(Exception e) 
		{ 
			System.out.println(ExceptionUtil.exception_details(e)); 
		} 


	}
	
	
	public static void main(String[] args) {
		try {
		new ZipDir().toFile("/Users/bill/tmp/toolkit/simdb/93c83115_eaf3_4174_9f99_adcd51a4a480/registry/rb/2010_11_23_16_39_44_111",
				new File("/Users/bill/tmp/zipwork/zip.zip"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
