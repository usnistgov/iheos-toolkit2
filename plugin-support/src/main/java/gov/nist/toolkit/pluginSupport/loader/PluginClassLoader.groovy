package gov.nist.toolkit.pluginSupport.loader



class PluginClassLoader extends GroovyClassLoader {
    private LinkedList<Loader> loaders = new LinkedList<>()
    private PluginClassLoader INSTANCE

    // directories in classpath
    PluginClassLoader(String... paths) throws IOException {
        INSTANCE = this
        setShouldRecompile(true)
        for (String path : paths) {
            File file = new File(path);

            Loader loader = loader(file);
            if (loader == null) {
                throw new RuntimeException("Path not exists " + path);
            }
            loaders.add(loader);
        }
    }

    // className must be *.groovy
    Class loadFile(String className) {
        for (Loader loader : loaders) {
            Class c = loader.load(className)
            if (c)
                return c
        }
        throw new ClassNotFoundException("Cannot find $className")
    }

    interface Loader {
        Class load(String filePath)
    }

    private Loader loader(File file) throws IOException {
        if (!file.exists()) {
            return null;
        } else if (file.isDirectory()) {
            return new DirLoader(file);
        } else {
            throw new RuntimeException("Loader for $file not supported");
        }
    }

    private class DirLoader implements Loader {
        File dir;

        DirLoader(File dir) {
            this.dir = dir;
        }

        @Override
        Class load(String filePath) {
            File file = findFile(filePath, dir);
            if (file == null) {
                return null;
            }
            System.out.println("Reading file " + file);
            try {
                return INSTANCE.parseClass(file);
            } catch (IOException e) {
                return null;
            }
        }
    }

    private static File findFile(String filePath, File classPath) {
        File file = classToFile(filePath, classPath)
        return file.exists() ? file : null;
    }

    private static File classToFile(String clas, File dir) {
        if (clas.endsWith('.groovy')) {
            String[] parts = clas.split('.groovy')
            parts[0] = parts[0].replaceAll('\\.', '/')
            String file = dir.toString() + '/' + parts[0] + '.groovy'
            return new File(file)
        }
        return null
    }


}
