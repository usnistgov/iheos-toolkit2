package gov.nist.toolkit.simulators.proxy.util

import org.apache.commons.lang.text.StrSubstitutor
import org.apache.http.Header
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHeaderElement
import org.apache.http.message.BasicNameValuePair
/**
 *
 */
class MtomContentTypeGenerator {
    static final boundary = 'MIMEBoundary_494859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f'
    static final type = 'application/xop+xml'
    static final start = '<1.694859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f@apache.org>'
    static final startInfo = 'application/soap+xml'

    static Header buildHeader(String action) {
        def nameValuePairs = []
        nameValuePairs << new BasicNameValuePair('boundary', boundary)
        nameValuePairs << new BasicNameValuePair('type', type)
        nameValuePairs << new BasicNameValuePair('start', start)
        nameValuePairs << new BasicNameValuePair('start-info', startInfo)
        nameValuePairs << new BasicNameValuePair('action', action)

        nameValuePairs = URLEncodedUtils.format(nameValuePairs, 'UTF-8')
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

    String encode(String input) {

    }

    static byte[] buildBody(List<PartSpec> parts) {
        def contentIdBase = '.694859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f'
        StringBuilder buf = new StringBuilder()

        def partHeaderTemplate = getClass().getResource('/templates/part_header.txt').text
        int index = 1
        parts.each {
            def map = [theContentType: it.contentType, theContentId:"${index}${contentIdBase}"]
            buf.append(new StrSubstitutor(map).replace(partHeaderTemplate)).append('\r\n')  // header
            buf.append(it.content).append('\r\n')   // body
            index++
        }
        buf.append(getClass().getResource('/templates/mtom_close.txt').text)

        return buf.toString().bytes

    }
}
