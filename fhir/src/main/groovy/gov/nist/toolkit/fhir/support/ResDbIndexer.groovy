package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.log4j.Logger
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
/**
 * Lucene index for a single Simulator
 * This is only used by SimIndexer, hence the
 * protected status.
 */
class ResDbIndexer {
    static private final Logger logger = Logger.getLogger(ResDbIndexer.class);
    FSDirectory indexDirectory = null
    IndexWriter indexWriter
    File indexDir
    boolean openForWriting = false

    ResDbIndexer(File _indexDir) {
        indexDir = _indexDir
    }

    /********************************************************
     *
     * Update the index support.  Called only by SimIndexer
     *
     ********************************************************/

    /**
     * After opening, index must be closed using finish()
     * @return
     */
    protected openIndexForWriting() {
        logger.info("open index for writing ${indexDir}")
        try {
            Directory dir = FSDirectory.open(indexDir.toPath())
            IndexWriterConfig iwc = new IndexWriterConfig()
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
            iwc.setCommitOnClose(true)
            indexWriter = new IndexWriter(dir, iwc)
            indexDirectory = dir
            openForWriting = true
            return true
        } catch (Exception e) {
            System.err.println("Error opening the index. " + ExceptionUtil.exception_details(e));
            throw new Exception("Error opening the Lucene index", e)
        }
    }

    protected commit() {
//        indexWriter.commit();
        indexWriter.close()
        indexWriter = null
    }

    protected close() {
        logger.info("close ${indexDir}")
        try {
            if (indexWriter)
                indexWriter.close()
            indexWriter = null


            if (indexReader) indexReader.close()
            indexReader = null
            if (indexDirectory) {
                indexDirectory.close()
                if (indexDirectory.checkPendingDeletions())
                    indexDirectory.deletePendingFiles()
                indexDirectory = null
            }
        } catch (Exception e) {
            logger.fatal("close of ${indexDir} failed - ${e.getMessage()}")
        }
    }

    static String PATH_FIELD = 'path'

    /**
     * add details of a resource to the index.  Called only by
     * SimIndexer.ResourceIndexer#index
     * @param resource
     */
    protected addResource(ResourceIndex resource) {
        Document doc = new Document()
        resource.items.each { ResourceIndexItem item ->
            doc.add(new StringField(item.field, item.value, Field.Store.YES))
        }
        doc.add(new StringField(PATH_FIELD, resource.path, Field.Store.YES))
        println 'indexing ' + doc
        try {
            indexWriter.addDocument(doc);
        } catch (IOException ex) {
            System.err.println("Error adding documents to the index. " + ex.getMessage());
        }
    }

    def addResource(ResourceIndexSet resourceIndexSet) {
        resourceIndexSet.items.each { addResource(it)}
    }

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
}
