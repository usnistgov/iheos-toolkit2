package gov.nist.toolkit.fhir.support

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
/**
 * Lucene index for a single Simulator
 * This is only used by SimIndexer, hence the
 * protected status.
 */
class ResDbIndexer {
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
        try {
            Directory dir = FSDirectory.open(indexDir.toPath())
            IndexWriterConfig iwc = new IndexWriterConfig()
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
            indexWriter = new IndexWriter(dir, iwc)
            openForWriting = true
            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    }

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
        doc.add(new StringField('path', resource.path, Field.Store.YES))
        println 'indexing ' + doc
        try {
            indexWriter.addDocument(doc);
        } catch (IOException ex) {
            System.err.println("Error adding documents to the index. " + ex.getMessage());
        }
    }

    protected commit() {
            indexWriter.commit();
    }

    protected close() {
        if (indexWriter)
            indexWriter.close()
        indexWriter = null
    }

}
