package gov.nist.toolkit.errorrecording

import gov.nist.toolkit.errorrecording.*
import groovy.xml.XmlUtil
import spock.lang.Specification

/**
 * Created by diane on 6/29/2016.
 */
class XMLGroovyExampleTest extends Specification {

        def 'Generate XML'() {

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
    }

