/**
 * docgen.groovy
 */

import groovy.transform.Field

File testdir = null
def doc = []  // strings

if (args.size() != 3) usage()
if (args[0] == '-test') {
    def testbasedir = new File(args[1])
    def testCollectionFile = new File(args[2])
    def testcollection = []
    testCollectionFile.eachLine { line -> testcollection.add(line.trim())}
    testcollection.each { testid ->
        def testdir1 = new File(testbasedir, testid)
        doc = []
        eachTest(doc, testdir1)
        genOutput(doc, testid)
    }
}

//eachTest(doc, testdir)
//println '**********************************************'

def genOutput(def doc, def testid) {
    def html = toHtml(doc)
    new File(testid + '.html').withWriter { out ->
        html.each { out.println it }
    }
}

def eachTest(def doc, File testdir) {
import groovy.transform.Field

File testdir = null
def doc = []  // strings

if (args.size() != 3) usage()
if (args[0] == '-test') {
    def testbasedir = new File(args[1])
    def testCollectionFile = new File(args[2])
    def testcollection = []
    testCollectionFile.eachLine { line -> testcollection.add(line.trim())}
    testcollection.each { testid ->
        def testdir1 = new File(testbasedir, testid)
        doc = []
        eachTest(doc, testdir1)
        genOutput(doc, testid)
    }
}

//eachTest(doc, testdir)
//println '**********************************************'

def genOutput(def doc, def testid) {
    def html = toHtml(doc)
    new File(testid + '.html').withWriter { out ->
        html.each { out.println it }
    }
}

def eachTest(def doc, File testdir) {
    if (!testdir.exists() || !testdir.isDirectory() || ! new File(testdir, 'index.idx').exists()) usage('Not a test directory')
    if (!testdir.exists() || !testdir.isDirectory() || ! new File(testdir, 'index.idx').exists()) usage('Not a test directory')
    doc.add("= Test: ${testdir.name} ")
    doc.add(' ')
    doc.add('[%hardbreaks]')
    def readme = readme(testdir)
    doc.add("*Description*: ${readme[0]}")
    doc.add("*Profile*: ${profile(testdir)}")
    doc.add(' ')

    if (!readme) {
        doc.add('=== Description')
        doc.add(' ')
    }

    if (readme.size() > 0) readme.remove(0)

        println "readme is ${readme}"
    }

    if (readme.size() > 0) readme.remove(0)
    doc.addAll(readme)
    doc.add(' ')

    sections(testdir).each { sectionName ->
        File sectionDir = new File(testdir, sectionName)
        eachSection(doc, sectionDir)

    }
}

def eachSection(def doc, File sectionDir) {
        walkTestplan(doc, sectionDir)
    }
}

def eachSection(def doc, File sectionDir) {
    def plan = testplan(sectionDir)
    doc.add(' ')
    doc.add("== Section ${sectionDir.name}")
    doc.add(' ')
    def readme = readme(sectionDir)
    doc.addAll(readme)
    doc.add(' ')

    plan.children().each { ele -> if (ele.name() == 'TestStep') eachTestStep(doc, sectionDir, ele)}

//    println "Step names: ${steps(plan).keySet()}"
}

def eachTestStep(def doc, File sectionDir, def teststep) {
    def transEle = transaction(teststep)
    def transName = transactionName(teststep)
    def actName = actorName(teststep)
    doc.add(' ')
    doc.add("=== Transaction: ${teststep.@id}")
    doc.add(' ')
    doc.add('[%hardbreaks]')
    teststep.children().findAll { it.name() == 'Goal'}.each { doc.add(it)}
    doc.add("*Target Actor*: ${actName}")
    def actName = actorName(teststep)
    doc.add(' ')
    doc.add("=== Transaction: ${teststep.@id}")
    doc.add(' ')
    doc.add('[%hardbreaks]')
    teststep.children().findAll { it.name() == 'Goal'}.each { doc.add(it)}
    doc.add("*Target Actor*: ${actName}")
    doc.add("*Transaction*: ${transName}")
    if (transName == 'StoredQuery') {
        doc.add("*QueryType*: ${storedQueryType(sectionDir, transEle)}")
    }
    doc.add("*Expected Status in Response*: ${teststep.ExpectedStatus.text()}")
    def expectedErrorCode = teststep.ExpectedErrorCode.text()
    if (expectedErrorCode)
        doc.add("*Expected Error Code*: ${expectedErrorCode}")
    def expectedErrorMsg = teststep.ExpectedErrorMessage.text()
    if (expectedErrorMsg) {
        doc.add("*Excpected Error Message*: ${expectedErrorMsg}")
    }

    doc.add(' ')
    doc.add('==== Additional Evaluation')
    def expectedContents = transEle.ExpectedContents
    def assertions = transEle.Assertions
    if (expectedContents && expectedContents.children().size() > 0) {
        documentExpectedContents(doc, expectedContents)
    } else if (assertions) {
        doc.add('[%hardbreaks]')
        assertions.children().findAll { it.name() == 'Assert'}.each { assertion ->
            doc.add("*Verify contents*: ${assertion.@desc.text()}")
        }
    } else {
        doc.add('None')
        doc.add(' ')
    }

}

def documentExpectedContents(def doc, def expectedContents) {
    assertionStart(doc)
    expectedContents.children().each { ele ->
        def name = ele.name()
        switch (name) {
            case 'hasSubmissionSet':
                assertion(doc, 'SubmissionSet')
                break
            case 'ssApproved':
                assertion(doc, 'SubmissionSet with status Approved')
                break
            case 'docRplcDoc':
                assertion(doc, 'Two DocumentEntries linked with RPLC Association')
                break
            case 'docsApproved':
                assertion(doc, 'All DocumentEntries have status Approved')
                break
            case 'ss1Doc':
                assertion(doc, 'Single SubmissionSet containing single DocumentEntry')
                break
            case 'ss2Doc':
                assertion(doc, 'Single SubmissionSet containing two DocumentEntries')
                break
            case 'sswithOneFol':
                assertion(doc, 'Single SubmissionSet containing one Folder and no DocumentEntries')
                break
            case 'sswithOneDocOneFol':
                assertion(doc, 'Single SubmissionSet containing one Folder and one DocumentEntry')
                assertion(doc, 'DocumentEntry in Folder')
                assertion(doc, 'Includes SubmissionSet to Folder-DocumentEntry Association')
                break
            case 'sswithTwoDocOneFolOneDocInFol':
                assertion(doc, 'Single SubmissionSet containing one Folder and two DocumentEntries')
                assertion(doc, 'Folder and DocumentEntries in SubmissionSet')
                assertion(doc, 'Folder contains one of the DocumentEntries')
                break
            case 'SSwithOneDoc':
                assertion(doc, 'SubmissionSet containing one DocumentEntry')
                break
            case 'NoSubmissionSet':
                assertion(doc, 'No SubmissionSets')
                break
            case 'NoDocument':
                assertion(doc, 'No DocumentEntries')
                break
            case 'SSApproved':
                assertion(doc, 'SubmissionSet with status Approved')
                break
            case 'DocDep':
                assertion(doc, 'All DocumentsEntries have Deprecated status')
                break
            case 'DocApp':
                assertion(doc, 'All DocumentsEntries have Approved status')
                break
            case 'HasRPLC':
                assertion(doc, 'Includes at least RPLC Association')
                assertion(doc, 'Multiple RPLC Associates not found for single DocumentEntry')
                break
            case 'DocRplcDoc':
                assertion(doc, 'Two DocumentEntries linked by a RPLC Association')
                break
            case 'OneDocDep':
                assertion(doc, 'Includes single Deprecated DocumentEntry')
                break
            case 'OneDocApp':
                assertion(doc, 'Includes single Approved DocumentEntry')
                break
            case 'FolApp':
                assertion(doc, 'All Folders have Approved status')
                break
            case 'SSwithTwoDoc':
                assertion(doc, 'SubmissionSet contains two DocumentEntries')
                break
            case 'SSwithOneDocOneFol':
                assertion(doc, 'Metadata contains a SubmissionSet, one DocumentEntry, and one Folder')
                assertion(doc, 'SubmissionSet contains the Folder and DocumentEntry')
                assertion(doc, 'Folder contains DocumentEntry')
                break
            case 'SSwithTwoDocOneFol':
                assertion(doc, 'Metadata contains a SubmissionSet, two DocumentEntries, and one Folder')
                assertion(doc, 'SubmissionSet contains both DocumentEntries and Folder')
                assertion(doc, 'Folder contains both DocumentEntries')
                assertion(doc, 'Folder to DocumentEntries Associations contained in SubmissionSet')
                break
            case 'SSwithTwoDocOneFolOneDocInFol':
                assertion(doc, 'Metadata contains a SubmissionSet, two DocumentEntries, and one Folder')
                assertion(doc, 'SubmissionSet contains both DocumentEntries and Folder')
                assertion(doc, 'Folder contains one of the DocumentEntries')
                break
            case 'SSwithOneFol':
                assertion(doc, 'Metadata contains a SubmissionSet, no DocumentEntries, and one Folder')
                assertion(doc, 'SubmissionSet contains Folder')
                break
            case 'None':
                assertion(doc, 'Metadata contains no SubmissionSets, DocumentEntries, or Folders')
                break
            case 'ObjectRefs':
                def count = ele.@count.text()
                assertion(doc, "Metadata contains ${count} ObjectRefs, no SubmissionSets, no DocumentEntries, no Folders, and no Associations")
                break
            case 'Documents':
                def count = ele.@count.text()
                assertion(doc, "Metadata contains ${count} DocumentEntries")
                break
            case 'Folders':
                def count = ele.@count.text()
                assertion(doc, "Metadata contains ${count} Folders")
                break
            case 'SubmissionSets':
                def count = ele.@count.text()
                assertion(doc, "Metadata contains ${count} SubmissionSets")
                break
            case 'Associations':
                def count = ele.@count.text()
                assertion(doc, "Metadata contains ${count} Associations")
                break
            case 'DocumentEntries':
                def count = ele.@count.text()
                assertion(doc, "Metadata contains ${count} DocumentEntries")
                break
            case 'HasSnapshotPattern':
                assertion(doc, 'Metadata contains single isSnapshotOf Association')
                assertion(doc, 'Metadata contains both source and target of Association')
                assertion(doc, 'Source and target are DocumentEntries and have status Approved')
                assertion(doc, 'Source object (DocumentEntry) is of type OnDemand')
                assertopm(doc, 'Target object (DocumentEntry) is of type Stable')
                break
            default:
                notImplemented(doc, name)
        }
    }
    doc.add(' ')
}

/**
 * given the transaction (XML) of a Stored Query return the Stored Query type (name not UUID)
 * @param doc
 * @param transaction
 */
def storedQueryType(File sectionDir, def transaction) {
    def queryFileName = transaction.MetadataFile.text()
    if (!queryFileName) return null
    def query = new XmlSlurper().parse(new File(sectionDir, queryFileName))
    if (!query) return null
    def uuid = query.AdhocQuery.@id.text()
    if (!uuid) return null
    return storedQueryName(uuid)
}

def storedQueryName(String uuid) {
    sqNames = [
            'urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d' : 'FindDocuments',
            'urn:uuid:f26abbcb-ac74-4422-8a30-edb644bbc1a9' : 'FindSubmissionSets',
            'urn:uuid:958f3006-baad-4929-a4de-ff1114824431' : 'FindFolders',
            'urn:uuid:10b545ea-725c-446d-9b95-8aeb444eddf3' : 'GetAll',
            'urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4' : 'GetDocuments',
            'urn:uuid:5737b14c-8a1a-4539-b659-e03a34a5e1e4' : 'GetFolders',
            'urn:uuid:a7ae438b-4bc2-4642-93e9-be891f7bb155' : 'GetAssociations',
            'urn:uuid:bab9529a-4a10-40b3-a01f-f68a615d247a' : 'GetDocumentsAndAssociations',
            'urn:uuid:51224314-5390-4169-9b91-b1980040715a' : 'GetSubmissionSets',
            'urn:uuid:e8e3cb2c-e39c-46b9-99e4-c12f57260b83' : 'GetSubmissionSetAndContents',
            'urn:uuid:b909a503-523d-4517-8acf-8e5834dfc4c7' : 'GetFolderAndContents',
            'urn:uuid:10cae35a-c7f9-4cf5-b61e-fc3278ffb578' : 'GetFoldersForDocument',
            'urn:uuid:d90e5407-b356-4d91-a89f-873917b4b0e6' : 'GetRelatedDocuments',
            'urn:uuid:12941a89-e02e-4be5-967c-ce4bfc8fe492' : 'FindDocumentsByReferenceId'
    ]
    return sqNames[uuid]
}

def assertionStart(def doc) {
    doc.add('[%hardbreaks]')
}

def assertion(def doc, def claim) {
    doc.add("*Verify contents*: ${claim}")
}

def notImplemented(def doc, def tag) {
    doc.add("Validation [${tag}] not implemented yet")
}

@Field actorMap = [
        XCQ : 'Responding Gateway',
        XCR : 'Responding Gateway',
        ImagingDocSetIigRetrieve : 'Imaging Initiating Gateway'
]

@Field transactionMap = [
        XCQ : 'Cross Community Query',
        XCR : 'Cross Community Retrieve',
        ImagingDocSetIigRetrieve : 'RAD-69'
]

def actorName(def teststep) {
    def transEle = transaction(teststep)
    if (transEle) {
        String transEleName = transEle.name()
        def name = transEleName.minus('Transaction')
        name = actorMap[name]
        return name
    } else return null
}


def transactionName(def teststep) {
    def transEle = transaction(teststep)
    if (transEle) {
        String transEleName = transEle.name()
        def name = transEleName.minus('Transaction')
        name = transactionMap[name]
        if (name == 'XCQ') name = 'Cross Community Query'
        name = transactionMap[name]
        return name
    } else return null
}

def transaction(def teststep) {
    return teststep.children().find { ele ->  ele.name().endsWith('Transaction')}
}

/**
 * determine profile from collections.txt
 * @param testdir
 */
def profile(File testdir) {
    def collections = []
    new File(testdir, 'collections.txt').eachLine { line -> collections.add(line.trim())}
    def xds = ['reg', 'rep']
    def xca = ['ig', 'rg']
    def xcai = ['iig', 'rig']
    def isXds = collections.find { it in xds}
    if (isXds) return 'XDS'
    def isXca = collections.find { it in xca }
    if (isXca) return 'XCA'
    def isXCAI = collections.find { it in xcai }
    if (isXCAI) return 'XCA-I'
    return ''
}

/**
 * load steps - name ==> node
 * @param testplan
 */
def steps(def testplan) {
    def steps = [:]
    testplan.TestStep.each { teststep ->
        def name = teststep.@id
        steps[name] = teststep
    }
    return steps
}

/**
 * load testplan
 * @param target - can be testplan.xml or section directory
 */
def testplan(File target) {
    if (target.isDirectory()) {
        return testplan( new File(target, 'testplan.xml'))
    }
    if (!target.exists())
        throw new Exception("${target} does not exist")
    return new XmlSlurper().parse(target)
}

/**
 * load readme
 * @param testdir - can be test or section
 */
def readme(File testdir) {
    def stage1 = []
    def stage2 = []
    def stage3 = []
    try {
        new File(testdir, 'readme.txt').eachLine { line -> stage1 << line.trim() }

        // clear out repetitive blank lines
        stage1.each {
            if (isBlank(it) && stage2.size() > 0 && isBlank(stage2.last())) return
            stage2 << it
        }

        // change blank lines into <p>
        stage2.each {
            if (isBlank(it)) stage3 << '<p>'
            else stage3 << it

        }
    } catch (Exception e) { }
    return stage3
}

def isBlank(def it) { it == '' || it == '<br />'}

    def content = []
    def stage1 = []
    def stage2 = []
    def stage3 = []
    try {
        new File(testdir, 'readme.txt').eachLine { line -> stage1 << line.trim() }

        // clear out repetitive blank lines
        stage1.each {
            if (isBlank(it) && stage2.size() > 0 && isBlank(stage2.last())) return
            stage2 << it
        }

        // change blank lines into <p>
        stage2.each {
            if (isBlank(it)) stage3 << '<p>'
            else stage3 << it

        }
    } catch (Exception e) { }
    return stage3
}

def isBlank(def it) { it == '' || it == '<br />'}

/**
 * read index.idx for a test
 * @param testdir
 * @return
 */
def sections(File testdir) {
    def list = []
    new File(testdir, 'index.idx').eachLine { line -> list.add(line.trim())}
    return list
}

def usage(def msg) {
    println msg
    usage()
}

def usage() {
    println 'Usage:'
    println 'docgen -test <testdir>'
//    println 'docgen -testkit <testkitdir>'
    System.exit(-1)
}

def toHtml(def doc) {
    def out = []

    out = doc.collect { node ->
        def line = node as String
        if (line.startsWith('==== ')) return "<h4>${line.minus('==== ')}</h4>"
        if (line.startsWith('=== ')) return "<h3>${line.minus('=== ')}</h3>"
        if (line.startsWith('== ')) return "<h2>${line.minus('== ')}</h2>"
        if (line.startsWith('= ')) return "<h1>${line.minus('= ')}</h1>"
        if (line.startsWith('*')) return line.replaceFirst('\\*', '<b>').replaceFirst('\\*', '</b>')
        return line
    }
    def out1 = []

    def breaks = false
    out1 = out.collect { line ->
        if (line.startsWith('[%hardbreaks]')) { breaks = true; return ' ' }
        if (line.trim().size() == 0) breaks = false
        if (breaks) return "${line}<br />"
        return line

    }

    // clean out excessive breaks
    def out2 = clean(out1)
    def out3 = clean(out2)

    return out3
}

def clean(def doc) {
    def out = []
    def last = ' '
    doc.each { line ->
        def skip = false
        if (last == ' ' && line == '<br />') skip = true
        if (last.startsWith('<h') && line == ' ') skip = true
        if (last.startsWith('<h') && line == '<br />') skip = true
        if (!skip)
            out.add(line)
        last = line
    }

    return out
}

def toHtml(def doc) {
    def out = []

    out = doc.collect { node ->
        def line = node as String
        if (line.startsWith('==== ')) return "<h4>${line.minus('==== ')}</h4>"
        if (line.startsWith('=== ')) return "<h3>${line.minus('=== ')}</h3>"
        if (line.startsWith('== ')) return "<h2>${line.minus('== ')}</h2>"
        if (line.startsWith('= ')) return "<h1>${line.minus('= ')}</h1>"
        if (line.startsWith('*')) return line.replaceFirst('\\*', '<b>').replaceFirst('\\*', '</b>')
        return line
    }
    def out1 = []

    def breaks = false
    out1 = out.collect { line ->
        if (line.startsWith('[%hardbreaks]')) { breaks = true; return ' ' }
        if (line.trim().size() == 0) breaks = false
        if (breaks) return "${line}<br />"
        return line

    }

    // clean out excessive breaks
    def out2 = clean(out1)
    def out3 = clean(out2)

    return out3
}

def clean(def doc) {
    def out = []
    def last = ' '
    doc.each { line ->
        def skip = false
        if (last == ' ' && line == '<br />') skip = true
        if (last.startsWith('<h') && line == ' ') skip = true
        if (last.startsWith('<h') && line == '<br />') skip = true
        if (!skip)
            out.add(line)
        last = line
    }

    return out
}