package gov.nist.toolkit.testengine.support

class ContentTypeParser {
    String fullContentType
    String contentType
    Map<String, String> parms = [:]

    ContentTypeParser(String contentType) {
        this.fullContentType = contentType
        parse()
    }

    private parse() {
        contentType = fullContentType
        if (contentType.contains(';')) {
            String[] parts = contentType.split(';')
            contentType = parts[0].trim()
            for (int i=1; i<parts.length; i++) {
                String x = parts[i]
                x = x.trim()
                if (x) {
                    String[] nameVal = x.split('=')
                    if (nameVal.length == 2) {
                        String name = nameVal[0].trim()
                        String val = nameVal[1].trim()
                        parms.put(name, val)
                    }
                }
            }
        }

    }

}
