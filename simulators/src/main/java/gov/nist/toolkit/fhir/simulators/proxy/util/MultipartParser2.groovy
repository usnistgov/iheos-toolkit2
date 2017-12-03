package gov.nist.toolkit.fhir.simulators.proxy.util

import gov.nist.toolkit.utilities.io.Io
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType

class MultipartParser2 {

    static List<BinaryPartSpec> parse(String x) {
        def boundary = null
        boolean inheader = false
        String contentType = null
        String contentId = null
        String contentEncoding = null
        def parts = [:]
        StringBuilder buf = new StringBuilder()

        int partCount = 0
        x.eachLine { String line ->
            line = line + '\n'
            if (!boundary) {
                if (line.trim().startsWith('--')) {
                    boundary = line.trim()
                    inheader = true
                }
                return
            }

            if (line.startsWith(boundary)) {
                if (!contentId)
                    contentId = "Part${partCount++}"
                parts[contentId] = new ByteArrayEntity(buf.toString().bytes, ContentType.parse(contentType))
                buf = new StringBuilder()
                inheader = true
                contentType = null
                contentId = null
                contentEncoding = null
                return
            }
            if (line.trim() == '' && inheader) {
                inheader = false
                return // end of header
            }
            if (inheader) {
                def partss = line.split(':', 2)
                if (partss[0].equalsIgnoreCase('content-type')) {
                    contentType = partss[1].split(';')[0].trim()
                }
                if (partss[0].equalsIgnoreCase('content-id')) {
                    contentId = partss[1].split(';')[0].trim()
                }
                if (partss[0].equalsIgnoreCase('content-encoding')) {
                    contentEncoding = partss[1].split(';')[0].trim()
                }
                return
            }
            buf.append(line)
        }
        parts.collect { String contentId1, HttpEntity entity ->
            Header h = entity.contentType
            new BinaryPartSpec(contentType, Io.getBytesFromInputStream(entity.content), contentId1)
        }
    }
}
