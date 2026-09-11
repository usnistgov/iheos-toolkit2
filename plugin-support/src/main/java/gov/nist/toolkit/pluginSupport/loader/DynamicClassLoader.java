package gov.nist.toolkit.pluginSupport.loader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import gov.nist.toolkit.utilities.io.Io;

public class DynamicClassLoader extends AggressiveClassLoader {
    private LinkedList<ByteLoader> loaders = new LinkedList<ByteLoader>();

    public DynamicClassLoader(String... paths) throws IOException {
        for (String path : paths) {
            File file = new File(path);
            ByteLoader loader = loader(file);
            if (loader == null) {
                throw new RuntimeException("Path not exists " + path);
            }
            loaders.add(loader);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public DynamicClassLoader(Collection<File> paths) throws IOException {
        for (File file : paths) {
            ByteLoader loader = loader(file);
            if (loader == null) {
                throw new RuntimeException("Path not exists " + file.getPath());
            }
            loaders.add(loader);
        }
    }

    private static ByteLoader loader(File file) throws IOException {
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

    interface ByteLoader {
        byte[] load(String filePath);
    }

    static class DirLoader implements ByteLoader {
        File dir;

        DirLoader(File dir) {
            this.dir = dir;
        }

        @Override
        public byte[] load(String filePath) {
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

    static class JarLoader implements ByteLoader {
        JarFile jarFile;

        JarLoader(JarFile jarFile) {
            this.jarFile = jarFile;
        }

        public byte[] load(String filePath) {
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
        protected void finalize() throws Throwable {
            jarFile.close();
            super.finalize();
        }
    }

    @Override
    protected byte[] loadNewClass(String name) {
        System.out.println("Loading class " + name);
        for (ByteLoader loader : loaders) {
            byte[] data = loader.load(AggressiveClassLoader.toFilePath(name));
            if (data != null) {
                return data;
            }
        }
        return null;
    }
}
