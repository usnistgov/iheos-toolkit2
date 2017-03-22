import java.nio.file.Files

/**
 * installdependencies
 */

class Module {
    String group
    String artifact
    String version

    public String toString() {
        "Module: $group, $artifact, $version"
    }
}

def modules = []

//def mvndepoutput = run('mvn dependency:list'.split(' '))

println 'Running mvn -X dependency:list'

String[] cmd = ['mvn', '-X', 'dependency:list']
def mvndepoutput = run(cmd, '../../..')

//println "mvn generated ${mvndepoutput.size()} chars"
//println mvndepoutput

println 'Parsing output, modules found are ...'
// parse maven dependencies
mvndepoutput.eachLine { String line ->
    def lin = line.trim()
    if (!lin.startsWith('[INFO]')) return
    if (!lin.endsWith(':compile')) return

    lin = lin.substring(6)  // strip off [INFO]
    lin = lin.trim()

    def parts = lin.split(':')
//    assert parts.size() == 5
    if (parts.size() != 5) return

    Module module = new Module()
    module.group = parts[0]
    module.artifact = parts[1]
    module.version = parts[3]

//    println "new artifact -> ${module}"

    print " module ${module}"

    modules << module
}

println ''

println 'Clearing ~/.groovy/lib'

// clear ~/.groovy/lib  and make empty
def userHome = System.getProperty('user.home')
//println "user.home is $userHome"
def groovyLib = "$userHome/.groovy/lib"
//println "groovyLib is $groovyLib"
def groovyLibFile = new File(groovyLib)
if (groovyLibFile.exists()) {
    def output = run("rm -r ${groovyLib}")
}
groovyLibFile.mkdirs()

def m2 = "$userHome/.m2/repository"

assert trim('/foo/', '/') == 'foo'
assert trim('/foo', '/') == 'foo'

println 'Installing modules...'

modules.each { Module m ->
    print " ${m}"
    File jar = findJarInRepository(m2, m)
    assert jar.exists()
//    println "$m ===>   ${jar} ===> Exists? ${jar.exists()}"
    String filename = jar.getName()
    Files.copy(jar.toPath(), new File(groovyLibFile, filename).toPath());
}

println ''

println "Installed ${modules.size()} jars into $groovyLibFile"

File findJarInRepository(String m2, Module module) {
    File file = File.listRoots()[0]
    // trim is needed to remove leading and trailing '/'
    def parts1 = trim(m2, '/').split('/')
    def parts2 = trim(module.group.trim(), '.').split('\\.')
    def parts = (parts1 + parts2).flatten()
    parts.each { String part ->
        if (part != '')
            file = new File(file, part)
    }
    file = new File(file, module.artifact)
    file = new File(file, module.version)
    file = new File(file, "${module.artifact}-${module.version}.jar")
    return file
}

String trim(String str, String c) {
    def x = str
    if (x.startsWith(c)) x = x.substring(c.size())
    if (x.endsWith(c)) x = x.substring(0, x.size()-c.size())
    return x
}

// Run shell command displaying stdout and stderr
def run2(def command, timeout) {
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(100000)
    println "out> $sout err> $serr"
}

// Run shell command displaying stderr and returning stdout
String run(def command) {
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(100000)
    if (serr.size() > 0)
        println "err> $serr"
    return sout
}

// Run shell command displaying stderr and returning stdout
String run(def command, String workingDir) {
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute(null, new File(workingDir))
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(100000)
    if (serr.size() > 0)
        println "err> $serr"
    return sout
}
