package gov.nist.toolkit.errorrecording

import groovy.xml.MarkupBuilder

/**
 * Created by diane on 6/29/2016.
 */
class XMLGroovyExample {
/**
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)

    def records = new XmlSlurper().parseText(writer.toString())


    xml.records() {
        car(name:'HSV Maloo', make:'Holden', year:2006) {
            country('Australia')
            record(type:'speed', 'Production Pickup Truck with speed of 271kph')
        }
        car(name:'Royale', make:'Bugatti', year:1931) {
            country('France')
            record(type:'price', 'Most Valuable Car at $15 million')
        }
    }


    assert records.car.first().name.text() == 'HSV Maloo'
    assert records.car.last().name.text() == 'Royale'
 **/

    void hello(){
        println("Hello world")
    }
}
