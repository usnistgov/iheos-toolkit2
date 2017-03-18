import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.xdsexception.ExceptionUtil
/**
 * installresource simid file file file...
 *   simulator is created if it does not exist
 */

try {
    Installation.instance().externalCache(Tk.ec)
    if (!Installation.instance().externalCache()) {
        println 'External Cache location not set'
        System.exit(-1)
    }

    SimId simId
    List<File> files = []

    simId = Tk.parseSimId(args[0].trim())

    List arglist = args.toList()
    arglist.remove(0)

    if (arglist.size() == 0) {
        println 'No resource files specified'
        System.exit(-1)
    }

    arglist.each { files.add(new File(it)) }

    files.each { File file -> assert file.exists() }

    println "simdb is ${FhirSimDb.getSimDbFile()}"
    SimDb simDb = Tk.simDb(simId, 'fhir')
    File event = simDb.getEventDir()

    files.each { File file ->
        File newFile = new File(event, file.name)
        println "copying $file to $newFile"
        newFile.bytes = file.bytes
    }

} catch (Throwable e) {
    println ExceptionUtil.exception_details(e)
}


