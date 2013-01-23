package gov.nist.direct.logging;

import java.io.File;
import java.io.IOException;

public interface Logger {

	public boolean log(Object o, File f) throws IOException;
}
