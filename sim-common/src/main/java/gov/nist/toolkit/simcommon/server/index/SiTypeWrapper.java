package gov.nist.toolkit.simcommon.server.index;

import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;

public class SiTypeWrapper {
    String className;
    Closeable indexer;
    FSDirectory fsDirectory;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Closeable getIndexer() {
        return indexer;
    }

    public void setIndexer(Closeable object) {
        this.indexer = object;
    }

    public FSDirectory getFsDirectory() {
        return fsDirectory;
    }

    public void setFsDirectory(FSDirectory fsDirectory) {
        this.fsDirectory = fsDirectory;
    }
}
