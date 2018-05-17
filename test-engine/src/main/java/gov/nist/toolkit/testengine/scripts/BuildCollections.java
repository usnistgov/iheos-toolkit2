package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.actortransaction.client.ActorOption;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.OptionType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BuildCollections extends HttpServlet {
    private static Logger logger = Logger.getLogger(BuildCollections.class);
    private File testkitIn;
    private File testkitOut;

    public static String SECTIONS[] = { "testdata", "tests", "examples", "selftest" };
    private Map<String, List<String>> collections = new HashMap<>();
    private boolean error;

    private void reset() {
        collections = new HashMap<>();
    }

   public void init(ServletConfig sConfig) throws ServletException {
      logger.info("Indexing testkit");
      run();
   }

    private String listAsFileContents(Collection<String> list) {
        StringBuilder buf = new StringBuilder();
        for (String ele : list) {
            buf.append(ele).append("\n");
        }
        return buf.toString();
    }

    private void write() throws IOException {
        File collectionsDir = new File(testkitOut + File.separator + Installation.collectionsDirName);
        File actorCollectionsDir = new File(testkitOut + File.separator + Installation.actorCollectionsDirName);
        // ActorType => list of test names
        // This layer is necessary because multiple collectionNames can map to single
        // actorCollection since some collections are based on the actor and some on transactions
        Map<ActorType, Set<String>> actorTestMap = new HashMap<>();

      collectionsDir.mkdirs(); // create if doesn't exist
      actorCollectionsDir.mkdirs();

      logger.info("Updating Collections...");
      logger.info("Collections found:\n" + collections.keySet());

      for (Iterator <String> it = collections.keySet().iterator(); it.hasNext();) {
         String collectionName = it.next();
         ActorType actorType = findActorType(collectionName);

         List <String> contents = collections.get(collectionName);
         if (actorType != null) {
            if (actorTestMap.get(actorType) == null) {
               actorTestMap.put(actorType, new HashSet <String>());
            }
            actorTestMap.get(actorType).addAll(contents);
         }
         File collectionFile = new File(collectionsDir + File.separator + collectionName + ".tc");
         logger.info(String.format("Writing %s", collectionFile));
         Io.stringToFile(collectionFile, listAsFileContents(contents));
      }

      for (ActorType actorType : actorTestMap.keySet()) {
         String name = actorType.getActorCode();
         String descriptiveName = actorType.getName();
         logger.info(String.format("Writing %s", new File(actorCollectionsDir + File.separator + name + ".tc")));
         Io.stringToFile(new File(actorCollectionsDir + File.separator + name + ".tc"),
            listAsFileContents(actorTestMap.get(actorType)));
         Io.stringToFile(new File(actorCollectionsDir, name + ".txt"), descriptiveName);
      }
   }

   private ActorType findActorType(String collectionName) {
         ActorType at;

         collectionName = collectionName.toLowerCase();

        ActorOption actorOption = new ActorOption(collectionName);
        at = ActorType.findActorByTcCode(actorOption.actorTypeId);
        // This is to handle a case where an actor only has optional test but no required tests. In this case, the default actorcollections will contain the first option tests.
        if (at!=null) {
           if (at.getOptions().contains(OptionType.REQUIRED)) {
               if (OptionType.REQUIRED.equals(actorOption.optionId)) {
                   return at;
               }
           } else {
               // Get first option in the list
               if (!at.getOptions().isEmpty()) {
                   OptionType firstOption = at.getOptions().get(0);
                   if (firstOption.equals(actorOption.optionId)) {
                       return at;
                   }
               }
           }
        }


        if (OptionType.REQUIRED.equals(actorOption.optionId)) {
            at = ActorType.findActorByTcCode(actorOption.actorTypeId);
            if (at != null) return at;
        }

         TransactionType tt = TransactionType.find(collectionName);
         if (tt == null) return null;
         at = ActorType.getActorType(tt);
         return at;
   }

    /**
     * Adds testnum to collection. Creates collection if needed
     *
     * @param collection name
     * @param testnum name
     */
    private void add(String collection, String testnum) {
        List<String> c = collections.get(collection);
        if (c == null) {
            c = new ArrayList<String>();
            collections.put(collection, c);
        }
        if ( ! c.contains(testnum))
            c.add(testnum);
    }

   /**
    * Add testnum to each collection in whitespace delimited tokenStr
    *
    * @param tokenStr string of collection tokens
    * @param testnum name
    */
   private void tokenize(String tokenStr, String testnum) {
      StringTokenizer st = new StringTokenizer(tokenStr);
      while (st.hasMoreElements()) {
         String tok = st.nextToken();
         add(tok, testnum);
      }
   }

    /**
     * Looks in testDir for collections.txt file. Adds testnum (testDir name) to
     * all collections in file.
     *
     * @param testDir test directory.
     */
    private void tokenize(File testDir) {
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

   /**
    * Processes all the {@link #SECTIONS}, looking for child test directories.
    * Adds each of these tests to the collections specified in its
    * collections.txt file.
    */
   private void scan() {
      error = false;
      for (int i = 0; i < SECTIONS.length; i++ ) {
         String section = SECTIONS[i];

         File sectionFile = new File(testkitIn + File.separator + section);
         File testDirs[] = sectionFile.listFiles();

         if (testDirs == null) {
            System.out.println("No tests defined in " + section + " (" + sectionFile + ")");
            error = true;
            continue;
         }

         for (int t = 0; t < testDirs.length; t++ ) {
            File testDir = testDirs[t];
            if (!testDir.isDirectory()) continue;
            if (testDir.getName().equals(".svn")) continue;
            File tagsFile = new File(testDir + File.separator + "collections.txt");
            if (!tagsFile.exists()) {
				// System.out.println("Test dir " + testDir + " has no collections.txt file");
               error = true;
               continue;
            }
            tokenize(testDir);
         }
      }
      // System.out.println(collections);
   }

   /**
    * deletes files and empty directories within collections subdirectory of
    * current {@link #testkitOut} directory. Not currently used.
    */
   void delete() {
      File collectionsDir = new File(testkitOut + File.separator + "collections");
      String[] contents = collectionsDir.list();
      if (contents == null) return;
      for (int i = 0; i < contents.length; i++ ) {
         String filename = contents[i];
         File contentsFile = new File(collectionsDir + File.separator + filename);
         contentsFile.delete();
      }
   }

   public void run() {
      for (File testkit : Installation.instance().getAllTestkits()) {
         testkitIn = testkit;
         testkitOut = testkit;
         reset();
         scan();
         try {
            write();
         } catch (Exception e) {
            logger.error(String.format("Cannot write built collections - %s", e.getMessage()));
         }
      }
   }
}
