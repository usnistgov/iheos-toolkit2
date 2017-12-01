package gov.nist.toolkit.fhir.simulators.proxy.util

class MultipartParser {

    static List<BinaryPartSpec> parse(String x) {
        List<BinaryPartSpec> partSpecs = []
        boolean inheader = false
//        boolean inbody = false
        def boundary = null
        String contentType = null
        String contentId = null
        StringBuilder buf = new StringBuilder()
        x.trim().eachLine { String line ->
            line = line.trim()
            if (!line && inheader) {
                inheader = false
            }
            else if (line.startsWith('--') && !boundary) {
                inheader = true;
                boundary = line.substring(2)
            }
            else if (line.startsWith("--${boundary}")) {
                assert contentType
                assert contentId
                //buf.deleteCharAt(buf.size() -1)
                partSpecs << new BinaryPartSpec(contentType, buf.toString().bytes, contentId)
                buf = new StringBuilder()
                inheader = true
                contentType = null
            }
            else if (inheader) {
                def parts = line.split(':')
                if (parts[0].equalsIgnoreCase('content-type')) {
                    contentType = parts[1].split(';')[0].trim()
                }
                if (parts[0].equalsIgnoreCase('content-id')) {
                    contentId = parts[1].split(';')[0].trim()
                }
            } else {
                buf.append(line).append('\n')
            }
        }
        partSpecs
    }

}
