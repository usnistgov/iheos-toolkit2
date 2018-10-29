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
                // last \n belongs to boundary
                if (buf.size() > 0)
                    buf.deleteCharAt(buf.size()-1)
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

    static CR = '\r'
    static LF = '\n'
    static CRLF = CR + LF
    static boundaryStart = CR + LF + '--'

    static List<BinaryPartSpec> parse(byte[] x) {
        int index = 0
        def boundary = null
        def endBoundary = null
        boolean inheader = false
        boolean done = false
        String contentType = null
        String contentId = null
        String contentEncoding = null
        def parts = [:]

        byte[] buf = new byte[x.size()]
        int size = 0

        index = find(x, index, boundaryStart.bytes)
        if (index == -1)
            return new ArrayList()
        //index += 2  // past initial CRLF
        boundary = new String(lineStartingAt(x, index)) + CRLF
        endBoundary = new String(lineStartingAt(x, index)) + '--' + CRLF
        index = index + boundary.size()// + 4  // past preceding and ending CRLF

        while (index < x.size() - 1) {

            contentType = ''
            contentId = ''
            contentEncoding = ''
            byte[] partContent
            int end
            // now at start of header
            if (x[index] == CR.bytes[0] + x[index + 1] == LF.bytes[0]) {
                // empty header
                index += 2
                // now at start of part content
            } else {
                byte[] headerBytes = upto(x, index, (CRLF + CRLF).bytes)
                if (headerBytes.size() == 0)
                    break
                String headers = new String(headerBytes)
                index = index + headerBytes.size()
                headers.eachLine { String line ->
                    def partss = line.trim().split(':', 2)
                    if (partss[0].equalsIgnoreCase('content-type')) {
                        contentType = partss[1].split(';')[0].trim()
                    }
                    if (partss[0].equalsIgnoreCase('content-id')) {
                        contentId = partss[1].split(';')[0].trim()
                    }
                    if (partss[0].equalsIgnoreCase('content-encoding')) {
                        contentEncoding = partss[1].split(';')[0].trim()
                    }
                }
                index += 4 // CRLF CRLF
            }

            //
            partContent = upto(x, index, boundary.bytes)
            if (partContent.size() == 0) {
                done = true
                partContent = upto(x, index, endBoundary.bytes)
            }
            end = find(x, index, boundary.bytes)

            String partContentString = new String(partContent)

            parts[contentId] = new ByteArrayEntity(partContent, ContentType.parse(contentType))

            if (end == -1 || done)
                break

            index = end
        }

        parts.collect { String contentId1, HttpEntity entity ->
            Header h = entity.contentType
            new BinaryPartSpec(contentType, Io.getBytesFromInputStream(entity.content), contentId1)
        }
    }

    // does not include CRLF at end
    static byte[] lineStartingAt(byte[] content, int startingAt) {
        byte[] buf = new byte[content.size()]
        int size=0

        for (int i=startingAt; i< content.size(); i++) {
            if (i >= content.size() - 2)
                return new byte[0]
            buf[size++] = content[i]
            if (content[i+1] == CR.bytes[0] && content[i+2] == LF.bytes[0])
                return Arrays.copyOf(buf, size)
        }
        return new byte[0]
    }

    // where does pattern start
    static int find(byte[] content, int startingAt, byte[] pattern) {
        for (int i=startingAt; i<content.size()-pattern.size()+1; i++) {
            if (startsWith(content, i, pattern))
                return i
        }
        return -1
    }

    // does not return pattern
    static byte[] upto(byte[] content, int startingAt, byte[] pattern) {
        byte[] buf = new byte[content.size()]
        int bufi = 0
        for (int i=startingAt; i<content.size()-1; i++) {
            if (startsWith(content, i, pattern))
                return Arrays.copyOf(buf, bufi)
            buf[bufi++] = content[i]
        }
        return new byte[0]
    }

    static boolean startsWith(byte[] content, int startingAt, byte[] pattern) {
        if (startingAt >= content.size())
            return false
        if (content.size() < startingAt + pattern.size())
            return false;
        for (int i=0; i<pattern.size(); i++) {
            if (content[startingAt+i] != pattern[i])
                return false
        }
        return true
    }
}
