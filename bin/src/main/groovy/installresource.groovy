import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimId


/**
 * installresource simid file file file...
 */

SimId simId
File files = []

simId = new SimId(args[0])

args.subList(1, args.size()-1).each { files.append(new File(it))}

files.each { File file -> assert file.exists() }

SimDb simDb = new SimDb(simId, 'db', 'manual')



