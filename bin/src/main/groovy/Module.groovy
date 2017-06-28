/**
 *
 */

class Module {
    static ROOT = new File('/Users/bill/tk')

    static List<File> getModules() {
        ROOT.listFiles().findAll() { File module ->
            new File(module,'pom.xml').exists()
        }
    }

    static List<File> getSourceFiles(String moduleName) {
        getSourceFiles(new File(ROOT, moduleName))
    }

    static List<File> getSourceFiles(File module) {
        def main = new File(new File(module, 'src'), 'main')
        println main
        findSrc(main, ['java', 'groovy'])
    }

    static List<File> findSrc(File dir, def langs) {
        def src = []
        dir.listFiles().each { File file ->
            if (langs.contains(fileExtension(file)) && !isTest(file))
                src.add(file)
            else if (file.isDirectory())
                src.addAll(findSrc(file, langs))
        }
        src
    }

    static fileExtension(File file) {
        def pointIndex = file.name.lastIndexOf('\\.')
        if (pointIndex != -1)
            return file.name.substring(pointIndex + 1)
        return null
    }

    static boolean isTest(File file) { file.name.indexOf('Test\\.') == -1 }

    static main(def argv) {
        println getSourceFiles('fhir')

    }
}
