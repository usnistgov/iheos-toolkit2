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
    static SEP = '--'
    static boundaryStart1 = CR + LF + '--'
    static boundaryStart2 = LF + '--'

    static List<BinaryPartSpec> parse(byte[] x) {
        int index
        def boundary // without preceding or following CR LF
        def endBoundary
        boolean inheader
        boolean done = false
        String contentType = null
        String contentId = null
        String contentEncoding = null
        def parts = [:]

//        byte[] buf = new byte[x.size()]
//        int size = 0

        x = stripCR(x)  // strip out CR chars - rely on LF

        index = 0
        index = find(x, index, SEP.bytes)
        if (index == -1)
            return new ArrayList()
        boundary = new String(lineStartingAt(x, index))
        endBoundary = boundary + '--'
        index += boundary.size()
        index ++  // past LF


        while (index < x.size() - 1) {

            contentType = ''
            contentId = ''
            contentEncoding = ''
            byte[] partContent
            int end
            // now at start of header
            if (x[index] == LF.bytes[0]) {
                // empty header
                index++
                // now at start of part content
            }
            if (x[index] == LF.bytes[0]) {
                // empty header
                index++
                // now at start of part content
            } else {
                byte[] headerBytes = upto(x, index, (LF + LF).bytes)
                if (headerBytes.size() == 0)
                    break  // no new header
                int offset = headerBytes.size() + 2
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
                index += 2 //  CR CR
            }

            //
            partContent = upto(x, index, boundary.bytes)
            if (partContent.size() == 0) {
                done = true
                partContent = upto(x, index, endBoundary.bytes)
            }
            partContent = endTrim(partContent, 1)
            end = find(x, index, boundary.bytes)

            // trailing CR belongs to header


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

    static int skip(byte[] x, int index, String what) {
        if (index >= x.size()) return index
        if (x[index] == what.bytes[0]) return index + 1
        return index
    }

    static byte[] strip(byte[] line) {
        byte[] buf = new byte[line.size()]
        int size=0
        int i = 0

        boolean prefix = true
        while (i<line.size()) {
            if (prefix && line[i] == CR || line[i] == LF) {
                i++
                continue
            }
            buf[size++] = line[i++]
            prefix = false
        }
        if (size > 0 && (buf[size-1] == LF.bytes[0] | buf[size-1] == CR.bytes[0]))
            size--
        if (size > 0 && (buf[size-1] == LF.bytes[0] | buf[size-1] == CR.bytes[0]))
            size--
        return Arrays.copyOf(buf, size)
    }

    // does not include CRLF at end
    static byte[] lineStartingAt(byte[] content, int startingAt) {
        byte[] buf = new byte[content.size()]
        int size=0

        for (int i=startingAt; i< content.size(); i++) {
            if (i >= content.size() - 1)
                return new byte[0]
            buf[size++] = content[i]
            if (content[i+1] == LF.bytes[0] /* && content[i+2] == LF.bytes[0] */)
                return Arrays.copyOf(buf, size)
        }
        return new byte[0]
    }

    static byte[] stripCR(byte[] x) {
        byte[] buf = new byte[x.size()]
        int size = 0

        for (int i=0; i<x.size(); i++) {
            if (x[i] == CR.bytes[0])
                continue
            buf[size++] = x[i]
        }
        return Arrays.copyOf(buf, size)
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

    static byte[] add(byte[] x, byte[] y) {
        byte[] buf = new byte[x.size() + y.size()]
        int size=0

        for (int i=0; i<x.size(); i++)
            buf[size++] = x[i]

        for (int i=0; i<y.size(); i++)
            buf[size++] = y[i]
        return buf
    }

    static byte[] endTrim(byte[] x, int amount) {
        Arrays.copyOf(x, x.size() - 1)
    }

}
