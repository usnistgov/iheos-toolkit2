package gov.nist.toolkit.fhir.simulators.sim.rep;

import java.io.File;
import java.nio.file.Path;

public class RepIndexSerializer {
    // use parent as path since index file may not have been created yet
    static public Path getRelativePath(String filename, Path absolute) {
        Path index = new File(filename).toPath().getParent();
        Path p = index.relativize(absolute);
        return p;
    }

    static public boolean canBeRelativized(String filename, Path absolute) {
        try {
            getRelativePath(filename, absolute);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public Path getAbsolutePath(String filename, Path relative) {
        Path index = new File(filename).toPath().getParent();
        return index.resolve(relative);
    }
}
