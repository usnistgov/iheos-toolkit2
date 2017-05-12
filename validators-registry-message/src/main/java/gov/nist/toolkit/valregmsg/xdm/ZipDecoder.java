package gov.nist.toolkit.valregmsg.xdm;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.utilities.io.Io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class ZipDecoder {
	IErrorRecorder er = null;

	public ZipDecoder() {}

	public ZipDecoder(IErrorRecorder er) { this.er = er; }

	OMap parse(InputStream in) throws ZipException, IOException {
		OMap contents = new OMap();

		ZipInputStream zis = null;
		zis = new ZipInputStream(in);

		while (true) {
			ZipEntry entry = null;
			entry = zis.getNextEntry();
			if (entry == null)
				break;
			System.out.println("Item: " + entry.getName());
			if (entry.getName().startsWith("__"))
				continue;
			if (entry.getName().contains("/."))
				continue;
			if (entry.isDirectory()) {
				// Extra content not restricted to ISO 9660
				if (er != null && entry.getName().contains("IHE_XDM"))
					new ValidateISO9660(er).run(entry.getName());
			}
			else {
				// Extra content not restricted to ISO 9660
				if (er != null && entry.getName().contains("IHE_XDM"))
					new ValidateISO9660(er).run(entry.getName());
			}
			String entryName = entry.getName().toUpperCase();

			byte[] bytes = null;

			bytes = Io.getBytesFromInputStream(zis);

			contents.put(new Path(entryName), new ByteArray(bytes));

		}

		return contents;

	}

}
