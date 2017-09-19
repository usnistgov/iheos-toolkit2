package gov.nist.toolkit.simulators.proxy.util

import org.apache.http.Header
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

    static Header gen(String action) {
        def nameValuePairs = new BasicNameValuePair[5]
        nameValuePairs[0] = new BasicNameValuePair('boundary', boundary)
        nameValuePairs[1] = new BasicNameValuePair('type', type)
        nameValuePairs[2] = new BasicNameValuePair('start', start)
        nameValuePairs[3] = new BasicNameValuePair('start-info', startInfo)
        nameValuePairs[4] = new BasicNameValuePair('action', action)
        BasicHeaderElement ele = new BasicHeaderElement('Content-Type', 'multipart/related', nameValuePairs)
        Header contentType = new BasicHeader('Content-Type', ele.toString())
        return contentType
//        request.addHeader(contentType)
//        InputStream contentInputStream = Io.bytesToInputStream(content)
//        BasicHttpEntity entity = new BasicHttpEntity()
//        entity.setContent(contentInputStream)
//        request.setEntity(entity)
//        return request
    }
}
