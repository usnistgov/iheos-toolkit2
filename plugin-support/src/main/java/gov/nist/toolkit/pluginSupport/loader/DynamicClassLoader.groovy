package gov.nist.toolkit.pluginSupport.loader;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

class DynamicClassLoader extends AggressiveClassLoader {
    private LinkedList<Loader> loaders = new LinkedList<>();

    DynamicClassLoader(String... paths) throws IOException {
        for (String path : paths) {
            File file = new File(path);

            Loader loader = loader(file);
            if (loader == null) {
                throw new RuntimeException("Path not exists " + path);
            }
            loaders.add(loader);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    DynamicClassLoader(Collection<File> paths) throws IOException {
        for (File file : paths) {
            Loader loader = loader(file);
            if (loader == null) {
                throw new RuntimeException("Path not exists " + file.getPath());
            }
            loaders.add(loader);
        }
    }


    private static Loader loader(File file) throws IOException {
        if (!file.exists()) {
            return null;
        } else if (file.isDirectory()) {
            return new DirLoader(file);
        } else if (file.getName().endsWith("jar")) {
            return new JarLoader(new JarFile(file));
        } else {
            throw new RuntimeException("Jarloader not supported");
        }
    }

    private static File findFile(String filePath, File classPath) {
        File file = new File(classPath, filePath);
        return file.exists() ? file : null;
    }

    interface Loader {
        byte[] load(String filePath);
    }

    static class DirLoader implements Loader {
        File dir;

        DirLoader(File dir) {
            this.dir = dir;
        }

        @Override
        byte[] load(String filePath) {
            File file = findFile(filePath, dir);
            if (file == null) {
                return null;
            }
            System.out.println("Reading file " + file);
            try {
                return Io.bytesFromFile(file);
            } catch (IOException e) {
                return null;
            }
        }
    }

    static class JarLoader implements Loader {
        JarFile jarFile;

        JarLoader(JarFile jarFile) {
            this.jarFile = jarFile;
        }

        byte[] load(String filePath) {
            ZipEntry entry = jarFile.getJarEntry(filePath);
            if (entry == null) {
                return null;
            }
            try {
                return Io.getBytesFromInputStream(jarFile.getInputStream(entry));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        void finalize() throws Throwable {
            jarFile.close();
            super.finalize();
        }
    }

//    private static F1<String, byte[]> jarLoader(final JarFile jarFile) {
//        return new F1<String, byte[]>() {
//            public byte[] e(String filePath) {
//                ZipEntry entry = jarFile.getJarEntry(filePath);
//                if (entry == null) {
//                    return null;
//                }
//                try {
//                    return IOUtil.readData(jarFile.getInputStream(entry));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            @Override
//            protected void finalize() throws Throwable {
//                IOUtil.close(jarFile);
//                super.finalize();
//            }
//        };
//    }

    @Override
    protected byte[] loadNewClass(String name) {
		System.out.println("Loading class " + name);
        for (Loader loader : loaders) {
            byte[] data = loader.load(AggressiveClassLoader.toFilePath(name));
            if (data!= null) {
                return data;
            }
        }
        return null;
    }
}