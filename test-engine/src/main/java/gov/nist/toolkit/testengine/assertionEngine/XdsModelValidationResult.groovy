package gov.nist.toolkit.testengine.assertionEngine

class XdsModelValidationResult {
    String assertionName

    enum Level {FATAL, ERROR, WARNING, INFO, DEBUG}
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

    def error(String err) {
        msgs << new Msg(Level.ERROR, err)
    }

    def info(String msg) {
        msgs << new Msg(Level.INFO, msg)
    }

    boolean hasErrors() {
        msgs.find { it.level <= Level.ERROR}
    }

    String toString() {
        StringBuilder buf = new StringBuilder()

        buf.append('Assertion ').append(assertionName).append('\n:')
        msgs.each { buf.append('  ').append(it).append('\n')}

        return buf.toString()
    }
}
