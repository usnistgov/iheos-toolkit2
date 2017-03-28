package gov.nist.toolkit.fhir.support

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
 *
 */
class ResDbIndexer {
    IndexWriter indexWriter
    File indexDir

    ResDbIndexer(File _indexDir) {
        indexDir = _indexDir
    }

    boolean createIndex() {
        try {
            Directory dir = FSDirectory.open(indexDir.toPath())
            IndexWriterConfig iwc = new IndexWriterConfig()
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE)
            indexWriter = new IndexWriter(dir, iwc)
            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    }

    IndexSearcher openIndex(File index) {
        FSDirectory indexDirectory = FSDirectory.open(index.toPath())
        IndexReader indexReader = DirectoryReader.open(indexDirectory)
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher
    }

    void addResource(ResourceIndex resource) {
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

    void finish() {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }
}
