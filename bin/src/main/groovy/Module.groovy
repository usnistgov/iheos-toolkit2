/**
 *
 */

class Module {
    static ROOT = new File('/Users/bill/tk')

    static void calledByUtil(String moduleName) {
        Set<File> calledby = calledBy(moduleName)
        calledby.each { File by ->
            String path = by.path
            int start = path.indexOf('toolkit')
            def printablePath = path.substring(start + 'toolkit/'.size())
            String pkgName = packageName(asModule(moduleName))
            if (!printablePath.startsWith(pkgName))
                println printablePath
        }
    }

    /**
     * what java/groovy classes call this module?
     * @param moduleName
     * @return list of module names
     */
    static Set<File> calledBy(String moduleName) {
        String pkgName = packageName(asModule(moduleName))
        println "${moduleName} ==> ${pkgName}"
        Set<File> calledBy = []
        getModules().each { File module ->
            List<File> sourceFiles = getSourceFiles(module)
            sourceFiles.each {File f ->
                f.eachLine { String line ->
                    if (line.indexOf('import') > -1 &&
                            line.indexOf(".${pkgName}.") > -1)
                        calledBy.add(f)
                }
            }
        }
//        calledBy.collect { File module -> nameOfModule(module)}
        calledBy
    }

    static String nameOfModule(File file) {
        (file.path - ROOT).split(File.separator)[1]
    }

    static File asModule(String moduleName) {
        new File(ROOT, moduleName)
    }

    static String packageName(File module) {
        List<File> srcs = findSrc(module, ['java', 'groovy'])
        assert srcs, 'No sources below ${module}'
        File aSrc = srcs.first()
        String[] parts = aSrc.path.split(File.separator)
//        println "Parts: ${parts}"
        int i = parts.findIndexOf { it == 'gov'}
        if (i>0) {
//            println "Package name is ${parts[i+3]}"
            return parts[i + 3]
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

}
