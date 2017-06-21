package gov.nist.toolkit.fhir.support

import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory

/**
 * Search the index support.
 */
class SimSearcher {
    FSDirectory indexDirectory = null
    IndexReader indexReader = null

    /**
     * searches are run against IndexSearcher
     * @param index
     * @return
     */
    IndexSearcher openIndexForSearching(File index) {
        indexDirectory = FSDirectory.open(index.toPath())
        indexReader = DirectoryReader.open(indexDirectory)
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher
    }

    void close() {
        if (indexDirectory) indexDirectory.close()
        indexDirectory = null
        if (indexReader) indexReader.close()
        indexReader = null
    }

}
