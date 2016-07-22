package gov.nist.toolkit.errorrecording

import groovy.xml.*

/**
 * Created by diane on 6/29/2016.
 */

/**
 * Uses Groovy MarkupBuilder
 */

class XMLGroovyExample {

    // TODO use annotation name in front of class to do static typechecking
    //typechecked

    def generateXml() {
        def stringWriter1 = new StringWriter()
        def peopleBuilder = new MarkupBuilder(stringWriter1)
        def name = "John"

        peopleBuilder.people {
            person {
                firstName(name)
                lastName('Doe')
                age(25)
            }
            person {
                firstName('Jane')
                lastName('Smith')
                age(31)
            }
        }

        // For testing purposes
        return stringWriter1.toString()
    }

    /**
     * Uses Groovy XmlSlurper and append node. XMLSlurper works only to append a node to one's list of children.
     * @return
     */
    def modifyXmlWithXmlSlurper() {
        // Generate and parse current / old XML
        def peopleXml = '''
            <people>
                 <person>
                    <firstName>John</firstName>
                    <lastName>Doe</lastName>
                    <age>25</age>
                  </person>
                  <person>
                    <firstName>Jane</firstName>
                    <lastName>Smith</lastName>
                    <age>31</age>
                  </person>
                </people>
            '''

        // Define the new element to add
        def newPersonXml  = '''
                  <person>
                    <firstName>Oscar</firstName>
                    <lastName>Smith</lastName>
                    <age>60</age>
                  </person>
            '''

        // Convert both main XML and element to add to parsed XML records form
        def peopleRecords = new XmlSlurper().parseText(peopleXml)
        def newRecord = new XmlSlurper().parseText(newPersonXml)

        // Append the new element
        peopleRecords.appendNode(newRecord)

        // Convert back to XML. This part is not always necessary but will be part of most processes where the goal
        // is to generate XML for display.
        def newPeopleXml = new StreamingMarkupBuilder().bind {
            mkp.yield peopleRecords
        }

        // For testing purposes
        return newPeopleXml.toString()
    }

    /**
     * Uses Groovy XmlParser and append node. XmlParser provides more flexibility vs XmlSlurper.
     * @return
     */
    def modifyXmlWithXmlParser() {
        // Generate and parse current / old XML
        def peopleXml = '''
            <people>
                 <person>
                    <firstName>John</firstName>
                    <lastName>Doe</lastName>
                    <age>25</age>
                  </person>
                  <person>
                    <firstName>Jane</firstName>
                    <lastName>Smith</lastName>
                    <age>31</age>
                  </person>
                </people>
            '''

        // Define the new element to add
        def newPersonXml  = '''
                  <person>
                    <firstName>Oscar</firstName>
                    <lastName>Smith</lastName>
                    <age>60</age>
                  </person>
            '''

        // Convert both main XML and element to add to parsed XML records form
        def peopleRecords = new XmlParser().parseText(peopleXml)
        def people = peopleRecords.children()
        def newRecord = new XmlParser().parseText(newPersonXml)

        // Append the new element
        people.add(2, newRecord)

        // Translate back into a String
        StringWriter sw = new StringWriter()
        new XmlNodePrinter(new PrintWriter(sw)).print(peopleRecords)

        // For testing purposes
        return sw.toString()
    }


    /**
     * Trims whitespaces including spaces, carriage returns, new lines
     * @param xml
     * @return trimmed XML
     */
    def trimXMLWhitespaces(String xml){
        return xml.replaceAll("\\s+", "")
    }
}
