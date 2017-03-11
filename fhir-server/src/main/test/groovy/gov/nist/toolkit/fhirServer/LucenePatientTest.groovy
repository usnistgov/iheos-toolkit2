package gov.nist.toolkit.fhirServer

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.index.*
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import spock.lang.Specification

/**
 *
 */
class LucenePatientTest extends Specification {
    File indexDir

    class Resource {
        String field
        String path

        Resource(String _field, String _path) {
            field = _field
            path = _path
        }
    }

    IndexWriter indexWriter

    def setup() {
        File outLocation = new File(this.getClass().getResource('/output/finder.txt').toURI().path).parentFile
        indexDir = new File(outLocation, 'lucine.index')
    }

    def 'create index and search'() {

        when: 'create index'
        boolean isOpen = createIndex(indexDir)

        then:
        isOpen

        when:
        addResource(indexWriter, new Resource('pid', '/dev/null1'))
        addResource(indexWriter, new Resource('hid', '/dev/null2'))
        addResource(indexWriter, new Resource('hid', '/dev/null3'))
        finish()

        then:
        true

        when:  'initialize for search'
        IndexSearcher indexSearcher = openIndex(indexDir)
        Term term1 = new Term('field', 'pid')
        Term term2 = new Term('field', 'hid')

        Query query1 = new TermQuery(term1)
        Query query2 = new TermQuery(term2)

        TopDocs docs1 = indexSearcher.search(query1, 10)
        TopDocs docs2 = indexSearcher.search(query2, 10)

        then:
        docs1.totalHits == 1
        docs2.totalHits == 2

    }

    boolean createIndex(File index){
        try {
            Directory dir = FSDirectory.open(index.toPath())
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

    void addResource(IndexWriter indexWriter, Resource resource){
        Document doc = new Document()
        doc.add(new StringField('field', resource.field, Field.Store.YES))
        doc.add(new StringField('path', resource.path, Field.Store.YES))
        try {
            indexWriter.addDocument(doc);
        } catch (IOException ex) {
            System.err.println("Error adding documents to the index. " +  ex.getMessage());
        }
    }

    void finish(){
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }
}
