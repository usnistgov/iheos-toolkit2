package gov.nist.toolkit.configDatatypes.server

import gov.nist.toolkit.configDatatypes.server.datatypes.Multipart
import gov.nist.toolkit.configDatatypes.server.datatypes.Part

/**
 *
 */
class MultipartParser {
    static Multipart parse(String header, String body) {
        Multipart multipart = new Multipart()
        multipart.startPartId = parseStart(header)

        Part part = new Part()
        body = body.trim()
        StringBuilder output = new StringBuilder()
        StringBuilder buf = new StringBuilder()
        boolean xmlMode = true

        body.eachLine { String line ->
            line = line.trim()
            if (xmlMode) {
                if (line.startsWith('--')) {
                    xmlMode = false

                    // end of body - input contains XML
                    if (buf.size() > 0) {
                        part.body = buf.toString()
                        multipart.parts.add(part)
                        part = new Part()
                    }

                    buf = new StringBuilder()

                    //output.append(line).append('\n')
                } else {
                    buf.append(line).append('\n')
                }
            } else {
                output.append(line).append('\n')
                if (line == '') {
                    xmlMode = true
                    // end of header - output contains header
                    part.header = output.toString()
                    part.id = parsePartId(part.header)
                    output = new StringBuilder()
                }
            }
        }
        return multipart
    }

    static String parsePartId(String partHeader) {
        String id = null
        partHeader.eachLine { String line ->
            if (line.toLowerCase().startsWith('content-id')) {
                int colonI = line.indexOf(':')
                if (colonI > -1) {
                    String value = line.substring(colonI+1).trim()
                    if (value.startsWith('<') && value.endsWith('>')) {
                        id = value.substring(1, value.size()-1)
                    }
                }
            }
        }

        return id
    }

    static String parseStart(String messageHeader) {
        String startId = null
        messageHeader.eachLine { String line ->
            if (line.toLowerCase().startsWith('content-type')) {
                int colonI = line.indexOf(':')
                if (colonI > -1) {
                    String value = line.substring(colonI+1).trim()
                    Map params = parseHeaderParams(value)
                    if (params['start']) {
                        if (params['start'].startsWith('"'))
                            startId = trim(params['start'], '"<', '>"')
                        else
                            startId = trim(params['start'], '<', '>')
                    }
                }
            }
        }
    }

    private static Map parseHeaderParams(String header) {
        Map params=[:]
        def parts = header.split(';')
        parts = parts.collect { it.trim() }
        parts.each {
            if (it.contains('=')) {
                def (name, value) = it.split('=')
                if (name && value) {
                    params[name.toLowerCase()] = value
                }
            }
        }
        return params
    }

    private static trim(String input, String left, String right) {
        if (input.startsWith(left) && input.endsWith(right))
            return input.substring(left.size(), input.size() - right.size())
        return input
    }
}
