package gov.nist.toolkit.testengine.assertionEngine

class XdsModelValidationResult {
    String assertionName
    AbstractXdsModelValidater validater

    enum Level {FATAL, ERROR, WARNING, INFO, DEBUG, TRACE, ALL}
    class Msg {
        Level level
        String msg

        Msg(Level level, String msg) {
            this.level = level
            this.msg = msg
        }

        String toString() {
            "${level}: $msg"
        }
    }
    List<Msg> msgs = []

    XdsModelValidationResult(AbstractXdsModelValidater validater) {
        this.validater = validater
        this.assertionName = (validater) ? validater.getClass().simpleName : 'null'
    }

    def error(String err) {
        msgs << new Msg(Level.ERROR, err)
    }

    def info(String msg) {
        msgs << new Msg(Level.INFO, msg)
    }

    def trace(String msg) {
        msgs << new Msg(Level.TRACE, msg)
    }

    boolean hasErrors() {
        msgs.find { it.level <= Level.ERROR}
    }

    String toString() {
        toString(Level.ALL)
    }

    String toString(Level level) {
        StringBuilder buf = new StringBuilder()

        buf.append('-----------------------\n')
        buf.append('Rule:        ').append(assertionName).append('\n')
        buf.append('Description: ').append(validater.description).append('\n')
        buf.append("${validater.numberObjectsProcessed} objects processed").append('\n')
        msgs.each { if (it.level <= level) buf.append('  ').append(it).append('\n')}

        return buf.toString()
    }
}
