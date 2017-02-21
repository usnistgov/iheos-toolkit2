package gov.nist.toolkit.errorrecording.xml.assertions;

import gov.nist.toolkit.errorrecording.xml.assertions.helpers.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by diane on 10/13/2016.
 */
public class AssertionLibrary extends ArrayList<Assertion> {
    private ResourceLoader RESOURCELOADER = ResourceLoader.getResourceLoader();
    private static AssertionLibrary instance = null;

    /**
     * Create an AssertionLibrary from a List of Assertions
     */
    protected AssertionLibrary(){
        loadAssertions();
    }

    public static AssertionLibrary getInstance(){
        if (instance == null){
            instance = new AssertionLibrary();
        }
        return instance;
    }


    /**
     * Specify the Assertions file location (for example, for testing purposes)
     * @param fileLocation
     * @return
     */
    public void loadAssertions(String fileLocation) {
        InputStream is = RESOURCELOADER.getFileAsInputStream(fileLocation);
        loadAssertions(is);
    }

    /**
     * Load the Assertions file from hardcoded location (default mode)
     * @return
     */
    public void loadAssertions() {
        InputStream is = RESOURCELOADER.getToolkitAssertions();
        loadAssertions(is);
    }

    private void loadAssertions(InputStream is) {
        String line = "";
        String cvsSplitBy = ",";

        // Reset the Library
        this.clear();

        // Populate assertions library
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Skip the first line (column names)
            br.readLine();

            // Process the rest
            Assertion temp;
            while ((line = br.readLine()) != null) {

                // Remove leading and trailing whitespaces, then skip blank lines
                line = line.trim();
                if (!line.equals("")) {

                    // Split with a limit of 5 cells, which allows to keep trailing empty cells
                    String[] ta = line.split(cvsSplitBy, 5);

                    temp = new Assertion(ta[0], ta[1], ta[2], ta[3], ta[4]);
                    this.add(temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return an Assertion based on its ID
     * @param assertionID
     * @return
     */
    public Assertion getAssertion(String assertionID){
        for (Assertion a : this){
            if (a.getAssertionID().equals(assertionID)){
                return a;
            }
        }
        return null;
    }


    /**
     * Returns a pretty printed String of the Assertion Library contents
     * @return formatted Assertion Library contents
     */
    @Override
    public String toString(){
        String prettyPrint = "";
        for (Assertion a : this) {
            prettyPrint = prettyPrint +
                    "Toolkit Assertion [TA= " + a.getAssertionID() +
                    " , Toolkit Error Message=" + a.getErrorMessage() +
                    " , Location=" + a.getLocation() + " , Gazelle Scheme ID=" + a.getGazelleScheme() +
                    " , Gazelle Assertion ID=" + a.getGazelleAssertionID() + "]\n";
        }
        return prettyPrint;
    }

}
