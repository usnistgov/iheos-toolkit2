package gov.nist.toolkit.errorrecording

import gov.nist.toolkit.errorrecording.*
import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

/**
 * Created by diane on 6/29/2016.
 */
class XMLGroovyExampleTest extends Specification {

    /**
     * Tests basic XML generation from class XMLGroovyExample
     */
    def 'Generate XML'() {
        println("--- Generate XML ---")

        setup:
        XMLGroovyExample ex = new XMLGroovyExample()

        when:
        def res = ex.generateXml()
        println(res)

        then:
        res == "<people>\n" +
                "  <person>\n" +
                "    <firstName>John</firstName>\n" +
                "    <lastName>Doe</lastName>\n" +
                "    <age>25</age>\n" +
                "  </person>\n" +
                "  <person>\n" +
                "    <firstName>Jane</firstName>\n" +
                "    <lastName>Smith</lastName>\n" +
                "    <age>31</age>\n" +
                "  </person>\n" +
                "</people>"
    }

    /**
     * Tests XML modification / adding an element
     */
    def 'Modify XML with XMLSlurper'(){
        println("--- Modify XML with XMLSlurper ---")
        setup:
        XMLGroovyExample ex = new XMLGroovyExample()

        // End result we are looking for:
        def goal = '''
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
                    <person>
                        <firstName>Oscar</firstName>
                        <lastName>Smith</lastName>
                        <age>60</age>
                    </person>
                  </people>
                '''
            goal = ex.trimXMLWhitespaces(goal)

        when:
        def res = ex.modifyXmlWithXmlSlurper()
        println(res)
        res = ex.trimXMLWhitespaces(res)

        then:
        res == goal
    }

    def 'Modify XML with XMLParser'(){
        println("--- Modify XML with XMLParser ---")

        setup:
        XMLGroovyExample ex = new XMLGroovyExample()

        // End result we are looking for:
        def goal = '''
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
                    <person>
                        <firstName>Oscar</firstName>
                        <lastName>Smith</lastName>
                        <age>60</age>
                    </person>
                  </people>
                '''
        goal = ex.trimXMLWhitespaces(goal)

        when:
        def res = ex.modifyXmlWithXmlParser()
        println(res)
        res = ex.trimXMLWhitespaces(res)

        then:
        res == goal
    }


}

