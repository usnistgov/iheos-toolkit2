package gov.nist.toolkit.fhir.support

import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import spock.lang.Specification

/**
 *
 */
class LucenePatientTest extends Specification {
    File indexDir
    Indexer indexer

    def setup() {
        File outLocation = new File(this.getClass().getResource('/output/finder.txt').toURI().path).parentFile
        indexDir = new File(outLocation, 'lucine.indexer')
        indexer = new Indexer(indexDir)
    }

    def 'create index and search'() {

        when: 'create indexer'
        boolean isOpen = indexer.createIndex()

        then:
        isOpen

        when:
        indexer.addResource(new ResourceIndex(new ResourceIndexItem('pid', '1'), '/dev/null1'))
        indexer.addResource(new ResourceIndex(new ResourceIndexItem('hid', '2'), '/dev/null2'))
        indexer.addResource(new ResourceIndex(new ResourceIndexItem('hid', '3'), '/dev/null3'))
        indexer.finish()

        then:
        true

        when:  'initialize for search'
        IndexSearcher indexSearcher = indexer.openIndex(indexDir)
        Term term1 = new Term('pid', '1')
        Term term2 = new Term('hid', '2')

        Query query1 = new TermQuery(term1)
        Query query2 = new TermQuery(term2)

        TopDocs docs1 = indexSearcher.search(query1, 10)
        TopDocs docs2 = indexSearcher.search(query2, 10)

        then:
        docs1.totalHits == 1
        docs2.totalHits == 1

    }

}
