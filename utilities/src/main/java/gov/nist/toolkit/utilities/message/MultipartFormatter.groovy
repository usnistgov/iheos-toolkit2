package gov.nist.toolkit.utilities.message

import gov.nist.toolkit.utilities.xml.OMFormatter

/**
 *
 */
class MultipartFormatter {

    static String format(String multipart) {
        multipart = multipart.trim()
        StringBuilder output = new StringBuilder()
        StringBuilder buf = new StringBuilder()
        boolean xmlMode = false

        multipart.eachLine { String line ->
            line = line.trim()
            if (xmlMode) {
                if (line.startsWith('--')) {
                    xmlMode = false

                    String input = buf.toString()
                    if (input.trim().startsWith('<')) {
                        Node inputNode = new XmlParser().parseText(input)
                        def xmlOutput = new StringWriter()
                        def xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), '  ')
                        xmlNodePrinter.with {
                            preserveWhitespace = false
                        }
                        xmlNodePrinter.print(inputNode)
                        output.append(xmlOutput.toString())
                        output.append('\n')
                    }
                    else
                        output.append(input)
                    buf = new StringBuilder()

                    output.append(line).append('\n')
                } else {
                    buf.append(line).append('\n')
                }
            } else {
                output.append(line).append('\n')
                if (line == '') {
                    xmlMode = true
                   // output.append('\n')
                }
            }
        }
        if (buf.size() > 0) {
            String s = buf.toString().trim();
            if (s.startsWith('<'))
                s = new OMFormatter(s).toString();
            output.append(s)
        }
        return output.toString()
    }
}
