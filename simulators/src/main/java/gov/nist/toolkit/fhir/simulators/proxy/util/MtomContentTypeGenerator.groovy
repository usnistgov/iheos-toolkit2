package gov.nist.toolkit.fhir.simulators.proxy.util

import org.apache.commons.lang.text.StrSubstitutor
import org.apache.http.Header
import org.apache.http.NameValuePair
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHeaderElement
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.ByteArrayBuffer
/**
 *
 */
class MtomContentTypeGenerator {
    static final boundary = 'MIMEBoundary_494859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f'
    static final type = 'application/xop+xml'
   // static final start = '<1.694859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f@apache.org>'
    static final startInfo = 'application/soap+xml'

    static Header buildHeader(String action, String startCid) {
        NameValuePair[] nameValuePairs = new NameValuePair[5]
        nameValuePairs[0] =  new BasicNameValuePair('boundary', encode(boundary))
        nameValuePairs[1] =  new BasicNameValuePair('type', encode(type))
        nameValuePairs[2] =  new BasicNameValuePair('start', encode("<${startCid}>"))
        nameValuePairs[3] = new BasicNameValuePair('start-info', encode(startInfo))
        nameValuePairs[4] =  new BasicNameValuePair('action', encode(action))

        //nameValuePairs = URLEncodedUtils.format(nameValuePairs, 'UTF-8')
//        BasicHeaderElement ele = new BasicHeaderElement('Content-Type', 'multipart/related', nameValuePairs)
        BasicHeaderElement ele = new BasicHeaderElement('multipart/related', null, nameValuePairs)
        Header contentType = new BasicHeader('Content-Type', ele.toString())
        return contentType
//        request.addHeader(contentType)
//        InputStream contentInputStream = Io.bytesToInputStream(content)
//        BasicHttpEntity entity = new BasicHttpEntity()
//        entity.setContent(contentInputStream)
//        request.setEntity(entity)
//        return request
    }

    static String encode(String input) {
        '"' + input + '"'
    }

    static byte[] buildBody(List<BinaryPartSpec> parts) {
        //def contentIdBase = '.694859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f'
//        StringBuilder buf = new StringBuilder()
        ByteArrayBuffer buf = new ByteArrayBuffer(4000)

        def partHeaderTemplate = getClass().getResource('/templates/part_header.txt').text
        parts.each {
            def map = [theContentType: it.contentType, theContentId:"${it.contentId}"]
            byte[] m = new StrSubstitutor(map).replace(partHeaderTemplate).bytes
            buf.append(m, 0, m.size())//.append('\n')  // header
            buf.append(it.content, 0, it.content.size())//.append('\n')   // body
        }
        byte[] m = getClass().getResource('/templates/mtom_close.txt').text.bytes
        buf.append(m, 0, m.size())

//        String s = buf.toString()
        return buf.toByteArray()

    }
}
