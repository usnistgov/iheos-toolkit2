package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.http.HttpMessageBa
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.fhir.simulators.sim.ids.WadoRetrieveResponseSim
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.simcommon.server.SimCommon
import gov.nist.toolkit.fhir.simulators.support.StoredDocument
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import org.apache.http.NameValuePair
import org.glassfish.grizzly.servlet.HttpServletResponseImpl
import spock.lang.Specification

import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletResponse

/**
 * Created by rmoult01 on 3/20/17.
 */
class WadoRetriveResponseSimSpec extends Specification {

    def 'Test WADO 55 message response generation' () {

        given: 'A WADO 55 Http Request message'

        HttpMessageBa httpMsg = Mock()
        httpMsg.getHeaderValue("Accept") >> acceptHeaderValue
        httpMsg.getQueryParameterValue("requestType") >> 'WADO'
        httpMsg.getQueryParameterValue("studyUID") >> studyUID
        httpMsg.getQueryParameterValue("seriesUID") >> seriesUID
        httpMsg.getQueryParameterValue("objectUID") >> objectUID
        httpMsg.getQueryParameterValue("contentType") >> contentType
        httpMsg.getQueryParameters() >> new ArrayList<NameValuePair>()

        MyResponse response = new MyResponse(Mock(HttpServletResponse))

        ErrorRecorder er = new GwtErrorRecorder()
        SimDb db = Mock()
        ValidationContext vc = Mock()
        SimulatorConfig simConfig = Mock()

        SimCommon common = Mock(SimCommon, constructorArgs: [db, simConfig, false, vc, null, null, null])
        common.getCommonErrorRecorder() >> er
        common.response = response

        DsSimCommon dsSimCommon = Mock(DsSimCommon, constructorArgs: [common, new MessageValidatorEngine()])
        dsSimCommon.getStoredImagingDocument(*_) >> {args -> getDoc((String)args[0], contentType)}

        when: 'A WadoRetriveResponseSim instance is created and run'
        WadoRetrieveResponseSim wrr = new WadoRetrieveResponseSim(common, httpMsg, dsSimCommon)
        wrr.run(er, Mock(MessageValidatorEngine))

        then: 'something ?'
        assert response.getStatus() == status
        assert response.getContentType() == contentType
        assert response.getContentLength() == "Document content".bytes.length


        where: 'Tests to run'
        testName | acceptHeaderValue | studyUID | seriesUID | objectUID | contentType || status
        'Simple Test' | 'application/dicom' |
                '1.3.6.1.4.1.21367.201599.1.201604020954048' |
                '1.3.6.1.4.1.21367.201599.2.201604020954048' |
                '1.3.6.1.4.1.21367.201599.3.201604020954048.1' |
                'application/dicom' | 200
    }

    private StoredDocument getDoc(String cuid, String contentType) {
        StoredDocument doc = new StoredDocument()
            doc.pathToDocument = ''
            doc.uid = cuid.split(':').last()
            doc.mimeType = contentType
            doc.charset = "UTF-8"
            doc.content = "Document content".bytes
            doc
    }

    class MyResponse extends HttpServletResponseImpl {

        String contentType
        int contentLength
        int status
        MyOutput outputStream = new MyOutput()
        String content

        public MyResponse(HttpServletResponse mock) {
            mock
        }

        String getContentType() {
            return contentType
        }

        void setContentType(String contentType) {
            this.contentType = contentType
        }

        int getContentLength() {
            return contentLength
        }

        void setContentLength(int contentLength) {
            this.contentLength = contentLength
        }

        int getStatus() {
            return status
        }

        void setStatus(int status) {
            this.status = status
        }

        ServletOutputStream getOutputStream() {
            return outputStream
        }

        void flushBuffer() {
            content = outputStream.os.toString()
        }

        byte[] getContent() {
            content.bytes
        }

    }

    class MyOutput extends ServletOutputStream {

        public OutputStream os = new ByteArrayOutputStream()

        void write(int b) { os.write(b)}

//        @Override
        boolean isReady() {
            return true
        }

//        @Override
        void setWriteListener(WriteListener writeListener) {

        }
        public void close() {
            os.close()
        }
    }
}
