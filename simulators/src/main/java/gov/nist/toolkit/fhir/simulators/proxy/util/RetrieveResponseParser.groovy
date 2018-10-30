package gov.nist.toolkit.fhir.simulators.proxy.util

class RetrieveResponseParser {
    List<RetrieveContent> parse(String msg) {
        // since this is has to work with our sim no need to check for start part - always first
        List<BinaryPartSpec> parts = MultipartParser2.parse(msg.bytes)
        List<RetrieveContent> contents = []
        byte[] contentBytes  = parts[0].content
        String contentString = new String(contentBytes)
        if (contentString.trim().contains('Envelope')) {
            def root = new XmlSlurper().parseText(contentString)
            if (root.name() == 'Envelope') {
                def fault = root?.Body?.Fault
                def status = root?.Body?.RetrieveDocumentSetResponse?.RegistryResponse?.@status
                if (fault.size()) {
                    assert 'fault'
                } else if (!status.size() || !status.text().endsWith('Success')) {
                    assert 'failure'
                } else {
                    root?.Body?.RetrieveDocumentSetResponse?.DocumentResponse?.each { dr ->
                        def repUid = dr.RepositoryUniqueId
                        def docUid = dr.DocumentUniqueId
                        def mimeType = dr.mimeType
                        def documentHref = dr.Document?.Include?.@href
                        def cid
                        String cidComponent = documentHref?.text()
                        if (cidComponent && cidComponent.startsWith('cid:')) {
                            cid = "<${cidComponent.substring('cid:'.size())}>"
                            BinaryPartSpec partSpec = findPart(parts, cid)
                            assert partSpec, "Part ${cid} not available in Retreive Response message"
                            RetrieveContent rc = new RetrieveContent()
                            rc.mimeType = mimeType
                            rc.documentUniqueId = docUid
                            rc.content = partSpec.content
                            contents << rc
                        }

                    }
                }
            }
        }
        else {
            RetrieveContent rc = new RetrieveContent()
            rc.mimeType = parts[0].contentType
            rc.content = parts[0].content
            contents.add(rc)
        }
        return contents
    }

    class RetrieveContent {
        String mimeType
        String documentUniqueId
        byte[] content
    }

    static BinaryPartSpec findPart(List<BinaryPartSpec> specs, String cid) {
        specs.find {
            println "${cid} - ${it.contentId}"
            it.contentId == cid
        }
    }
}
