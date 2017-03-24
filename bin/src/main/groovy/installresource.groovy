import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.support.ResDb
import gov.nist.toolkit.xdsexception.ExceptionUtil
/**
 * installresource simid file file file...
 *   simulator is created if it does not exist
 */

try {
    Tk.init()

    SimId simId
    List<File> files = []

    if (args.size() < 2) {
        println 'Usage: installresource.groovy [-c] simId resource_file+'
        println '-c create simulator if it does not exist'
    }

    List arglist = args.toList()

    def create = false

    if (arglist[0] == '-c') {
        create = true
        arglist.remove(0)
    }

    simId = Tk.parseSimId(arglist[0].trim())
    arglist.remove(0)

    if (create) {
        println "Creating sim ${simId}"
        new ResDb().mkSim(simId, 'fhir')
    }

    if (arglist.size() == 0) {
        println 'No resource files specified'
        System.exit(-1)
    }

    arglist.each { files.add(new File(it)) }

    files.each { File file -> assert file.exists() }

    println "resdb is ${new ResDb().getSimDbFile()}"
    ResDb resDb = Tk.resDb(simId, 'fhir', 'PUT')
    File event = resDb.getEventDir()

    files.each { File file ->
        File newFile = new File(event, file.name)
        println "copying $file to $newFile"
        newFile.bytes = file.bytes
    }

} catch (Throwable e) {
    println ExceptionUtil.exception_details(e)
}


