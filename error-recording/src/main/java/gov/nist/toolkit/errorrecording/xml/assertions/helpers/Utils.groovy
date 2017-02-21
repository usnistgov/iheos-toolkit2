package gov.nist.toolkit.errorrecording.xml.assertions.helpers

import org.w3c.tidy.Tidy

/**
 * Created by diane on 7/28/2016.
 */
class Utils {

    /**
     * Trims whitespaces including spaces, carriage returns, new lines
     * @param xml
     * @return trimmed XML
     */
    def static trimXMLWhitespaces(String xml){
        return xml.replaceAll("\\s+", "")
    }

    /**
     * Pretty-print the XML output for debug purposes
     * Source John Rellis - http://johnrellis.blogspot.com/
     * @param singleLine
     * @return
     */
    def static tidyMeUp(String str) {
        StringWriter writer = new StringWriter()
        Tidy tidy = new Tidy()
        tidy.identity {
            setEscapeCdata(false)//leave cdata untouched
            setIndentCdata(true)//indent the CData
            setXmlTags(true)//working with xml not html
            parse(new StringReader(str), writer)
        }
        writer.toString()
    }
}
