package gov.nist.toolkit.valregmsg.xdm;

import gov.nist.toolkit.utilities.io.Io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class ZipDecoder {

	OMap parse(InputStream in) throws ZipException, IOException {
		OMap contents = new OMap();

		ZipInputStream zis = null;
		zis = new ZipInputStream(in);

		while (true) {
			ZipEntry entry = null;
			entry = zis.getNextEntry();
			if (entry == null)
				break;
			if (entry.getName().startsWith("__"))
				continue;
			if (entry.getName().contains("/."))
				continue;
			if (entry.isDirectory())
				System.out.print("Directory: ");
			else
				System.out.print("File: ");
			String entryName = entry.getName().toUpperCase();
			System.out.println(entryName);

			byte[] bytes = null;

			bytes = Io.getBytesFromInputStream(zis);

			contents.put(new Path(entryName), new ByteArray(bytes));

		}

		return contents;

	}

}
