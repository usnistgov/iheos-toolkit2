package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BuildCollections extends HttpServlet {
    static Logger logger = Logger.getLogger(BuildCollections.class);
    File testkitIn;
    File testkitOut;

    String sections[] = { "testdata", "tests", "examples", "selftest" };
    Map<String, List<String>> collections = new HashMap<String, List<String>>();
    boolean error;

    public void init(ServletConfig sConfig) throws ServletException {
        logger.info("Indexing testkit");
        run();
    }

    void write() throws IOException {
        File collectionsDir = new File(testkitOut + File.separator + "collections");
        File actorCollectionsDir = new File(testkitOut + File.separator + "actorcollections");

        collectionsDir.mkdir();  // create if doesn't exist

        for (Iterator<String> it = collections.keySet().iterator(); it.hasNext(); ) {
            String collectionName = it.next();
            List<String> contents = collections.get(collectionName);
            StringBuffer buf = new StringBuffer();
            for (String testnum : contents) {
                buf.append(testnum).append("\n");
            }
            File collectionFile = new File(collectionsDir + File.separator + collectionName + ".tc");
            logger.info(String.format("Writing %s", collectionFile));
            Io.stringToFile(collectionFile, buf.toString());
            if (ActorType.findActor(collectionName) != null) {
                File actorCollectionFile = new File(actorCollectionsDir + File.separator + collectionName + ".tc");
                logger.info(String.format("Writing %s", actorCollectionFile));
                Io.stringToFile(actorCollectionFile, buf.toString());
            }
        }

    }

    void add(String collection, String testnum) {
        List<String> c = collections.get(collection);
        if (c == null) {
            c = new ArrayList<String>();
            collections.put(collection, c);
        }
        if ( ! c.contains(testnum))
            c.add(testnum);
    }

    void tokenize(String tokenStr, String testnum) {
        StringTokenizer st = new StringTokenizer(tokenStr);
        while (st.hasMoreElements()) {
            String tok = st.nextToken();
            add(tok, testnum);
        }
    }

    void tokenize(File testDir) {
        File collFile = new File(testDir + File.separator + "collections.txt");
        if ( !collFile.exists()) {
            error = true;
            System.out.println(collFile + " does not exist");
        }
        String testnum = testDir.getName();
        String fileContents;
        try {
            fileContents = Io.stringFromFile(collFile);
            tokenize(fileContents, testnum);
        } catch (IOException e) {
            System.out.println("Cannot read " + collFile);
            error = true;
        }
    }

    void scan() {
        error = false;
        for (int i=0; i<sections.length; i++) {
            String section = sections[i];

            File sectionFile = new File(testkitIn + File.separator + section);
            File testDirs[] = sectionFile.listFiles();

            if (testDirs == null) {
                System.out.println("No tests defined in " + section + " (" + sectionFile + ")");
                error = true;
                continue;
            }

            for (int t=0; t<testDirs.length; t++) {
                File testDir = testDirs[t];
                if (!testDir.isDirectory())
                    continue;
                if (testDir.getName().equals(".svn"))
                    continue;
                File tagsFile = new File(testDir + File.separator + "collections.txt");
                if ( !tagsFile.exists()) {
                    System.out.println("Test dir " + testDir + " has no collections.txt file");
                    error = true;
                    continue;
                }
                tokenize(testDir);
            }
        }
        //System.out.println(collections);
    }

    void delete() {
        File collectionsDir = new File(testkitOut + File.separator + "collections");
        String[] contents = collectionsDir.list();
        if (contents == null)
            return;
        for (int i=0; i<contents.length; i++) {
            String filename = contents[i];
            File contentsFile = new File(collectionsDir + File.separator + filename);
            contentsFile.delete();
        }
    }

    public void run() {
        File testkit = Installation.installation().testkitFile();
        testkitIn = testkit;
        testkitOut = testkit;
        scan();
        try {
            write();
        } catch (Exception e) {
            logger.error(String.format("Cannot write built collections - %s", e.getMessage()));
        }
    }

//    public static void main(String[] args) {
//        BuildCollections bc = new BuildCollections();
//        bc.testkitIn = new File(args[0]);
//        bc.testkitOut = new File(args[1]);
//        System.out.println("Testkit being scanned: " + bc.testkitIn);
//        System.out.println("Collections will be written to " + bc.testkitOut);
//        bc.scan();
////		bc.delete();
//        bc.write();
//    }

}
