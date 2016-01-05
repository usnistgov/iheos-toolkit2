package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
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

    String listAsFileContents(List<String> list) {
        StringBuilder buf = new StringBuilder();
        for (String ele : list) {
            buf.append(ele).append("\n");
        }
        return buf.toString();
    }

    void write() throws IOException {
        File collectionsDir = new File(testkitOut + File.separator + "collections");
        File actorCollectionsDir = new File(testkitOut + File.separator + "actorcollections");
        // ActorType => list of test names
        // This layer is necessary because multiple collectionNames can map to single
        // actorCollection since some collections are based on the actor and some on transactions
        Map<ActorType, List<String>> actorTestMap = new HashMap<>();

        collectionsDir.mkdir();  // create if doesn't exist

        logger.info("Collections found:\n" + collections.keySet());

        for (Iterator<String> it = collections.keySet().iterator(); it.hasNext(); ) {
            String collectionName = it.next();
            ActorType actorType = findActorType(collectionName);

            List<String> contents = collections.get(collectionName);
            if (actorType != null) {
                if (actorTestMap.get(actorType) == null) {
                    actorTestMap.put(actorType, new ArrayList<String>());
                }
                actorTestMap.get(actorType).addAll(contents);
            }
            File collectionFile = new File(collectionsDir + File.separator + collectionName + ".tc");
            logger.info(String.format("Writing %s", collectionFile));
            Io.stringToFile(collectionFile, listAsFileContents(contents));
        }

        for (ActorType actorType : actorTestMap.keySet()) {
            String name = actorType.getShortName();
            String descriptiveName = actorType.getName();
            Io.stringToFile(new File(actorCollectionsDir + File.separator + name + ".tc"), listAsFileContents(actorTestMap.get(actorType)));
            Io.stringToFile(new File(actorCollectionsDir, name + ".txt"), descriptiveName);
        }
    }

    ActorType findActorType(String collectionName) {
        ActorType at;

        collectionName = collectionName.toLowerCase();

        at = ActorType.findActor(collectionName.toLowerCase());
        if (at != null) return at;

        TransactionType tt = TransactionType.find(collectionName);
        if (tt == null) return null;
        at = ActorType.getActorType(tt);
        return at;
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
}
