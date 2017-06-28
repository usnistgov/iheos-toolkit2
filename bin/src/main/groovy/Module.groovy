/**
 *
 */

class Module {
    static ROOT = new File('/Users/bill/tk')

    static List<File> calledBy(String moduleName) {
        String pkgName = packageName(asModule(moduleName))
        println "${moduleName} ==> ${pkgName}"
        List<File> sourceFiles = getSourceFiles(moduleName)
        Set calledBy = []
        sourceFiles.each {File f ->
            f.eachLine { String line ->
                if (line.indexOf('import') > -1 && line.index(pkgName) > -1)
                    calledBy.add(f.name)
            }
        }
        calledBy
    }

    static File asModule(String moduleName) {
        new File(ROOT, moduleName)
    }

    static String packageName(File module) {
        File main = new File(new File(module, 'src'), 'main')
        if (!main.exists()) return null
        File groovy = new File(main, 'groovy')
        println "groovy: ${groovy}"
        if (groovy.exists() && groovy.listFiles().size() > 0) {
            // TODO - dig into the source tree
            File firstFile = groovy.listFiles()[0]
            String[] parts = firstFile.name.split('.')
            if (parts.size() >= 4) {
                def pkg = parts[3]
                return pkg
            }
        }
        File jav = new File(main, 'java')
        println "jav: ${jav}"
        if (jav.exists() && jav.listFiles().size() > 0) {
            // TODO - dig into the source tree
            File firstFile = jav.listFiles()[0]
            String[] parts = firstFile.name.split('.')
            if (parts.size() >= 4) {
                def pkg = parts[3]
                return pkg
            }
        }
        return null
    }

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

    static String fileExtension(File file) {
        def pointIndex = file.name.lastIndexOf('.')
        if (pointIndex != -1) {
            return file.name.substring(pointIndex + 1)
        }
        return null
    }

    static boolean isTest(File file) { file.name.indexOf('Test.') != -1 }

    static main(def argv) {
        println getSourceFiles('fhir')

    }
}
