import groovy.xml.XmlUtil
/**
 * Sectionize test definitions
 */

def testkit = '/Users/bill/git/iheos-toolkit2/xdstools2/src/main/webapp/toolkitx/testkit'
def areas = ['testdata', 'tests', 'testdata-registry', 'testdata-repository', 'testdata-xdr', 'examples']
def tests = new File(testkit, 'tests')
def testdata = new File(testkit, 'testdata')

processTestKit(testkit)

//processTestPlan(new File(testdata, '12345'))

def processTestKit(String testkit) {
    def area = 'examples'
    File areaDir = new File(new File(testkit), area)
    areaDir.eachFile { File testDir ->
        if (testDir.isDirectory() && new File(testDir, "testplan.xml").exists()) {
            println "Test ${testDir}.name"
            processTestPlan(testDir)
        }
    }
}

def processTestPlan(File testplanDir) {
    def testPlanFile = new File(testplanDir, 'testplan.xml')
    def testplan = new XmlParser().parse(testPlanFile)
    def testId = testplan.Test.text()
    println "Test ID is $testId"
    def sections = []
    testplan.each { node ->
        if (node.name() == 'TestStep') {
            def testStepNode = node
            def stepId = node.attribute('id')
            println "Step ID is $stepId"

            def sectionName = stepId
            def sectionTestId = "$testId/$sectionName"

            def sectionDir = new File("$testplanDir/$sectionName")
            sectionDir.mkdirs()

            def wrapper = """
<TestPlan>
  <Test>$sectionTestId</Test>
  ${XmlUtil.serialize(testStepNode)}
</TestPlan>
"""
            new File("$sectionDir/testplan.xml").write(wrapper)
            sections << sectionName

            def documents = testStepNode.depthFirst().findAll { it.name() == 'Document' }
            documents.each {
                def filename = it.text()
                println "Copying $filename"
                def buffer = new File(testplanDir, filename).text
                new File(sectionDir, filename).write(buffer)
            }

            def metadata = testStepNode.depthFirst().findAll { it.name() == 'MetadataFile' }
            metadata.each {
                def filename = it.text()
                println "Copying $filename"
                try {
                    def buffer = new File(testplanDir, filename).text
                    new File(sectionDir, filename).write(buffer)
                } catch (FileNotFoundException e) {
                    //ignore
                }
            }
        }
    }
    def index_idx = new File(testplanDir, 'index.idx')
    sections.each { index_idx << "$it\n" }

    testPlanFile.delete()
}

